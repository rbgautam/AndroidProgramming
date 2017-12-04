package com.iaai.onyard.classes;

import java.util.ArrayList;

import com.iaai.onyard.application.OnYard.OnYardFieldInputType;
import com.iaai.onyard.application.OnYard.OnYardFieldType;


public abstract class OnYardField {

    protected ArrayList<OnYardFieldOption> mOptions;
    /**
     * Selection indicator for DROPDOWN fields
     */
    protected Integer mSelectedOptionPos;
    /**
     * Selection indicator for ALPHANUMERIC, NUMERIC, and TEXT fields
     */
    protected String mEnteredValue;
    protected String mName;
    protected boolean mIsRequired;
    protected Integer mMinValue;
    protected Integer mMaxValue;
    protected Integer mMaxStringLength;
    protected int mId;
    protected OnYardFieldType mFieldType;
    protected OnYardFieldInputType mInputType;
    protected String mDataMemberName;

    /**
     * Get the index of the currently selected option.
     * 
     * @return The index of the currently selected option, or null if no option is selected.
     */
    public Integer getSelectedIndex() {
        return mSelectedOptionPos;
    }

    /**
     * Set the currently selected option.
     * 
     * @return The newly selected option.
     */
    public void setSelectedOption(OnYardFieldOption option) {
        for (int index = 0; index < mOptions.size(); index++) {
            if (mOptions.get(index).equals(option)) {
                mSelectedOptionPos = index;
            }
        }
    }

    /**
     * Set the field's current value. Only use this method with NUMERIC, ALPHANUMERIC, OR TEXT
     * fields.
     * 
     * @param value The new value.
     */
    public void setEnteredValue(String value) {
        if (value == null || value.equals("")) {
            mEnteredValue = null;
        }
        else {
            mEnteredValue = value;
        }
    }

    /**
     * Get the field's current value. Only use this method with NUMERIC, ALPHANUMERIC, OR TEXT
     * fields.
     * 
     * @return The selected value.
     */
    public String getEnteredValue() {
        return mEnteredValue;
    }

    /**
     * Get the currently selected option.
     * 
     * @return The currently selected option, or null if no option is selected.
     */
    public OnYardFieldOption getSelectedOption() {
        if (mSelectedOptionPos != null) {
            return mOptions.get(mSelectedOptionPos);
        }
        else {
            return null;
        }
    }

    public boolean hasSelection() {
        return mSelectedOptionPos != null || mEnteredValue != null;
    }

    public OnYardFieldType getFieldType() {
        return mFieldType;
    }

    public OnYardFieldInputType getInputType() {
        return mInputType;
    }

    public ArrayList<OnYardFieldOption> getOptions() {
        return mOptions;
    }

    public String getName() {
        return mName;
    }

    public boolean isRequired() {
        return mIsRequired;
    }

    public int getMinValue() {
        return mMinValue;
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public int getMaxLength() {
        return mMaxStringLength;
    }

    public int getId() {
        return mId;
    }

    public String getDataMemberName() {
        return mDataMemberName;
    }

    public boolean equals(OnYardField field) {
        if (mOptions != null) {
            if (field.mOptions == null) {
                return false;
            }
            final int length = Math.max(mOptions.size(), field.mOptions.size());
            for (int index = 0; index < length; index++) {
                if (!mOptions.get(index).equals(field.mOptions.get(index))) {
                    return false;
                }
            }
        }

        return mSelectedOptionPos == null ? field.mSelectedOptionPos == null : mSelectedOptionPos
                .equals(field.mSelectedOptionPos)
                && (mName == null ? field.mName == null : mName.equals(field.mName))
                && mIsRequired == field.mIsRequired && mId == field.mId;
    }
}
