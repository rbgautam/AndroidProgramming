package com.iaai.onyard.activity.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.adapter.AuctionDateArrayAdapter;
import com.iaai.onyard.application.OnYard.Broadcast;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.application.OnYard.SearchMode;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.event.AuctionSchedulesRetrievedEvent;
import com.iaai.onyard.task.GetAuctionSchedulesTask;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyardproviderapi.classes.AuctionScheduleInfo;
import com.squareup.otto.Subscribe;

public class SearchSetSalePageFragment extends PageFragment {

    @InjectView(R.id.auction_date_spinner)
    Spinner mAuctionDateSpinner;
    @InjectView(R.id.auction_date_layout)
    LinearLayout mAuctionDateLayout;

    private VehicleSearchFragment mSearchFragment;
    private SetSaleAuctionListFragment mSetSaleAuctionList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_search_setsale, container,
                    false);
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
        mSearchFragment = VehicleSearchFragment.newInstance(SearchMode.SETSALE);
        getChildFragmentManager().beginTransaction()
        .replace(R.id.search_setsale_search_frame, mSearchFragment).commit();

        mSetSaleAuctionList = new SetSaleAuctionListFragment();
        getChildFragmentManager().beginTransaction()
        .add(R.id.setsaleAction_fragment_container, mSetSaleAuctionList).commit();

    }

    @Override
    public void onResume() {
        try {
            super.onResume();
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
        }

        try {
            final Activity activity = getActivity();
            if (activity != null) {
                activity.registerReceiver(mSyncReceiver,
                        new IntentFilter(Broadcast.SYNC_COMPLETED));
            }
            repopulateSpinner();
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    @Override
    public void onPause() {
        try {
            final Activity activity = getActivity();
            if (activity != null) {
                activity.unregisterReceiver(mSyncReceiver);
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }

        try {
            super.onPause();
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
        }
    }

    protected void repopulateSpinner() {
        final Activity activity = getActivity();
        if (activity != null) {
            new GetAuctionSchedulesTask().execute(activity.getApplicationContext(), getEventBus());
        }
    }

    @Subscribe
    public void onAuctionSchedulesRetrieved(AuctionSchedulesRetrievedEvent event) {
        try {
            final Activity activity = getActivity();
            if (activity != null) {
                final AuctionDateArrayAdapter adapter = new AuctionDateArrayAdapter(activity,
                        event.getAuctionScheduleList());
                final OnYardPreferences preferences = new OnYardPreferences(activity);
                final Long prevSelectedAuctionDate = preferences.getSelectedAuctionDate();
                mAuctionDateSpinner.setAdapter(adapter);
                mAuctionDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        final AuctionScheduleInfo auction = (AuctionScheduleInfo) parent
                                .getItemAtPosition(pos);

                        final Long auctionDate = auction.getAuctionDate();
                        if (auctionDate != null && !auctionDate.equals(prevSelectedAuctionDate)) {
                            preferences.resetSetSalePreferences();
                        }
                        preferences.setSelectedAuctionDatePref(auctionDate);

                        mSetSaleAuctionList.repopulateList();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        preferences.setSelectedAuctionDatePref(0L);
                    }
                });

                if (prevSelectedAuctionDate != 0L
                        && prevSelectedAuctionDate > DataHelper.getUnixUtcTimeStamp()) {
                    mAuctionDateSpinner
                    .setSelection(adapter.getAuctionDatePos(prevSelectedAuctionDate));
                    preferences.setSelectedAuctionDatePref(prevSelectedAuctionDate);
                }

                setSpinnerVisibility();
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    protected void setSpinnerVisibility() {
        try {
            if (mAuctionDateSpinner.getCount() > 0) {
                mAuctionDateLayout.setVisibility(View.VISIBLE);
            }
            else {
                mAuctionDateLayout.setVisibility(View.GONE);
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    private final BroadcastReceiver mSyncReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                repopulateSpinner();
            }
            catch (final Exception e) {
                logWarning(e);
            }
        }
    };

    @Override
    public void onBecomeCurrentPage() {}

    @Override
    protected int getActionBarIconId() {
        return R.drawable.ic_action_go_to_today;
    }

    @Override
    protected String getActionBarTitle() {
        return "Set Sale";
    }

    @Override
    protected int getActionBarColorId() {
        return R.color.setsale_orange;
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
