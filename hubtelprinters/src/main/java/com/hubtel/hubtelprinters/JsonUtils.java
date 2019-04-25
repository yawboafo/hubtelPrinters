package com.hubtel.hubtelprinters;

import android.util.Log;

import com.hubtel.hubtelprinters.printerCore.PrinterModel;
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class JsonUtils {



    static String createJsonStringOfActiveHubtelDevices(HubtelDeviceInfo deviceInfo) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();




            jsonObject.put("deviceType",               deviceInfo.getDeviceType());
            jsonObject.put("target",             deviceInfo.getTarget());
            jsonObject.put("deviceName",                 deviceInfo.getDeviceName());
            jsonObject.put("ipAddress",             deviceInfo.getIpAddress());
            jsonObject.put("macAddress",               deviceInfo.getMacAddress());
            jsonObject.put("bdAddress",                deviceInfo.getBdAddress());
            jsonObject.put("portName", deviceInfo.getPortName());
            jsonObject.put("usbserialnumber",                deviceInfo.getUsbserialnumber());
            jsonObject.put("deviceManufacturer",                deviceInfo.getDeviceManufacturer());
        } catch (JSONException e) {
            Log.d("debug",e.toString());
        }

        return jsonObject.toString();
    }


    static HubtelDeviceInfo createJsonStringOfActiveHubtelDevices(String deviceInfo) {

        JSONObject jsonObject;
        HubtelDeviceInfo hubtelDeviceInfo = new HubtelDeviceInfo() ;
        try {
            jsonObject = new JSONObject(deviceInfo);
            Log.d("debug",""+jsonObject.toString());

           // JSONObject jsonObject =
            hubtelDeviceInfo.setDeviceType(jsonObject.getInt("deviceType"));
            hubtelDeviceInfo.setDeviceName(jsonObject.getString("deviceName"));
            hubtelDeviceInfo.setBdAddress(jsonObject.getString("bdAddress"));

            hubtelDeviceInfo.setIpAddress(jsonObject.getString("ipAddress"));
            hubtelDeviceInfo.setMacAddress(jsonObject.getString("macAddress"));
            hubtelDeviceInfo.setTarget(jsonObject.getString("target"));
            hubtelDeviceInfo.setBdAddress(jsonObject.getString("bdAddress"));
            hubtelDeviceInfo.setDeviceManufacturer(jsonObject.getString("deviceManufacturer"));



        } catch (JSONException e) {
            Log.d("debug",e.toString());
        }

        return hubtelDeviceInfo;
    }

    static String createJsonStringOfPrinterSettingList(List<PrinterModel> printerSettingList) {
        JSONArray jsonArray = new JSONArray();

        try {
            for (PrinterModel printerSettings : printerSettingList) {
                JSONObject jsonObject = new JSONObject();


                jsonObject.put("ModelIndex",               printerSettings.getModelIndex());
                jsonObject.put("Manufacturer",             printerSettings.getmManufacturer());
                jsonObject.put("PortName",                 printerSettings.getPortName());
                jsonObject.put("PortSettings",             printerSettings.getPortSettings());
                jsonObject.put("MACAddress",               printerSettings.getMacAddress());
                jsonObject.put("ModelName",                printerSettings.getModelName());
                jsonObject.put("CashDrawerOpenActiveHigh", printerSettings.getCashDrawerOpenActiveHigh());
                jsonObject.put("PaperSize",                printerSettings.getPaperSize());

                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            // do nothing
        }

        return jsonArray.toString();
    }

    static List<PrinterModel> createPrinterSettingListFromJsonString(String jsonString) {
        List<PrinterModel> printerSettingList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                PrinterModel printerSettings = new PrinterModel(

                        jsonObject.getInt("ModelIndex"),
                        jsonObject.getString("Manufacturer"),
                        jsonObject.getString("PortName"),
                        jsonObject.getString("PortSettings"),
                        jsonObject.getString("MACAddress"),
                        jsonObject.getString("ModelName"),
                        jsonObject.getBoolean("CashDrawerOpenActiveHigh"),
                        jsonObject.getInt("PaperSize"));

                printerSettingList.add(printerSettings);
            }
        }
        catch (JSONException e) {
            // do nothing
        }

        return printerSettingList;
    }
}
