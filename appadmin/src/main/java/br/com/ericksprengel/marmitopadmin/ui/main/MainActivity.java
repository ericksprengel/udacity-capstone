package br.com.ericksprengel.marmitopadmin.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import br.com.ericksprengel.marmitopadmin.BuildConfig;
import br.com.ericksprengel.marmitopadmin.R;
import br.com.ericksprengel.marmitopadmin.utils.MenuUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    // Views
    @BindView(R.id.main_ac_menu_id_textview) TextView mMenuId;
    @BindView(R.id.main_ac_uid_textview) TextView mUid;

    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMenuId.setText(MenuUtils.getMenuOfTheDay());

        // Firebase init
        mFirebaseAuth = FirebaseAuth.getInstance();

        authenticate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @OnClick(R.id.main_ac_create_menu_items_button)
    public void createMenuOfTheDay() {
        MenuUtils.createMenuOfTheDay();
    }

    // FIREBASE AUTH

    private static final int RC_SIGN_IN = 123;

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
                    onSignedInInitialize(user);
                } else {
                    onSignedOutCleanup();
                    // User is signed out
                    MainActivity.this.startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void onSignedInInitialize(FirebaseUser user) {
        mUid.setText(String.format("%s (%s)", user.getDisplayName(), user.getUid()));
        Log.d(LOG_TAG, "Logged in as " + user.getUid());
        Toast.makeText(MainActivity.this, "You're now signed in. Welcome, " + mUsername, Toast.LENGTH_SHORT).show();
    }

    private void onSignedOutCleanup() {
        mUid.setText("not logged");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO: super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Log.d(LOG_TAG, "Sign in completed.");
                return;
            } else if(resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show(); //TODO: string
                finish();
            } else {
                Toast.makeText(this, "Something is wrong.", Toast.LENGTH_SHORT).show(); //TODO: string
                finish();
            }
        }
    }
}
