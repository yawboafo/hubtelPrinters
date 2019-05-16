package com.hubtel.hubtelprinters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.epson.epos2.ConnectionListener;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.hubtel.hubtelprinters.Delegates.PrinterConnectionDelegate;
import com.hubtel.hubtelprinters.printerCore.PrinterConstants;
import com.hubtel.hubtelprinters.printerCore.PrinterModel;
import com.hubtel.hubtelprinters.printerCore.PrinterModelCapacity;
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;

import java.util.List;

public class PrinterConnectionTask {


    Context activity;
    private int       mModelIndex;
    private String    mPortName;
    private String    mPortSettings;
    private String    mMacAddress;
    private String    mModelName;
    private String mManufacturer;
    private Boolean   mDrawerOpenStatus;
    private PrinterModel printerModel;
    private Printer mPrinter = null;
    private int       mPrinterSettingIndex = 0;
    private boolean            mTryConnect;
    private static StarIOPort port = null;
    public PrinterConnectionTask(Context _activity){
      activity = _activity;

    }


    private void initEpsonPrinter(ConnectionListener epSonconnectionListener) {

        try {
            mPrinter = new Printer(0, 0, activity);

          //  mPrinter.setReceiveEventListener(epSonreceiveListener);
            mPrinter.setConnectionEventListener(epSonconnectionListener);


        } catch (Exception e) {

        }





    }
    public void connectEpsonPrinter(HubtelDeviceInfo deviceInfo, PrinterConnectionDelegate delegate, ConnectionListener epSonconnectionListener) {
        boolean isBeginTransaction = false;


        initEpsonPrinter(epSonconnectionListener);

        if(delegate !=null)
        delegate.printerConnectionBegan(deviceInfo);


        if (mPrinter == null) {
            if(delegate !=null)
                delegate.printerConnectionFailed("Printer instance you no initialize am");
        }

        try {

            mPrinter.setConnectionEventListener(epSonconnectionListener);
            mPrinter.connect((deviceInfo.getTarget()), Printer.PARAM_DEFAULT);

            if(delegate !=null)
            delegate.printerConnectionSuccess(deviceInfo);
        }
        catch (Exception e) {

            if(delegate !=null)
            delegate.printerConnectionFailed(e.getLocalizedMessage() + "Epson connection error");


        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        }
        catch (Exception e) {
            if(delegate !=null)
            delegate.printerConnectionFailed(e.getLocalizedMessage() + "Epson beginTransaction error");

        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            }
            catch (Epos2Exception e) {
                // Do nothing

            }
        }


    }
    public void connectToStarPrinter(List<PrinterModel> printermodelList, HubtelDeviceInfo deviceInfo , PrinterConnectionDelegate delegate, SharedPreferences prefs){

        if(delegate !=null)
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


        HubtelDeviceHelper.saveActivePrinterModel(printermodelList, mPrinterSettingIndex, printerModel,prefs);





        printerModel = HubtelDeviceHelper.getSavedPrinterModel(printermodelList);
        rawConnectToStarPrinters(deviceInfo,delegate);

    }
    public void rawConnectToStarPrinters(final HubtelDeviceInfo hubtelDeviceInfo,final PrinterConnectionDelegate delegate) {
        AsyncTask<Void, Void, StarIOPort> task = new AsyncTask<Void, Void, StarIOPort>() {





            @Override
            protected void onPreExecute() {
                mTryConnect = true;
                if(delegate!=null)
                delegate.printerConnectionBegan(hubtelDeviceInfo);
            }

            @Override
            protected StarIOPort doInBackground(Void... voids) {



                if(port != null){

                    return port;

                }else{
                    try {
                        synchronized (activity) {

                            port = StarIOPort.getPort(printerModel.getPortName(), printerModel.getPortSettings(), 10000,activity);
                        }
                    } catch (StarIOPortException e) {
                        if(delegate!=null)
                        delegate.printerConnectionFailed(e.getLocalizedMessage());
                    }
                    return port;

                }





            }

            @Override
            protected void onPostExecute(StarIOPort port) {

                mTryConnect = false;
                if(delegate!=null)
                delegate.printerConnectionSuccess(hubtelDeviceInfo);

            }
        };

        if (!mTryConnect) {
            task.execute();
        }
    }


}
