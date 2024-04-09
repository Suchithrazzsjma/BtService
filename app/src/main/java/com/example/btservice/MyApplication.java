package com.example.btservice;
import android.app.Application;
import android.content.Intent;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Start your service here
        Intent serviceIntent = new Intent(this, BluetoothScanService.class);
        startService(serviceIntent);
    }
}