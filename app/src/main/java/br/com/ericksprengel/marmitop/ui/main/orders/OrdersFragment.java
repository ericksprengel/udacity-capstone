package br.com.ericksprengel.marmitop.ui.main.orders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.data.OrderDay;
import br.com.ericksprengel.marmitop.ui.AuthenticatedFragment;
import br.com.ericksprengel.marmitop.ui.orderday.OrderDayActivity;
import butterknife.BindView;
import butterknife.ButterKnife;


public class OrdersFragment extends AuthenticatedFragment implements OrdersAdapter.OnOrderDayClickListener {

    // Views
    @BindView(R.id.order_days_frag_recyclerview) RecyclerView mRecyclerView;

    private OrdersAdapter mOrdersAdapter;

    // Database objects
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mOrdersDatabaseReference;
    ChildEventListener mChildEventListener;

    public OrdersFragment() {}

    public static OrdersFragment newInstance() {
        return new OrdersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        ButterKnife.bind(this, rootView);

        // Firebase init
        // - Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Menu list init
        mOrdersAdapter = new OrdersAdapter(this);
        mRecyclerView.setAdapter(mOrdersAdapter);

        return rootView;
    }

    @Override
    protected void onSignedInInitialize(FirebaseUser user) {
        attachDatabaseReadListener(user);
    }

    @Override
    public void onSignedOutCleanup() {
        mOrdersAdapter.clear();
        detachDatabaseReadListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        mOrdersAdapter.clear();
        detachDatabaseReadListener();
    }

    @Override
    public void onOrderDayClick(OrderDay orderDay) {
        startActivity(OrderDayActivity.getStartIntent(getContext(), orderDay));
    }

    private void attachDatabaseReadListener(FirebaseUser user) {
        if(mOrdersDatabaseReference == null) {
            mOrdersDatabaseReference = mFirebaseDatabase.getReference("user_orders")
                    .child(user.getUid());
        }

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    OrderDay orderDay = dataSnapshot.getValue(OrderDay.class);
                    assert orderDay != null;
                    orderDay.setMenuId(dataSnapshot.getKey());
                    mOrdersAdapter.add(orderDay);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mOrdersDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mOrdersDatabaseReference != null && mChildEventListener != null) {
            mOrdersDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

}
