package com.iaai.onyard.activity.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.adapter.CheckinSearchArrayAdapter;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.event.CheckinVehiclesRetrievedEvent;
import com.iaai.onyard.event.HolidaysRetrievedEvent;
import com.iaai.onyard.task.GetCheckinVehiclesTask;
import com.iaai.onyard.task.GetHolidaysTask;
import com.iaai.onyardproviderapi.classes.HolidayInfo;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.squareup.otto.Subscribe;

public class CheckinListFragment extends SearchListFragment {

    @InjectView(R.id.checkin_search_list)
    ListView mCheckinListView;
    @InjectView(R.id.checkin_list_title)
    TextView mTxtTitle;
    @InjectView(R.id.checkin_list_title_line)
    View mTitleLine;

    boolean mCheckinListRetrieved = false;
    boolean mHolidayListRetrieved = false;
    private GetCheckinVehiclesTask mGetVehiclesTask;

    ArrayList<VehicleInfo> mVehicleList = new ArrayList<VehicleInfo>();
    ArrayList<HolidayInfo> mHolidayList = new ArrayList<HolidayInfo>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_checkin_list, container, false);
            ButterKnife.inject(this, view);

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return null;
        }
    }

    @Subscribe
    public void onCheckinVehiclesRetrieved(CheckinVehiclesRetrievedEvent event) {
        try {
            mVehicleList = event.getCheckinList();
            mCheckinListRetrieved = true;

            if (isAllDataRetrieved()) {
                createSearchArrayAdapter();
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    @Subscribe
    public void onHolidaysRetrieved(HolidaysRetrievedEvent event) {
        try {
            mHolidayList = event.getHolidayList();
            mHolidayListRetrieved = true;

            if (isAllDataRetrieved()) {
                createSearchArrayAdapter();
            }
            else {
                repopulateList();
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    private boolean isAllDataRetrieved() {
        return mHolidayListRetrieved && mCheckinListRetrieved;
    }

    private void createSearchArrayAdapter() {
        final Activity activity = getActivity();
        if (activity != null) {
            final CheckinSearchArrayAdapter adapter = new CheckinSearchArrayAdapter(activity,
                    mVehicleList, mHolidayList);

            mCheckinListView.setAdapter(adapter);
            mCheckinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        if (getActivity() != null && !mIsItemLoading) {
                            mIsItemLoading = true;
                            showProgressDialog();
                            final VehicleInfo vehicle = (VehicleInfo) parent.getAdapter().getItem(
                                    position);

                            createSessionData(vehicle.getStockNumber());
                        }
                    }
                    catch (final Exception e) {
                        showFatalErrorDialog(ErrorMessage.VEHICLE_DATA_LOAD);
                        logWarning(e);
                    }
                }
            });

            setListVisibility();
        }
    }

    @Override
    protected void repopulateList() {
        super.repopulateList();

        final Activity activity = getActivity();
        if (activity != null) {
            if (mHolidayListRetrieved) {
                mGetVehiclesTask = new GetCheckinVehiclesTask();
                mGetVehiclesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        activity.getApplicationContext(), getEventBus(), mHolidayList);
            }
            else {
                new GetHolidaysTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        activity.getApplicationContext(), getEventBus());
            }
        }
    }

    @Override
    protected void setListVisibility() {
        try {
            if (mCheckinListView.getCount() > 0) {
                mCheckinListView.setVisibility(View.VISIBLE);
                mTxtTitle.setVisibility(View.VISIBLE);
                mTitleLine.setVisibility(View.VISIBLE);
            }
            else {
                mCheckinListView.setVisibility(View.GONE);
                mTxtTitle.setVisibility(View.GONE);
                mTitleLine.setVisibility(View.GONE);
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    @Override
    protected void cancelAsyncTask() {
        if (mGetVehiclesTask != null) {
            mGetVehiclesTask.cancel(true);
        }
    }
}
