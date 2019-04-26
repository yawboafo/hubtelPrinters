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


import com.epson.epos2.ConnectionListener;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.hubtel.hubtelprinters.printerCore.Communication;
import com.hubtel.hubtelprinters.printerCore.PrinterConstants;
import com.hubtel.hubtelprinters.printerCore.PrinterModel;
import com.hubtel.hubtelprinters.printerCore.PrinterModelCapacity;
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;
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
    public static final String PREF_KEY_ACTIVE_HUBTEL_DEVICE  = "pref_key_hubtel_device_json";
    private Context mContext;
    private List<PrinterModel> printermodelList;
    private int mAllReceiptSettings;
    private FilterOption mFilterOption = null;


    private int       mModelIndex;
    private String    mPortName;
    private String    mPortSettings;
    private String    mMacAddress;
    private String    mModelName;
    private String mManufacturer;
    private Boolean   mDrawerOpenStatus;
    private int       mPaperSize;
    private Activity activity;
    private List<PortInfo> portList;
    private  List<HubtelDeviceInfo> hubtelDeviceInfoList;
    private static HubtelDeviceInfo activeHubtelDevice;
    public PrinterManagerDelegate delegate=null;
    private Printer mPrinter = null;
    private PrinterModel printerModel;
    SharedPreferences prefs;



    private int       mPrinterSettingIndex = 0;

    private boolean            mTryConnect = false;


    private static StarIOPort port = null;


    public  PrinterManager(){}

    public PrinterManager(Activity activity){



        portList = Collections.emptyList();
        hubtelDeviceInfoList = new ArrayList<>();
        this.activity = activity;
        mContext = activity;

        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

         if (!prefs.contains(PREF_KEY_PRINTER_SETTINGS_JSON)) {
             prefs.edit()
                     .clear()
                     .apply();
         }

           printermodelList = JsonUtils.createPrinterSettingListFromJsonString(prefs.getString(PREF_KEY_PRINTER_SETTINGS_JSON, ""));

        if (printermodelList.size() > 0){

            printerModel = getSavedPrinterModel();
        }



          //initEpsonPrinter();



        try{
            activeHubtelDevice = getActiveHubtelDevice();
        }catch (Exception e){}
    }



    private Printer initEpsonPrinter() {

        try {
            mPrinter = new Printer(0, 0, activity);

            mPrinter.setReceiveEventListener(epSonreceiveListener);
            mPrinter.setConnectionEventListener(epSonconnectionListener);


        } catch (Exception e) {

        }



        return mPrinter;

    }




    private boolean connectPrinter(HubtelDeviceInfo deviceInfo) {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

        try {
            Log.d("Debug",deviceInfo.getTarget());
            mPrinter.connect(deviceInfo.getTarget(), Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {

            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        }
        catch (Exception e) {

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




    public void unRegisterReceiver(){
        mPrinter.setReceiveEventListener(null);
        mPrinter.setConnectionEventListener(null);
        mPrinter.setStatusChangeEventListener(null);

        try {


            Discovery.stop();
            mPrinter.disconnect();
        }
        catch (Epos2Exception e) {

        }


    }





    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        }
        catch (final Exception e) {
          delegate.printerConnectionFailed("Failed to disconnect Printer");
        }

        try {
            mPrinter.disconnect();
        }
        catch (final Exception e) {
            delegate.printerConnectionFailed("Failed to disconnect Printer");
        }

        finalizeObject();
    }

    public void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);
        mPrinter.setConnectionEventListener(null);
        mPrinter.setStatusChangeEventListener(null);

        mPrinter = null;
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

    public  void connectToPrinter(final  HubtelDeviceInfo portInfo){

                setActiveHubtelDevice(portInfo);

                switch (getActiveHubtelDevice().getDeviceManufacturer()){


                    case "Epson":

                        Runnable task = new Runnable() {
                            @Override
                            public void run() {
                                connectEpsonPrinterRaw(portInfo);
                            }
                        };
                        task.run();


                        break;
                    case "Star":
                        connectToStarPrinter(portInfo);
                        break;
                }




        //activeHubtelDevice = portInfo;




    }


    private boolean connectEpsonPrinterRaw(HubtelDeviceInfo deviceInfo) {
        boolean isBeginTransaction = false;
        delegate.printerConnectionBegan();
        if (mPrinter == null) {
            return false;
        }

        try {

            mPrinter.setConnectionEventListener(epSonconnectionListener);
            mPrinter.connect((deviceInfo.getTarget()), Printer.PARAM_DEFAULT);

            delegate.printerConnectionSuccess(deviceInfo);
        }
        catch (Exception e) {


            delegate.printerConnectionFailed(e.getLocalizedMessage() + "Epson connection error");

            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        }
        catch (Exception e) {

            delegate.printerConnectionFailed(e.getLocalizedMessage() + "Epson beginTransaction error");

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

    private void connectToStarPrinter(HubtelDeviceInfo portInfo){


        delegate.printerConnectionBegan();
        this.mManufacturer = portInfo.getDeviceManufacturer();
        this.mPortName = portInfo.getPortName();
        this.mModelName = portInfo.getPortName().substring(PrinterConstants.IF_TYPE_BLUETOOTH.length());
        this.mModelIndex = PrinterModelCapacity.getModel( this.mModelName);
        this.mPortSettings = PrinterModelCapacity.getPortSettings(mModelIndex);
        this.mDrawerOpenStatus = true ;
        if(portInfo.getMacAddress().startsWith("(") && portInfo.getMacAddress().endsWith(")"))
            this.mMacAddress = portInfo.getMacAddress().substring(1, portInfo.getMacAddress().length() - 1);
        else
            this.mMacAddress = portInfo.getMacAddress();




        printerModel = new PrinterModel(this.mModelIndex,
                this.mManufacturer,
                this.mPortName,
                this.mPortSettings,
                this.mMacAddress,
                this.mModelName,
                true,
                PrinterConstants.PAPER_SIZE_FOUR_INCH);


        saveActivePrinterModel(
                mPrinterSettingIndex,
                printerModel
        );



        printerModel = getSavedPrinterModel();


        if (port == null)
            rawConnectToStarPrinters(portInfo);
        else
            delegate.printerConnectionSuccess(portInfo);
    }

    public void searchPrinter(){




        hubtelDeviceInfoList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            delegate.printerSearchFailed("Bluetooth Permission not granted");
        }else{



            searchEpsonPrinters();
            searchStarPrinters();
        }





        }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        }
        else if (status.getOnline() == Printer.FALSE) {
            return false;
        }
        else {

        }

        return true;
    }





    private boolean createReceiptData(ReceiptObject object) {


        final int pageAreaHeight = 500;
        final int pageAreaWidth = 500;

        if (mPrinter == null) {
            return false;
        }

        try {


/*

            mPrinter.addPageBegin();
            mPrinter.addPageArea(0, 0, bitmap.getWidth(), bitmap.getHeight() + 50);
            mPrinter.addPagePosition(0, bitmap.getHeight());




            mPrinter.addImage(
                    bitmap, 0, 0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    Printer.PARAM_DEFAULT,
                    Printer.PARAM_DEFAULT,
                    Printer.PARAM_DEFAULT,
                    Printer.PARAM_DEFAULT,
                    Printer.PARAM_DEFAULT);

*/

            mPrinter.addPageEnd();

            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {

            delegate.printingFailed(e.getLocalizedMessage());
            return false;
        }



        return true;
    }
    private boolean createReceiptData(Bitmap bitmap) {


        if (mPrinter == null) {
            return false;
        }

        try {



            mPrinter.addPageBegin();
            mPrinter.addPageArea(0, 0, bitmap.getWidth(), bitmap.getHeight() + 50);
            mPrinter.addPagePosition(0, bitmap.getHeight());




         mPrinter.addImage(
                    bitmap, 0, 0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    Printer.PARAM_DEFAULT,
                    Printer.PARAM_DEFAULT,
                    Printer.PARAM_DEFAULT,
                    Printer.PARAM_DEFAULT,
                    Printer.PARAM_DEFAULT);


            mPrinter.addPageEnd();

            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {

            delegate.printingFailed(e.getLocalizedMessage());
            return false;
        }



        return true;
    }

    private boolean printData(HubtelDeviceInfo deviceInfo) {



        delegate.printingBegan(deviceInfo);

        if (mPrinter == null) {

            delegate.printingFailed("Failed to print to  epson printer ");
            return false;
        }

      /*  if (!connectPrinter(deviceInfo)) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        dispPrinterWarnings(status);

        if (!isPrintable(status)) {
            delegate.printingCompletedResult(status + "sendData");
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }*/

        try {

            delegate.printingBegan(deviceInfo);
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {

            delegate.printingCompletedResult(e + "sendData");

            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }
    private void dispPrinterWarnings(PrinterStatusInfo status) {

        String warningsMsg = "";

        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += activity.getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += activity.getString(R.string.handlingmsg_warn_battery_near_end);
        }



        delegate.printingCompletedResult(warningsMsg);
    }

    public void openCashDrawer(){

            ICommandBuilder.PeripheralChannel channel = ICommandBuilder.PeripheralChannel.No1;
            StarIoExt.Emulation emulation = PrinterModelCapacity.getEmulation(getSavedPrinterModel().getModelIndex());


            byte[] data = openCashDrawerCommand(emulation, channel);



            Communication.sendCommands(this, data, getSavedPrinterModel().getPortName(), getSavedPrinterModel().getPortSettings(), 10000, activity, new Communication.SendCallback() {
                 @Override
                 public void onStatus(boolean result, Communication.Result communicateResult) {


                     Log.d("debug",communicateResult.toString());



                     delegate.cashDrawertatusReport(communicateResult);
                 }
             });
        }

    public void rawConnectToStarPrinters(final HubtelDeviceInfo hubtelDeviceInfo) {
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
                delegate.printerConnectionSuccess(hubtelDeviceInfo);

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

    public  void printOrderPayment(final ReceiptObject object){





        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (getActiveHubtelDevice().getDeviceManufacturer()){


                    case "Epson":


                        //LocalisedReceiptBuilder localisedReceiptBuilder1 = new LocalisedReceiptBuilder(activity);
                        //Bitmap _data = localisedReceiptBuilder1.orderPaymentReceipt(object);
                      //  createReceiptData(_data);


                        printData(getActiveHubtelDevice());


                       delegate.printingCompletedResult("Epson receipt not supported for now ");

                        break;
                    case "Star":
                        LocalisedReceiptBuilder localisedReceiptBuilder = new LocalisedReceiptBuilder(activity);
                        Bitmap data = localisedReceiptBuilder.orderPaymentReceipt(object);
                        printMainJob(data);
                        break;
                }
            }
        }).start();






    }

    private void addStarPortToListofHubtelDeviceInfo(List<PortInfo> list){

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

                addStarPortToListofHubtelDeviceInfo(result);



                if(result.size() > 1) {

                    delegate.printerSearchSuccess(hubtelDeviceInfoList);
                }else if (result.size() == 1) {

                    delegate.printerSearchSuccess(hubtelDeviceInfoList.get(0));
                }else{
                    delegate.printerSearchReturnZeroResults();
                }



            }

        };
        task.execute();
    }
    private void searchEpsonPrinters(){




        try {


            Discovery.stop();

        }
        catch (Epos2Exception e) {

        }

        FilterOption mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);

        try {
            Discovery.start(activity, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {



        }




    }

    private static byte[] openCashDrawerCommand(StarIoExt.Emulation emulation, ICommandBuilder.PeripheralChannel channel) {
        ICommandBuilder builder = StarIoExt.createCommandBuilder(emulation);

        builder.beginDocument();

        builder.appendPeripheral(channel);

        builder.endDocument();

        return builder.getCommands();
    }

    private void saveActivePrinterModel(int index, PrinterModel settings) {
        if (printermodelList.size() > 1) {
            printermodelList.remove(index);
        }

        printermodelList.add(index, settings);


        prefs.edit()
                .putString(PREF_KEY_PRINTER_SETTINGS_JSON, JsonUtils.createJsonStringOfPrinterSettingList(printermodelList))
                .apply();
    }


    private void setActiveHubtelDevice(HubtelDeviceInfo device){


        Log.d("debug",JsonUtils.createJsonStringOfActiveHubtelDevices(device));

    prefs.edit()
            .putString(PREF_KEY_ACTIVE_HUBTEL_DEVICE, JsonUtils.createJsonStringOfActiveHubtelDevices(device))
            .apply();
      }

    private HubtelDeviceInfo getActiveHubtelDevice(){




        return JsonUtils.createJsonStringOfActiveHubtelDevices(prefs.getString(PREF_KEY_ACTIVE_HUBTEL_DEVICE, ""));

    }


    private PrinterModel getSavedPrinterModel() {
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





    final Communication.SendCallback mCallback = new Communication.SendCallback() {
        @Override
        public void onStatus(boolean result, Communication.Result communicateResult) {

            delegate.printingCompletedResult(communicateResult);



        }
    };


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





            if(hubtelDeviceInfoList.contains(hubtelDeviceInfo)){

            }else{
                hubtelDeviceInfoList.add(hubtelDeviceInfo);
                delegate.printerSearchSuccess(hubtelDeviceInfoList);
            }





        }
    };

    final ConnectionListener epSonconnectionListener = new ConnectionListener() {
        @Override
        public void onConnection(Object o, int i) {
        Log.d("Debug epson ","device was connected : "+o.toString());
        }
    };

    final ReceiveListener epSonreceiveListener = new ReceiveListener() {
        @Override
        public void onPtrReceive(Printer printer, int i, PrinterStatusInfo printerStatusInfo, String s) {

            delegate.printingCompletedResult("Epson Print Success");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    disconnectPrinter();
                }
            }).start();

            Log.d("Debug epson",s +"was connected "+printer.getAdmin());
        }
    };

}


