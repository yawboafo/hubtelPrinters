package com.hubtel.hubtelprinters.printerCore;

import android.content.SharedPreferences;

import com.epson.epos2.ConnectionListener;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.printer.Printer;
import com.hubtel.hubtelprinters.Delegates.PrinterConnectionDelegate;
import com.hubtel.hubtelprinters.PrinterUtilities;
import com.hubtel.hubtelprinters.printerCore.PrinterConstants;
import com.hubtel.hubtelprinters.printerCore.PrinterModel;
import com.hubtel.hubtelprinters.printerCore.PrinterModelCapacity;
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;

public class PrinterConnections {



    private int       mModelIndex;
    private String    mPortName;
    private String    mPortSettings;
    private String    mMacAddress;
    private String    mModelName;
    private String mManufacturer;
    private Boolean   mDrawerOpenStatus;
    private int       mPaperSize;
    private PrinterModel printerModel;

    private int       mPrinterSettingIndex = 0;
    PrinterUtilities printerUtilities;


   public PrinterConnections(){


       printerUtilities = new PrinterUtilities();


   }




    public void connectToStarPrinter(HubtelDeviceInfo deviceInfo, PrinterConnectionDelegate delegate, SharedPreferences pref){


        if (delegate  != null)
            delegate.printerConnectionBegan(deviceInfo);
        this.mManufacturer = deviceInfo.getDeviceManufacturer();
        this.mPortName = deviceInfo.getPortName();
        this.mModelName = deviceInfo.getPortName().substring(PrinterConstants.IF_TYPE_BLUETOOTH.length());
        this.mModelIndex = PrinterModelCapacity.getModel( this.mModelName);
        this.mPortSettings = PrinterModelCapacity.getPortSettings(mModelIndex);
        this.mDrawerOpenStatus = true ;
        if(deviceInfo.getMacAddress().startsWith("(") && deviceInfo.getMacAddress().endsWith(")"))
            this.mMacAddress = deviceInfo.getMacAddress().substring(1, deviceInfo.getMacAddress().length() - 1);
        else
            this.mMacAddress = deviceInfo.getMacAddress();




        printerModel = new PrinterModel(this.mModelIndex,
                this.mManufacturer,
                this.mPortName,
                this.mPortSettings,
                this.mMacAddress,
                this.mModelName,
                true,
                PrinterConstants.PAPER_SIZE_FOUR_INCH);


        printerUtilities.saveActivePrinterModel(pref, mPrinterSettingIndex, printerModel);

        if(delegate != null) delegate.printerConnectionBegan(deviceInfo);
    }






    public boolean connectEpsonPrinterRaw(Printer mPrinter , HubtelDeviceInfo deviceInfo, PrinterConnectionDelegate delegate, ConnectionListener connectionListener) {
        boolean isBeginTransaction = false;

        if (delegate  != null)
        delegate.printerConnectionBegan(deviceInfo);


        if (mPrinter == null) {
            return false;
        }

        try {

            mPrinter.setConnectionEventListener(connectionListener);
            mPrinter.connect((deviceInfo.getTarget()), Printer.PARAM_DEFAULT);


            if (delegate  != null)
            delegate.printerConnectionSuccess(deviceInfo);
        }
        catch (Exception e) {

            if (delegate  != null)
            delegate.printerConnectionFailed(deviceInfo,e.getLocalizedMessage() + "Epson connection error");

            return false;
        }

      try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        }
        catch (Exception e) {
            if (delegate  != null)
            delegate.printerConnectionFailed(deviceInfo,e.getLocalizedMessage() + "Epson beginTransaction error");

        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            }
            catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

}
