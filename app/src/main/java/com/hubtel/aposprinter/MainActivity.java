package com.hubtel.aposprinter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.discovery.Discovery;
import com.epson.epos2.discovery.DiscoveryListener;
import com.epson.epos2.discovery.FilterOption;
import com.hubtel.hubtelprinters.CardDetails;
import com.hubtel.hubtelprinters.Communication;
import com.hubtel.hubtelprinters.HubtelDeviceInfo;
import com.hubtel.hubtelprinters.PrinterManager;
import com.hubtel.hubtelprinters.PrinterManagerDelegate;

import com.hubtel.hubtelprinters.receiptbuilder.ReceiptObject;
import com.hubtel.hubtelprinters.receiptbuilder.ReceiptOrderItem;
import com.starmicronics.stario.PortInfo;
import com.starmicronics.stario.StarIOPort;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PrinterManagerDelegate {
    PrinterManager printerManager;
    private static final int REQUEST_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        requestRuntimePermission();

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //Intent  intent = new Intent(MainActivity.this, DiscoveryActivity.class);
                //startActivityForResult(intent, 0);



              printerManager = new PrinterManager(MainActivity.this);
                printerManager.delegate = MainActivity.this;

                printerManager.searchPrinter();


              //  searchepsonPrinters();

             /**   if(printerManager.isPortOpened() == true) {

                    printsomething();

                 }else{
                    printerManager.searchPrinter();
                }
**/


            }
        });
    }



    private void searchepsonPrinters(){



       FilterOption mFilterOption = new FilterOption();
        mFilterOption.setDeviceType(Discovery.TYPE_PRINTER);
        mFilterOption.setEpsonFilter(Discovery.FILTER_NAME);
        try {
            Discovery.start(MainActivity.this, mFilterOption, mDiscoveryListener);
        }
        catch (Exception e) {


            Log.d("DebugErre",e.toString());
           // delegate.printerSearchFailed(e.getLocalizedMessage()+"epson");

        }
    }
    private DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onDiscovery(final DeviceInfo deviceInfo) {


            HubtelDeviceInfo hubtelDeviceInfo = new HubtelDeviceInfo();

            hubtelDeviceInfo.setBdAddress(deviceInfo.getBdAddress());
            hubtelDeviceInfo.setDeviceType(deviceInfo.getDeviceType());
            hubtelDeviceInfo.setIpAddress(deviceInfo.getIpAddress());
            hubtelDeviceInfo.setMacAddress(deviceInfo.getMacAddress());
            hubtelDeviceInfo.setTarget(deviceInfo.getTarget());
            hubtelDeviceInfo.setBdAddress(deviceInfo.getBdAddress());
            hubtelDeviceInfo.setDeviceManufacturer("Epson");



           // hubtelDeviceInfoList.add(hubtelDeviceInfo);



            //delegate.printerSearchSuccess(hubtelDeviceInfoList);


        }
    };




    private void requestRuntimePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> requestPermissions = new ArrayList<>();

        if (permissionStorage == PackageManager.PERMISSION_DENIED) {
            requestPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionLocation == PackageManager.PERMISSION_DENIED) {
            requestPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!requestPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, requestPermissions.toArray(new String[requestPermissions.size()]), REQUEST_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode != REQUEST_PERMISSION || grantResults.length == 0) {
            return;
        }

        List<String> requestPermissions = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                requestPermissions.add(permissions[i]);
            }
            if (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)
                    && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                requestPermissions.add(permissions[i]);
            }
        }

        if (!requestPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, requestPermissions.toArray(new String[requestPermissions.size()]), REQUEST_PERMISSION);
        }
    }
    @Override
    public void printerSearchBegan() {

    }

    @Override
    public void printerSearchSuccess(List<HubtelDeviceInfo> hubtelDeviceInfoList) {

        final String  name = ""+hubtelDeviceInfoList.get(0).getDeviceName();

       MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = (TextView)findViewById(R.id.textView);
                textView.setText(name);
            }
        });



    }


    @Override
    public void printerSearchFailed(String error) {
        Log.d("DebugPrinter","Printer failed "+ error);
    }

    @Override
    public void printerSearchReturnZeroResults() {
        Log.d("DebugPrinter","Printer failed  to connect with nothing");
    }

    @Override
    public void printerConnectionBegan() {

    }

    @Override
    public void printerConnectionSuccess(StarIOPort port) {


        TextView view = (TextView)findViewById(R.id.textView);
        view.setText(printerManager.getPrinterName());



        printsomething();

    }

    @Override
    public void printerConnectionFailed(String withError) {

    }

    @Override
    public void printingStatusReport(Communication.Result communicateResult) {


        if (communicateResult == Communication.Result.Success){

            printerManager.openCashDrawar();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        if (data != null && resultCode == RESULT_OK) {
            String target = data.getStringExtra(getString(R.string.title_target));

            Log.d("Debug",target);
            if (target != null) {
              //  EditText mEdtTarget = (EditText)findViewById(R.id.edtTarget);
                //mEdtTarget.setText(target);
            }
        }
    }

    @Override
    public void cashDrawertatusReport(Communication.Result communicateResult) {

    }

    void printsomething(){

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

      /**   CardDetails cardDetails = new CardDetails();
         cardDetails.setAuthorization("98989");
         cardDetails.setMid("191910022");
         cardDetails.setCard("98911****89");
         cardDetails.setSchema("Visa");
         cardDetails.setTransID("32HDD333D999D");
         cardDetails.setTid("ZHUB232");


         _object.setCardDetails(cardDetails);
**/

         Bitmap myLogo = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.hubtel);
         Bitmap bMapScaled = Bitmap.createScaledBitmap(myLogo, 150, 150, true);


         Bitmap qrcode = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.qrcode);
         Bitmap qrcodescaled = Bitmap.createScaledBitmap(qrcode, 250, 250, true);


         _object.setLogo(bMapScaled);
          _object.setQrcode(qrcodescaled);

         printerManager.printSomething(_object);
     }


}
