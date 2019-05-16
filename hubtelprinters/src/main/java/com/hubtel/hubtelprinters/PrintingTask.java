package com.hubtel.hubtelprinters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.util.Log;
import com.epson.epos2.printer.Printer;
import com.hubtel.hubtelprinters.Delegates.PrintingTaskDelegate;
import com.hubtel.hubtelprinters.printerCore.Communication;
import com.hubtel.hubtelprinters.printerCore.PrinterModel;
import com.hubtel.hubtelprinters.printerCore.PrinterModelCapacity;
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;
import com.hubtel.hubtelprinters.receiptbuilder.LocalisedReceiptBuilder;
import com.hubtel.hubtelprinters.receiptbuilder.ReceiptObject;
import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;

import java.util.List;

public class PrintingTask {
    private Printer mPrinter = null;
    Context activity;
    HubtelDeviceInfo deviceInfo;
    private List<PrinterModel> printermodelList;
    private PrinterModel printerModel;
    private PrintingTaskDelegate delegate;


    public PrintingTask(Activity _activity , HubtelDeviceInfo _deviceInfo,SharedPreferences prefs, PrintingTaskDelegate _delegate,Printer _mPrinter){
        activity = _activity;
        printermodelList = HubtelDeviceHelper.getprintermodelList(prefs);
        delegate =  _delegate;
        deviceInfo = _deviceInfo;
        mPrinter = _mPrinter;


    }

    public PrintingTask(Context _activity , HubtelDeviceInfo _deviceInfo,SharedPreferences prefs, PrintingTaskDelegate _delegate){
        activity = _activity;
        printermodelList = HubtelDeviceHelper.getprintermodelList(prefs);
        delegate =  _delegate;
        deviceInfo = _deviceInfo;



    }

    public  void printOrderPayment(final HubtelDeviceInfo deviceInfo,final ReceiptObject object){



        if (object == null ){

            if(delegate!=null)
            delegate.printingTaskFailed("Order is null");


            return;
        }


        if (deviceInfo == null ) {
            if(delegate!=null)
                delegate.printingTaskFailed("Printer not connected");


            return;

        }

        if (deviceInfo.getDeviceManufacturer() == null ) {
            if(delegate!=null)
                delegate.printingTaskFailed("Printer not connected");


            return;

        }

            switch (deviceInfo.getDeviceManufacturer()){


                case "Epson":


                    LocalisedReceiptBuilder localisedReceiptBuilder1 = new LocalisedReceiptBuilder(activity);
                    Bitmap _data = localisedReceiptBuilder1.orderPaymentReceipt(object);
                    createReceiptData4Epson(_data);


                    printEpsonData(deviceInfo);


                    //delegate.printingCompletedResult("Epson receipt not supported for now ");

                    break;
                case "Star":
                    LocalisedReceiptBuilder localisedReceiptBuilder = new LocalisedReceiptBuilder(activity);
                    Bitmap data = localisedReceiptBuilder.orderPaymentReceipt(object);
                    printMainJob(data,mCallback);
                    break;
            }

     /**   new Thread(new Runnable() {
            @Override
            public void run() {
                switch (deviceInfo.getDeviceManufacturer()){


                    case "Epson":


                        LocalisedReceiptBuilder localisedReceiptBuilder1 = new LocalisedReceiptBuilder(activity);
                        Bitmap _data = localisedReceiptBuilder1.orderPaymentReceipt(object);
                        createReceiptData4Epson(_data);


                        printEpsonData(deviceInfo);


                        //delegate.printingCompletedResult("Epson receipt not supported for now ");

                        break;
                    case "Star":
                        LocalisedReceiptBuilder localisedReceiptBuilder = new LocalisedReceiptBuilder(activity);
                        Bitmap data = localisedReceiptBuilder.orderPaymentReceipt(object);
                        printMainJob(data,mCallback);
                        break;
                }
            }
        }).start();
**/





    }
    public  void printEndOfDay(final HubtelDeviceInfo deviceInfo,final ReceiptObject object){

        Log.e("debug","debugInfo  : " + deviceInfo.toString());

        if (object == null ){

            if(delegate!=null)
                delegate.printingTaskFailed("Order is null");



            return;
        }else if (deviceInfo == null || deviceInfo.getDeviceManufacturer() == null ) {

            if(delegate!=null)
                delegate.printingTaskFailed("Printer not connected");




        }else {
            switch (deviceInfo.getDeviceManufacturer()){


                case "Epson":


                    LocalisedReceiptBuilder localisedReceiptBuilder1 = new LocalisedReceiptBuilder(activity);
                    Bitmap _data = localisedReceiptBuilder1.endofDayReceipt(object);
                    createReceiptData4Epson(_data);


                    printEpsonData(deviceInfo);


                    //delegate.printingCompletedResult("Epson receipt not supported for now ");

                    break;
                case "Star":
                    LocalisedReceiptBuilder localisedReceiptBuilder12 = new LocalisedReceiptBuilder(activity);
                    Bitmap _data1 = localisedReceiptBuilder12.endofDayReceipt(object);
                    printMainJob(_data1,mCallback);
                    break;
            }

        }










    }

    public void printMainJob(Bitmap data,Communication.SendCallback mCallback){

        if(delegate!=null)
            delegate.printingTaskBegan(deviceInfo);
        ICommandBuilder builder = StarIoExt.createCommandBuilder(PrinterModelCapacity.getEmulation (HubtelDeviceHelper.getSavedPrinterModel(printermodelList).getModelIndex()));
        builder.beginDocument();
        builder.appendBitmap(data, true);
        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);
        builder.endDocument();
        Communication.sendCommands(this, builder.getCommands(), HubtelDeviceHelper.getSavedPrinterModel(printermodelList).getPortName(), HubtelDeviceHelper.getSavedPrinterModel(printermodelList).getPortSettings(), 10000, activity, mCallback);
    }
    private boolean printEpsonData(HubtelDeviceInfo deviceInfo) {




        if (mPrinter == null) {
            if(delegate!=null)
            delegate.printingTaskFailed("Failed to print to  epson printer ");
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

          //  delegate.printingTaskBegan(deviceInfo);
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {

            if(delegate!=null)
            delegate.printingTaskFailed(e + "sendData");

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
    private boolean createReceiptData4Epson(ReceiptObject object) {
        if(delegate!=null)
            delegate.printingTaskBegan(deviceInfo);

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

            delegate.printingTaskFailed(e.getLocalizedMessage());
            return false;
        }



        return true;
    }
    private boolean createReceiptData4Epson(Bitmap bitmap) {

        if(delegate!=null)
            delegate.printingTaskBegan(deviceInfo);
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
            if(delegate!=null)
                delegate.printingTaskFailed(e.getLocalizedMessage());
            return false;
        }



        return true;
    }

    final Communication.SendCallback mCallback = new Communication.SendCallback() {
        @Override
        public void onStatus(boolean result, Communication.Result communicateResult) {
            if(delegate!=null)
                delegate.printingTaskCompleted(deviceInfo,result);



        }
    };
}
