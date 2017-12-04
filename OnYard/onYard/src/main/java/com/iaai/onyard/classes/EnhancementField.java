package com.iaai.onyard.classes;

import java.util.ArrayList;

import com.iaai.onyard.application.OnYard.EnhancementOptions;
import com.iaai.onyard.application.OnYard.OnYardFieldInputType;
import com.iaai.onyard.utility.EnhancementFieldHelper;
import com.iaai.onyardproviderapi.classes.StockEnhancementInfo;

public class EnhancementField extends OnYardField {

    @SuppressWarnings("unused")
    private final Integer mVehicleCycleId;
    private final boolean mIsApprovalNeeded;
    private final Integer mInitialSelectedOptionPos;

    public EnhancementField(StockEnhancementInfo stockEnhancement) {
        final boolean isWorkPendingStatus = EnhancementOptions.WORK_PENDING_VALUE
                .equals(stockEnhancement.getCurrentStatusCode());

        mName = stockEnhancement.getEnhancementDescription();
        mSelectedOptionPos = null;
        mIsRequired = stockEnhancement.isRequired() || isWorkPendingStatus;
        mId = stockEnhancement.getEnhancementId();
        mIsApprovalNeeded = stockEnhancement.isApprovalNeeded();
        mVehicleCycleId = stockEnhancement.getVehicleCycleId();

        mOptions = new ArrayList<OnYardFieldOption>();
        if (mIsApprovalNeeded && !isWorkPendingStatus) {
            mOptions.add(EnhancementFieldHelper.getRequestApprovalOption());
        }
        mOptions.add(EnhancementFieldHelper.getCompleteOption());
        if (mIsRequired) {
            mOptions.add(EnhancementFieldHelper.getNaOption());
        }

        final String statusCode = stockEnhancement.getCurrentStatusCode();
        if (statusCode != null) {
            for (final OnYardFieldOption option : mOptions) {
                if (option.getValue() != null && option.getValue().equals(statusCode)) {
                    setSelectedOption(option);
                }
            }
        }

        mInitialSelectedOptionPos = mSelectedOptionPos;
    }

    public boolean isInitalOptionSelected() {
        return mInitialSelectedOptionPos != null
                && mInitialSelectedOptionPos.equals(mSelectedOptionPos);
    }

    public boolean equals(EnhancementField field) {
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
                && mInitialSelectedOptionPos == null ? field.mInitialSelectedOptionPos == null
                : mInitialSelectedOptionPos.equals(field.mInitialSelectedOptionPos)
                && (mName == null ? field.mName == null : mName.equals(field.mName))
                && mIsRequired == field.mIsRequired && mId == field.mId;
    }

    @Override
    public OnYardFieldInputType getInputType() {
        return OnYardFieldInputType.LIST;
    }

    @Override
    public String getEnteredValue() {
        return null;
    }

    @Override
    public boolean hasSelection() {
        return mSelectedOptionPos != null;
    }
}
