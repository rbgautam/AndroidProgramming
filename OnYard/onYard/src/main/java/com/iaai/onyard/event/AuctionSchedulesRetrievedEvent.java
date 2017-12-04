package com.iaai.onyard.event;

import java.util.ArrayList;

import com.iaai.onyardproviderapi.classes.AuctionScheduleInfo;


public class AuctionSchedulesRetrievedEvent {

    private final ArrayList<AuctionScheduleInfo> mAuctionSchedulesList;

    public AuctionSchedulesRetrievedEvent(ArrayList<AuctionScheduleInfo> auctionSchedules) {
        mAuctionSchedulesList = auctionSchedules;
    }

    public ArrayList<AuctionScheduleInfo> getAuctionScheduleList() {
        return mAuctionSchedulesList;
    }
}
