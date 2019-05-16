package com.hubtel.hubtelprinters;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.hubtel.hubtelprinters.Delegates.PrinterSeachDelegate;
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;
import com.starmicronics.stario.StarIOPortException;

import java.util.List;

public class PrinterSeachTask {

    Context _activity;
    private List<PortInfo> portList;
    private  PrinterSeachDelegate printerSeachDelegate;
    public PrinterSeachTask(Context activity){



        _activity = activity;
    }


    public void searchEpsonPrinters(DiscoveryListener mDiscoveryListener , PrinterSeachDelegate delegate){


        printerSeachDelegate = delegate;



        if(delegate!=null)
            delegate.printerSearchBegan();




      while (true) {
            try {
                Discovery.stop();
                break;
            }
            catch (Epos2Exception e) {
                if (e.getErrorStatus() != Epos2Exception.ERR_PROCESSING) {
                    break;
                }
            }
        }





        FilterOption mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);

        try {
            Discovery.start(_activity, mFilterOption, _mDiscoveryListener);
        }
        catch (Exception e) {

            try {
                Discovery.stop();
            }
            catch (Epos2Exception _e) {

            }
            if(delegate!=null)
                delegate.printerSearchFailed(e.getLocalizedMessage());

        }




    }
    final DiscoveryListener _mDiscoveryListener = new DiscoveryListener() {
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
            hubtelDeviceInfo.setPortName(deviceInfo.getTarget());




            if( HubtelDeviceHelper.hubtelDeviceInfoList.contains(hubtelDeviceInfo)){

            }else{
                HubtelDeviceHelper.hubtelDeviceInfoList.add(hubtelDeviceInfo);

                if(printerSeachDelegate!=null)
                    printerSeachDelegate.printerSearchCompleted( HubtelDeviceHelper.hubtelDeviceInfoList);
            }





        }
    };
    public   void searchStarPrinters(final PrinterSeachDelegate delegate){


         HubtelDeviceHelper.hubtelDeviceInfoList.clear();
      //  HubtelDeviceHelper.hubtelDeviceInfoList.re
        printerSeachDelegate = delegate;
        AsyncTask<Void, Void, List<PortInfo>> task = new  AsyncTask<Void, Void, List<PortInfo>>() {






            @Override
            protected List<PortInfo> doInBackground(Void...params){


                try{

                    if(delegate!= null)
                    delegate.printerSearchBegan();


                    portList= StarIOPort.searchPrinter("BT:");

                }catch(StarIOPortException e){


                    if(delegate!= null)
                    delegate.printerSearchFailed(e.getLocalizedMessage());

                }

                return portList;

            }


            @Override
            protected void onCancelled(){
                super.onCancelled();


                if(delegate!= null)
                delegate.printerSearchFailed("Task Cancelled");
            }

            @Override
            protected void onPostExecute(List<PortInfo> result){
                super.onPostExecute(result);

                HubtelDeviceHelper.addStarPortToListofHubtelDeviceInfo(result);

                if(delegate!= null)
                    delegate.printerSearchCompleted(HubtelDeviceHelper.hubtelDeviceInfoList);






            }

        };
        task.execute();
    }





}
