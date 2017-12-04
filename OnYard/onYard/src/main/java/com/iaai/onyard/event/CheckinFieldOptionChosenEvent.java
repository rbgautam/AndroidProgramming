package com.iaai.onyard.event;

import com.iaai.onyard.classes.OnYardFieldOption;


public class CheckinFieldOptionChosenEvent {

    private final OnYardFieldOption mOption;

    public CheckinFieldOptionChosenEvent(OnYardFieldOption option) {
        mOption = option;
    }

    public OnYardFieldOption getOption() {
        return mOption;
    }
}
