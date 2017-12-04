package com.iaai.onyard.session;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.classes.EnhancementField;
import com.iaai.onyard.classes.OnYardFieldOption;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.listener.CreateEnhancementDataListener;
import com.iaai.onyard.listener.GetEnhancementFieldsListener;
import com.iaai.onyard.task.CommitEnhancementDataTask;
import com.iaai.onyard.task.GetStockEnhancementsTask;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyardproviderapi.classes.VehicleInfo;

public class EnhancementData implements GetEnhancementFieldsListener {

    private ArrayList<EnhancementField> mEnhancementFieldList;
    private final CreateEnhancementDataListener mListener;
    protected long mStartDateTime;
    protected long mEndDateTime;

    public EnhancementData(Context context,
            CreateEnhancementDataListener listener,
            VehicleInfo vehicle) {
        mEnhancementFieldList = new ArrayList<EnhancementField>();
        mListener = listener;
        mStartDateTime = DataHelper.getUnixUtcTimeStamp();
        mEndDateTime = DataHelper.getUnixUtcTimeStamp();
        initEnhancementFields(context, vehicle);
    }

    public ArrayList<EnhancementField> getEnhancementFields() {
        return mEnhancementFieldList;
    }

    public EnhancementField getFieldAt(int position) {
        if (!isValidPos(position)) {
            return null;
        }

        return mEnhancementFieldList.get(position);
    }

    public EnhancementField getFieldById(int id) {
        for (final EnhancementField field : mEnhancementFieldList) {
            if (field.getId() == id) {
                return field;
            }
        }

        return null;
    }

    public boolean isAnyDataEntered() {
        boolean isAnyDataEntered = false;
        for (final EnhancementField field : mEnhancementFieldList) {
            if (field.hasSelection() && !field.isInitalOptionSelected()) {
                isAnyDataEntered = true;
            }
        }

        return isAnyDataEntered;
    }

    public void setSelectedOption(int position, OnYardFieldOption option) {
        if (isValidPos(position) && option != null) {
            mEnhancementFieldList.get(position).setSelectedOption(option);
        }
    }

    public int getFieldPosById(int id) {
        return mEnhancementFieldList.indexOf(getFieldById(id));
    }

    private void initEnhancementFields(Context context, VehicleInfo vehicle) {
        final GetStockEnhancementsTask task = new GetStockEnhancementsTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context, vehicle);
    }

    @Override
    public void onEnhancementFieldsRetrieved(ArrayList<EnhancementField> fields) {
        mEnhancementFieldList = fields;

        if (mListener != null) {
            mListener.onEnhancementDataCreated();
        }
    }

    public void commitData(VehicleInfo vehicle, Context context) {
        markEndDateTime();
        final int branchNumber = Integer.parseInt(new OnYardPreferences(context)
        .getEffectiveBranchNumber());
        String userLogin = AuthenticationHelper.getLoggedInUser(context.getContentResolver());
        if (userLogin == null) {
            userLogin = OnYard.DEFAULT_USER_LOGIN;
        }

        new CommitEnhancementDataTask().execute(new VehicleInfo(vehicle), mEnhancementFieldList,
                mStartDateTime, mEndDateTime, context, branchNumber, userLogin);
    }

    public void removeSelectedEnhancements() {
        markStartDateTime();
        final ArrayList<EnhancementField> newList = new ArrayList<EnhancementField>();
        for (final EnhancementField field : mEnhancementFieldList) {
            if (!field.hasSelection()) {
                newList.add(field);
            }
        }

        mEnhancementFieldList = newList;
    }

    private void markEndDateTime() {
        mEndDateTime = DataHelper.getUnixUtcTimeStamp();
    }

    protected void markStartDateTime() {
        mStartDateTime = DataHelper.getUnixUtcTimeStamp();
    }

    private boolean isValidPos(int pos) {
        return pos >= 0 && pos < mEnhancementFieldList.size();
    }
}
