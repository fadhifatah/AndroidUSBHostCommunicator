package com.fadhifatah.usbhostcommunicator;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fadhifatah.usbhostcommunicator.adapter.ItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button button;
    Button button2;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.usb_otg_support);
        button = findViewById(R.id.check_usb_otg_support);
        button2 = findViewById(R.id.get_usb_otg_devices);
        recyclerView = findViewById(R.id.list_usb_otg);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);

                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST)) {
                    String s = "Your device is support USB On-The-Go";
                    textView.setText(s);

                    button2.setVisibility(View.VISIBLE);
                }
                else {
                    String s = "Your device is  not support USB On-The-Go";
                    textView.setText(s);
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

                HashMap<String, UsbDevice> deviceHashMap = usbManager.getDeviceList();
                List<UsbDevice> usbDeviceList = new ArrayList<>(deviceHashMap.values());

                recyclerView.setAdapter(new ItemAdapter(usbDeviceList));
            }
        });
    }
}
