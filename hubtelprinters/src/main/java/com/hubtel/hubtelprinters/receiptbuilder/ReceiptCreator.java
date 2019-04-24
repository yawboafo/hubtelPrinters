package com.hubtel.hubtelprinters.receiptbuilder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

/**ASSEMBLED BY  YAW KORANTENG BOAFO HUBTEL 2018

 **/

public class ReceiptCreator {
    List<CanvasItem> listItens = new ArrayList<>();
    private int backgroundColor = Color.WHITE;
    private float textSize;
    private int color = Color.BLACK;
    private int width;
    private int marginTop, marginBottom, marginLeft, marginRight;
    private Typeface typeface;
    private Paint.Align align = Paint.Align.LEFT;

    public ReceiptCreator(int width) {
        this.width = width;
    }

    public ReceiptCreator setTextSize(float textSize) {
        this.textSize = textSize;
        return this;
    }

    public ReceiptCreator setBackgroudColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public ReceiptCreator setColor(int color) {
        this.color = color;
        return this;
    }

    public ReceiptCreator setTypeface(Context context, String typefacePath) {
        typeface = Typeface.createFromAsset(context.getAssets(), typefacePath);
        return this;
    }

    public ReceiptCreator setTypeface(Typeface _typeface) {
        typeface = _typeface;
        return this;
    }



    public ReceiptCreator setDefaultTypeface() {
        typeface = null;
        return this;
    }

    public ReceiptCreator setAlign(Paint.Align align) {
        this.align = align;
        return this;
    }

    public ReceiptCreator setMargin(int margin) {
        this.marginLeft = margin;
        this.marginRight = margin;
        this.marginTop = margin;
        this.marginBottom = margin;
        return this;
    }

    public ReceiptCreator setMargin(int marginTopBottom, int marginLeftRight) {
        this.marginLeft = marginLeftRight;
        this.marginRight = marginLeftRight;
        this.marginTop = marginTopBottom;
        this.marginBottom = marginTopBottom;
        return this;
    }

    public ReceiptCreator setMarginLeft(int margin) {
        this.marginLeft = margin;
        return this;
    }

    public ReceiptCreator setMarginRight(int margin) {
        this.marginRight = margin;
        return this;
    }

    public ReceiptCreator setMarginTop(int margin) {
        this.marginTop = margin;
        return this;
    }

    public ReceiptCreator setMarginBottom(int margin) {
        this.marginBottom = margin;
        return this;
    }

    public ReceiptCreator addText(String text) {

        if ( text == null ||  text.isEmpty() || text.contains("null")){
            return  this;
        }

        return addText(text, true);
    }




    public ReceiptCreator addText(String text, Boolean newLine) {

        if ( text == null ||  text.isEmpty() || text.contains("null")){
            return  this;
        }


        CreateText drawerText = new CreateText(text);
        drawerText.setTextSize(this.textSize);
        drawerText.setColor(this.color);
        drawerText.setNewLine(newLine);
        if (typeface != null) {
            drawerText.setTypeface(typeface);
        }
        if (align != null) {
            drawerText.setAlign(align);
        }



        listItens.add(drawerText);
        return this;
    }

    public ReceiptCreator addTextLeft(String text) {

        if ( text == null ||  text.isEmpty() || text.contains("null")){
            return  this;
        }


        CreateText drawerText = new CreateText(text);
        drawerText.setTextSize(this.textSize);
        drawerText.setColor(this.color);
        drawerText.setNewLine(true);
        if (typeface != null) {
            drawerText.setTypeface(typeface);
        }

        drawerText.setAlign(Paint.Align.LEFT);

        listItens.add(drawerText);
        return this;
    }


    public ReceiptCreator addTextLeft(String text,Boolean newline) {

        if ( text == null ||  text.isEmpty() || text.contains("null")){
            return  this;
        }


        CreateText drawerText = new CreateText(text);
        drawerText.setTextSize(this.textSize);
        drawerText.setColor(this.color);
        drawerText.setNewLine(newline);
        if (typeface != null) {
            drawerText.setTypeface(typeface);
        }

        drawerText.setAlign(Paint.Align.LEFT);

        listItens.add(drawerText);
        return this;
    }


