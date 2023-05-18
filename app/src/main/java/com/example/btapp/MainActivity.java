package com.example.btapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private BluetoothDiscoveryStartedReceiver receiver;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private BluetoothReceiver bluetoothReceiver;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate the BroadcastReceiver
        receiver = new BluetoothDiscoveryStartedReceiver(new BluetoothDiscoveryStartedReceiver.BluetoothDiscoveryListener() {
            @Override
            public void onDiscoveryStarted() {
                // Perform any action when Bluetooth discovery starts
                Toast.makeText(MainActivity.this, "Bluetooth discovery started", Toast.LENGTH_SHORT).show();
            }

        });

        // Register the BroadcastReceiver with the appropriate intent filter
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(receiver, filter1);



        // Instantiate the BroadcastReceiver
        bluetoothReceiver = new BluetoothReceiver(new BluetoothReceiver.BluetoothListener() {
            @Override
            public void onDeviceFound(BluetoothDevice device) {
                // Perform any action with the discovered Bluetooth device
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                // Store the device information in the database or perform any other desired operation
                Toast.makeText(MainActivity.this, "Device Found: " +deviceName + deviceAddress, Toast.LENGTH_LONG).show();

            }
        });

        // Register the BroadcastReceiver with the appropriate intent filter
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);

        requestLocationPermission();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister the BroadcastReceiver when your activity or fragment is destroyed
        unregisterReceiver(receiver);
        unregisterReceiver(bluetoothReceiver);
    }


    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location Permission");
                builder.setMessage("This app needs access to your location to discover nearby Bluetooth devices.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            } else {
                // No explanation needed, request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            // Location permission is already granted, start Bluetooth discovery
            startBluetoothDiscovery();
            System.out.println("START DISCOVERY");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, start Bluetooth discovery
                startBluetoothDiscovery();
            } else {
                // Location permission denied
                Toast.makeText(this, "Location permission denied. Bluetooth discovery cannot be performed.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void startBluetoothDiscovery() {
        // Start Bluetooth discovery
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            // Bluetooth is disabled, request the user to enable it
            enableBluetoothLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            // User enabled Bluetooth, start Bluetooth discovery
                            startBluetoothDiscovery();
                        } else {
                            // User did not enable Bluetooth, show a message or take appropriate action
                            Toast.makeText(this, "Bluetooth is required for this application.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableBluetoothIntent);
        } else {
            // Bluetooth is already enabled, start Bluetooth discovery
            bluetoothAdapter.startDiscovery();
        }
    }
}





