package com.iaai.onyard.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.CheckinId;
import com.iaai.onyard.application.OnYard.OnYardFieldInputType;
import com.iaai.onyard.classes.CheckinField;
import com.iaai.onyard.classes.OnYardFieldOption;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.listener.CreateCheckinDataListener;
import com.iaai.onyard.listener.GetCheckinTemplateListener;
import com.iaai.onyard.task.CommitCheckinDataTask;
import com.iaai.onyard.task.CommitCheckinMetricsTask;
import com.iaai.onyard.task.GetCheckinTemplateTask;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.CheckinFieldHelper;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

public class CheckinData implements GetCheckinTemplateListener {

    private ArrayList<CheckinField> mCheckinFieldList;
    private final CreateCheckinDataListener mListener;
    protected final long mStartDateTime;
    protected long mEndDateTime;
    private HashMap<Integer, OnYardFieldOption> mGuessedOptions;
    private final String mBranchState;
    private final String mExtColor;

    public CheckinData(int salvageType, String extColor, Context context,
            CreateCheckinDataListener listener) {
        mCheckinFieldList = new ArrayList<CheckinField>();
        mListener = listener;
        mStartDateTime = DataHelper.getUnixUtcTimeStamp();
        mEndDateTime = DataHelper.getUnixUtcTimeStamp();
        mBranchState = getBranchState(context);
        mExtColor = extColor;
        initCheckinFields(context, salvageType);
    }

    public ArrayList<CheckinField> getCheckinFields() {
        return mCheckinFieldList;
    }

    public CheckinField getFieldAt(int position) {
        if (!isValidPos(position)) {
            return null;
        }

        return mCheckinFieldList.get(position);
    }

    public CheckinField getFieldById(int id) {
        for (final CheckinField field : mCheckinFieldList) {
            if (field.getId() == id) {
                return field;
            }
        }

        return null;
    }

    public boolean areAllRequiredFieldsPopulated() {
        for (final CheckinField field : mCheckinFieldList) {
            if (field.isRequired() && !field.hasSelection()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAnyDataEntered() {
        boolean isAnyDataEntered = false;
        for (final CheckinField field : mCheckinFieldList) {
            if (field.hasSelection() && field.getId() != CheckinId.LOSS_TYPE
                    && field.getId() != CheckinId.SALVAGE_TYPE) {
                isAnyDataEntered = true;
            }
        }

        return isAnyDataEntered;
    }

    public void setEnteredValue(int position, String value) {
        if (isValidPos(position) && value != null) {
            final CheckinField field = mCheckinFieldList.get(position);
            if (field.getInputType() == OnYardFieldInputType.ALPHANUMERIC) {
                value = value.toUpperCase(Locale.US);
            }

            mCheckinFieldList.get(position).setEnteredValue(value);
        }
    }

    public void setSelectedOption(int position, OnYardFieldOption option) {
        if (isValidPos(position) && option != null) {
            mCheckinFieldList.get(position).setSelectedOption(option);
        }
    }

    public void setSelectedOptionIfNotSelected(int position, OnYardFieldOption option) {
        if (isValidPos(position) && !mCheckinFieldList.get(position).hasSelection()) {
            setSelectedOption(position, option);
        }
    }
    public int getFieldPosById(int id) {
        return mCheckinFieldList.indexOf(getFieldById(id));
    }

    private void initCheckinFields(Context context, int salvageType) {
        final GetCheckinTemplateTask task = new GetCheckinTemplateTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context, salvageType);
    }

    public OnYardFieldOption getGuessedOption(int id) {
        return mGuessedOptions.get(id);
    }

    private void setGuessedOption(int id, OnYardFieldOption option) {
        mGuessedOptions.put(id, option);
    }

    @Override
    public void onCheckinTemplateRetrieved(ArrayList<CheckinField> fields) {
        mCheckinFieldList = fields;
        initGuessedOptions();

        if (mListener != null) {
            mListener.onCheckinDataCreated();
        }
    }

    private void initGuessedOptions() {
        mGuessedOptions = new HashMap<Integer, OnYardFieldOption>();

        // exterior color
        if (!DataHelper.isNullOrEmpty(mExtColor)) {
            final CheckinField colorField = getFieldById(CheckinId.EXTERIOR_COLOR);
            if (colorField != null) {
                final ArrayList<OnYardFieldOption> colors = colorField.getOptions();
                for (final OnYardFieldOption color : colors) {
                    if (color.getDisplayName().equals(mExtColor)) {
                        setGuessedOption(CheckinId.EXTERIOR_COLOR, color);
                        break;
                    }
                }
            }
        }

        // number of wheels and tires for automobile template
        final OnYardFieldOption autoWheelOption = CheckinFieldHelper.getNumeric4Option();
        setGuessedOption(CheckinId.NUMBER_OF_TIRES_AUTOMOBILE, autoWheelOption);
        setGuessedOption(CheckinId.NUMBER_OF_WHEELS_AUTOMOBILE, autoWheelOption);

        // plate state option
        final CheckinField stateField = getFieldById(CheckinId.STATE);
        if (stateField != null) {
            final ArrayList<OnYardFieldOption> states = stateField.getOptions();
            for (final OnYardFieldOption state : states) {
                if (state.getValue().equals(mBranchState)) {
                    setGuessedOption(CheckinId.STATE, state);
                    break;
                }
            }
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

        new CommitCheckinDataTask().execute(new VehicleInfo(vehicle), mCheckinFieldList,
                DataHelper.convertUnixUtcToLocal(mStartDateTime),
                DataHelper.convertUnixUtcToLocal(mEndDateTime), context, branchNumber, userLogin);
        new CommitCheckinMetricsTask().execute(context, userLogin, branchNumber);
    }

    private void markEndDateTime() {
        mEndDateTime = DataHelper.getUnixUtcTimeStamp();
    }

    private String getBranchState(Context context) {
        final String branchNumber = new OnYardPreferences(context).getEffectiveBranchNumber();

        Cursor queryResult = null;
        try {
            queryResult = context.getContentResolver().query(OnYardContract.Branch.CONTENT_URI,
                    null, OnYardContract.Branch.COLUMN_NAME_BRANCH_NUMBER + "=?",
                    new String[] { branchNumber }, null);
            if (queryResult != null && queryResult.moveToFirst()) {
                return queryResult.getString(queryResult
                        .getColumnIndex(OnYardContract.Branch.COLUMN_NAME_BRANCH_STATE));
            }
            else {
                return null;
            }
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
    }

    private boolean isValidPos(int pos) {
        return pos >= 0 && pos < mCheckinFieldList.size();
    }
}
