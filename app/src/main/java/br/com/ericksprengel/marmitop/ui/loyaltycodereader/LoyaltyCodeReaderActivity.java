package br.com.ericksprengel.marmitop.ui.loyaltycodereader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.utils.MenuUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoyaltyCodeReaderActivity extends AppCompatActivity {

    public static final String LOG_TAG = LoyaltyCodeReaderActivity.class.getSimpleName();

    public static final int BARCODE_PERMISSIONS_REQUEST_CAMERA = 0x100;

    public static final String PARAMS_BARCODE = "PARAMS_BARCODE";

    @BindView(R.id.loyalty_code_reader_ac_surfaceview) SurfaceView mSurfaceView;

    private String mBarcode = "";
    private CameraSource mCameraSource;

    // Database objects
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mMenuItemDatabaseReference;


    private SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {

        @SuppressLint("MissingPermission")
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            startCapturing();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopCapturing();
        }
    };
    private boolean mReading = true;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoyaltyCodeReaderActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyalty_code_reader);
        ButterKnife.bind(this);

        // Firebase init
        // - Auth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // - Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMenuItemDatabaseReference = mFirebaseDatabase.getReference("loyalty_points")
                .child(user.getUid())
                .child(MenuUtils.getMenuOfTheDay());

        Log.e(LOG_TAG, "onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        requestPermissions();
        startReader();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseReader();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCameraSource != null) {
            mCameraSource.release();
        }
    }

    public void requestPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                //TODO: show more detailed explanation about camera permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        BARCODE_PERMISSIONS_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        BARCODE_PERMISSIONS_REQUEST_CAMERA);

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case BARCODE_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startReader();
                } else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(PARAMS_BARCODE, mBarcode);
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    finish();
                }
            }
        }
    }

    private void startReader() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.e(LOG_TAG, "startReader: start!");
        mSurfaceView.getHolder().addCallback(mSurfaceHolderCallback);
    }

    private void pauseReader() {
        Log.e(LOG_TAG, "pauseReader");
        if(mCameraSource != null) {
            mCameraSource.stop();
        }
    }

    @SuppressLint("MissingPermission")
    private void startCapturing() {
        Log.e(LOG_TAG, "startCapturing");
        final BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        mCameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override public void release() { }

            @Override
            public void receiveDetections(Detector.Detections detections) {
                SparseArray<Barcode> items = detections.getDetectedItems();
                if(items.size() == 0) {
                    Log.e(LOG_TAG, "Detections: 0");
                } else {
                    Log.e(LOG_TAG, "Detections: " + items.size() + "  Value: " + items.valueAt(0).rawValue);
                    if(mReading) {
                        mReading = false;
                        saveCode(items.valueAt(0).rawValue);
                    }
                }
            }
        });

        try {
            mCameraSource.start(mSurfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopCapturing() {
        if(mCameraSource != null) {
            mCameraSource.stop();
        }
    }

    private void saveCode(String barcode) {
        mMenuItemDatabaseReference.setValue(barcode).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(LOG_TAG, "onFailure", e);
                showInvalidCodeMessage();
            }
        })
        .addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(LOG_TAG, "onSuccess");
                finish();
            }
        });
    }

    private void showInvalidCodeMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Código inválido.")
                .setMessage("O QRCode lido não é válido.\nPor favor tente novamente ou peça ajuda ao atendente.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mReading = true;
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        mReading = true;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}