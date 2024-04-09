package com.example.btservice;

import androidx.appcompat.app.AppCompatActivity;

////////////////////////

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class BluetoothScanService extends Service {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private String expectedDeviceAddress = "00:00:00:00:00:00"; // Replace with your expected device address
    private final String CHANNEL_ID = "BackgroundServiceChannel";
    private final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            stopSelf();
            return;
        }
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        startScanning();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startScanning() {
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                if (device.getAddress().equals(expectedDeviceAddress)) {
                    connectToDevice(device);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        bluetoothLeScanner.startScan(null, scanSettings, scanCallback);
    }

    private void connectToDevice(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        device.connectGatt(this, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    if (ActivityCompat.checkSelfPermission(BluetoothScanService.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Log.i("BluetoothScanService", "Connected to device: " + device.getName());
                    showConnectedNotification(device.getName());
                    /*Intent broadcastIntent = new Intent(MainActivity.ACTION_DEVICE_CONNECTED);
                    broadcastIntent.putExtra(MainActivity.EXTRA_DEVICE_NAME, device.getName());
                    broadcastIntent.putExtra(MainActivity.EXTRA_DEVICE_ADDRESS, device.getAddress());
                    sendBroadcast(broadcastIntent);
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.i("BluetoothScanService", "Device disconnected");
                    showScanningNotification();
                    startScanning(); // Restart scanning when the device is disconnected
                }*/
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    // Broadcast device connected action
                    Intent broadcastIntent = new Intent(MainActivity.ACTION_DEVICE_CONNECTED);
                    broadcastIntent.putExtra(MainActivity.EXTRA_DEVICE_NAME, device.getName());
                    broadcastIntent.putExtra(MainActivity.EXTRA_DEVICE_ADDRESS, device.getAddress());
                    sendBroadcast(broadcastIntent);
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // Device disconnected
                    Log.i("BluetoothScanService", "Device disconnected");
                    // Show scanning notification
                    showScanningNotification();
                    // Restart scanning
                    startScanning();
                }
            }
        });
    }

    private void showConnectedNotification(String deviceName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Connected to Device")
                .setContentText("Connected to device: " + deviceName)
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void showScanningNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App is Running")
                .setContentText("Scanning for devices...")
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScanning();
    }

    private void stopScanning() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ScanCallback scanCallback = new ScanCallback() {
            // Override necessary methods
        };
        bluetoothLeScanner.stopScan(scanCallback);
    }
}
