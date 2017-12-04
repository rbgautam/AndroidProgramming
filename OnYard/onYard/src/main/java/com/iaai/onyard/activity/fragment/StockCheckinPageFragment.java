package com.iaai.onyard.activity.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
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
import com.iaai.onyard.adapter.CheckinArrayAdapter;
import com.iaai.onyard.adapter.OnYardFieldArrayAdapter;
import com.iaai.onyard.application.OnYard.CheckinId;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.classes.CheckinField;
import com.iaai.onyard.classes.OnYardField;
import com.iaai.onyard.classes.OnYardFieldOption;
import com.iaai.onyard.event.CheckinFieldOptionChosenEvent;
import com.iaai.onyard.event.CheckinInputEnteredEvent;
import com.iaai.onyard.event.OverrideVinEvent;
import com.iaai.onyard.event.SalvageTypeChangedEvent;
import com.iaai.onyard.event.SaveConditionsChangedEvent;
import com.iaai.onyard.event.SessionDataCreatedEvent;
import com.iaai.onyard.event.VinScanEnteredEvent;
import com.iaai.onyard.session.CheckinData;
import com.iaai.onyard.utility.CheckinFieldHelper;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.squareup.otto.Subscribe;


public class StockCheckinPageFragment extends StockPageListFragment {

    @InjectView(R.id.stock_checkin_list)
    ListView mCheckinList;
    @InjectView(R.id.stock_checkin_input_frame)
    FrameLayout mInputFrame;
    @InjectView(R.id.checkin_coming_soon)
    TextView mComingSoonMessage;
    @InjectView(R.id.checkin_wrong_status)
    TextView mCheckinWrongStatusMessage;

