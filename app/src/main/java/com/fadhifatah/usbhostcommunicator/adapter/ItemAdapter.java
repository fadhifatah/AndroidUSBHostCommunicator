package com.fadhifatah.usbhostcommunicator.adapter;

import android.hardware.usb.UsbDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fadhifatah.usbhostcommunicator.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
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

        String s = device.getDeviceName() + " - ";
        holder.textView.setText(device.getDeviceName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Communicate with it
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
