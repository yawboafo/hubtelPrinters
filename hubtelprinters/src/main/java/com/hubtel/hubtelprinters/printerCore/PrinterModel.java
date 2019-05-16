package com.hubtel.hubtelprinters.printerCore;




import android.support.annotation.NonNull;

import com.starmicronics.stario.StarIOPort;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PrinterModel {

    private String mManufacturer;
    private int     mModelIndex;
    private String  mPortName;
    private String  mPortSettings;
    private String  mMacAddress;
    private String  mModelName;
    private boolean mCashDrawerOpenActiveHigh;
    private int     mPaperSize;
    private StarIOPort port = null;
    public PrinterModel(int modelIndex, @NonNull String manufacturer, @NonNull String portName, @NonNull String portSettings, @NonNull String macAddress, @NonNull String modelName, boolean cashDrawerOpenActiveHigh, int paperSize) {
        mModelIndex = modelIndex;
        mPortName   = portName;
        mPortSettings = portSettings;
        mMacAddress = macAddress;
        mModelName = modelName;
        mCashDrawerOpenActiveHigh = cashDrawerOpenActiveHigh;
        mPaperSize = paperSize;
        mManufacturer = manufacturer;
    }

    public int getModelIndex() {
        return mModelIndex;
    }

    public String getPortName() {
        return mPortName;
    }

    public String getPortSettings() {
        return mPortSettings;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public String getModelName() {
        return mModelName;
    }

    public StarIOPort getActivePort(){

        return  port;
    }

    public boolean getCashDrawerOpenActiveHigh() {
        return mCashDrawerOpenActiveHigh;
    }

    public int getPaperSize() {
        return mPaperSize;
    }


    public String getmManufacturer() {
        return mManufacturer;
    }


}


