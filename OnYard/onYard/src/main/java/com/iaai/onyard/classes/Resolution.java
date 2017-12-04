package com.iaai.onyard.classes;


public class Resolution {

    private int mWidth;
    private int mHeight;

    public Resolution() {

    }

    public Resolution(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public boolean equals(Resolution resolution) {
        if (!(mWidth == resolution.getWidth() && mHeight == resolution.getHeight())) {
            return false;
        }

        return true;
    }
}
