package com.iaai.onyard.event;


public class SetSaleInputEnteredEvent {

    private final String mInput;

    public SetSaleInputEnteredEvent(String input) {
        mInput = input;
    }

    public String getInput() {
        return mInput;
    }
}
