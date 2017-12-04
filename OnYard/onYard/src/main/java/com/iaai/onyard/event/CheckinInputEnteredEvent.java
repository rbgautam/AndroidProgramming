package com.iaai.onyard.event;


public class CheckinInputEnteredEvent {

    private final String mInput;

    public CheckinInputEnteredEvent(String input) {
        mInput = input;
    }

    public String getInput() {
        return mInput;
    }

}
