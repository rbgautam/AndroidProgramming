package com.iaai.onyard.listener;

import android.content.Context;

import com.iaai.onyardproviderapi.classes.VehicleInfo;

public interface GetVehicleInfoListener {

    void onVehicleInfoRetrieved(VehicleInfo vehicle, Context appContext);
}
