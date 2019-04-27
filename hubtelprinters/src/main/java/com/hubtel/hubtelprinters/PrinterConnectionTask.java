package com.hubtel.hubtelprinters;

import android.app.Activity;

import com.epson.epos2.ConnectionListener;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.hubtel.hubtelprinters.Delegates.PrinterConnectionDelegate;
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;

public class PrinterConnectionTask {


    Activity activity;

    public PrinterConnectionTask(Activity _activity){
      activity = _activity;

    }
    private Printer mPrinter = null;

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
}
