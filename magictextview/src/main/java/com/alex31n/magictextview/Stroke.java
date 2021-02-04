package com.alex31n.magictextview;

import android.graphics.Paint;

import androidx.annotation.ColorInt;

public class Stroke {
    private float strokeWidth;
    @ColorInt private int strokeColor;
    private Paint.Join strokeJoin;
    private float strokeMiter;

    public Stroke() {
    }

    public Stroke(float strokeWidth, int strokeColor, Paint.Join strokeJoin, float strokeMiter) {
        this.strokeWidth = strokeWidth;
        this.strokeColor = strokeColor;
        this.strokeJoin = strokeJoin;
        this.strokeMiter = strokeMiter;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public Paint.Join getStrokeJoin() {
        return strokeJoin;
    }

    public void setStrokeJoin(Paint.Join strokeJoin) {
        this.strokeJoin = strokeJoin;
    }

    public float getStrokeMiter() {
        return strokeMiter;
    }

    public void setStrokeMiter(float strokeMiter) {
        this.strokeMiter = strokeMiter;
    }
}
