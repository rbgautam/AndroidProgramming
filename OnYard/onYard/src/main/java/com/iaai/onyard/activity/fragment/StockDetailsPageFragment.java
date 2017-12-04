package com.iaai.onyard.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard.ErrorMessage;

public class StockDetailsPageFragment extends StockPageFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_stock_details, container, false);

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return container;
        }
    }

    @Override
    protected int getActionBarIconId() {
        return R.drawable.ic_onyard_logo;
    }

    @Override
    protected String getActionBarTitle() {
        return "OnYard";
    }

    @Override
    protected int getActionBarColorId() {
        return R.color.iaa_red;
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
        return false;
    }

    @Override
    public boolean isVerifyRequired() {
        return false;
    }
}
