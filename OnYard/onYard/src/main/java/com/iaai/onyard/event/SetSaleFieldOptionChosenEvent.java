package com.iaai.onyard.event;

import com.iaai.onyard.classes.OnYardFieldOption;


public class SetSaleFieldOptionChosenEvent {

    private final OnYardFieldOption mOption;

    public SetSaleFieldOptionChosenEvent(OnYardFieldOption option) {
        mOption = option;
    }

    public OnYardFieldOption getOption() {
        return mOption;
    }
}
