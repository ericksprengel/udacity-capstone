package br.com.ericksprengel.marmitop.ui.addtoorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.data.MtopMenuItem;
import br.com.ericksprengel.marmitop.data.Order;
import br.com.ericksprengel.marmitop.ui.AuthenticatedActivity;
import br.com.ericksprengel.marmitop.utils.MenuUtils;
import br.com.ericksprengel.marmitop.views.QuantityView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddToOrderActivity extends AuthenticatedActivity implements OptionsAdapter.OnOptionClickListener, QuantityView.OnQuantityListener {

    private static final String LOG_TAG = AddToOrderActivity.class.getName();

    public static final String EXTRA_MTOP_MENU_ITEM_KEY = "mtop_menu_item_key";

    public static final String STATE_OPTION = "state_option";

    @BindView(R.id.add_to_order_ac_description_textview) TextView mDescription;
    @BindView(R.id.add_to_order_ac_options_recyclerview) RecyclerView mOptions;
    @BindView(R.id.add_to_order_ac_quantityview) QuantityView mQuantity;
    @BindView(R.id.add_to_order_ac_add_to_order_button) View mAddToOrderButton;

    // Database objects
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mMenuItemDatabaseReference;
    DatabaseReference mUserOrdersDatabaseReference;
    ValueEventListener mValueEventListener;

    private MtopMenuItem mMtopMenuItem;
    private OptionsAdapter mOptionsAdapter;

    public static Intent getStartIntent(Context context, MtopMenuItem mtopMenuItemtem) {
        Intent intent = new Intent(context, AddToOrderActivity.class);
        intent.putExtra(EXTRA_MTOP_MENU_ITEM_KEY, mtopMenuItemtem.getKey());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_order);
        ButterKnife.bind(this);

        String mtopMenuItem = getIntent().getStringExtra(EXTRA_MTOP_MENU_ITEM_KEY);
        Log.d(LOG_TAG, "Openning AddToOrderActivity for item: " + mtopMenuItem);

        // Firebase init
        // - Auth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // - Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMenuItemDatabaseReference = mFirebaseDatabase.getReference("menus")
                .child(MenuUtils.getMenuOfTheDay())
                .child(mtopMenuItem);
        mUserOrdersDatabaseReference = mFirebaseDatabase.getReference("user_orders")
                .child(user.getUid())
                .child(MenuUtils.getMenuOfTheDay())
                .child("orders");

        mOptionsAdapter = new OptionsAdapter(this);
        mOptions.setAdapter(mOptionsAdapter);

        mQuantity.setOnQuantityListener(this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mOptionsAdapter.setSelectedOption(savedInstanceState.getString(STATE_OPTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_OPTION, mOptionsAdapter.getSelectedOptionKey());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onSignedInInitialize(FirebaseUser user) {
        attachDatabaseReadListener();
    }

    @Override
    public void onSignedOutCleanup(FirebaseUser user) {
        detachDatabaseReadListener();
    }


    private void attachDatabaseReadListener() {
        if (mValueEventListener== null) {
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mMtopMenuItem = dataSnapshot.getValue(MtopMenuItem.class);
                    mMtopMenuItem.setKey(dataSnapshot.getKey());
                    Log.e(LOG_TAG, "onDataChange: " + mMtopMenuItem.getName());
                    updateView();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mMenuItemDatabaseReference.addListenerForSingleValueEvent(mValueEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mValueEventListener != null) {
            mMenuItemDatabaseReference.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
    }

    private void updateView() {
        setTitle(mMtopMenuItem.getName());
        mDescription.setText(mMtopMenuItem.getDescription());

        List<Pair<String, MtopMenuItem.Option>> options = new ArrayList<>();
        for(Map.Entry entry : mMtopMenuItem.getOptions().entrySet()) {
            Pair pair = new Pair(entry.getKey(), entry.getValue());
            options.add(pair);
        }
        mOptionsAdapter.setOptions(options);
    }

    @Override
    public void onOptionClick(MtopMenuItem.Option option) {

    }

    @Override
    public void onQuantityChanged(int quantity) {
        mAddToOrderButton.setEnabled(quantity > 0);
    }

    @OnClick(R.id.add_to_order_ac_add_to_order_button)
    public void addToOrder() {
        Order order = new Order(mMtopMenuItem, mQuantity.getQuantity(), mOptionsAdapter.getSelectedOption());
        mUserOrdersDatabaseReference.push().setValue(order);
        finish();
    }
}
