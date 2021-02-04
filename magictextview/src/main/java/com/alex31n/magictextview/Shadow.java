package com.alex31n.magictextview;

import androidx.annotation.ColorInt;

public class Shadow {
    private float radius;
    private float dx;
    private float dy;
    @ColorInt private int color;

    public Shadow() {
    }

    public Shadow(float radius, float dx, float dy, int color) {
        this.radius = radius;
        this.dx = dx;
        this.dy = dy;
        this.color = color;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getDx() {
        return dx;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public float getDy() {
        return dy;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public int getColor() {
        return color;
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
    }
}
