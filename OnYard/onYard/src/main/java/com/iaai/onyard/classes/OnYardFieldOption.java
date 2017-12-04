package com.iaai.onyard.classes;


public class OnYardFieldOption {

    private final String mDisplayName;
    private final String mValue;

    public OnYardFieldOption(String name, String value) {
        mDisplayName = name;
        mValue = value;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getValue() {
        return mValue;
    }

    public boolean equals(OnYardFieldOption option) {
        return (mDisplayName == null ? option.getDisplayName() == null : mDisplayName.equals(option
                .getDisplayName()))
                && (mValue == null ? option.getValue() == null : mValue.equals(option.getValue()));
    }
}
