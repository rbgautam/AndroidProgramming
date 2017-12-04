package com.iaai.onyard.event;


public class SalvageTypeChangedEvent {

    private final int mNewSalvageType;

    public SalvageTypeChangedEvent(String value) {
        mNewSalvageType = Integer.parseInt(value);
    }

    public int getNewSalvageType() {
        return mNewSalvageType;
    }
}
