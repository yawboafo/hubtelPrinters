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

import com.hubtel.hubtelprinters.PrinterConstants;

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



        String itemizedListHeader = String.format("%-5s %-30s %4s", "QTY", "DESCRIPTION", "AMOUNT") + "\n";





        ReceiptCreator receiptCreator = new ReceiptCreator(570);



        receiptCreator.setMargin(2, 2).
                setAlign(Paint.Align.CENTER).
                setColor(Color.BLACK).
                setTextSize(27)
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
                .setTextSize(20)
                .setTypeface(getBoldTypeFace())
                .addTextLeft("Date :  "+ object.getPaymentDate()+"\n")
                .addTextLeft("Receipt No : "+ object.getPaymentReceiptNumber()+"\n")
                .addTextLeft("Payment Type : "+ object.getPaymentType()+"\n")
                .addTextLeft("Customer  : "+ object.getCustomer()+"\n")
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
                .setAlign(Paint.Align.CENTER)
                .addParagraph().addText("Served by "+ object.getEmployeeName())
                .addParagraph()
                .setTypeface(getBoldTypeFace()).
                setTextSize(19).
                addText("Scan QR code to redeem rewards").
                addText("and get the e-receipt").
                addParagraph()
                .addImage(object.getQrcode())
                .addParagraph()
                .addTextCenter("Processed by Hubtel")
        ;
/*8

        ReceiptCreator receiptCreator = new ReceiptCreator(570);
        receiptCreator.setMargin(2, 2).
                setAlign(Paint.Align.CENTER).
                setColor(Color.BLACK).
                setTextSize(27)
                .setTypeface(getBoldTypeFace()).
                addTextCenter(object.getBusinessName()+"\n")
                .setTextSize(20)
                .setTypeface(getNormalTypeFace())
                .addText(object.getBusinessBranch()+"\n")
                .addText(object.getBusinessPhone()+"\n")
                .addText(object.getBusinessAddress()+"\n")
                .addText(object.getBusinessWebUrl()+"\n")
                .addParagraph()
                .setTextSize(20)
                .setTypeface(getBoldTypeFace())
                .setAlign(Paint.Align.LEFT)
                .addText("Date :  "+ object.getPaymentDate()+"\n")
                .addText("Receipt No : "+ object.getPaymentReceiptNumber()+"\n")
                .addText("Payment Type : "+ object.getPaymentType()+"\n")
                .addLine()
                .addParagraph()
                .addTextLeft(itemizedListHeader)
                .addParagraph()
                .addLine()
                .addImage(givemeListString(object.getItems()))
                .addParagraph()
                .addLine()
                .addParagraph()
                .setTypeface(getBoldTypeFace())
                .setAlign(Paint.Align.LEFT)
               .addTextLeft(subTotalString).
                addTextLeft(discountString).
                addTextLeft(taxString)
               .addParagraph().addLine()
               .addLine()
               .addParagraph()
               .addTextLeft(TotalString).
                addTextLeft(amountendered).
                addTextLeft(change).
                addTextLeft(gratis_points).
                addParagraph()
        .addLine()
               ;
**/



        return receiptCreator.build();
    }


    private Bitmap givemeListString(List<ReceiptOrderItem> items){

        String itemizedList = "";
        for (ReceiptOrderItem item : items) {
            String itemName = "";
            if (item.getName().length() > 30)
                itemName = item.getName().substring(0, 27) + "...";
            else
                itemName = item.getName();

                itemizedList += String.format("%-5s %-30s %4s", item.getQuantity(), itemName, "  " +  item.getAmount()) + "\n";
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