package br.com.ericksprengel.marmitop.ui.main.loyaltyprogram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.data.OrderDay;
import br.com.ericksprengel.marmitop.ui.AuthenticatedFragment;
import br.com.ericksprengel.marmitop.ui.main.orders.OrdersAdapter;
import br.com.ericksprengel.marmitop.ui.main.orders.OrdersFragment;
import br.com.ericksprengel.marmitop.ui.orderday.OrderDayActivity;
import butterknife.BindView;
import butterknife.ButterKnife;


public class LoyaltyProgramFragment extends AuthenticatedFragment {

    // Views
    @BindView(R.id.loyalty_program_fg_recyclerview) RecyclerView mRecyclerView;

    private LoyaltyProgramPointsAdapter mLoyaltyProgramPointsAdapter;

    // Database objects
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mLoyaltyProgramDatabaseReference;
    ChildEventListener mChildEventListener;

    public LoyaltyProgramFragment() {}

    public static LoyaltyProgramFragment newInstance() {
        return new LoyaltyProgramFragment();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_loyalty_program, container, false);
        ButterKnife.bind(this, rootView);

        // Firebase init
        // - Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Menu list init
        mLoyaltyProgramPointsAdapter = new LoyaltyProgramPointsAdapter();
        mRecyclerView.setAdapter(mLoyaltyProgramPointsAdapter);

        return rootView;
    }

    @Override
    protected void onSignedInInitialize(FirebaseUser user) {
        attachDatabaseReadListener(user);
    }

    @Override
    public void onSignedOutCleanup(FirebaseUser user) {
        mLoyaltyProgramPointsAdapter.clear();
        detachDatabaseReadListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLoyaltyProgramPointsAdapter.clear();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener(FirebaseUser user) {
        if(mLoyaltyProgramDatabaseReference == null) {
            mLoyaltyProgramDatabaseReference = mFirebaseDatabase.getReference("loyalty_points")
                    .child(user.getUid());
        }

        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // TODO: It'll be used to validate menu's qrcode.
                    // String qrcode = dataSnapshot.getValue(String.class);
                    // String menuId = dataSnapshot.getKey();
                    mLoyaltyProgramPointsAdapter.add();
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mLoyaltyProgramDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mLoyaltyProgramDatabaseReference != null && mChildEventListener != null) {
            mLoyaltyProgramDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}
