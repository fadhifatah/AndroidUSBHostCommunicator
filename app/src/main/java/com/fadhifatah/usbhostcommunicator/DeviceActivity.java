package com.fadhifatah.usbhostcommunicator;

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

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.ArrayList;
import java.util.List;

public class DeviceActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();
    private UsbManager usbManager;
    private UsbDeviceConnection deviceConnection;
    private UsbSerialDevice serialDevice;
    private UsbDevice device;

    private UsbSerialInterface.UsbReadCallback callback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {
//            TODO: received message, show in recycle view
            String s = new String(bytes);
            textView2.setText(s);

            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
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
                    device.write(message.getBytes());
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                serialDevice.setBaudRate(115200);
                serialDevice.setDataBits(UsbSerialInterface.DATA_BITS_8);
                serialDevice.setStopBits(UsbSerialInterface.STOP_BITS_1);
                serialDevice.setParity(UsbSerialInterface.PARITY_NONE);
                serialDevice.setFlowControl(UsbSerialInterface.FLOW_CONTROL_RTS_CTS);
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
