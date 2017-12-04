package com.iaai.onyard.event;


public class VinScanEnteredEvent {

    private final String mInput;

    public VinScanEnteredEvent(String input) {
        mInput = input;
    }

    public String getInput() {
        return mInput;
    }

}
