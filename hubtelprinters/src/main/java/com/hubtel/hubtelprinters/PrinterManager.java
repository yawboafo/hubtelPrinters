package com.hubtel.hubtelprinters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import com.hubtel.hubtelprinters.receiptbuilder.ReceiptCreator;
import com.hubtel.hubtelprinters.receiptbuilder.LocalisedReceiptBuilder;
import com.hubtel.hubtelprinters.receiptbuilder.ReceiptObject;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.epson.epos2.discovery.DeviceInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PrinterManager {


    public static final String PREF_KEY_PRINTER_SETTINGS_JSON = "pref_key_printer_settings_json";
    public static final String PREF_KEY_ALLRECEIPTS_SETTINGS  = "pref_key_allreceipts_settings";

    private Context mContext;
    private List<PrinterModel> printermodelList;
    private int mAllReceiptSettings;
    private FilterOption mFilterOption = null;


    private int       mModelIndex;
    private String    mPortName;
    private String    mPortSettings;
    private String    mMacAddress;
    private String    mModelName;
    private Boolean   mDrawerOpenStatus;
    private int       mPaperSize;
    private Activity activity;
    private List<PortInfo> portList;
    private  List<HubtelDeviceInfo> hubtelDeviceInfoList;
    private static PortInfo activePortInfo;
    public PrinterManagerDelegate delegate=null;

    private PrinterModel printerModel;




    private int       mPrinterSettingIndex = 0;

    private boolean            mTryConnect = false;


    private static StarIOPort port = null;


     public PrinterManager(Activity activity){



        portList = Collections.emptyList();
        hubtelDeviceInfoList = new ArrayList<>();
        this.activity = activity;
        mContext = activity;

         SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

         if (!prefs.contains(PREF_KEY_PRINTER_SETTINGS_JSON)) {
             prefs.edit()
                     .clear()
                     .apply();
         }

         printermodelList = JsonUtils.createPrinterSettingListFromJsonString(prefs.getString(PREF_KEY_PRINTER_SETTINGS_JSON, ""));

        if (printermodelList.size() > 0){

            printerModel = getSavedPrinterModel();
        }
    }

     public StarIOPort getPort() {

         return  port;
     }

     public boolean isPortOpened(){

         if(port == null)
             return  false;
         else
             return true;

     }




    public  String getPrinterName(){


         return
                 this.mPortName.substring(PrinterConstants.IF_TYPE_BLUETOOTH.length());
    }

    public  void  connectToStarPrinter(HubtelDeviceInfo portInfo){




        this.mPortName = portInfo.getPortName();
        this.mModelName = portInfo.getPortName().substring(PrinterConstants.IF_TYPE_BLUETOOTH.length());
        this.mModelIndex = PrinterModelCapacity.getModel( this.mModelName);
        this.mPortSettings = PrinterModelCapacity.getPortSettings(mModelIndex);
        this.mDrawerOpenStatus = true ;
        if(portInfo.getMacAddress().startsWith("(") && portInfo.getMacAddress().endsWith(")"))
            this.mMacAddress = portInfo.getMacAddress().substring(1, portInfo.getMacAddress().length() - 1);
          else
            this.mMacAddress = portInfo.getMacAddress();




        printerModel = new PrinterModel(this.mModelIndex,this.mPortName,this.mPortSettings,this.mMacAddress,this.mModelName,true, PrinterConstants.PAPER_SIZE_FOUR_INCH);
        saveActivePrinterModel(
                mPrinterSettingIndex,
                printerModel
        );



        printerModel = getSavedPrinterModel();


        if (port == null)
            rawConnect();
        else
            delegate.printerConnectionSuccess(port);

    }


    public void searchPrinter(){

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            delegate.printerSearchFailed("Bluetooth Permission not granted");
        }else{



            searchepsonPrinters();

            //searchStarPrinters();
        }





        }
        private  void searchStarPrinters(){
            AsyncTask<Void, Void, List<PortInfo>> task = new  AsyncTask<Void, Void, List<PortInfo>>() {






                @Override
                protected List<PortInfo> doInBackground(Void...params){


                    try{

                        delegate.printerSearchBegan();
                        portList= StarIOPort.searchPrinter("BT:");

                    }catch(StarIOPortException e){

                        delegate.printerSearchFailed(e.getLocalizedMessage());

                    }

                    return portList;

                }


                @Override
                protected void onCancelled(){
                    super.onCancelled();

                    delegate.printerSearchFailed("Task Cancelled");
                }

                @Override
                protected void onPostExecute(List<PortInfo> result){
                    super.onPostExecute(result);


                    if(result.size() > 0)
                    addStarPortToListofHubtelDeviceInfo(result);


                    searchepsonPrinters();



                  //  Log.d("Devices","dives "+result.toString());

                   // if(result.size()==0)
                   //     delegate.printerSearchReturnZeroResults();
                  //  else
                  //      delegate.printerSearchSuccess(result);
                }

            };
            task.execute();
        }



        public void openCashDrawar(){

            ICommandBuilder.PeripheralChannel channel = ICommandBuilder.PeripheralChannel.No1;
            StarIoExt.Emulation emulation = PrinterModelCapacity.getEmulation(getSavedPrinterModel().getModelIndex());


            byte[] data = openCashDrawar(emulation, channel);



            Communication.sendCommands(this, data, getSavedPrinterModel().getPortName(), getSavedPrinterModel().getPortSettings(), 10000, activity, new Communication.SendCallback() {
                 @Override
                 public void onStatus(boolean result, Communication.Result communicateResult) {


                     Log.d("debug",communicateResult.toString());



                     delegate.cashDrawertatusReport(communicateResult);
                 }
             });
        }

    public void rawConnect() {
        AsyncTask<Void, Void, StarIOPort> task = new AsyncTask<Void, Void, StarIOPort>() {





            @Override
            protected void onPreExecute() {
                mTryConnect = true;
               delegate.printerConnectionBegan();
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
                        delegate.printerConnectionFailed(e.getLocalizedMessage());
                    }
                    return port;

                }





            }

            @Override
            protected void onPostExecute(StarIOPort port) {

                mTryConnect = false;
                delegate.printerConnectionSuccess(port);

            }
        };

        if (!mTryConnect) {
            task.execute();
        }
    }

    public void disconnect() {
         port = null;
    }

    public static byte[] SampleReceipt(StarIoExt.Emulation emulation) {

        ICommandBuilder builder = StarIoExt.createCommandBuilder(emulation);
        Log.d("got inside", "createTestReceiptData");
        builder.beginDocument();
        //  Typeface typeface = Typeface.defaultFromStyle();

        ReceiptCreator receiptCreator = new ReceiptCreator(570);
        receiptCreator.setMargin(2, 2).
                setAlign(Paint.Align.CENTER).
                setColor(Color.BLACK).
                setTextSize(25).

                 addTextCenter("HUBTEL LIMITED")
                .addText("TESTING SOMETHING \n Pringing ")
                .addBlankSpace(12)
                .setTextSize(20)
                .setAlign(Paint.Align.CENTER)
                .addText("A Hubtel Technology")
                .addLine();


        builder.appendBitmap(receiptCreator.build(), true);


        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);

        builder.endDocument();

        return builder.getCommands();
    }



    public void printMainJob(Bitmap data){


        ICommandBuilder builder = StarIoExt.createCommandBuilder(PrinterModelCapacity.getEmulation(getSavedPrinterModel().getModelIndex()));
        builder.beginDocument();
        builder.appendBitmap(data, true);
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();
        Communication.sendCommands(this, builder.getCommands(), getSavedPrinterModel().getPortName(), getSavedPrinterModel().getPortSettings(), 10000, activity, mCallback);
    }

    public  void printSomething(ReceiptObject object){

        LocalisedReceiptBuilder localisedReceiptBuilder = new LocalisedReceiptBuilder(activity);
        Bitmap data = localisedReceiptBuilder.orderPaymentReceipt(object);

         printMainJob(data);
    }



    final Communication.SendCallback mCallback = new Communication.SendCallback() {
        @Override
        public void onStatus(boolean result, Communication.Result communicateResult) {




            delegate.printingStatusReport(communicateResult);



        }
    };



     private void addStarPortToListofHubtelDeviceInfo(List<PortInfo> list){

         //    public HubtelDeviceInfo(int deviceType, String target,
         //    String deviceName, String ipAddress, String macAddress,
         //    String bdAddress, String portName, String USBSerialNumber,
         //    String devicemanufacturer) {

         for (PortInfo info : list){

             hubtelDeviceInfoList.add(new HubtelDeviceInfo(
                     0,
                     "",
                      info.getModelName(),
                     "",
                      info.getMacAddress(),
                     "",
                       info.getPortName(),
                       info.getUSBSerialNumber(),
                     "Star Micronics"));


         }


     }


    private void searchepsonPrinters(){



     FilterOption   mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
        try {
            Discovery.start(activity, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {

            delegate.printerSearchFailed(e.getLocalizedMessage()+"epson");

        }
    }
    final DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {


            HubtelDeviceInfo hubtelDeviceInfo = new HubtelDeviceInfo();


            hubtelDeviceInfo.setDeviceName(deviceInfo.getDeviceName());
            hubtelDeviceInfo.setBdAddress(deviceInfo.getBdAddress());
            hubtelDeviceInfo.setDeviceType(deviceInfo.getDeviceType());
            hubtelDeviceInfo.setIpAddress(deviceInfo.getIpAddress());
            hubtelDeviceInfo.setMacAddress(deviceInfo.getMacAddress());
            hubtelDeviceInfo.setTarget(deviceInfo.getTarget());
            hubtelDeviceInfo.setBdAddress(deviceInfo.getBdAddress());
            hubtelDeviceInfo.setDeviceManufacturer("Epson");



            hubtelDeviceInfoList.add(hubtelDeviceInfo);



            delegate.printerSearchSuccess(hubtelDeviceInfoList);


        }
    };




    private static byte[] openCashDrawar(StarIoExt.Emulation emulation, ICommandBuilder.PeripheralChannel channel) {
        ICommandBuilder builder = StarIoExt.createCommandBuilder(emulation);

        builder.beginDocument();

        builder.appendPeripheral(channel);

        builder.endDocument();

        return builder.getCommands();
    }





















    public void saveActivePrinterModel(int index, PrinterModel settings) {
        if (printermodelList.size() > 1) {
            printermodelList.remove(index);
        }

        printermodelList.add(index, settings);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        prefs.edit()
                .putString(PREF_KEY_PRINTER_SETTINGS_JSON, JsonUtils.createJsonStringOfPrinterSettingList(printermodelList))
                .apply();
    }

    public PrinterModel getSavedPrinterModel() {
        if (printermodelList.isEmpty()) {
            return null;
        }

        return printermodelList.get(0);
    }

    public PrinterModel getSavedPrinterModel(int index) {
        if (printermodelList.isEmpty() || (printermodelList.size() - 1) < index) {
            return null;
        }

        return printermodelList.get(index);
    }

    public List<PrinterModel> getPrinterSettingsList() {
        return printermodelList;
    }

    public void storeAllReceiptSettings(int allReceiptSettings) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        prefs.edit()
                .putInt(PREF_KEY_ALLRECEIPTS_SETTINGS, allReceiptSettings)
                .apply();
    }

    public int getAllReceiptSetting() {
        return mAllReceiptSettings;
    }



}


