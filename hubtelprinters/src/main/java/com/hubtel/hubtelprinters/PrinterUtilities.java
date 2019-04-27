package com.hubtel.hubtelprinters;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hubtel.hubtelprinters.printerCore.PrinterModel;
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;

import java.util.List;

import static com.hubtel.hubtelprinters.PrinterManager.PREF_KEY_ACTIVE_HUBTEL_DEVICE;
import static com.hubtel.hubtelprinters.PrinterManager.PREF_KEY_ALLRECEIPTS_SETTINGS;
import static com.hubtel.hubtelprinters.PrinterManager.PREF_KEY_PRINTER_SETTINGS_JSON;

public class PrinterUtilities {


    public PrinterUtilities() {


    }

    private List<PrinterModel> printermodelList;

    public void saveActivePrinterModel(SharedPreferences prefs, int index, PrinterModel settings) {

        printermodelList = JsonUtils.createPrinterSettingListFromJsonString(prefs.getString(PREF_KEY_PRINTER_SETTINGS_JSON, ""));


        if (printermodelList.size() > 1) {
            printermodelList.remove(index);
        }

        printermodelList.add(index, settings);


        prefs.edit()
                .putString(PREF_KEY_PRINTER_SETTINGS_JSON, JsonUtils.createJsonStringOfPrinterSettingList(printermodelList))
                .apply();
    }




    public List<PrinterModel> getPrinterModelList(SharedPreferences prefs) {



        return JsonUtils.createPrinterSettingListFromJsonString(prefs.getString(PREF_KEY_PRINTER_SETTINGS_JSON, ""));
    }



    public static void setActiveHubtelDevice(SharedPreferences prefs, HubtelDeviceInfo device) {


        Log.d("debug", JsonUtils.createJsonStringOfActiveHubtelDevices(device));

        prefs.edit()
                .putString(PREF_KEY_ACTIVE_HUBTEL_DEVICE, JsonUtils.createJsonStringOfActiveHubtelDevices(device))
                .apply();
    }


    public PrinterModel getSavedPrinterModel(SharedPreferences prefs) {

        printermodelList = JsonUtils.createPrinterSettingListFromJsonString(prefs.getString(PREF_KEY_PRINTER_SETTINGS_JSON, ""));
        if (printermodelList.isEmpty()) {
            return null;
        }

        return printermodelList.get(0);
    }



    public HubtelDeviceInfo getActiveHubtelDevice(SharedPreferences prefs) {


        Log.d("Debug", JsonUtils.createJsonStringOfActiveHubtelDevices(prefs.getString(PREF_KEY_ACTIVE_HUBTEL_DEVICE, "")).toString());

        return JsonUtils.createJsonStringOfActiveHubtelDevices(prefs.getString(PREF_KEY_ACTIVE_HUBTEL_DEVICE, ""));


    }

    public void storeAllReceiptSettings(SharedPreferences prefs,int allReceiptSettings) {


        prefs.edit()
                .putInt(PREF_KEY_ALLRECEIPTS_SETTINGS, allReceiptSettings)
                .apply();
    }



    public PrinterModel getSavedPrinterModel(SharedPreferences prefs,int index) {
        printermodelList = JsonUtils.createPrinterSettingListFromJsonString(prefs.getString(PREF_KEY_PRINTER_SETTINGS_JSON, ""));

        if (printermodelList.isEmpty() || (printermodelList.size() - 1) < index) {
            return null;
        }

        return printermodelList.get(index);
    }
}