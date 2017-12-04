package com.iaai.onyard.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.application.OnYard.SearchMode;
import com.iaai.onyard.utility.AuthenticationHelper;

public class SearchGeneralPageFragment extends PageFragment {

    private VehicleSearchFragment mSearchFragment;
    private SyncInfoFragment mSyncInfoFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_search_general, container, false);
            ButterKnife.inject(this, view);

            initFragments();

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.INITIALIZATION);
            logError(e);
            return container;
        }
    }

    private void initFragments() {
        mSearchFragment = VehicleSearchFragment.newInstance(SearchMode.GENERAL);
        getChildFragmentManager().beginTransaction()
        .replace(R.id.vehicle_search_frame, mSearchFragment).commit();

        mSyncInfoFragment = new SyncInfoFragment();
        getChildFragmentManager().beginTransaction()
        .replace(R.id.sync_info_frame, mSyncInfoFragment).commit();
    }

    @Override
    public void onBecomeCurrentPage() {}

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
        return mSearchFragment;
    }

    @Override
    protected String getActionBarSubTitle() {
        try {
            final Activity activity = getActivity();
            if (activity != null) {
                if (AuthenticationHelper.isAnyUserLoggedIn(activity.getContentResolver())) {
                    return AuthenticationHelper.getLoggedInUser(activity.getContentResolver());
                }
                else {
                    return "Touch here to start";
                }
            }
            else {
                return "";
            }
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logWarning(e);
            return null;
        }
    }

    @Override
    protected boolean isActionBarTitleBold() {
        return true;
    }

    @Override
    protected boolean isActionBarSubtitleBold() {
        return false;
    }
}
