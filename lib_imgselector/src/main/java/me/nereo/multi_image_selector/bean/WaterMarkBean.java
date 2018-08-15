package me.nereo.multi_image_selector.bean;

import android.graphics.Color;

import java.io.Serializable;

/**
 * Created by stf on 2018-08-14.
 */

public class WaterMarkBean implements Serializable {
    private String mark;
    private String color;
    private int textSize;
    private Boolean antiAlias;
    private int alpha;
    private int rotate;

    public WaterMarkBean() {
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSzie) {
        this.textSize = textSzie;
    }

    public Boolean getAntiAlias() {
        return antiAlias;
    }

    public void setAntiAlias(Boolean antiAlias) {
        this.antiAlias = antiAlias;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }
}
