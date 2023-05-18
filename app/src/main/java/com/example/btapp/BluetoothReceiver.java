package com.example.btapp;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothReceiver extends BroadcastReceiver {

    private BluetoothListener listener;

    public BluetoothReceiver(BluetoothListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (listener != null) {
                listener.onDeviceFound(device);
            }
        }
    }

    public interface BluetoothListener {
        void onDeviceFound(BluetoothDevice device);
    }
}
