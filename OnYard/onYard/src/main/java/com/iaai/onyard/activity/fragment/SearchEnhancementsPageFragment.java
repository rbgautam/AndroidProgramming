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

public class SearchEnhancementsPageFragment extends PageFragment {

    private VehicleSearchFragment mSearchFragment;
    private EnhancementListFragment mEnhancementsListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_search_enhancements, container,
                    false);

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
        mSearchFragment = VehicleSearchFragment.newInstance(SearchMode.ENHANCEMENT);
        getChildFragmentManager().beginTransaction()
        .add(R.id.enhancements_fragment_container, mSearchFragment)
        .commit();

        mEnhancementsListFragment = new EnhancementListFragment();
        getChildFragmentManager().beginTransaction()
        .add(R.id.enhancements_fragment_container, mEnhancementsListFragment).commit();
    }

    @Override
    public void onBecomeCurrentPage() {
    }

    @Override
    protected int getActionBarIconId() {
        return R.drawable.ic_action_enhancement;
    }

    @Override
    protected String getActionBarTitle() {
        return "Enhancements";
    }

    @Override
    protected int getActionBarColorId() {
        return R.color.enhancements_purple;
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
