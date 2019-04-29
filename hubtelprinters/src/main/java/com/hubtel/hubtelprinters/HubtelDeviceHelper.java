package com.hubtel.hubtelprinters;

import android.content.SharedPreferences;

import com.hubtel.hubtelprinters.printerCore.PrinterModel;
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;
import com.starmicronics.stario.PortInfo;

import java.util.ArrayList;
import java.util.List;

import static com.hubtel.hubtelprinters.printerCore.PrinterConstants.PREF_KEY_PRINTER_SETTINGS_JSON;


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


    static void saveActivePrinterModel(  List<PrinterModel> printermodelList, int index, PrinterModel settings, SharedPreferences prefs) {

        if (printermodelList.size() > 1) {
            printermodelList.remove(index);
        }

        printermodelList.add(index, settings);


        prefs.edit()
                .putString(PREF_KEY_PRINTER_SETTINGS_JSON, JsonUtils.createJsonStringOfPrinterSettingList(printermodelList))
                .apply();
    }



    static PrinterModel getSavedPrinterModel( List<PrinterModel> printermodelList) {
        if (printermodelList.isEmpty()) {
            return null;
        }

        return printermodelList.get(0);
    }
}
