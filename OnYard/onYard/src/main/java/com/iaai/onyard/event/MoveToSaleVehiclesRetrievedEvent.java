package com.iaai.onyard.event;

import java.util.ArrayList;

import com.iaai.onyardproviderapi.classes.VehicleInfo;

public class MoveToSaleVehiclesRetrievedEvent {

    ArrayList<VehicleInfo> mMoveToSaleVehiclesList;

    public ArrayList<VehicleInfo> getMoveToSaleList() {
        return mMoveToSaleVehiclesList;
    }

    public MoveToSaleVehiclesRetrievedEvent(ArrayList<VehicleInfo> mlocationInfo) {
        mMoveToSaleVehiclesList = mlocationInfo;
    }

}
