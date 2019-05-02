package com.hubtel.hubtelprinters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
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
    Activity activity;
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



    public  void printOrderPayment(final HubtelDeviceInfo deviceInfo,final ReceiptObject object){


        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (deviceInfo.getDeviceManufacturer()){


                    case "Epson":


                        LocalisedReceiptBuilder localisedReceiptBuilder1 = new LocalisedReceiptBuilder(activity);
                        Bitmap _data = localisedReceiptBuilder1.orderPaymentReceipt(object);
                      //  createReceiptData4Epson(_data);

                        createReceiptData();


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

      if (!connectPrinter(deviceInfo)) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

       // dispPrinterWarnings(status);

        if (!isPrintable(status)) {
           // delegate.printingCompletedResult(status + "sendData");
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

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


    private boolean connectPrinter(HubtelDeviceInfo deviceInfo) {
        boolean isBeginTransaction = false;

        if (mPrinter == null) {
            return false;
        }

      /**  try {
            Log.d("Debug",deviceInfo.getTarget());
            mPrinter.connect(deviceInfo.getTarget(), Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {

            return false;
        }**/

        try {
            mPrinter.endTransaction();
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        }
        catch (Exception e) {


            Log.e("debugErr",e.toString());
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

        try {
            mPrinter = new Printer(0, 0, activity);
            mPrinter.setReceiveEventListener(epSonreceiveListener);
           // mPrinter.setConnectionEventListener(epSonconnectionListener);


        } catch (Exception e) {

        }


        if(delegate!=null)
            delegate.printingTaskBegan(deviceInfo);
        if (mPrinter == null) {
            delegate.printingTaskFailed("Failed to init printer object");
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

    final ReceiveListener epSonreceiveListener = new ReceiveListener() {
        @Override
        public void onPtrReceive(Printer printer, int i, PrinterStatusInfo printerStatusInfo, String s) {


            if(delegate!=null)
                delegate.printingTaskCompleted(deviceInfo,true);

            //delegate.printingCompletedResult("Epson Print Success");
            new Thread(new Runnable() {
                @Override
                public void run() {
                  //  disconnectPrinter();
                }
            }).start();

            Log.d("Debug epson",s +"was connected "+printer.getAdmin());
        }
    };


    private boolean createReceiptData() {
        String method = "";
      //  Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
        StringBuilder textData = new StringBuilder();
        final int barcodeWidth = 2;
        final int barcodeHeight = 100;

        if (mPrinter == null) {
            return false;
        }

        try {
            method = "addTextAlign";
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            method = "addImage";
           /** mPrinter.addImage(logoData, 0, 0,
                    logoData.getWidth(),
                    logoData.getHeight(),
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);**/

            method = "addFeedLine";
            mPrinter.addFeedLine(1);
            textData.append("THE STORE 123 (555) 555 – 5555\n");
            textData.append("STORE DIRECTOR – John Smith\n");
            textData.append("\n");
            textData.append("7/01/07 16:58 6153 05 0191 134\n");
            textData.append("ST# 21 OP# 001 TE# 01 TR# 747\n");
            textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append("400 OHEIDA 3PK SPRINGF  9.99 R\n");
            textData.append("410 3 CUP BLK TEAPOT    9.99 R\n");
            textData.append("445 EMERIL GRIDDLE/PAN 17.99 R\n");
            textData.append("438 CANDYMAKER ASSORT   4.99 R\n");
            textData.append("474 TRIPOD              8.99 R\n");
            textData.append("433 BLK LOGO PRNTED ZO  7.99 R\n");
            textData.append("458 AQUA MICROTERRY SC  6.99 R\n");
            textData.append("493 30L BLK FF DRESS   16.99 R\n");
            textData.append("407 LEVITATING DESKTOP  7.99 R\n");
            textData.append("441 **Blue Overprint P  2.99 R\n");
            textData.append("476 REPOSE 4PCPM CHOC   5.49 R\n");
            textData.append("461 WESTGATE BLACK 25  59.99 R\n");
            textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append("SUBTOTAL                160.38\n");
            textData.append("TAX                      14.43\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextSize";
            mPrinter.addTextSize(2, 2);
            method = "addText";
            mPrinter.addText("TOTAL    174.81\n");
            method = "addTextSize";
            mPrinter.addTextSize(1, 1);
            method = "addFeedLine";
            mPrinter.addFeedLine(1);

            textData.append("CASH                    200.00\n");
            textData.append("CHANGE                   25.19\n");
            textData.append("------------------------------\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());

            textData.append("Purchased item total number\n");
            textData.append("Sign Up and Save !\n");
            textData.append("With Preferred Saving Card\n");
            method = "addText";
            mPrinter.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addFeedLine";
            mPrinter.addFeedLine(2);

            method = "addBarcode";
            mPrinter.addBarcode("01209457",
                    Printer.BARCODE_CODE39,
                    Printer.HRI_BELOW,
                    Printer.FONT_A,
                    barcodeWidth,
                    barcodeHeight);

            method = "addCut";
            mPrinter.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
           // ShowMsg.showException(e, method, mContext);
            return false;
        }

        textData = null;

        return true;
    }
}
