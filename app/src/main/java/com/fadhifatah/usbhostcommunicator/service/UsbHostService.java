package com.fadhifatah.usbhostcommunicator.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsbHostService extends Service {

    private static String TAG = "ServiceUsbHost";

    private UsbManager usbManager;
    private UsbDeviceConnection deviceConnection;
    private UsbSerialDevice serialDevice;
    private UsbDevice device;

    private String dataTemp = "";

    private UsbSerialInterface.UsbReadCallback callback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {
            // TODO: received data from rest, save to DB, modify chat message
            String s = new String(bytes);

            if (bytes[bytes.length - 1] != 0x0a) {
                dataTemp = dataTemp + s;
            }
            else {
                dataTemp = dataTemp + s;
                String dataReceived = dataTemp;
                dataTemp = "";
                Log.d(TAG, dataReceived);
            }
        }
    };

    @Override
    public void onCreate() {
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceHashMap = usbManager.getDeviceList();
        List<UsbDevice> deviceList = new ArrayList<>(deviceHashMap.values());

        if (deviceList.size() > 0) {
            device = deviceList.get(0);
            starSerialConnection();
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopSelf();
                    startService(new Intent(getApplicationContext(), UsbHostService.class));
                }
            }, 10000);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }



    private void starSerialConnection() {
        deviceConnection = usbManager.openDevice(device);
        serialDevice = UsbSerialDevice.createUsbSerialDevice(device, deviceConnection);

        if (serialDevice != null) {
            if (serialDevice.open()) {
                serialDevice.setBaudRate(9600);
                serialDevice.setDataBits(UsbSerialInterface.DATA_BITS_8);
                serialDevice.setStopBits(UsbSerialInterface.STOP_BITS_1);
                serialDevice.setParity(UsbSerialInterface.PARITY_NONE);
                serialDevice.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                serialDevice.setDTR(true);
                serialDevice.setRTS(true);
                serialDevice.read(callback);
                Log.d(TAG, "Success Open");
                Toast.makeText(this, "Open Serial Device", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Failed");
                Toast.makeText(this, "Can't Open Serial Device", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopUsbConnection();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    private void stopUsbConnection() {
        if (serialDevice != null) {
            serialDevice.close();
        }

        if (deviceConnection != null) {
            deviceConnection.close();
        }
    }
}
