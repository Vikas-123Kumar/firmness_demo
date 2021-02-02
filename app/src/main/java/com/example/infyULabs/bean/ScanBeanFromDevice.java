package com.example.infyULabs.bean;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ScanBeanFromDevice {
    public String ScanData;
    public BluetoothSocket bluetoothSocket;

    public ScanBeanFromDevice() {
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;

    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        Log.e("set socket", bluetoothSocket.isConnected() + "");
        this.bluetoothSocket = bluetoothSocket;
    }

    public String getScanData() {
        return ScanData;
    }

    public void setScanData(String scanData) {
        this.ScanData = scanData;
    }

    @Override
    public String toString() {
        return "ScanBeanFromDevice{" +
                "bluetoothSocket=" + bluetoothSocket +
                '}';
    }
}
