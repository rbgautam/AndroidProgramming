package com.iaai.onyard.event;

import java.util.ArrayList;

import com.iaai.onyardproviderapi.classes.VehicleInfo;

public class ReshootVehiclesRetrievedEvent {

    private final ArrayList<VehicleInfo> mReshootVehiclesList;

    public ReshootVehiclesRetrievedEvent(ArrayList<VehicleInfo> reshootVehicles) {
        mReshootVehiclesList = reshootVehicles;
    }

    public ArrayList<VehicleInfo> getReshootList() {
        return mReshootVehiclesList;
    }
}
