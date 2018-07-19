package com.fadhifatah.usbhostcommunicator.adapter;

import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fadhifatah.usbhostcommunicator.activity.DeviceActivity;
import com.fadhifatah.usbhostcommunicator.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

    private List<UsbDevice> usbDeviceList;

    public ItemAdapter(List<UsbDevice> usbDeviceList) {
        this.usbDeviceList = usbDeviceList;
    }

    @NonNull
    @Override
    public ItemAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemHolder holder, int position) {
        final UsbDevice device = usbDeviceList.get(position);

        String s = device.getDeviceName() + " - " + device.getProductName() + " - " + device.getManufacturerName() + " - " + device.getSerialNumber();
        holder.textView.setText(s);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Communicate with it
                Intent intent = new Intent(view.getContext(), DeviceActivity.class);
                intent.putExtra("Device", device);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usbDeviceList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView textView;

        ItemHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.linear_layout);
            textView = itemView.findViewById(R.id.device_name);
        }
    }
}
