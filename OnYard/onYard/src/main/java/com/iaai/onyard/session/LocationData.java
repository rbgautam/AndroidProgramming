package com.iaai.onyard.session;

import android.content.Context;
import android.location.Location;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.task.CommitLocationDataTask;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyardproviderapi.classes.VehicleInfo;

public class LocationData {

    private final long mStartDateTime;
    private long mEndDateTime;
    private String mNewAisle;
    private Integer mNewStall;

    public LocationData() {
        mStartDateTime = DataHelper.getUnixUtcTimeStamp();
        mEndDateTime = DataHelper.getUnixUtcTimeStamp();
    }

    public boolean areAllRequiredFieldsPopulated() {
        return mNewAisle != null && mNewStall != null;
    }

    public boolean isAnyDataEntered() {
        return mNewAisle != null || mNewStall != null;
    }

    public void setNewAisle(String value) {
        mNewAisle = value;
    }

    public void setNewStall(Integer value) {
        mNewStall = value;
    }

    public String getNewAisle() {
        return mNewAisle;
    }

    public int getNewStall() {
        return mNewStall;
    }

    public void commitData(VehicleInfo vehicle, Context context, Location location) {
        markEndDateTime();
        final int branchNumber = Integer.parseInt(new OnYardPreferences(context)
        .getEffectiveBranchNumber());
        String userLogin = AuthenticationHelper.getLoggedInUser(context.getContentResolver());
        if (userLogin == null) {
            userLogin = OnYard.DEFAULT_USER_LOGIN;
        }

        new CommitLocationDataTask().execute(new VehicleInfo(vehicle), mStartDateTime,
                mEndDateTime, mNewAisle,
                mNewStall, context, userLogin, branchNumber, location);
    }

    private void markEndDateTime() {
        mEndDateTime = DataHelper.getUnixUtcTimeStamp();
    }
}
