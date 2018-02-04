package br.com.ericksprengel.marmitop.ui.main.menu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.ericksprengel.marmitop.data.MtopMenuItem;
import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.ui.addtoorder.AddToOrderActivity;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MenuFragment extends Fragment implements MenuAdapter.OnMtopMenuItemClickListener {

    // Views
    @BindView(R.id.menu_frag_recycleview) RecyclerView mRecyclerView;

    private MenuAdapter mMenuAdapter;

    // Database objects
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mMenuDatabaseReference;
    ChildEventListener mChildEventListener;

    public MenuFragment() {}

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, rootView);

        // Firebase init
        // - Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        // - Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMenuDatabaseReference = mFirebaseDatabase.getReference("menus")
//                .child(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                .child("2018-01-29");

        // Menu list init
        mMenuAdapter = new MenuAdapter(this);
        mRecyclerView.setAdapter(mMenuAdapter);

        authenticate();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        mMenuAdapter.clear();
        detachDatabaseReadListener();
    }

    @Override
    public void onMtopMenuItemClick(MtopMenuItem mtopMenuItemtem) {
        Toast.makeText(getContext(), "Fazer adicionar ao pedido!\n" + mtopMenuItemtem.getName(), Toast.LENGTH_SHORT).show();
        startActivity(AddToOrderActivity.getStartIntent(getContext(), mtopMenuItemtem));
    }


    // FIREBASE AUTH

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public void authenticate() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    onSignedOutCleanup();
                }
            }
        };
    }




    private void onSignedInInitialize(String username) {
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        mMenuAdapter.clear();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    MtopMenuItem mtopMenuItem = dataSnapshot.getValue(MtopMenuItem.class);
                    mtopMenuItem.setKey(dataSnapshot.getKey());
                    Log.e("SPRENGEL", "Novo prato!" + mtopMenuItem.getName());
                    mMenuAdapter.add(mtopMenuItem);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mMenuDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMenuDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }










    //TODO: it's just for tests
    private void createMtopMenuItem() {
        MtopMenuItem menuItem = new MtopMenuItem();
        menuItem.setName("Feijoada");
        menuItem.setDescription("arroz, feijão e fritas." +  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        Toast.makeText(getContext(), menuItem.getDescription(), Toast.LENGTH_SHORT).show();
        mMenuDatabaseReference.child(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).push().setValue(menuItem);
    }
}
