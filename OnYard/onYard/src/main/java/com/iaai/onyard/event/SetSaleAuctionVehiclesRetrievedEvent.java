package com.iaai.onyard.event;

import java.util.ArrayList;

import com.iaai.onyardproviderapi.classes.VehicleInfo;


public class SetSaleAuctionVehiclesRetrievedEvent {

    private final ArrayList<VehicleInfo> mAuctionVehiclesList;

    public SetSaleAuctionVehiclesRetrievedEvent(ArrayList<VehicleInfo> auctionVehiclesList) {
        mAuctionVehiclesList = auctionVehiclesList;
    }

    public ArrayList<VehicleInfo> getAuctionVehiclesList() {
        return mAuctionVehiclesList;
    }
}
