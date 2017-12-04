package com.iaai.onyard.event;

import java.util.ArrayList;

import com.iaai.onyardproviderapi.classes.VehicleInfo;


public class CheckinVehiclesRetrievedEvent {

    private final ArrayList<VehicleInfo> mCheckinVehiclesList;

    public CheckinVehiclesRetrievedEvent(ArrayList<VehicleInfo> checkinVehicles) {
        mCheckinVehiclesList = checkinVehicles;
    }

    public ArrayList<VehicleInfo> getCheckinList() {
        return mCheckinVehiclesList;
    }
}
