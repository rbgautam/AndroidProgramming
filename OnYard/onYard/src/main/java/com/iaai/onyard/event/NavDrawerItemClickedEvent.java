package com.iaai.onyard.event;


public class NavDrawerItemClickedEvent {

    private final int mIndex;

    public NavDrawerItemClickedEvent(int index) {
        mIndex = index;
    }

    public int getItemIndex() {
        return mIndex;
    }

}
