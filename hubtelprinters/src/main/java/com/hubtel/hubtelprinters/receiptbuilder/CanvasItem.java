package com.hubtel.hubtelprinters.receiptbuilder;

import android.graphics.Canvas;

interface CanvasItem {
    void drawOnCanvas(Canvas canvas, float x, float y);

    int getHeight();
}
