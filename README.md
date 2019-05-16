# Hubtel POS  Printers SDK
This is android SDK/Library for Hubtel thermal printers .
_Currently supports (Epson and Star Printers)_
The printers are to aid Hubtel POS devices that are connected to print receipt for sales transactions performed.


## Getting Started
1. Clone the repository onto your computer.
```git
git clone https://github.com/yawboafo/hubtelPrinters.git
```

In your Android Studio, head on to File -> Project Structure -> Dependencies
![](Hubtel%20POS%20%20Printers%20SDK/Screenshot%202019-05-16%20at%208.50.11%20AM.png)

2. File -> New -> Import Module
Locate the  **hubtelprinters** in the project you just cloned and import it into your project.

![](Hubtel%20POS%20%20Printers%20SDK/Screenshot%202019-05-16%20at%2010.20.12%20AM.png)

This should import all the .jar files you just cloned. 

3. After which you will need to implement the module in your project.

![](Hubtel%20POS%20%20Printers%20SDK/Screenshot%202019-05-16%20at%209.46.25%20AM.png)

![](Hubtel%20POS%20%20Printers%20SDK/Screenshot%202019-05-16%20at%209.46.48%20AM.png)


Your file structure should look like this ;

![](Hubtel%20POS%20%20Printers%20SDK/Screenshot%202019-05-16%20at%209.56.37%20AM.png)

With all the dependencies imported.


## Integrating 
Add **PrinterSeachDelegate**, **PrinterConnectionDelegate**, **PrintingTaskDelegate** the Activity you want to implement the printing functionality.

1. In our case we are adding it to the **MainActivity** of the project.
```kotlin
class MainActivity : AppCompatActivity(),PrinterSeachDelegate, PrinterConnectionDelegate, PrintingTaskDelegate {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    
    override fun printingTaskBegan(deviceInfo: HubtelDeviceInfo?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun printingTaskFailed(deviceInfo: HubtelDeviceInfo?, error: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun printingTaskCompleted(deviceInfo: HubtelDeviceInfo?, results: Boolean?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun printingTaskCompleted(deviceInfo: HubtelDeviceInfo?, results: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun printingTaskFailed(error: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cashDrawertatusReport(result: Communication.Result?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun printerConnectionBegan(deviceInfo: HubtelDeviceInfo?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun printerConnectionFailed(deviceInfo: HubtelDeviceInfo?, error: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun printerConnectionSuccess(deviceInfo: HubtelDeviceInfo?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun printerConnectionFailed(error: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun printerSearchBegan() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun printerSearchFailed(error: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun printerSearchCompleted(devices: MutableList<HubtelDeviceInfo>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

```

2. We will add a button to the MainActivity layout to trigger a print when there is a click or tap. 
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:gravity="center"
        android:layout_height="match_parent"
        tools:context=".MainActivity">

    <Button
            android:id="@+id/printer"
            android:text="Printer"
            style="@style/Widget.AppCompat.Button.Colored"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>

</LinearLayout>
```

In your **MainActivity.kt**, declare PrinterManager and initiate it.
```kotlin
private var printerManager: PrinterManager = PrinterManager()
```

```kotlin
printerManager = PrinterManager(this)

//Instantiate the search delegate
printerManager.seachDelegate = this

//Instantiate the connection delegate
printerManager.connectionDelegate = this

//Instantiate the printTask delegate
printerManager.printingTaskDelegate = this
```

1. The search delegate is to help the POS device  search for available printers around.
2. The connection delegate connects the POS device to the printer after the search is successful
3.  The printer delegate performs the printer task after the above operations succeed.

We have created to initiate the search for available printer and another to initiate the print once it’s connected.

Add the Bluetooth permission to the  **AndroidManifest.xml** to your project.
```xml
<uses-permission android:name="android.permission.BLUETOOTH">
```

4. 
```kotlin
val receiptObject = ReceiptObject()