    private boolean mSalvageTypeChanged;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_stock_checkin, container, false);
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
            showErrorDialog(ErrorMessage.CHECKIN);
            logError(e);
        }
    }

    @Override
    protected void initList() {
        super.initList();

        if (shouldDisplayList()) {
            mCheckinWrongStatusMessage.setVisibility(View.GONE);
            mComingSoonMessage.setVisibility(View.GONE);
        }
        else {
            if (getFieldList().isEmpty()) {
                mComingSoonMessage.setVisibility(View.VISIBLE);
                mCheckinWrongStatusMessage.setVisibility(View.GONE);
            }
            else {
                mComingSoonMessage.setVisibility(View.GONE);
                mCheckinWrongStatusMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    @Subscribe
    public void onSessionDataCreated(SessionDataCreatedEvent event) {
        try {
            if (getActivity() != null) {
                if (getSessionData().wasCheckinCommitted() || mSalvageTypeChanged) {
                    dismissProgressDialog();
                    mSalvageTypeChanged = false;
                    initList();

                    getEventBus().post(new SaveConditionsChangedEvent());
                }
                else {
                    setAdapterCommitAttempted(true);
                    refreshList();
                }
            }
        }
        catch (final Exception e) {
            logError(e);
            showErrorDialog(ErrorMessage.CHECKIN);
        }
    }

    @Subscribe
    public void onSalvageTypeChanged(SalvageTypeChangedEvent event) {
        try {
            showProgressDialog();
            mSalvageTypeChanged = true;
            mInputFrame.setVisibility(View.GONE);

            final Activity activity = getActivity();
            if (activity != null) {
                getSessionData().recreateCheckinData(activity.getApplicationContext(),
                        event.getNewSalvageType());
            }
        }
        catch (final Exception e) {
            logError(e);
            showErrorDialog(ErrorMessage.CHECKIN);
        }
    }

    @Subscribe
    public void onCheckinOptionChosen(CheckinFieldOptionChosenEvent event) {
        try {
            if (getActivity() != null) {
                getSessionData().getCheckinData().setSelectedOption(getListSelectedPos(),
                        event.getOption());

                if (applyRules()) {
                    refreshList();
                    goToNextField();
                }
                getEventBus().post(new SaveConditionsChangedEvent());
            }
        }
        catch (final Exception e) {
            logError(e);
            showErrorDialog(ErrorMessage.CHECKIN);
        }
    }

    @Subscribe
    public void onCheckinInputEntered(CheckinInputEnteredEvent event) {
        try {
            if (getActivity() != null) {
                getSessionData().getCheckinData().setEnteredValue(getListSelectedPos(),
                        event.getInput());
                refreshList();
                goToNextField();

                getEventBus().post(new SaveConditionsChangedEvent());
            }
        }
        catch (final Exception e) {
            logError(e);
            showErrorDialog(ErrorMessage.CHECKIN);
        }
    }

    @Subscribe
    public void onVinScanEntered(VinScanEnteredEvent event) {
        try {
            if (getActivity() != null) {
                final CheckinData checkinData = getSessionData().getCheckinData();

                checkinData.setSelectedOption(checkinData.getFieldPosById(CheckinId.VIN_STATUS),
                        CheckinFieldHelper.getVinStatusOkOption());

                checkinData.setSelectedOption(
                        checkinData.getFieldPosById(CheckinId.VIN_STATUS_NOT_REQUIRED),
                        CheckinFieldHelper.getVinStatusOkOption());

                getEventBus().post(new CheckinInputEnteredEvent(event.getInput()));
            }
        }
        catch (final Exception e) {
            logError(e);
            showErrorDialog(ErrorMessage.CHECKIN);
        }
    }

    @Subscribe
    public void onOverrideVin(OverrideVinEvent event) {
        try {
            if (getActivity() != null) {
                final CheckinData checkinData = getSessionData().getCheckinData();

                checkinData.setSelectedOption(checkinData.getFieldPosById(CheckinId.VIN_STATUS),
                        CheckinFieldHelper.getVinStatusRetaggedOption());

                checkinData.setSelectedOption(
                        checkinData.getFieldPosById(CheckinId.VIN_STATUS_NOT_REQUIRED),
                        CheckinFieldHelper.getVinStatusRetaggedOption());

                getEventBus().post(new CheckinInputEnteredEvent(event.getInput()));
                hideSoftKeyboard();
                forceScrollToSelectedField();
            }
        }
        catch (final Exception e) {
            logError(e);
            showErrorDialog(ErrorMessage.CHECKIN);
        }
    }

    /**
     * applies check-in form rules
     * 
     * @return true for regular navigation (go to the next unanswered question false for custom
     *         navigation
     */
    private boolean applyRules() {
        boolean doNextField = true;
        if (getActivity() != null) {
            final CheckinData checkinData = getSessionData().getCheckinData();
            final CheckinField currentField = checkinData.getFieldAt(getListSelectedPos());

            final OnYardFieldOption airbagNoneOption = CheckinFieldHelper.getAirbagNoneOption();

            switch (currentField.getId()) {
                // Airbag rule: if any "none" selected for any airbag question assign "none" to all
                // following
                // airbag questions, scroll to the next question after airbag section
                case CheckinId.DRIVER_AIRBAG:
                    if (currentField.getSelectedOption().equals(airbagNoneOption)) {
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.PASSENGER_AIRBAG), airbagNoneOption);
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.LEFT_SIDE_AIRBAG), airbagNoneOption);
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.RIGHT_SIDE_AIRBAG), airbagNoneOption);

                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.RIGHT_SIDE_AIRBAG));
                    }
                    break;
                case CheckinId.PASSENGER_AIRBAG:
                    if (currentField.getSelectedOption().equals(airbagNoneOption)) {
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.LEFT_SIDE_AIRBAG), airbagNoneOption);
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.RIGHT_SIDE_AIRBAG), airbagNoneOption);

                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.RIGHT_SIDE_AIRBAG));
                    }
                    break;
                case CheckinId.LEFT_SIDE_AIRBAG:
                    if (currentField.getSelectedOption().equals(airbagNoneOption)) {
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.RIGHT_SIDE_AIRBAG), airbagNoneOption);

                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.RIGHT_SIDE_AIRBAG));
                    }
                    break;

                case CheckinId.RADIO_AUTOMOBILE:
                    // RADIO rule: if RADIO is N/A assign N/A to CD player, CD changer and cassette,
                    // scroll to the next field after cassette
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getRadioNaOption())) {
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.CD_PLAYER),
                                CheckinFieldHelper.getCdPlayerNaOption());
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.CD_CHANGER),
                                CheckinFieldHelper.getCdChangerNaOption());
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.CASSETTE),
                                CheckinFieldHelper.getCasetteNaOption());

                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.CASSETTE));
                    }
                    break;

                case CheckinId.RADIO_MOTORCYCLE:
                    // RADIO rule: if RADIO is N/A assign N/A to CD player and
                    // scroll to the next field
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getRadioNaOption())) {
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.CD_PLAYER),
                                CheckinFieldHelper.getCdPlayerNaOption());

                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.CD_PLAYER));
                    }
                    break;

                case CheckinId.PLATE_CONDITION:
                    // Plate Condition rule: if Plate Condition is marked Destroyed or Not Available,
                    // mark Number of Plates 0 and set focus on Right Rear Door
                    if (currentField.getSelectedOption().equals(
                            CheckinFieldHelper.getPlateConditionDestroyedOption())
                            || currentField.getSelectedOption().equals(
                                    CheckinFieldHelper.getPlateConditionNaOption())) {
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.NUMBER_OF_PLATES),
                                CheckinFieldHelper.getNumPlates0Option());

                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.NUMBER_OF_PLATES));
                    }
                    break;

                case CheckinId.KEYS:
                    // KEYS rule
                    // If Keys is marked as present, mark Make Keys "no" and put focus on Key Fob question
                    // If Keys is marked Missing, switch focus to Make Keys but mark Key Fob as missing,
                    // Engine Status as Can't Test and Run & Drive as No.
                    if (currentField.getSelectedOption().equals(
                            CheckinFieldHelper.getKeysPresentOption())) {
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.MAKE_KEYS),
                                CheckinFieldHelper.getMakeKeysNoOption());
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.KEY_FOB) - 1);
                    }
                    else {
                        if (currentField.getSelectedOption().equals(
                                CheckinFieldHelper.getKeysMissingOption())) {
                            checkinData.setSelectedOptionIfNotSelected(
                                    checkinData.getFieldPosById(CheckinId.KEY_FOB),
                                    CheckinFieldHelper.getKeyFobMissingOption());
                            checkinData.setSelectedOptionIfNotSelected(
                                    checkinData.getFieldPosById(CheckinId.ENGINE_STATUS),
                                    CheckinFieldHelper.getEngineStatusCantTestOption());
                            checkinData.setSelectedOptionIfNotSelected(
                                    checkinData.getFieldPosById(CheckinId.RUN_AND_DRIVE),
                                    CheckinFieldHelper.getRunDriveNoOption());

                            if (checkinData.getFieldById(CheckinId.MAKE_KEYS).hasSelection()) {
                                applyMakeKeysRule();
                                doNextField = false;
                            }
                            else {
                                setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.MAKE_KEYS) - 1);
                            }
                        }
                    }
                    break;

                case CheckinId.MAKE_KEYS:
                    // When Make Keys is answered and Keys is marked Missing: focus should be dropped,
                    // the input frame should be hidden, and Make Keys thru Odometer Status should be displayed
                    if (currentField.hasSelection()
                            && checkinData.getFieldById(CheckinId.KEYS).hasSelection()
                            && checkinData.getFieldById(CheckinId.KEYS).getSelectedOption()
                            .equals(CheckinFieldHelper.getKeysMissingOption()))
                    {
                        applyMakeKeysRule();
                        doNextField = false;
                    }
                    break;

                case CheckinId.HAS_OTHER:
                    // If No is chosen for Has Other, Other should be skipped
                    if (currentField.getSelectedOption().equals(
                            CheckinFieldHelper.getHasOtherNoOption())) {
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.OTHER));
                    }
                    break;

                case CheckinId.HAS_OTHER_OPTIONS:
                    // If No is chosen for Has Other Options, Other Options should be skipped
                    if (currentField.getSelectedOption().equals(
                            CheckinFieldHelper.getHasOtherNoOption())) {
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.OTHER_OPTIONS));
                    }
                    break;

                case CheckinId.ENGINE_STARTS:
                    // If No is chosen for Engine Starts, Run & Drive should be set to No and skipped
                    if (currentField.getSelectedOption()
                            .equals(
                                    CheckinFieldHelper.getEngineStartsNoOption())) {
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.RUN_AND_DRIVE),
                                CheckinFieldHelper.getRunDriveNoOption());
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.RUN_AND_DRIVE));
                    }
                    break;

                case CheckinId.HAS_AM_FM_RADIO:
                    // If No is chosen for Has AM/FM Radio then Has Cassetes, Has Radop Pull-Out, Has
                    // Removable Face
                    // and Has CD Player should be skipped
                    if (currentField.getSelectedOption().equals(
                            CheckinFieldHelper.getHasNoOption())) {
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.HAS_CD_PLAYER));
                    }
                    break;

                case CheckinId.HAS_AC:
                    // If No is chosen for Has A/C then A/C Make, A/C Model and Number of A/C Units
                    // should be skipped
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasNoOption())) {
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.NUMBER_OF_AC_UNIT));
                    }
                    break;

                case CheckinId.HAS_HEAT:
                    // If No is chosen for Has Heat then Heat Type should be skipped
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasNoOption())) {
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.HEAT_TYPE));
                    }
                    break;

                case CheckinId.HAS_REFRIGERATOR:
                    // If No is chosen for Has Refrigerator then Refrigerator Make and Refrigerator
                    // Model
                    // should be skipped
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasNoOption())) {
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.REFRIGERATOR_MODEL));
                    }
                    break;

                case CheckinId.TRAILER_TYPE:
                    // If option different from Other is chosen for Trailer Type, Other Trailer Type
                    // should be skipped
                    if (!currentField.getSelectedOption().equals(
                            CheckinFieldHelper.geTrailerTypeOtherOption())) {
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.OTHER_TRAILER_TYPE));
                    }
                    break;

                case CheckinId.WIDTH_TYPE:
                    // If option different from Other is chosen for Width Type, Other Width Type should
                    // be skipped
                    if (!currentField.getSelectedOption().equals(
                            CheckinFieldHelper.geWidthTypeOtherOption())) {
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.OTHER_WIDTH_TYPE));
                    }
                    break;

                case CheckinId.AXLE_TYPE:
                    // If option different from Other is chosen for Axle Type, Other Axle Type should be
                    // skipped
                    if (!currentField.getSelectedOption().equals(
                            CheckinFieldHelper.geAxleTypeOtherOption())) {
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.OTHER_AXLE_TYPE));
                    }
                    break;

                case CheckinId.HAS_SECOND_ENGINE:
                    // If there is No 2nd engine, all 2nd engine fields should be skipped
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasNoOption())) {
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.ENGINE_2_STARTS));
                    }
                    break;

                case CheckinId.HAS_GENERATOR:
                    // If there is No generator, all generator fields should be skipped
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasNoOption())) {
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.GENERATOR_KILOWATTS));
                    }
                    break;

                case CheckinId.HAS_SONAR:
                    // If there is No sonar, sonar make and model should be skipped
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasNoOption())) {
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.SONAR_MODEL));
                    }
                    break;

                case CheckinId.HAS_RADAR:
                    // If there is No radar, radar make and model should be skipped
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasNoOption())) {
                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.RADAR_MODEL));
                    }
                    break;

                case CheckinId.HAS_TRAILER:
                    // If there is No trailer, all trailer fields should be skipped
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasNoOption())) {
                        setSelectedFieldPos(checkinData
                                .getFieldPosById(CheckinId.NUM_OF_MARINE_TRAILER_AXLES));
                    }
                    break;

                case CheckinId.HAS_VEHICLE_TURBOCHARGER:
                    // If 'Yes' is chosen: set 'Has Vehicle Supercharger' to 'No'; skip 'Has Vehicle
                    // Supercharger'
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasYesOption())) {
                        checkinData.setSelectedOption(
                                checkinData.getFieldPosById(CheckinId.HAS_VEHICLE_SUPERCHARGER),
                                CheckinFieldHelper.getHasNoOption());
                        setSelectedFieldPos(checkinData
                                .getFieldPosById(CheckinId.HAS_VEHICLE_SUPERCHARGER));
                    }
                    break;

                case CheckinId.HAS_VEHICLE_SUPERCHARGER:
                    // If 'Yes' is chosen: set 'Has Vehicle Turbocharger' to 'No'; set focus on "Vehicle
                    // Engine Starts'
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasYesOption())) {
                        checkinData.setSelectedOption(
                                checkinData.getFieldPosById(CheckinId.HAS_VEHICLE_TURBOCHARGER),
                                CheckinFieldHelper.getHasNoOption());
                    }
                    break;

                case CheckinId.HAS_EQUIPMENT_ENGINE:
                    // If 'No' is chosen: skip 'Equipment Engine Number' through 'Equipment Engine
                    // Coolant Level'
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasNoOption())) {
                        setSelectedFieldPos(checkinData
                                .getFieldPosById(CheckinId.EQUIPMENT_ENGINE_COOLANT_LEVEL));
                    }
                    break;

                case CheckinId.HAS_EQUIPMENT_TURBOCHARGER:
                    // If 'Yes' is chosen: set 'Has Equipment Supercharger' to 'No'; skip 'Has Equipment
                    // Supercharger'
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasYesOption())) {
                        checkinData.setSelectedOption(
                                checkinData.getFieldPosById(CheckinId.HAS_EQUIPMENT_SUPERCHARGER),
                                CheckinFieldHelper.getHasNoOption());
                        setSelectedFieldPos(checkinData
                                .getFieldPosById(CheckinId.HAS_EQUIPMENT_SUPERCHARGER));
                    }
                    break;

                case CheckinId.HAS_EQUIPMENT_SUPERCHARGER:
                    // If 'Yes' is chosen: set 'Has Equipment Turbocharger' to 'No'; set focus on
                    // 'Equipment Engine Starts'
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasYesOption())) {
                        checkinData.setSelectedOption(
                                checkinData.getFieldPosById(CheckinId.HAS_EQUIPMENT_TURBOCHARGER),
                                CheckinFieldHelper.getHasNoOption());
                    }
                    break;

                case CheckinId.HAS_OROPS:
                    // If 'Yes' is chosen: set 'Has EROPS' to 'No'; set focus on 'Has Grapple'
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasYesOption())) {
                        checkinData.setSelectedOption(
                                checkinData.getFieldPosById(CheckinId.HAS_EROPS),
                                CheckinFieldHelper.getHasNoOption());
                        setSelectedFieldPos(checkinData
                                .getFieldPosById(CheckinId.HAS_EROPS));
                    }
                    break;

                case CheckinId.HAS_EROPS:
                    // If 'Yes' is chosen: set 'Has OROPS' to 'No'; set focus on 'Has Grapple'
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasYesOption())) {
                        checkinData.setSelectedOption(
                                checkinData.getFieldPosById(CheckinId.HAS_OROPS),
                                CheckinFieldHelper.getHasNoOption());
                    }
                    break;

                case CheckinId.HAS_ALARM:
                    // If 'No' is chosen: set 'Is Alarm Factory Installed' to 'No'; set 'Other Alarm' to
                    // blank; set focus to 'Has AM/FM Radio'
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasNoOption())) {
                        checkinData.setSelectedOption(
                                checkinData.getFieldPosById(CheckinId.IS_ALARM_FACTORY_INSTALLED),
                                CheckinFieldHelper.getHasNoOption());

                        checkinData.setEnteredValue(checkinData.getFieldPosById(CheckinId.OTHER_ALARM), "");

                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.OTHER_ALARM));
                    }
                    break;

                case CheckinId.IS_ALARM_FACTORY_INSTALLED:
                    // If 'Yes' is chosen: set 'Other Alarm' to blank; ; set focus to 'Has AM/FM Radio'
                    if (currentField.getSelectedOption().equals(CheckinFieldHelper.getHasYesOption())) {
                        checkinData.setEnteredValue(checkinData.getFieldPosById(CheckinId.OTHER_ALARM), "");

                        setSelectedFieldPos(checkinData.getFieldPosById(CheckinId.OTHER_ALARM));
                    }
                    break;

                case CheckinId.DRIVER_AIRBAG_OTHER:
                    // If 'None' is chosen: set all following airbag questions to 'None'; set focus on
                    // field following last airbag question

                    if (currentField.getSelectedOption().equals(airbagNoneOption)) {
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.PASSENGER_AIRBAG_OTHER),
                                airbagNoneOption);
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.LEFT_SIDE_AIRBAG_OTHER),
                                airbagNoneOption);
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.RIGHT_SIDE_AIRBAG_OTHER),
                                airbagNoneOption);

                        setSelectedFieldPos(checkinData
                                .getFieldPosById(CheckinId.RIGHT_SIDE_AIRBAG_OTHER));
                    }
                    break;
                case CheckinId.PASSENGER_AIRBAG_OTHER:
                    // If 'None' is chosen: set all following airbag questions to 'None'; set focus on
                    // field following last airbag question

                    if (currentField.getSelectedOption().equals(airbagNoneOption)) {
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.LEFT_SIDE_AIRBAG_OTHER),
                                airbagNoneOption);
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.RIGHT_SIDE_AIRBAG_OTHER),
                                airbagNoneOption);

                        setSelectedFieldPos(checkinData
                                .getFieldPosById(CheckinId.RIGHT_SIDE_AIRBAG_OTHER));
                    }
                    break;
                case CheckinId.LEFT_SIDE_AIRBAG_OTHER:
                    // If 'None' is chosen: set all following airbag questions to 'None'; set focus on
                    // field following last airbag question

                    if (currentField.getSelectedOption().equals(airbagNoneOption)) {
                        checkinData.setSelectedOptionIfNotSelected(
                                checkinData.getFieldPosById(CheckinId.RIGHT_SIDE_AIRBAG_OTHER),
                                airbagNoneOption);

                        setSelectedFieldPos(checkinData
                                .getFieldPosById(CheckinId.RIGHT_SIDE_AIRBAG_OTHER));
                    }
                    break;

                default:
                    break;
            }
        }
        return doNextField;
    }

    private void applyMakeKeysRule() {
        if (getActivity() != null) {
            goToField(getSessionData().getCheckinData().getFieldPosById(CheckinId.KEY_FOB));

            mInputFrame.setVisibility(View.GONE);
            clearListFocus();
        }
    }

    private OnYardFieldOption getLossTypeOptionByValue(ContentResolver contentResolver, String value) {
        Cursor queryResult = null;
        try {
            queryResult = contentResolver.query(OnYardContract.LossType.CONTENT_URI, null,
                    OnYardContract.LossType.COLUMN_NAME_CODE + "=?", new String[] { value }, null);
            if (queryResult == null) {
                return null;
            }
            if (queryResult.moveToFirst()) {
                final OnYardFieldOption option = new OnYardFieldOption(
                        queryResult.getString(queryResult
                                .getColumnIndex(OnYardContract.LossType.COLUMN_NAME_DESCRIPTION)),
                                value);
                return option;
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

    @Override
    protected int getActionBarIconId() {
        return R.drawable.ic_action_paste;
    }

    @Override
    protected String getActionBarTitle() {
        return "Check-In";
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
        return R.color.checkin_green;
    }

    @Override
    public boolean isSaveAllowed() {
        try {
            if (getActivity() != null) {
                return getSessionData().isCheckinCommitAllowed();
            }
            else {
                return false;
            }
        }
        catch (final Exception e) {
            logError(e);
            showFatalErrorDialog(ErrorMessage.SAVE);
            return false;
        }
    }

    @Override
    protected ListView getListView() {
        return mCheckinList;
    }

    @Override
    protected FrameLayout getInputFrame() {
        return mInputFrame;
    }

    @Override
    protected boolean shouldDisplayList() {
        if (getActivity() != null) {
            return getSessionData().getVehicleInfo().isCheckinEligible();
        }
        else {
            return false;
        }
    }

    @Override
    protected ArrayList<? extends OnYardField> getFieldList() {
        if (getActivity() != null) {
            return getSessionData().getCheckinData().getCheckinFields();
        }
        else {
            return new ArrayList<CheckinField>();
        }
    }

    @Override
    protected OnYardFieldArrayAdapter getListAdapter() {
        final Activity activity = getActivity();
        if (activity != null) {
            return new CheckinArrayAdapter(activity, getFieldList());
        }
        else {
            return null;
        }
    }

    @Override
    protected int getDetailsFrameId() {
        return R.id.stock_checkin_vehicle_details_frame;
    }

    @Override
    protected int getInputFrameId() {
        return mInputFrame.getId();
    }

    @Override
    protected void prepopulateFields() {
        final Activity activity = getActivity();
        if (activity != null) {
            final VehicleInfo vehicleInfo = getSessionData().getVehicleInfo();
            final CheckinData checkinData = getSessionData().getCheckinData();

            // prepopulate Loss Type
            final String lossType = vehicleInfo.getLossType();
            if (!DataHelper.isNullOrEmpty(lossType)) {
                final int lossTypePosition = checkinData.getFieldPosById(CheckinId.LOSS_TYPE);
                final OnYardFieldOption lossOption = getLossTypeOptionByValue(
                        activity.getContentResolver(), lossType);
                if (lossOption != null) {
                    checkinData.setSelectedOption(lossTypePosition, lossOption);
                }
            }

            // salvage type
            final String salvageType = String.valueOf(vehicleInfo.getSalvageType());
            if (!DataHelper.isNullOrEmpty(salvageType)) {
                final int salvageTypePosition = checkinData.getFieldPosById(CheckinId.SALVAGE_TYPE);
                final OnYardFieldOption salvageTypeOption = CheckinFieldHelper
                        .getSalvageTypeOption(activity, salvageType);
                if (salvageTypeOption != null) {
                    checkinData.setSelectedOption(salvageTypePosition, salvageTypeOption);
                }
            }
        }
    }

    @Override
    protected OnYardFieldOption getGuessedOption(int selectedPos) {
        if (getActivity() != null) {
            final CheckinData checkinData = getSessionData().getCheckinData();
            return checkinData.getGuessedOption(checkinData.getFieldAt(getListSelectedPos()).getId());
        }
        else {
            return null;
        }
    }

    @Override
    public boolean isVerifyRequired() {
        return false;
    }

    @Override
    protected InputFragment getInputFragment() {
        return new CheckinInputFragment();
    }
}
