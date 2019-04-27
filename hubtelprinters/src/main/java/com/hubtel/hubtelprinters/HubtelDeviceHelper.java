package com.hubtel.hubtelprinters;

import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;
import com.starmicronics.stario.PortInfo;

import java.util.ArrayList;
import java.util.List;

public class HubtelDeviceHelper {


    static List<HubtelDeviceInfo> hubtelDeviceInfoList = new ArrayList<>();



    static void addStarPortToListofHubtelDeviceInfo(List<PortInfo> list){

        //    public HubtelDeviceInfo(int deviceType, String target,
        //    String deviceName, String ipAddress, String macAddress,
        //    String bdAddress, String portName, String USBSerialNumber,
        //    String devicemanufacturer) {

        for (PortInfo info : list)

        {




            if (hubtelDeviceInfoList.contains(info)){

            }else {

                hubtelDeviceInfoList.add(new HubtelDeviceInfo(
                        0,
                        "",
                        info.getModelName(),
                        "",
                        info.getMacAddress(),
                        "",
                        info.getPortName(),
                        info.getUSBSerialNumber(),
                        "Star"));
            }




        }


    }
}
