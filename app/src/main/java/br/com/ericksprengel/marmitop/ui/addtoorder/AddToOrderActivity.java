package br.com.ericksprengel.marmitop.ui.addtoorder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.data.MtopMenuItem;
import br.com.ericksprengel.marmitop.ui.AuthenticatedActivity;
import br.com.ericksprengel.marmitop.ui.main.menu.MenuAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddToOrderActivity extends AuthenticatedActivity {

    private static final String LOG_TAG = AddToOrderActivity.class.getName();

    public static final String EXTRA_MTOP_MENU_ITEM_KEY = "mtop_menu_item_key";

    @BindView(R.id.add_to_order_ac_name_textview) TextView mName;
    @BindView(R.id.add_to_order_ac_description_textview) TextView mDescription;
    @BindView(R.id.add_to_order_ac_options_recyclerview) RecyclerView mOptions;
    @BindView(R.id.add_to_order_ac_quantity_edittext) EditText mQuantity;

    // Database objects
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mMenuItemDatabaseReference;
    ValueEventListener mValueEventListener;

    private MtopMenuItem mMtopMenuItem;

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
        // - Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMenuItemDatabaseReference = mFirebaseDatabase.getReference("menus")
                .child("2018-01-29")
                .child(mtopMenuItem);
//                .child(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        authenticate();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachDatabaseReadListener();
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
        mName.setText(mMtopMenuItem.getName());
        mDescription.setText(mMtopMenuItem.getDescription());
        mOptionsAdapter.setOptions(mMtopMenuItem.getOptions());
    }
}