    public ReceiptCreator addTextRight(String text) {

        if ( text == null ||  text.isEmpty() || text.contains("null")){
            return  this;
        }

        CreateText drawerText = new CreateText(text);
        drawerText.setTextSize(this.textSize);
        drawerText.setColor(this.color);
        drawerText.setNewLine(true);
        if (typeface != null) {
            drawerText.setTypeface(typeface);
        }

        drawerText.setAlign(Paint.Align.RIGHT);

        listItens.add(drawerText);
        return this;
    }


    public ReceiptCreator addTextRight(String text, Boolean newline) {


        if ( text == null ||  text.isEmpty() || text.contains("null")){
            return  this;
        }


        CreateText drawerText = new CreateText(text);
        drawerText.setTextSize(this.textSize);
        drawerText.setColor(this.color);
        drawerText.setNewLine(newline);
        if (typeface != null) {
            drawerText.setTypeface(typeface);
        }

        drawerText.setAlign(Paint.Align.RIGHT);

        listItens.add(drawerText);
        return this;
    }

    public ReceiptCreator addTextRightAndLeft(String LString,String RString){

        if ( LString.contains("null") ||  LString == null ||  RString.isEmpty()   || RString == null ||  RString.isEmpty()  || RString.contains("null")){
            return  this;
        }


        CreateText drawerTextR = new CreateText(RString);
        drawerTextR.setTextSize(this.textSize);
        drawerTextR.setColor(this.color);
        drawerTextR.setNewLine(false);
        if (typeface != null) {
            drawerTextR.setTypeface(typeface);
        }

        drawerTextR.setAlign(Paint.Align.RIGHT);

        listItens.add(drawerTextR);


        ////

        CreateText drawerTextL = new CreateText(LString);
        drawerTextL.setTextSize(this.textSize);
        drawerTextL.setColor(this.color);
        drawerTextL.setNewLine(true);
        if (typeface != null) {
            drawerTextL.setTypeface(typeface);
        }

        drawerTextL.setAlign(Paint.Align.LEFT);

        listItens.add(drawerTextL);





        return this;


    }

    public ReceiptCreator addTextCenter(String text) {

        if ( text == null ||  text.isEmpty() || text.contains("null")){
           return  this;
        }

        CreateText drawerText = new CreateText(text);
        drawerText.setTextSize(this.textSize);
        drawerText.setColor(this.color);
        drawerText.setNewLine(true);
        if (typeface != null) {
            drawerText.setTypeface(typeface);
        }

        drawerText.setAlign(Paint.Align.CENTER);


        listItens.add(drawerText);

        return this;
    }

    public ReceiptCreator addImage(Bitmap bitmap) {
        if (bitmap == null)

            return this ;

        CreateImage drawerImage = new CreateImage(bitmap);
        if (align != null) {
            drawerImage.setAlign(align);
        }

        if (drawerImage!=null) {
            listItens.add(drawerImage);
        }
        return this;
    }
    public ReceiptCreator addLogo(Bitmap bitmap) {

        if (bitmap == null)

            return this ;

        CreateImage drawerImage = new CreateImage(bitmap);
        if (align != null) {
            drawerImage.setAlign(Paint.Align.CENTER);
        }
        listItens.add(drawerImage);
        return this;
    }
    public ReceiptCreator addItem(CanvasItem item) {
        listItens.add(item);
        return this;
    }

    public ReceiptCreator addBlankSpace(int heigth) {
        listItens.add(new CreateBlankSpace(heigth));
        return this;
    }

    public ReceiptCreator addParagraph() {
        listItens.add(new CreateBlankSpace((int) textSize));
        return this;
    }

    public ReceiptCreator addLine() {
        return addLine(width - marginRight - marginLeft);
    }

    public ReceiptCreator addLine(int size) {
        CreateLine line = new CreateLine(size);
        line.setAlign(align);
        line.setColor(color);
        listItens.add(line);
        return this;
    }



    private int getHeight() {
        int height = 5 + marginTop + marginBottom;
        for (CanvasItem item : listItens) {
            height += item.getHeight();
        }
        return height;
    }

    private Bitmap drawImage() {
        Bitmap image = Bitmap.createBitmap(width - marginRight - marginLeft, getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(backgroundColor);
        float size = marginTop;
        for (CanvasItem item : listItens) {
            item.drawOnCanvas(canvas, 0, size);
            size += item.getHeight();
        }
        return image;
    }

    public Bitmap build() {
        Bitmap image = Bitmap.createBitmap(width, getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint();
        canvas.drawColor(backgroundColor);
        canvas.drawBitmap(drawImage(), marginLeft, 0, paint);
        return image;
    }

}
