package com.iaai.onyard.event;

import java.util.ArrayList;

import com.iaai.onyardproviderapi.classes.HolidayInfo;


public class HolidaysRetrievedEvent {

    private final ArrayList<HolidayInfo> mHolidayList;

    public HolidaysRetrievedEvent(ArrayList<HolidayInfo> holidays) {
        mHolidayList = holidays;
    }

    public ArrayList<HolidayInfo> getHolidayList() {
        return mHolidayList;
    }
}
