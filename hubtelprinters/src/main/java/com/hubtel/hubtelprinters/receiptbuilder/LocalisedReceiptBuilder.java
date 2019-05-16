package com.hubtel.hubtelprinters.receiptbuilder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.hubtel.hubtelprinters.printerCore.PrinterConstants;

import java.util.List;

public class LocalisedReceiptBuilder {


    public Context context;

    private Typeface BoldTypeFace,NormalFace;


    public LocalisedReceiptBuilder(Typeface boldTypeFace, Typeface normalFace) {
        BoldTypeFace = boldTypeFace;
        NormalFace = normalFace;
    }
    public LocalisedReceiptBuilder(Context context) {


        this.context = context;
    }

    public LocalisedReceiptBuilder( ) {



    }

    public Bitmap orderPaymentReceipt(ReceiptObject object) {


//%-5s %-30s %4s
        String itemizedListHeader = String.format("%-5s %-33s %4s", "QTY", "DESCRIPTION", "AMOUNT") + "\n";





        ReceiptCreator receiptCreator = new ReceiptCreator(570);



        receiptCreator.setMargin(1, 1).
                setAlign(Paint.Align.CENTER).
                setColor(Color.BLACK).
                setTextSize(25)
                .setTypeface(getBoldTypeFace())
                .addLogo(object.getLogo())
                .addTextCenter(object.getBusinessName())
                .setTextSize(19)
                .setTypeface(getNormalTypeFace())
                .addTextCenter(object.getBusinessBranch()+"\n")
                .addTextCenter(object.getBusinessPhone()+"\n")
                .addTextCenter(object.getBusinessAddress()+"\n")
                .addTextCenter(object.getBusinessWebUrl()+"\n")
                .addParagraph()
                .setTypeface(getBoldTypeFace())
                .setTextSize(25)
                .addTextCenter(object.isDuplicate())
                .addParagraph()
                .setTextSize(19)
                .setTypeface(getBoldTypeFace())
                .addTextLeft("Date :  "+ object.getPaymentDate()+"\n")
                .addTextLeft(object.getRONumber())
                .addTextLeft("Payment Type : "+ object.getPaymentType()+"\n")
                .addCustomerInfo(object.getCustomer())
                .addParagraph()
                .addLine()
                .addParagraph()
                .addTextLeft(itemizedListHeader)
                .addParagraph()
                .addLine()
                .setAlign(Paint.Align.LEFT)
                .addImage(givemeListString(object.getItems()))
                .addParagraph()
                .addLine()
                .addParagraph()
                .setTypeface(getBoldTypeFace())
                .addTextRightAndLeft("Subtotal",object.getSubtotal())
                .addTextRightAndLeft("Discount",object.getDiscount())
                .addTextRightAndLeft("Tax",object.getTax())
                .addParagraph()
                .addLine()
                .addParagraph()
                .addTextRightAndLeft("Total",object.getTotal())
                .addTextRightAndLeft("Amount Paid",object.getAmountPaid())
                .addTextRightAndLeft("Change",object.getChange())
                .addTextRightAndLeft("Gratis Reward",object.getGratisPoint())
                .addParagraph()
                .addLine()
                .setTypeface(getNormalTypeFace())
                .setTextSize(19)
                .addCardDetails(object.getCardDetails())
                .addParagraph()
                .addTextCenter("Served by "+ object.getEmployeeName())
                .addParagraph()
                .setTypeface(getBoldTypeFace()).
                setTextSize(19).
                addTextCenter("Scan QR code to redeem rewards").
                addTextCenter("and get the e-receipt").
                addParagraph()
                .setAlign(Paint.Align.CENTER)
                .addImage(object.getQrcode())
                .addParagraph()
                .addTextCenter("Processed by Hubtel")
        ;



        return receiptCreator.build();
    }


