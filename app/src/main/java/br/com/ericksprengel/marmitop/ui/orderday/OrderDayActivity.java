package br.com.ericksprengel.marmitop.ui.orderday;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.data.Order;
import br.com.ericksprengel.marmitop.data.OrderDay;
import br.com.ericksprengel.marmitop.ui.AuthenticatedActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDayActivity extends AuthenticatedActivity implements OrdersAdapter.OnOrderQuantityListener {

    public static final String EXTRA_ORDER_DAY_KEY = "extra_order_day_key";

    // Views
    @BindView(R.id.order_day_ac_recyclerview) RecyclerView mRecyclerView;

    private String mMenuId;
    private OrdersAdapter mOrdersAdapter;

    // Database objects
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mOrdersDatabaseReference;
    ChildEventListener mChildEventListener;

    public static Intent getStartIntent(Context context, OrderDay orderDay) {
        Intent intent = new Intent(context, OrderDayActivity.class);
        intent.putExtra(EXTRA_ORDER_DAY_KEY, orderDay.getMenuId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_day);
        ButterKnife.bind(this);

        mMenuId = getIntent().getStringExtra(EXTRA_ORDER_DAY_KEY);

        setTitle(getString(R.string.order_day_ac_title, mMenuId));

        // Firebase init
        // - Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Menu list init
        mOrdersAdapter = new OrdersAdapter(this);
        mRecyclerView.setAdapter(mOrdersAdapter);
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
    public void onOrderQuantityChanged(String orderKey, Order order) {
        if(order.getQuantity() == 0) {
            Snackbar.make(mRecyclerView,
                    getString(R.string.order_day_ac_item_quantity_zero_alert,
                            getString(R.string.order_day_ac_save_button)),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.order_day_ac_save_buton)
    public void saveOrderChanges() {
        for (int i = 0; i < mOrdersAdapter.getItemCount(); i++) {
            String orderKey = mOrdersAdapter.getOrderKey(i);
            Order order = mOrdersAdapter.getOrder(i);

            if(order.getQuantity() == 0) {
                mOrdersDatabaseReference.child(orderKey).removeValue();
            } else {
                mOrdersDatabaseReference.child(orderKey)
                        .child("quantity").setValue(order.getQuantity());
            }
        }
        finish();
    }

    private void attachDatabaseReadListener(FirebaseUser user) {
        if(mOrdersDatabaseReference == null) {
            mOrdersDatabaseReference = mFirebaseDatabase.getReference("user_orders")
                    .child(user.getUid())
                    .child(mMenuId)
                    .child("orders");
        }

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String orderKey = dataSnapshot.getKey();
                    Order order = dataSnapshot.getValue(Order.class);
                    mOrdersAdapter.add(orderKey, order);
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
