package br.com.ericksprengel.marmitop.ui.main.menu;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.apis.holidays.HolidaysTask;
import br.com.ericksprengel.marmitop.data.Event;
import br.com.ericksprengel.marmitop.data.Events;
import br.com.ericksprengel.marmitop.data.MtopMenuItem;
import br.com.ericksprengel.marmitop.ui.addtoorder.AddToOrderActivity;
import br.com.ericksprengel.marmitop.utils.MenuUtils;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MenuFragment extends Fragment implements MenuAdapter.OnMtopMenuItemClickListener, HolidaysTask.HolidaysCallback {

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
        HolidaysTask task = new HolidaysTask(getContext(), this);
        task.execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, rootView);

        // Firebase init
        // - Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        // - Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMenuDatabaseReference = mFirebaseDatabase.getReference("menus")
                .child(MenuUtils.getMenuOfTheDay());

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
                    onSignedInInitialize();
                } else {
                    onSignedOutCleanup();
                }
            }
        };
    }




    private void onSignedInInitialize() {
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
                    assert mtopMenuItem != null;
                    mtopMenuItem.setKey(dataSnapshot.getKey());
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

    @Override
    public void onHolidaysLoaded(Events events) {
        if (events != null) {
            Calendar now = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

            // for debug warning popup for holidays.
//            try {
//                now.setTime(sdf.parse("12/02/2018"));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }

            for(Event event : events.eventList) {
                Calendar eventDate = Calendar.getInstance();
                try {
                    eventDate.setTime(sdf.parse(event.date));
                    if(now.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR) &&
                            now.get(Calendar.DAY_OF_YEAR) == eventDate.get(Calendar.DAY_OF_YEAR)) {
                        showHolidayDialog(event);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showHolidayDialog(Event event) {
        Context context = getContext();
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.menu_fg_holiday_dialog_title)
                .setMessage(getResources().getString(R.string.menu_fg_holiday_dialog_message, event.name ))
                .setPositiveButton(R.string.menu_fg_holiday_dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nothing to do
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
