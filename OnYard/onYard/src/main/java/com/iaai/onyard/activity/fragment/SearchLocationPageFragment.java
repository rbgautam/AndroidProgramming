package com.iaai.onyard.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.application.OnYard.SearchMode;
import com.iaai.onyard.utility.AuthenticationHelper;

public class SearchLocationPageFragment extends PageFragment {

    private VehicleSearchFragment mSearchFragment;

    // private LocationListFragment mLocationListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_search_location, container, false);

            initFragments();

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return container;
        }
    }

    private void initFragments() {
        mSearchFragment = VehicleSearchFragment.newInstance(SearchMode.LOCATION);
        getChildFragmentManager().beginTransaction()
        .add(R.id.location_fragment_container, mSearchFragment).commit();

        // mLocationListFragment = new LocationListFragment();
        // getChildFragmentManager().beginTransaction()
        // .add(R.id.location_fragment_container, mLocationListFragment).commit();
    }

    @Override
    public void onBecomeCurrentPage() {}

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
        return mSearchFragment;
    }

    @Override
    protected String getActionBarSubTitle() {
        try {
            final Activity activity = getActivity();
            if (activity != null) {
                return AuthenticationHelper.getLoggedInUser(activity.getContentResolver());
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
    protected boolean isActionBarTitleBold() {
        return true;
    }

    @Override
    protected boolean isActionBarSubtitleBold() {
        return false;
    }
}
