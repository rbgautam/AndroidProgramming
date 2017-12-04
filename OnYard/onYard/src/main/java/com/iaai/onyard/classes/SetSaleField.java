package com.iaai.onyard.classes;

import java.util.ArrayList;

import com.iaai.onyard.application.OnYard.OnYardFieldInputType;
import com.iaai.onyard.application.OnYard.OnYardFieldType;


public class SetSaleField extends OnYardField {

    public SetSaleField(int id, String caption, boolean isRequired, OnYardFieldInputType inputType,
            OnYardFieldType fieldType, Integer minValue, Integer maxValue, Integer maxStringLength,
            String dataMemberName, ArrayList<OnYardFieldOption> options) {
        mName = caption;
        mSelectedOptionPos = null;
        mEnteredValue = null;
        mIsRequired = isRequired;
        mMinValue = minValue;
        mMaxValue = maxValue;
        mMaxStringLength = maxStringLength;
        mId = id;
        mDataMemberName = dataMemberName;
        mFieldType = fieldType;
        mInputType = inputType;
        mOptions = options;
    }

    public static OnYardFieldOption getOddEvenNumberingYesOption() {
        return new OnYardFieldOption("Yes", "true");
    }

    public static OnYardFieldOption getOddEvenNumberingNoOption() {
        return new OnYardFieldOption("No", "false");
    }

    public static OnYardFieldOption getAuctionLaneOption(int auctionNumber) {
        return new OnYardFieldOption(getAuctionLaneFromNumber(auctionNumber),
                String.valueOf(auctionNumber));
    }

    public static String getAuctionLaneFromNumber(int auctionNumber) {
        return String.valueOf((char) ('A' - 1 + auctionNumber));
    }
}
