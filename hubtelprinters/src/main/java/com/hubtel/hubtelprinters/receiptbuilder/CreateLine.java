package com.hubtel.hubtelprinters.receiptbuilder;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CreateLine implements CanvasItem {
    private Paint paint = new Paint();
    private int size;

    public CreateLine(int size) {
        this.size = size;
    }

    @Override
    public void drawOnCanvas(Canvas canvas, float x, float y) {
        float xPos = getX(canvas, x);
        canvas.drawLine(xPos, y + 5, xPos + size, y + 5, paint);
    }

    private float getX(Canvas canvas, float x) {
        float xPos = x;
        if (paint.getTextAlign().equals(Paint.Align.CENTER)) {
            xPos += (canvas.getWidth() - size) / 2;
        } else if (paint.getTextAlign().equals(Paint.Align.RIGHT)) {
            xPos += canvas.getWidth() - size;
        }
        return xPos;
    }

    @Override
    public int getHeight() {
        return 6;
    }

    public int getColor() {
        return paint.getColor();
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public void setAlign(Paint.Align align) {
        paint.setTextAlign(align);
    }

    public Paint.Align getAlign() {
        return paint.getTextAlign();
    }
}
