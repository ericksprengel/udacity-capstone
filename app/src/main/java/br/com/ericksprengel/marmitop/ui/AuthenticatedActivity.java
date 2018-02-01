package br.com.ericksprengel.marmitop.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.data.MtopMenuItem;
import br.com.ericksprengel.marmitop.ui.addtoorder.AddToOrderActivity;
import butterknife.ButterKnife;


public abstract class AuthenticatedActivity  extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    // Database objects
    FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase init
        // - Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        // - Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        authenticate();
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
    }

    public void authenticate() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    onSignedInInitialize(user);
                } else {
                    onSignedOutCleanup(user);
                }
            }
        };
    }

    protected abstract void onSignedInInitialize(FirebaseUser user);

    public abstract void onSignedOutCleanup(FirebaseUser user);
}
