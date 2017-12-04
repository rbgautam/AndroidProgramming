package com.iaai.onyard.event;


public class ConnectivityCheckedEvent {

    private final boolean mIsConnected;

    public ConnectivityCheckedEvent(boolean isConnected) {
        mIsConnected = isConnected;
    }

    public boolean isConnected() {
        return mIsConnected;
    }
}