    public Bitmap endofDayReceipt(ReceiptObject object ){

        ReceiptCreator receiptCreator = new ReceiptCreator(570);

        receiptCreator.setMargin(1, 1).
                setAlign(Paint.Align.CENTER).
                setColor(Color.BLACK).
                setTextSize(25)
                .setTypeface(getBoldTypeFace())
                .addLogo(object.getLogo())
                .addTextCenter(object.getBusinessName())
                .setTextSize(20)
                .setTypeface(getNormalTypeFace())
                .addTextCenter(object.getBusinessBranch()+"\n")
                .addTextCenter(object.getBusinessPhone()+"\n")
                .addTextCenter(object.getBusinessAddress()+"\n")
                .addTextCenter(object.getBusinessWebUrl()+"\n")
                .addParagraph()
                //.addTextCenter("Sales Summary for Today , " + object.getPaymentDate())
                .addParagraph()
                .setTextSize(25)
                .setTypeface(getBoldTypeFace())
                .addTextRightAndLeft("Summary",object.getPaymentDate())
                .addLine()
                .addParagraph()
                .setTextSize(20)
                .addTextRightAndLeft("Net Sales",object.getNetSaleTotal())
                .addLine()
                .addParagraph()
                .setTextSize(25)
                .setTypeface(getBoldTypeFace())
                .addTextLeft("Collections")
                .addLine()
                .addParagraph()
                .setTextSize(20)
                .setTypeface(getNormalTypeFace())
                .addTextRightAndLeft("Cash",object.getCashTotal())
                .addTextRightAndLeft("Mobile Money",object.getMobileMoneyTotal())
                .addTextRightAndLeft("Card",object.getCardTotal())
                .addTextRightAndLeft("Hubtel",object.getHubtelTotal())
                .addParagraph()
                .setTextSize(24)
                .setTypeface(getBoldTypeFace())
                .addLine()
                .addParagraph()
                .addTextRightAndLeft("Items","Count")
                .addLine()
                .addParagraph()
                .setAlign(Paint.Align.LEFT)
                .addImage(givemeItemsForEndOfDaySales(object.getDayItemList()))
                .addParagraph()
                .addLine()
                .addParagraph()
                .addTextLeft("Items Picked Up")
                .addParagraph()
                .addLine()
                .addParagraph()
                .addImage(givemeItemsForEndOfDaySales(object.getItemsPicked()))
                .addParagraph().addLine().addParagraph()
                .setTextSize(20)
                .addTextCenter("Processed by Hubtel")
                .addTextCenter("0800-222-081");







        return  receiptCreator.build();

    }

    private Bitmap givemeItemsForEndOfDaySales(List<ReceiptOrderDayItem> items){


        if (items == null || items.size() == 0)
            return null;

        String itemizedList = "";
        for (ReceiptOrderDayItem item : items) {
           /** String itemName = "";
            if (item.getName().length() > 30)
                itemName = item.getName().substring(0, 30) ;
            else
                itemName = item.getName();
**/

            //%-5s %-30.30s %-4s"
            //itemizedList += String.format("%-5s %-27s %4s", item.getQuantity(), itemName, "  " +  item.getAmount()) + "\n";


            itemizedList += String.format("%-42s %1s", item.getName(), "" + item.getCount()) + "\n";

           // itemizedList += String.format("%-30s %1s", itemName, item.getCount())+ "\n";
        }


        return createBitmapFromText(itemizedList,20, PrinterConstants.PAPER_SIZE_FOUR_INCH,getNormalTypeFace());

    }


    private Bitmap givemeListString(List<ReceiptOrderItem> items){


        if (items == null || items.size() == 0)
            return null;

        String itemizedList = "";
        for (ReceiptOrderItem item : items) {
            String itemName = "";
            if (item.getName().length() > 30)
                itemName = item.getName().substring(0, 30) ;
            else
                itemName = item.getName();


            //%-5s %-30.30s %-4s"
                //itemizedList += String.format("%-5s %-27s %4s", item.getQuantity(), itemName, "  " +  item.getAmount()) + "\n";


            itemizedList += String.format("%-5s %-30.30s %-4s", item.getQuantity(), itemName, "  " +  item.getAmount()) + "\n";
        }


        return createBitmapFromText(itemizedList,19, PrinterConstants.PAPER_SIZE_FOUR_INCH,getNormalTypeFace());

    }

    public Typeface getBoldTypeFace() {
        BoldTypeFace = Typeface.createFromAsset(context.getAssets(),"font/menlobold.ttf");

        return (BoldTypeFace == null ? Typeface.DEFAULT_BOLD : BoldTypeFace) ;
    }

    public void setBoldTypeFace(Typeface boldTypeFace) {
        BoldTypeFace = boldTypeFace;
    }

    public Typeface getNormalTypeFace() {
        NormalFace = Typeface.createFromAsset(context.getAssets(),"font/menloregular.ttf");

        return (NormalFace == null ? Typeface.DEFAULT : NormalFace);
    }

    public void setNormalFace(Typeface normalFace) {
        NormalFace = normalFace;
    }

    static public Bitmap createBitmapFromText(String printText, int textSize, int printWidth, Typeface typeface) {
        Paint paint = new Paint();
        Bitmap bitmap;
        Canvas canvas;

        paint.setTextSize(textSize);
        paint.setTypeface(typeface);

        paint.getTextBounds(printText, 0, printText.length(), new Rect());

        TextPaint textPaint = new TextPaint(paint);
        android.text.StaticLayout staticLayout = new StaticLayout(printText, textPaint, printWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);

        // Create bitmap
        bitmap = Bitmap.createBitmap(staticLayout.getWidth(), staticLayout.getHeight(), Bitmap.Config.ARGB_8888);

        // Create canvas
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.translate(0, 0);
        staticLayout.draw(canvas);

        return bitmap;
    }


}