val logo = BitmapFactory.decodeResource(resources, R.drawable.unnamed)
val qrCode = BitmapFactory.decodeResource(resources, R.drawable.default_qrcode)

receiptObject.logo = Bitmap.createScaledBitmap(logo, 150, 150, false)
receiptObject.businessName = "Hubtel Limited"
receiptObject.businessBranch = "Kokomlemle, Accra"
receiptObject.businessAddress = ""
receiptObject.isOrder = true
receiptObject.businessPhone = "23323984610"
receiptObject.businessWebUrl = "www.hubtel.com"
receiptObject.paymentDate = "14th July 2018"
receiptObject.paymentType = "MoMo"
receiptObject.subtotal = ""
receiptObject.businessAddress = "Hotel Road"
receiptObject.tax = "GHS 10.00"
receiptObject.discount = "GHS 0.00"
receiptObject.total = "GHS 390"
receiptObject.customer = "233240000000"
receiptObject.employeeName = "Mary Poppins"
receiptObject.amountPaid = "GHS 4,00"
receiptObject.qrcode = Bitmap.createScaledBitmap(qrCode, 250, 250, false)

val receiptItem = ArrayList<ReceiptOrderItem>()
receiptItem.add(ReceiptOrderItem("1", "Blue Band", "GHS 30.00"))
receiptItem.add(ReceiptOrderItem("2", "Milo", "GHS 19.00"))
receiptItem.add(ReceiptOrderItem("5", "Nido", "GHS 10.00"))
receiptItem.add(ReceiptOrderItem("1", "Cerelac", "GHS 2.00"))
receiptItem.add(ReceiptOrderItem("2", "Bread", "GHS 2.00"))
receiptItem.add(ReceiptOrderItem("7", "Corn Flakes", "GHS 76.00"))
receiptItem.add(ReceiptOrderItem("3", "Cassava", "GHS 5.00"))
receiptItem.add(ReceiptOrderItem("2", "Yam", "GHS 12.00"))
receiptItem.add(ReceiptOrderItem("5", "Plantain", "GHS 20.00"))
receiptItem.add(ReceiptOrderItem("6", "Ginger", "GHS 9.00"))
receiptItem.add(ReceiptOrderItem("8", "Coke", "GHS 90.00"))
```


5. Pass the **receiptObject** to the printerOrderPayment 
```kotlin
printerManager.printOrderPayment(receiptObject)
```

6. The 
```kotlin
override fun printerSearchCompleted(devices: MutableList<HubtelDeviceInfo>?) {
        printerManager.connectToPrinter(devices?.first())
} 
```

4. Final Code
```kotlin
package com.maxwellcofie.printers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.hubtel.hubtelprinters.Delegates.PrinterConnectionDelegate
import com.hubtel.hubtelprinters.Delegates.PrinterSeachDelegate
import com.hubtel.hubtelprinters.Delegates.PrintingTaskDelegate
import com.hubtel.hubtelprinters.PrinterManager
import com.hubtel.hubtelprinters.printerCore.Communication
import com.hubtel.hubtelprinters.receiptbuilder.HubtelDeviceInfo
import com.hubtel.hubtelprinters.receiptbuilder.ReceiptObject
import com.hubtel.hubtelprinters.receiptbuilder.ReceiptOrderItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PrinterSeachDelegate, PrinterConnectionDelegate, PrintingTaskDelegate {

    private var printerManager: PrinterManager = PrinterManager()
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this@MainActivity

        printerManager = PrinterManager(this)
        printerManager.seachDelegate = this
        printerManager.connectionDelegate = this
        printerManager.printingTaskDelegate = this

        //Initialise the search job
        searchPrinters()

        //Button clicked
        printer.setOnClickListener {
            initiatePrint()
        }

    }

    private fun searchPrinters() {
        printerManager.searchPrinter()
    }


    private fun initiatePrint() {
        val receiptObject = ReceiptObject()

        val logo = BitmapFactory.decodeResource(resources, R.drawable.unnamed)
        val qrCode = BitmapFactory.decodeResource(resources, R.drawable.default_qrcode)


        receiptObject.logo = Bitmap.createScaledBitmap(logo, 150, 150, false)
        receiptObject.businessName = "Hubtel Limited"
        receiptObject.businessBranch = "Kokomlemle, Accra"
        receiptObject.businessAddress = ""
        receiptObject.isOrder = true
        receiptObject.businessPhone = "23323984610"
        receiptObject.businessWebUrl = "www.hubtel.com"
        receiptObject.paymentDate = "14th July 2018"
        receiptObject.paymentType = "MoMo"
        receiptObject.subtotal = ""
        receiptObject.businessAddress = "Hotel Road"
        receiptObject.tax = "GHS 10.00"
        receiptObject.discount = "GHS 0.00"
        receiptObject.total = "GHS 390"
        receiptObject.customer = "233240000000"
        receiptObject.employeeName = "Mary Poppins"
        receiptObject.amountPaid = "GHS 4,00"
        receiptObject.qrcode = Bitmap.createScaledBitmap(qrCode, 250, 250, false)

        val receiptItem = ArrayList<ReceiptOrderItem>()
        receiptItem.add(ReceiptOrderItem("1", "Blue Band", "GHS 30.00"))
        receiptItem.add(ReceiptOrderItem("2", "Milo", "GHS 19.00"))
        receiptItem.add(ReceiptOrderItem("5", "Nido", "GHS 10.00"))
        receiptItem.add(ReceiptOrderItem("1", "Cerelac", "GHS 2.00"))
        receiptItem.add(ReceiptOrderItem("2", "Bread", "GHS 2.00"))
        receiptItem.add(ReceiptOrderItem("7", "Corn Flakes", "GHS 76.00"))
        receiptItem.add(ReceiptOrderItem("3", "Cassava", "GHS 5.00"))
        receiptItem.add(ReceiptOrderItem("2", "Yam", "GHS 12.00"))
        receiptItem.add(ReceiptOrderItem("5", "Plantain", "GHS 20.00"))
        receiptItem.add(ReceiptOrderItem("6", "Ginger", "GHS 9.00"))
        receiptItem.add(ReceiptOrderItem("8", "Coke", "GHS 90.00"))

        receiptObject.items = receiptItem
        printerManager.printOrderPayment(receiptObject)
    }

    override fun printingTaskBegan(deviceInfo: HubtelDeviceInfo?) {
        Toast.makeText(context, "Printing Started ...", Toast.LENGTH_SHORT).show()
    }

    override fun printingTaskFailed(deviceInfo: HubtelDeviceInfo?, error: String?) {
    }

    override fun printingTaskCompleted(deviceInfo: HubtelDeviceInfo?, results: Boolean?) {
        Toast.makeText(context, "Printing Completed!", Toast.LENGTH_SHORT).show()
    }

    override fun printingTaskCompleted(deviceInfo: HubtelDeviceInfo?, results: String?) {
    }

    override fun printingTaskFailed(error: String?) {
    }

    override fun cashDrawertatusReport(result: Communication.Result?) {
        printerManager.openCashDrawer()
    }

    override fun printerConnectionBegan(deviceInfo: HubtelDeviceInfo?) {
    }

    override fun printerConnectionFailed(deviceInfo: HubtelDeviceInfo?, error: String?) {
    }

    override fun printerConnectionSuccess(deviceInfo: HubtelDeviceInfo?) {
        Toast.makeText(context, "Connected to  ${deviceInfo?.portName}", Toast.LENGTH_SHORT).show()
    }

    override fun printerConnectionFailed(error: String?) {
    }

    override fun printerSearchBegan() {
    }

    override fun printerSearchFailed(error: String?) {
    }

    override fun printerSearchCompleted(devices: MutableList<HubtelDeviceInfo>?) {
        printerManager.connectToPrinter(devices?.first())
    }
}
```






