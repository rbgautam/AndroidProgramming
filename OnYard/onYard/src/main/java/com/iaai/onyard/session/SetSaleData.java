package com.iaai.onyard.session;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;

import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.OnYardFieldInputType;
import com.iaai.onyard.application.OnYard.OnYardFieldType;
import com.iaai.onyard.application.OnYard.SetSaleId;
import com.iaai.onyard.classes.OnYardFieldOption;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.classes.SetSaleField;
import com.iaai.onyard.http.SetSaleHttpPost;
import com.iaai.onyard.task.CommitSetSaleDataTask;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyardproviderapi.classes.AuctionScheduleInfo;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

public class SetSaleData {

    private ArrayList<SetSaleField> mSetSaleFieldList;
    private boolean mShouldIncludeInSave;
    protected final long mStartDateTime;
    protected long mEndDateTime;

    public SetSaleData(VehicleInfo vehicle, Context context) {
        mStartDateTime = DataHelper.getUnixUtcTimeStamp();
        mEndDateTime = DataHelper.getUnixUtcTimeStamp();
        mShouldIncludeInSave = false;
        mSetSaleFieldList = new ArrayList<SetSaleField>();

        if (vehicle.isSetSaleEligible(Integer.parseInt(new OnYardPreferences(context)
        .getEffectiveBranchNumber()))) {
            initSetSaleFields(vehicle.getAuctionDate(), context);
        }
    }

    private void initSetSaleFields(Long auctionDate, Context context) {
        final int numberOfAuctions = getNumberOfAuctions(context, auctionDate);

        mSetSaleFieldList = new ArrayList<SetSaleField>();
        mSetSaleFieldList.add(getSaleAisleField());
        mSetSaleFieldList.add(getAuctionNumberField(auctionDate, numberOfAuctions));
        if (numberOfAuctions == 2) {
            mSetSaleFieldList.add(getOddEvenNumberingField());
        }
        mSetSaleFieldList.add(getAuctionItemSeqNumberField());
    }

    private SetSaleField getSaleAisleField() {
        return new SetSaleField(SetSaleId.SALE_AISLE, "Sale Aisle", true,
                OnYardFieldInputType.ALPHANUMERIC, OnYardFieldType.STRING, null, null, 3,
                SetSaleHttpPost.SALE_AISLE_KEY, null);
    }

    private SetSaleField getAuctionNumberField(Long auctionDate,
            int numberOfAuctions) {

        final ArrayList<OnYardFieldOption> auctionNumberOptions = new ArrayList<OnYardFieldOption>();
        for (int auctionNumber = 1; auctionNumber <= numberOfAuctions; auctionNumber++) {
            auctionNumberOptions.add(SetSaleField.getAuctionLaneOption(auctionNumber));
        }

        return new SetSaleField(SetSaleId.AUCTION_NUMBER, "Auction Lane", true,
                OnYardFieldInputType.LIST, OnYardFieldType.INTEGER, null, null, 3,
                SetSaleHttpPost.AUCTION_NUMBER_KEY, auctionNumberOptions);
    }

    private SetSaleField getOddEvenNumberingField() {
        final ArrayList<OnYardFieldOption> oddEvenNumberingOptions = new ArrayList<OnYardFieldOption>();
        oddEvenNumberingOptions.add(SetSaleField.getOddEvenNumberingYesOption());
        oddEvenNumberingOptions.add(SetSaleField.getOddEvenNumberingNoOption());

        return new SetSaleField(SetSaleId.ODD_EVEN_NUMBERING, "Odd/Even Numbering", true,
                OnYardFieldInputType.LIST, OnYardFieldType.STRING, null, null, 3, null,
                oddEvenNumberingOptions);
    }

    private SetSaleField getAuctionItemSeqNumberField() {
        return new SetSaleField(SetSaleId.AUCTION_ITEM_SEQUENCE_NUMBER, "Item Number", true,
                OnYardFieldInputType.NUMERIC, OnYardFieldType.INTEGER, 1, 32767, null,
                SetSaleHttpPost.AUCTION_ITEM_SEQUENCE_NUMBER_KEY, null);
    }

    public boolean shouldIncludeInSave() {
        return mShouldIncludeInSave;
    }

    public void includeInSave(boolean includeInSave) {
        mShouldIncludeInSave = includeInSave;
    }

    public ArrayList<SetSaleField> getSetSaleFields() {
        return mSetSaleFieldList;
    }

    public SetSaleField getFieldAt(int position) {
        if (!isValidPos(position)) {
            return null;
        }

        return mSetSaleFieldList.get(position);
    }

    public SetSaleField getFieldById(int id) {
        for (final SetSaleField field : mSetSaleFieldList) {
            if (field.getId() == id) {
                return field;
            }
        }

        return null;
    }

