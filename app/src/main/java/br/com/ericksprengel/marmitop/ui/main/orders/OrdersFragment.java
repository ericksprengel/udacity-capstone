package br.com.ericksprengel.marmitop.ui.main.orders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.data.MtopMenuItem;
import br.com.ericksprengel.marmitop.data.Order;
import br.com.ericksprengel.marmitop.data.OrderDay;
import br.com.ericksprengel.marmitop.ui.AuthenticatedFragment;
import br.com.ericksprengel.marmitop.ui.addtoorder.AddToOrderActivity;
import br.com.ericksprengel.marmitop.ui.main.menu.MenuAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class OrdersFragment extends AuthenticatedFragment implements OrdersAdapter.OnOrderDayClickListener {

    // Views
    @BindView(R.id.order_days_frag_recycleview)
    RecyclerView mRecyclerView;

    private OrdersAdapter mOrdersAdapter;

    // Database objects
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mMenusDatabaseReference;
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
    public void onSignedOutCleanup(FirebaseUser user) {
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
        Toast.makeText(getContext(), "Exibir order day!\n" + orderDay.getMenuId(), Toast.LENGTH_SHORT).show();
        //startActivity(AddToOrderActivity.getStartIntent(getContext(), orderDay));
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
