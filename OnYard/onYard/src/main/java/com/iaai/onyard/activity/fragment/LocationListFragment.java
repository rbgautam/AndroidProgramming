package com.iaai.onyard.activity.fragment;

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
import com.iaai.onyard.adapter.LocationSearchArrayAdapter;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.event.MoveToSaleVehiclesRetrievedEvent;
import com.iaai.onyard.task.GetPendingMoveToSaleTask;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.squareup.otto.Subscribe;

public class LocationListFragment extends SearchListFragment {

    @InjectView(R.id.location_list)
    ListView mLocationListView;
    @InjectView(R.id.location_list_title)
    TextView mTxtTitle;
    @InjectView(R.id.location_list_title_line)
    View mTitleLine;

    private GetPendingMoveToSaleTask mGetPendingMoveToSaleTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_location_list, container, false);
            ButterKnife.inject(this, view);

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return null;
        }
    }

    @Override
    protected void repopulateList() {
        super.repopulateList();

        final Activity activity = getActivity();
        if (activity != null) {
            mGetPendingMoveToSaleTask = new GetPendingMoveToSaleTask();
            mGetPendingMoveToSaleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    activity.getApplicationContext(), getEventBus());
        }
    }

    @Subscribe
    public void onSetLocationVehicleEvent(MoveToSaleVehiclesRetrievedEvent event) {
        try {
            final Activity activity = getActivity();
            if (activity != null) {
                final LocationSearchArrayAdapter adapter = new LocationSearchArrayAdapter(activity,
                        event.getMoveToSaleList());
                mLocationListView.setAdapter(adapter);
                mLocationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            if (getActivity() != null && !mIsItemLoading) {
                                mIsItemLoading = true;
                                showProgressDialog();
                                final VehicleInfo vehicle = (VehicleInfo) parent
                                        .getAdapter().getItem(position);
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
        catch (final Exception e) {
            logWarning(e);
        }
    }

    @Override
    protected void cancelAsyncTask() {
        if (mGetPendingMoveToSaleTask != null) {
            mGetPendingMoveToSaleTask.cancel(true);
        }
    }

    @Override
    protected void setListVisibility() {
        try {
            if (mLocationListView.getCount() > 0) {
                mLocationListView.setVisibility(View.VISIBLE);
                mTxtTitle.setVisibility(View.VISIBLE);
                mTitleLine.setVisibility(View.VISIBLE);
            }
            else {
                mLocationListView.setVisibility(View.GONE);
                mTxtTitle.setVisibility(View.GONE);
                mTitleLine.setVisibility(View.GONE);
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }
}