    public boolean areAllRequiredFieldsPopulated() {
        for (final SetSaleField field : mSetSaleFieldList) {
            if (field.isRequired() && !field.hasSelection()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAnyDataEntered() {
        boolean isAnyDataEntered = false;
        for (final SetSaleField field : mSetSaleFieldList) {
            if (field.hasSelection()) {
                isAnyDataEntered = true;
            }
        }

        return isAnyDataEntered;
    }

    public void setEnteredValue(int position, String value) {
        if (isValidPos(position) && value != null) {
            final SetSaleField field = mSetSaleFieldList.get(position);
            if (field.getInputType() == OnYardFieldInputType.ALPHANUMERIC) {
                value = value.toUpperCase(Locale.US);
            }

            mSetSaleFieldList.get(position).setEnteredValue(value);
        }
    }

    public void setSelectedOption(int position, OnYardFieldOption option) {
        if (isValidPos(position) && option != null) {
            mSetSaleFieldList.get(position).setSelectedOption(option);
        }
    }

    public void setSelectedOptionIfNotSelected(int position, OnYardFieldOption option) {
        if (isValidPos(position) && !mSetSaleFieldList.get(position).hasSelection()) {
            setSelectedOption(position, option);
        }
    }
    public int getFieldPosById(int id) {
        return mSetSaleFieldList.indexOf(getFieldById(id));
    }

    public void commitData(VehicleInfo vehicle, Context context, Location location) {
        markEndDateTime();
        final int branchNumber = Integer.parseInt(new OnYardPreferences(context)
        .getEffectiveBranchNumber());
        String userLogin = AuthenticationHelper.getLoggedInUser(context.getContentResolver());
        if (userLogin == null) {
            userLogin = OnYard.DEFAULT_USER_LOGIN;
        }

        new CommitSetSaleDataTask().execute(new VehicleInfo(vehicle), mSetSaleFieldList,
                mStartDateTime, mEndDateTime, context, branchNumber, userLogin, location);
    }

    /**
     * If currently selected Auction Lane and Auction Item Sequence Number are already in use by a
     * stock, return the stock number of that stock. Otherwise, return null.
     * 
     * @param vehicle The current vehicle info.
     * @param context The current context.
     * @return The stock number using the selected Lane/Item Number combination, or null if
     *         combination is not in use.
     */
    protected String getStockNumUsingLaneItem(VehicleInfo vehicle, Context context) {
        if (!vehicle.isSetSaleEligible(Integer.parseInt(new OnYardPreferences(context)
        .getEffectiveBranchNumber()))) {
            return null;
        }

        final long auctionDate = vehicle.getAuctionDate();
        final int auctionNumber = Integer.parseInt(getFieldById(SetSaleId.AUCTION_NUMBER)
                .getSelectedOption().getValue());
        final int auctionItemSeqNumber = Integer.parseInt(getFieldById(
                SetSaleId.AUCTION_ITEM_SEQUENCE_NUMBER).getEnteredValue());

        Cursor queryResult = null;
        try {
            queryResult = context.getContentResolver().query(
                    OnYardContract.Vehicles.CONTENT_URI,
                    new String[] { OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER },
                    OnYardContract.Vehicles.COLUMN_NAME_AUCTION_DATE_UNIX + "=? AND "
                            + OnYardContract.Vehicles.COLUMN_NAME_AUCTION_NUMBER + "=? AND "
                            + OnYardContract.Vehicles.COLUMN_NAME_AUCTION_ITEM_SEQ_NUMBER + "=?",
                            new String[] { String.valueOf(auctionDate), String.valueOf(auctionNumber),
                            String.valueOf(auctionItemSeqNumber) }, null);

            if (queryResult == null || !queryResult.moveToFirst()) {
                return null;
            }

            return new VehicleInfo(queryResult).getStockNumber();
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
    }

    private void markEndDateTime() {
        mEndDateTime = DataHelper.getUnixUtcTimeStamp();
    }

    private boolean isValidPos(int pos) {
        return pos >= 0 && pos < mSetSaleFieldList.size();
    }

    private int getNumberOfAuctions(Context context, Long auctionDate) {
        Cursor queryResult = null;
        try {
            queryResult = context.getContentResolver().query(
                    OnYardContract.AuctionSchedule.CONTENT_URI, null,
                    OnYardContract.AuctionSchedule.COLUMN_NAME_AUCTION_DATE + "=?",
                    new String[] { String.valueOf(auctionDate) }, null);

            if (queryResult == null || !queryResult.moveToFirst()) {
                return 0;
            }

            return new AuctionScheduleInfo(queryResult).getNumAuctions();
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
    }
}
