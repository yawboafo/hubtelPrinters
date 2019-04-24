package com.hubtel.aposprinter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hubtel.hubtelprinters.CardDetails;
import com.hubtel.hubtelprinters.Communication;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                printerManager = new PrinterManager(MainActivity.this);
                printerManager.delegate = MainActivity.this;




                if(printerManager.isPortOpened() == true) {

                    printsomething();

                 }else{
                    printerManager.searchPrinter();
                }



            }
        });
    }

    @Override
    public void printerSearchBegan() {

    }

    @Override
    public void printerSearchSuccess(List<PortInfo> portInfoList) {

        printerManager.connectToStarPrinter(portInfoList.get(0));





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
         items.add(new ReceiptOrderItem("1","Yam Balls pizza ","GHS 1,300.00"));
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

         printerManager.printSomething(_object);
     }


}
