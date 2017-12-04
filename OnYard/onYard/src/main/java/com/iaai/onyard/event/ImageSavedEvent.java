package com.iaai.onyard.event;


public class ImageSavedEvent {

    private final boolean mSuccess;

    public ImageSavedEvent(boolean success) {
        mSuccess = success;
    }

    public boolean isSuccessful() {
        return mSuccess;
    }
}
