package com.hubtel.aposprinter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;

import java.util.ArrayList;
import java.util.List;


 public class DeviceAdapter extends ArrayAdapter<HubtelDeviceInfo> {

    private Context mContext;
    private List<HubtelDeviceInfo> hubtelDeviceInfoList = new ArrayList<>();

    public DeviceAdapter(@NonNull Context context,  List<HubtelDeviceInfo> list) {
        super(context, 0 , list);
        mContext = context;
        hubtelDeviceInfoList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_at,parent,false);

        HubtelDeviceInfo currentMovie = hubtelDeviceInfoList.get(position);



        TextView name = (TextView) listItem.findViewById(R.id.PrinterName);
        name.setText(currentMovie.getDeviceManufacturer());

        TextView release = (TextView) listItem.findViewById(R.id.Target);
        release.setText(currentMovie.getDeviceName() == null  || currentMovie.getDeviceName().isEmpty() ? currentMovie.getPortName():currentMovie.getDeviceName());

        return listItem;
    }
}
