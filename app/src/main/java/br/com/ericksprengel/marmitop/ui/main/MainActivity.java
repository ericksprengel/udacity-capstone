package br.com.ericksprengel.marmitop.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import br.com.ericksprengel.mamitop.data.MtopMenuItem;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

import br.com.ericksprengel.marmitop.BuildConfig;
import br.com.ericksprengel.marmitop.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_ac_bottomnavigationview) BottomNavigationView mBottomNavigationView;
    @BindView(R.id.main_ac_viewpager) ViewPager mViewPager;

    MainPagerAdapter mPagerAdapter;

    // Database objects
    FirebaseDatabase mDatabase;
    DatabaseReference mMenus;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_menu:
                    MtopMenuItem menuItem = new MtopMenuItem();
                    menuItem.setName("Feijoada");
                    menuItem.setDescription("arroz, feijão e fritas."  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                    Toast.makeText(MainActivity.this, menuItem.getDescription(), Toast.LENGTH_SHORT).show();
                    mMenus.child(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).push().setValue(menuItem);
                    mViewPager.setCurrentItem(MainPagerAdapter.ITEM_MENU, true);
                    return true;
                case R.id.navigation_orders:
                    mViewPager.setCurrentItem(MainPagerAdapter.ITEM_ORDERS, true);
                    return true;
                case R.id.navigation_loyalty_program:
                    mViewPager.setCurrentItem(MainPagerAdapter.ITEM_LOYALTY_PROGRAM, true);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Database init
        mDatabase = FirebaseDatabase.getInstance();
        mMenus = mDatabase.getReference("menus");


        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            private static final float MIN_SCALE = 0.85f;
            private static final float MIN_ALPHA = 0.5f;

            @Override
            public void transformPage(@NonNull View page, float position) {
                int pageWidth = page.getWidth();
                int pageHeight = page.getHeight();

                View view = page; //page.findViewById(R.id.step_frag_description_textview);

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setAlpha(0);

                } else if (position <= 1) { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                    float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                    float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                    if (position < 0) {
                        view.setTranslationX(horzMargin - vertMargin / 2);
                    } else {
                        view.setTranslationX(-horzMargin + vertMargin / 2);
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                    // Fade the page relative to its size.
                    view.setAlpha(MIN_ALPHA +
                            (scaleFactor - MIN_SCALE) /
                                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));
                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setAlpha(0);
                }
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case MainPagerAdapter.ITEM_MENU:
                        mBottomNavigationView.setSelectedItemId(R.id.navigation_menu);
                        break;
                    case MainPagerAdapter.ITEM_ORDERS:
                        mBottomNavigationView.setSelectedItemId(R.id.navigation_orders);
                        break;
                    case MainPagerAdapter.ITEM_LOYALTY_PROGRAM:
                        mBottomNavigationView.setSelectedItemId(R.id.navigation_loyalty_program);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

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

    private void loadMenu() {
        Toast.makeText(this, "TODO: load menu", Toast.LENGTH_LONG).show();
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
                    loadMenu();
                    Toast.makeText(MainActivity.this, "You're now signed in. Welcome to FriendlyChat.", Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    AuthUI.IdpConfig idpConfigPhone = new AuthUI.IdpConfig.PhoneBuilder()
                            .setDefaultCountryIso("br")
                            .build();

                    MainActivity.this.startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            idpConfigPhone,
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO: super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                this.startActivity(SignedInActivity.createIntent(this, response));
                this.finish();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "TODO(string): Sign in cancelled", Toast.LENGTH_LONG).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "TODO(string): No internet connection", Toast.LENGTH_LONG).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "TODO(string): Unknown error", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            Toast.makeText(this, "TODO(string): unknown_sign_in_response", Toast.LENGTH_LONG).show();
        }
    }
}
