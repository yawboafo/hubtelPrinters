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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hubtel.hubtelprinters.Delegates.PrinterConnectionDelegate;
import com.hubtel.hubtelprinters.Delegates.PrinterSeachDelegate;
import com.hubtel.hubtelprinters.Delegates.PrintingTaskDelegate;
import com.hubtel.hubtelprinters.receiptbuilder.CardDetails;
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo;
import com.hubtel.hubtelprinters.PrinterManager;

import com.hubtel.hubtelprinters.receiptbuilder.ReceiptObject;
import com.hubtel.hubtelprinters.receiptbuilder.ReceiptOrderItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PrinterSeachDelegate, PrinterConnectionDelegate, PrintingTaskDelegate {
    PrinterManager printerManager;
    private static final int REQUEST_PERMISSION = 100;
    private ListView listView;
    private DeviceAdapter mAdapter;
   TextView textView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        printerManager = new PrinterManager(MainActivity.this);
        printerManager.seachDelegate = MainActivity.this;
        printerManager.connectionDelegate = MainActivity.this;
       printerManager.printingTaskDelegate = MainActivity.this;
        requestRuntimePermission();


        Log.d("Debug","Active device " + printerManager.getActiveHubtelDevice().getHumanReadableName());

        textView = (TextView)findViewById(R.id.textView);
        listView = (ListView) findViewById(R.id.listview);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 HubtelDeviceInfo o = (HubtelDeviceInfo) listView.getItemAtPosition(i);

                 System.out.println(o.getDeviceManufacturer());


                 printerManager.connectToPrinter(o);

             }
         });



        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                printsomething();
            }
        });

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //Intent  intent = new Intent(MainActivity.this, DiscoveryActivity.class);
                //startActivityForResult(intent, 0);





                printerManager.searchPrinter();




            }
        });


        if (printerManager.getActiveHubtelDevice() != null ){
            TextView view = (TextView) findViewById(R.id.textView);

          //  if (printerManager.getActiveHubtelDevice() != null)
           // view.setText("Connected to "+printerManager.getActiveHubtelDevice().getHumanReadableName() + " Printer");
        }
    }


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
    protected void onPause() {
        super.onPause();
        printerManager.unRegisterReceiver();
        printerManager.delegate = null;

    }



   /** @Override
    public void printerSearchSuccess(final List<HubtelDeviceInfo> hubtelDeviceInfoList) {


        Log.d("Debug","Hubtel pos device list count "+hubtelDeviceInfoList.size());
        Log.d("Debug ","connecting with this printer "+ hubtelDeviceInfoList.get(0).getDeviceManufacturer());


        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {



                mAdapter = new DeviceAdapter(MainActivity.this,hubtelDeviceInfoList);
                listView.setAdapter(mAdapter);

            }
        });






       for (HubtelDeviceInfo j : hubtelDeviceInfoList){
           Log.d("Debug ","manu " + j.getDeviceManufacturer() + "name "+ j.getDeviceName()+ "name "+ j.getPortName() );
       }



    }
**/







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

       CardDetails cardDetails = new CardDetails();
         cardDetails.setAuthorization("98989");
         cardDetails.setMid("191910022");
         cardDetails.setCard("98911****89");
         cardDetails.setSchema("Visa");
         cardDetails.setTransID("32HDD333D999D");
         cardDetails.setTid("ZHUB232");


         _object.setCardDetails(cardDetails);


         Bitmap myLogo = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.hubtel);
         Bitmap bMapScaled = Bitmap.createScaledBitmap(myLogo, 150, 150, true);


         Bitmap qrcode = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.qrcode);
         Bitmap qrcodescaled = Bitmap.createScaledBitmap(qrcode, 250, 250, true);


         _object.setLogo(bMapScaled);
          _object.setQrcode(qrcodescaled);



        printerManager.printOrderPayment(_object);
     }


    @Override
    public void printerSearchBegan() {

    }

    @Override
    public void printerSearchFailed(String error) {

    }

    @Override
    public void printerSearchCompleted(final List<HubtelDeviceInfo> devices) {

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {



                mAdapter = new DeviceAdapter(MainActivity.this,devices);
                listView.setAdapter(mAdapter);

            }
        });

    }


    @Override
    public void printerConnectionBegan(HubtelDeviceInfo deviceInfo) {

        textView.setText("Connecting to " + deviceInfo.getHumanReadableName());

    }

    @Override
    public void printerConnectionFailed(HubtelDeviceInfo deviceInfo, String error) {
        textView.setText("Connecting to " + deviceInfo.getHumanReadableName() + " failed ");
    }

    @Override
    public void printerConnectionSuccess(HubtelDeviceInfo deviceInfo) {
        textView.setText("connected to " + deviceInfo.getHumanReadableName());
    }

    @Override
    public void printerConnectionFailed(String error) {

    }

    @Override
    public void printingTaskBegan(HubtelDeviceInfo deviceInfo) {

    }

    @Override
    public void printingTaskFailed(HubtelDeviceInfo deviceInfo, String error) {

    }

    @Override
    public void printingTaskCompleted(HubtelDeviceInfo deviceInfo, Boolean results) {

    }

    @Override
    public void printingTaskFailed(String error) {

    }
}
