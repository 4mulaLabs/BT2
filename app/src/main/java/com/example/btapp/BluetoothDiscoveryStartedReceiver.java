package com.example.btapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothDiscoveryStartedReceiver extends BroadcastReceiver {

    private BluetoothDiscoveryListener listener;

    public BluetoothDiscoveryStartedReceiver(BluetoothDiscoveryListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            if (listener != null) {
                listener.onDiscoveryStarted();
            }
        }
    }

    public interface BluetoothDiscoveryListener {
        void onDiscoveryStarted();
    }
}
