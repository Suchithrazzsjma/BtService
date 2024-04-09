package com.example.btservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
/*<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">


    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    <uses-permission android:name="android.permission.ACCESS_LOCATION_EXTRA_COMMANDS" />
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission
        android:name="android.permission.BLUETOOTH_PRIVILEGED"
        tools:ignore="ProtectedPermissions" />

    <uses-feature
        android:name="android.hardware.bluetooth_le"
        android:required="true" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />


    <application
android:allowBackup="true"
android:icon="@mipmap/ic_launcher"
android:label="@string/app_name"
android:roundIcon="@mipmap/ic_launcher_round"
android:supportsRtl="true"
    android:theme="@style/Theme.Btservice"
    tools:targetApi="31">

<activity android:name=".MainActivity"
    android:exported="true"> <!-- Ensure MainActivity is exported -->
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<service android:name=".BluetoothScanService" />


<receiver android:name=".MainActivity$MyBroadcastReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="com.example.btservice.ACTION_DEVICE_CONNECTED" />
    </intent-filter>
</receiver>


</application>
    </manifest>*/
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView connectedDeviceNameTextView;
    private TextView connectedDeviceAddressTextView;

    public static final String ACTION_DEVICE_CONNECTED = "com.example.btktapp.ACTION_DEVICE_CONNECTED";
    public static final String EXTRA_DEVICE_NAME = "extra_device_name";
    public static final String EXTRA_DEVICE_ADDRESS = "extra_device_address";

    private final BroadcastReceiver deviceConnectedReceiver = new MyBroadcastReceiver();

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String deviceName = intent.getStringExtra(EXTRA_DEVICE_NAME);
            String deviceAddress = intent.getStringExtra(EXTRA_DEVICE_ADDRESS);
            connectedDeviceNameTextView.setText("Connected Device Name: " + deviceName);
            connectedDeviceAddressTextView.setText("Connected Device Address: " + deviceAddress);
            //startActivity(new Intent(MainActivity.this, MainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectedDeviceNameTextView = findViewById(R.id.connectedDeviceNameTextView);
        connectedDeviceAddressTextView = findViewById(R.id.connectedDeviceAddressTextView);

        registerReceiver(deviceConnectedReceiver, new IntentFilter(ACTION_DEVICE_CONNECTED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(deviceConnectedReceiver);
    }
}
