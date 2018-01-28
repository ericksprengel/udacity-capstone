package br.com.ericksprengel.marmitop.ui;

import android.app.Application;

import com.facebook.stetho.Stetho;


public class MarmitopApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO Fabric.with(this, new Crashlytics());
        Stetho.initializeWithDefaults(this);
    }
}
