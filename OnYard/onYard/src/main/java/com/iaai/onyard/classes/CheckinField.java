package com.iaai.onyard.classes;

import android.content.ContentResolver;

import com.iaai.onyard.application.OnYard.CheckinId;
import com.iaai.onyard.application.OnYard.OnYardFieldInputType;
import com.iaai.onyard.application.OnYard.OnYardFieldType;
import com.iaai.onyard.utility.CheckinFieldHelper;
import com.iaai.onyardproviderapi.classes.CheckinFieldInfo;


public class CheckinField extends OnYardField {

    private final String mFeatureCode;
    private final Integer mFeatureGroupNumber;

    public CheckinField(CheckinFieldInfo fieldInfo, ContentResolver contentResolver, int salvageType) {
        mName = fieldInfo.getCaption();
        mSelectedOptionPos = null;
        mEnteredValue = null;
        mIsRequired = fieldInfo.isRequired();
        mMinValue = fieldInfo.getMinIntValue();
        mMaxValue = fieldInfo.getMaxIntValue();
        mMaxStringLength = fieldInfo.getMaxStringLength();
        mId = fieldInfo.getId();
        mDataMemberName = fieldInfo.getDataMemberName();
        mFieldType = OnYardFieldType.toFieldType(fieldInfo.getFieldType());
        mInputType = OnYardFieldInputType.toInputType(fieldInfo.getInputType());
        mFeatureCode = fieldInfo.getFeatureCode();
        mFeatureGroupNumber = fieldInfo.getFeatureGroupNumber();

        if (mInputType == OnYardFieldInputType.LIST || mInputType == OnYardFieldInputType.CHECKBOX) {
            mOptions = CheckinFieldHelper.getOptionsById(mId, fieldInfo.getFeatureCode(),
                    contentResolver, salvageType, mInputType);
        }
        else {
            mOptions = null;
        }
    }

    @Override
    public void setEnteredValue(String value) {
        if (mId == CheckinId.STALL && value.equals("0")) {
            value = "";
        }

        super.setEnteredValue(value);
    }

    public String getFeatureCode() {
        return mFeatureCode;
    }

    public Integer getFeatureGroupNumber() {
        return mFeatureGroupNumber;
    }

    @Override
    public boolean equals(OnYardField field) {
        final CheckinField checkinField = (CheckinField) field;

        return super.equals(checkinField)
                && (mFeatureCode == null ? checkinField.mFeatureCode == null : mFeatureCode
                .equals(checkinField.mFeatureCode))
                && mFeatureGroupNumber == checkinField.mFeatureGroupNumber;
    }
}
