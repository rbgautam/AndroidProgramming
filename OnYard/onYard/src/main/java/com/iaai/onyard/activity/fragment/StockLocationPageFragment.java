package com.iaai.onyard.activity.fragment;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.event.SaveConditionsChangedEvent;
import com.iaai.onyard.event.SessionDataCreatedEvent;
import com.iaai.onyard.filter.InputFilterAlphaNumeric;
import com.iaai.onyard.filter.InputFilterMinMax;
import com.squareup.otto.Subscribe;

public class StockLocationPageFragment extends StockPageFragment {

    @InjectView(R.id.txt_new_aisle)
    EditText mNewAisle;
    @InjectView(R.id.txt_new_stall)
    EditText mNewStall;

    private VehicleDetailsFragment mDetailsFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_stock_location, container, false);
            ButterKnife.inject(this, view);

            initFragments();
            initViews();

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return container;
        }
    }

    private void initFragments() {
        mDetailsFragment = new VehicleDetailsFragment();
        getChildFragmentManager().beginTransaction()
        .replace(R.id.stock_location_vehicle_details_frame, mDetailsFragment).commit();
    }

    private void initViews() {
        mNewAisle.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3),
                new InputFilterAlphaNumeric() });
        mNewStall.setFilters(new InputFilter[] { new InputFilterMinMax(1, 32767) });

        final View.OnFocusChangeListener collapseListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                try {
                    if (hasFocus) {
                        mDetailsFragment.collapseDetails();
                    }
                }
                catch (final Exception e) {
                    logWarning(e);
                }
            }
        };
        mNewAisle.setOnFocusChangeListener(collapseListener);
        mNewStall.setOnFocusChangeListener(collapseListener);

        final TextWatcher aisleTextWatcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (getActivity() != null) {
                        final String aisleText = s.toString().trim().toUpperCase(Locale.US);
                        if (!aisleText.isEmpty()) {
                            getSessionData().getLocationData().setNewAisle(aisleText);
                        }
                        else {
                            getSessionData().getLocationData().setNewAisle(null);
                        }

                        getEventBus().post(new SaveConditionsChangedEvent());
                    }
                }
                catch (final Exception e) {
                    logWarning(e);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };

        final TextWatcher stallTextWatcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (getActivity() != null) {
                        final String stallText = s.toString().trim();
                        if (!stallText.isEmpty() && !stallText.equals("0")) {
                            getSessionData().getLocationData().setNewStall(Integer.parseInt(stallText));
                        }
                        else {
                            getSessionData().getLocationData().setNewStall(null);
                        }

                        getEventBus().post(new SaveConditionsChangedEvent());
                    }
                }
                catch (final Exception e) {
                    logWarning(e);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
        mNewAisle.addTextChangedListener(aisleTextWatcher);
        mNewStall.addTextChangedListener(stallTextWatcher);
    }

    @Subscribe
    public void onSessionDataCreated(SessionDataCreatedEvent event) {
        try {
            if (getActivity() != null) {
                if (getSessionData().wasLocationCommitted()) {
                    mNewAisle.setText("");
                    mNewStall.setText("");

                    getEventBus().post(new SaveConditionsChangedEvent());
                }
            }
        }
        catch (final Exception e) {
            logError(e);
            showErrorDialog(ErrorMessage.SAVE);
        }
    }

    @Override
    protected int getActionBarIconId() {
        return R.drawable.ic_action_place;
    }

    @Override
    protected String getActionBarTitle() {
        return "Location";
    }

    @Override
    protected int getActionBarColorId() {
        return R.color.location_yellow;
    }

    @Override
    public Fragment getOnActivityResultFragment() {
        return null;
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
    public void onBecomeCurrentPage() {}

    @Override
    public boolean isSaveAllowed() {
        try {
            if (getActivity() != null) {
                return getSessionData().isLocationCommitAllowed();
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
    public boolean isVerifyRequired() {
        return false;
    }
}
