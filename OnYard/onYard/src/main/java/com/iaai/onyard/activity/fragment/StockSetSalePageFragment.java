package com.iaai.onyard.activity.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.adapter.OnYardFieldArrayAdapter;
import com.iaai.onyard.adapter.SetSaleArrayAdapter;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.application.OnYard.SetSaleId;
import com.iaai.onyard.classes.OnYardField;
import com.iaai.onyard.classes.OnYardFieldOption;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.classes.SetSaleField;
import com.iaai.onyard.event.SaveConditionsChangedEvent;
import com.iaai.onyard.event.SessionDataCreatedEvent;
import com.iaai.onyard.event.SetSaleFieldOptionChosenEvent;
import com.iaai.onyard.event.SetSaleInputEnteredEvent;
import com.iaai.onyard.session.SetSaleData;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.squareup.otto.Subscribe;


public class StockSetSalePageFragment extends StockPageListFragment {

    @InjectView(R.id.stock_setsale_list)
    ListView mSetSaleList;
    @InjectView(R.id.stock_setsale_input_frame)
    FrameLayout mInputFrame;
    @InjectView(R.id.setsale_is_completed)
    TextView mSetSaleCompletedMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_stock_setsale, container, false);
            ButterKnife.inject(this, view);

            initFragments();
            initList();

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return container;
        }
    }

    @Subscribe
    public void onSaveConditionsChanged(SaveConditionsChangedEvent event) {
        try {
            final Activity activity = getActivity();
            if (activity != null) {
                activity.invalidateOptionsMenu();
            }
        }
        catch (final Exception e) {
            showErrorDialog(ErrorMessage.SET_SALE);
            logError(e);
        }
    }

    @Override
    @Subscribe
    public void onSessionDataCreated(SessionDataCreatedEvent event) {
        try {
            if (getActivity() != null) {
                if (getSessionData().wasSetSaleCommitted()) {
                    dismissProgressDialog();

                    mSetSaleList.setVisibility(View.GONE);
                    mSetSaleCompletedMessage.setVisibility(View.VISIBLE);
                }
                else {
                    setAdapterCommitAttempted(true);
                    refreshList();
                }
            }
        }
        catch (final Exception e) {
            logError(e);
            showErrorDialog(ErrorMessage.SET_SALE);
        }
    }

    @Subscribe
    public void onSetSaleOptionChosen(SetSaleFieldOptionChosenEvent event) {
        try {
            if (getActivity() != null) {
                final SetSaleData setSaleData = getSessionData().getSetSaleData();
                setSaleData.setSelectedOption(getListSelectedPos(), event.getOption());
                setSaleData.includeInSave(true);

                refreshList();
                goToNextField();

                getEventBus().post(new SaveConditionsChangedEvent());
            }
        }
        catch (final Exception e) {
            logError(e);
            showErrorDialog(ErrorMessage.SET_SALE);
        }
    }

    @Subscribe
    public void onSetSaleInputEntered(SetSaleInputEnteredEvent event) {
        try {
            if (getActivity() != null) {
                final SetSaleData setSaleData = getSessionData().getSetSaleData();
                setSaleData.setEnteredValue(getListSelectedPos(), event.getInput());
                setSaleData.includeInSave(true);

                refreshList();
                goToNextField();

                getEventBus().post(new SaveConditionsChangedEvent());
            }
        }
        catch (final Exception e) {
            logError(e);
            showErrorDialog(ErrorMessage.SET_SALE);
        }
    }

    @Override
    protected int getActionBarIconId() {
        return R.drawable.ic_action_go_to_today;
    }

    @Override
    protected String getActionBarTitle() {
        return "Set Sale";
    }

    @Override
    protected String getActionBarSubTitle() {
        try {
            if (getActivity() != null) {
                return getSessionData().getVehicleInfo().getStockNumber();
            }
            else {
                return "";
            }
        }
        catch (final Exception e) {
            logWarning(e);
            return null;
        }
    }

    @Override
    protected int getActionBarColorId() {
        return R.color.setsale_orange;
    }

    @Override
    public boolean isSaveAllowed() {
        try {
            if (getActivity() != null) {
                return getSessionData().isSetSaleCommitAllowed()
                        && getSessionData().shouldIncludeSetSaleInSave();
            }
            else {
                return false;
            }
        }
        catch (final Exception e) {
            logWarning(e);
            return false;
        }
    }

    @Override
    public boolean isVerifyRequired() {
        try {
            if (getActivity() != null) {
                return getSessionData().isSetSaleCommitAllowed()
                        && !getSessionData().shouldIncludeSetSaleInSave();
            }
            else {
                return false;
            }
        }
        catch (final Exception e) {
            logWarning(e);
            return false;
        }
    }

    @Override
    protected ListView getListView() {
        return mSetSaleList;
    }

    @Override
    protected FrameLayout getInputFrame() {
        return mInputFrame;
    }

    @Override
    protected boolean shouldDisplayList() {
        return !getFieldList().isEmpty();
    }

    @Override
    protected ArrayList<? extends OnYardField> getFieldList() {
        if (getActivity() != null) {
            return getSessionData().getSetSaleData().getSetSaleFields();
        }
        else {
            return new ArrayList<SetSaleField>();
        }
    }

    @Override
    protected OnYardFieldArrayAdapter getListAdapter() {
        final Activity activity = getActivity();
        if (activity != null) {
            return new SetSaleArrayAdapter(activity, getFieldList());
        }
        else {
            return null;
        }
    }

    @Override
    protected InputFragment getInputFragment() {
        return new SetSaleInputFragment();
    }

    @Override
    protected int getDetailsFrameId() {
        return R.id.stock_setsale_vehicle_details_frame;
    }

    @Override
    protected int getInputFrameId() {
        return mInputFrame.getId();
    }

    @Override
    protected void prepopulateFields() {
        final Activity activity = getActivity();
        if (activity != null) {
            final OnYardPreferences preferences = new OnYardPreferences(activity);
            final VehicleInfo vehicleInfo = getSessionData().getVehicleInfo();
            final SetSaleData setSaleData = getSessionData().getSetSaleData();

            final boolean isPrefAuctionDate = vehicleInfo.getAuctionDate() == preferences
                    .getSelectedAuctionDate();
            final boolean isVehicleSetForSale = vehicleInfo.getAuctionDate() > 0
                    && vehicleInfo.getAuctionNumber() != null
                    && vehicleInfo.getAuctionItemSequenceNumber() != null;

            // sale aisle
            final String vehSaleAisle = vehicleInfo.getAisle();
            final String lastSaleAisle = preferences.getLastSaleAisle();
            final int saleAislePosition = setSaleData.getFieldPosById(SetSaleId.SALE_AISLE);
            if (isVehicleSetForSale) {
                setSaleData.setEnteredValue(saleAislePosition, vehSaleAisle);
            }
            else {
                if (!DataHelper.isNullOrEmpty(lastSaleAisle) && isPrefAuctionDate) {
                    setSaleData.setEnteredValue(saleAislePosition, lastSaleAisle);
                }
            }

            // auction number
            final Integer vehAuctionNumber = vehicleInfo.getAuctionNumber();
            final int prefAuctionNumber = preferences.getLastAuctionNumber();
            final int auctionNumberPosition = setSaleData.getFieldPosById(SetSaleId.AUCTION_NUMBER);
            if (isVehicleSetForSale) {
                setSaleData.setSelectedOption(auctionNumberPosition,
                        SetSaleField.getAuctionLaneOption(vehAuctionNumber));
            }
            else {
                if (prefAuctionNumber > 0 && isPrefAuctionDate) {
                    setSaleData.setSelectedOption(auctionNumberPosition,
                            SetSaleField.getAuctionLaneOption(prefAuctionNumber));
                }
                else {
                    final int numberOfAuctions = setSaleData.getFieldById(SetSaleId.AUCTION_NUMBER)
                            .getOptions().size();
                    if (numberOfAuctions == 1) {
                        setSaleData.setSelectedOption(auctionNumberPosition,
                                SetSaleField.getAuctionLaneOption(1));
                    }
                }
            }


            // odd/even numbering
            final boolean isOddEvenEnabled = preferences.getIsOddEvenNumberingEnabled();
            final int oddEvenNumberingPosition = setSaleData
                    .getFieldPosById(SetSaleId.ODD_EVEN_NUMBERING);
            if (isOddEvenEnabled && isPrefAuctionDate) {
                setSaleData.setSelectedOption(oddEvenNumberingPosition,
                        SetSaleField.getOddEvenNumberingYesOption());
            }
            else {
                setSaleData.setSelectedOption(oddEvenNumberingPosition,
                        SetSaleField.getOddEvenNumberingNoOption());
            }

            // auction item seq number
            final int lastAuctionItemSeqNumber = preferences.getLastAuctionItemSeqNumber();
            final Integer vehAuctionItemSeqNumber = vehicleInfo.getAuctionItemSequenceNumber();
            int finalAuctionItemSeqNumber;
            final int auctionItemSeqNumberPosition = setSaleData
                    .getFieldPosById(SetSaleId.AUCTION_ITEM_SEQUENCE_NUMBER);
            if (isVehicleSetForSale) {
                finalAuctionItemSeqNumber = vehAuctionItemSeqNumber;
                setSaleData.setEnteredValue(auctionItemSeqNumberPosition,
                        String.valueOf(finalAuctionItemSeqNumber));
            }
            else {
                if (isPrefAuctionDate && lastAuctionItemSeqNumber != 0) {
                    if (isOddEvenEnabled) {
                        finalAuctionItemSeqNumber = lastAuctionItemSeqNumber + 2;
                    }
                    else {
                        finalAuctionItemSeqNumber = lastAuctionItemSeqNumber + 1;
                    }
                    setSaleData.setEnteredValue(auctionItemSeqNumberPosition,
                            String.valueOf(finalAuctionItemSeqNumber));
                }
            }
        }
    }

    @Override
    protected OnYardFieldOption getGuessedOption(int selectedPos) {
        return null;
    }
}
