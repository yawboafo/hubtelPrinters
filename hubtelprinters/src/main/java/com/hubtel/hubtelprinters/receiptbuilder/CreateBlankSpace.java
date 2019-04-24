package com.hubtel.hubtelprinters.receiptbuilder;

import android.graphics.Canvas;

public class CreateBlankSpace implements CanvasItem {

    private int blankSpace;

    public CreateBlankSpace(int blankSpace) {
        this.blankSpace = blankSpace;
    }

    @Override
    public void drawOnCanvas(Canvas canvas, float x, float y) {
    }

    @Override
    public int getHeight() {
        return blankSpace;
    }
}
