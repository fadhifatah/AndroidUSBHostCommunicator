package com.fadhifatah.usbhostcommunicator.activity;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fadhifatah.usbhostcommunicator.R;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

public class DeviceActivity extends AppCompatActivity {

    private static final byte CR  = 0x0d;
    private static final byte LF  = 0x0a;

    private UsbManager usbManager;
    private UsbDeviceConnection deviceConnection;
    private UsbSerialDevice serialDevice;
    private UsbDevice device;

    private String dataTemp = "";
    private String dataReceived = "";

    private UsbSerialInterface.UsbReadCallback callback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(final byte[] bytes) {
//            TODO: do action fo received dataTemp, if last byte is 0x0a it is the end else wait another dataTemp!
            String s = new String(bytes);

            if (bytes[bytes.length - 1] != LF) {
                dataTemp = dataTemp + s;
            }
            else {
                dataTemp = dataTemp + s;
                dataReceived = dataTemp;
                dataTemp = "";

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String s = textView2.getText().toString() + " - " + dataReceived;
                        textView2.setText(s);
                    }
                });
            }
        }
    };

    TextView textView;
    TextView textView2;
    EditText editText;
    ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        textView = findViewById(R.id.device_id);
        editText = findViewById(R.id.edit_message);
        button = findViewById(R.id.send_message);
        textView2 = findViewById(R.id.test_response);

        device = getIntent().getParcelableExtra("Device");
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        textView.setText(device.getDeviceName());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                UsbSerialDevice device = serialDevice;
                if (device != null) {
                    // EOF is used for ending message
                    byte[] modMessage = new byte[message.getBytes().length + 1];

                    System.arraycopy(message.getBytes(), 0, modMessage, 0, message.getBytes().length);
                    modMessage[message.getBytes().length] = CR;

                    device.write(modMessage);
                    editText.getText().clear();
                    Toast.makeText(getApplicationContext(), message + "Sent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        starSerialConnection(device);
    }

    private void starSerialConnection(UsbDevice device) {
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
                Toast.makeText(this, "Open", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Can't Open", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUsbConnection();
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
