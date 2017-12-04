package com.iaai.onyard.event;


public class ToggleProgressDialogEvent {

    private final boolean mShouldShow;

    public ToggleProgressDialogEvent(boolean shouldShow) {
        mShouldShow = shouldShow;
    }

    public boolean shouldShowDialog() {
        return mShouldShow;
    }
}
