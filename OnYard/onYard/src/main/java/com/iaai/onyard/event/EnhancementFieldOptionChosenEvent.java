package com.iaai.onyard.event;

import com.iaai.onyard.classes.OnYardFieldOption;


public class EnhancementFieldOptionChosenEvent {

    private final OnYardFieldOption mOption;

    public EnhancementFieldOptionChosenEvent(OnYardFieldOption option) {
        mOption = option;
    }

    public OnYardFieldOption getOption() {
        return mOption;
    }
}
