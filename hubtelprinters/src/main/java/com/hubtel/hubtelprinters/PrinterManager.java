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
import com.hubtel.hubtelprinters.Delegates.PrinterConnectionDelegate;
import com.hubtel.hubtelprinters.Delegates.PrinterManagerDelegate;
import com.hubtel.hubtelprinters.printerCore.Communication;
import com.hubtel.hubtelprinters.printerCore.PrinterConnections;
import com.hubtel.hubtelprinters.printerCore.PrinterConstants;
import com.hubtel.hubtelprinters.printerCore.PrinterModel;
import com.hubtel.hubtelprinters.printerCore.PrinterModelCapacity;
import com.hubtel.hubtelprinters.receiptbuilder.CardDetails;
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;
import com.hubtel.hubtelprinters.receiptbuilder.ReceiptCreator;
import com.hubtel.hubtelprinters.receiptbuilder.LocalisedReceiptBuilder;
import com.hubtel.hubtelprinters.receiptbuilder.ReceiptObject;
import com.hubtel.hubtelprinters.receiptbuilder.ReceiptOrderItem;
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



    private Activity activity;
    private List<PortInfo> portList;
    private  List<HubtelDeviceInfo> hubtelDeviceInfoList;
    private static HubtelDeviceInfo activeHubtelDevice;
    public PrinterManagerDelegate delegate=null;
    private Printer mPrinter = null;
    private PrinterModel printerModel;
    SharedPreferences prefs;
    PrinterUtilities printerUtilities;




    private boolean  mTryConnect = false;


    private static StarIOPort port = null;




    public PrinterConnectionDelegate connectionDelegate=null;



    public  PrinterManager(){}

    public PrinterManager(Activity activity){

        printerUtilities = new PrinterUtilities();

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

           printermodelList = printerUtilities.getPrinterModelList(prefs);



        if (printermodelList.size() > 0){

            printerModel = printerUtilities.getSavedPrinterModel(prefs);
        }



       //   initEpsonPrinter();



        try{
            activeHubtelDevice = printerUtilities.getActiveHubtelDevice(prefs);

        }catch (Exception e){


        }
    }



    public HubtelDeviceInfo getActiveHubtelDevice() {

        return printerUtilities.getActiveHubtelDevice(prefs);
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


        try {



            mPrinter.setReceiveEventListener(null);
            mPrinter.setConnectionEventListener(null);
            mPrinter.setStatusChangeEventListener(null);

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



    public  void connectToPrinter(final  HubtelDeviceInfo portInfo){

        printerUtilities.setActiveHubtelDevice(prefs,portInfo);

                switch (portInfo.getDeviceManufacturer()){


                    case "Epson":

                        Runnable task = new Runnable() {
                            @Override
                            public void run() {

                                PrinterConnections connections = new PrinterConnections();
                                connections.connectEpsonPrinterRaw(mPrinter,portInfo,connectionDelegate,epSonconnectionListener);
                            }
                        };
                        task.run();


                        break;
                    case "Star":

                          PrinterConnections connections = new PrinterConnections();
                          connections.connectToStarPrinter(portInfo,connectionDelegate,prefs);
                        break;
                }




        //activeHubtelDevice = portInfo;




    }


    public int getAllReceiptSetting() {
        return mAllReceiptSettings;
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
    public void printSample(){

        ReceiptObject _object = new ReceiptObject();


        List<ReceiptOrderItem> items = new ArrayList<>();
        items.add(new ReceiptOrderItem("21","Yam Balls banana ","GHS 12.00"));
        items.add(new ReceiptOrderItem("3","Yam Balls pizza ","GHS 300.00"));
        items.add(new ReceiptOrderItem("331","Rabbit ","GHS 300.00"));
        items.add(new ReceiptOrderItem("21","The men of the league ","GHS 300.00"));
        items.add(new ReceiptOrderItem("10","Yam Balls ","GHS 300.00"));
        items.add(new ReceiptOrderItem("1","Yam Balls banana ","GHS 300.00"));
        items.add(new ReceiptOrderItem("1","Yam Balls pizza ","GHS 300.00"));
        items.add(new ReceiptOrderItem("1","Rabbit ","GHS 300.00"));
        items.add(new ReceiptOrderItem("1","The men of the league ","GHS 300.00"));
        items.add(new ReceiptOrderItem("1","Yam Balls ","GHS 300.00"));
        items.add(new ReceiptOrderItem("1","Yam Balls banana ","GHS 1,300.00"));
        items.add(new ReceiptOrderItem("1","Yam Balls pizza ","GHS 1,30,000.00"));
        items.add(new ReceiptOrderItem("1","Rabbit ","GHS 30,000.00"));
        items.add(new ReceiptOrderItem("1","The men of the league ","GHS 300.00"));
        items.add(new ReceiptOrderItem("1","Yam Balls ","GHS 300.00"));
        items.add(new ReceiptOrderItem("1","Yam Balls banana ","GHS 300.00"));
        items.add(new ReceiptOrderItem("1","Yam Balls pizza ","GHS 300.00"));
        items.add(new ReceiptOrderItem("1","Rabbit ","GHS 300.00"));
        items.add(new ReceiptOrderItem("1","The men of the league ","GHS 300.00"));
        items.add(new ReceiptOrderItem("1","Yam Balls ","GHS 300.00"));






        _object.setBusinessName("Hubtel Limited");
        _object.setBusinessBranch("Main");
        _object.setBusinessPhone( "0540256631");
        _object.setBusinessAddress("Kokomlemle/Accra");
        _object.setBusinessWebUrl( "www.hubtel.com");
        _object.setPaymentDate("December 5, 2018, 2:20 am");
        _object.setPaymentReceiptNumber( "099121-1212-9821");
        _object.setPaymentType("Cash");
        _object.setItems(items);
        _object.setSubtotal("GHS 1,090.00");
        _object.setDiscount("GHS 0.00");
        _object.setTax("GHS 0.00");
        _object.setGratisPoint("0.0 pts");
        _object.setAmountPaid("GHS 1,000.00");
        _object.setSubtotal("GHS 1,090.00");
        _object.setChange("GHS 90.00");
        _object.setTotal("GHS 1,090.00");
        _object.setEmployeeName("Apostle Boafo");
        _object.setCustomer("0540256631");
        _object.setDuplicate(false);

        CardDetails cardDetails = new CardDetails();
        cardDetails.setAuthorization("98989");
        cardDetails.setMid("191910022");
        cardDetails.setCard("98911****89");
        cardDetails.setSchema("Visa");
        cardDetails.setTransID("32HDD333D999D");
        cardDetails.setTid("ZHUB232");


        _object.setCardDetails(cardDetails);




printOrderPayment(_object);

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
            StarIoExt.Emulation emulation = PrinterModelCapacity.getEmulation(printerUtilities.getSavedPrinterModel(prefs).getModelIndex());


            byte[] data = openCashDrawerCommand(emulation, channel);



            Communication.sendCommands(this, data, printerUtilities.getSavedPrinterModel(prefs).getPortName(), printerUtilities.getSavedPrinterModel(prefs).getPortSettings(), 10000, activity, new Communication.SendCallback() {
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


        ICommandBuilder builder = StarIoExt.createCommandBuilder(PrinterModelCapacity.getEmulation(printerUtilities.getSavedPrinterModel(prefs).getModelIndex()));
        builder.beginDocument();
        builder.appendBitmap(data, true);
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();
        Communication.sendCommands(this, builder.getCommands(), printerUtilities.getSavedPrinterModel(prefs).getPortName(), printerUtilities.getSavedPrinterModel(prefs).getPortSettings(), 10000, activity, mCallback);
    }


    public  void printOrderPayment(final HubtelDeviceInfo deviceInfo,final ReceiptObject object){


        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (deviceInfo.getDeviceManufacturer()){


                    case "Epson":


                        LocalisedReceiptBuilder localisedReceiptBuilder1 = new LocalisedReceiptBuilder(activity);
                        Bitmap _data = localisedReceiptBuilder1.orderPaymentReceipt(object);
                        createReceiptData(_data);


                        initEpsonPrinter();

                        printData(deviceInfo);


                        //delegate.printingCompletedResult("Epson receipt not supported for now ");

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

    public  void printOrderPayment(final ReceiptObject object){


        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (printerUtilities.getActiveHubtelDevice(prefs).getDeviceManufacturer()){


                    case "Epson":


                        LocalisedReceiptBuilder localisedReceiptBuilder1 = new LocalisedReceiptBuilder(activity);
                        Bitmap _data = localisedReceiptBuilder1.orderPaymentReceipt(object);
                        createReceiptData(_data);


                        printData(printerUtilities.getActiveHubtelDevice(prefs));


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


         initEpsonPrinter();

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












    public List<PrinterModel> getPrinterSettingsList() {
        return printermodelList;
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


