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
import com.iaai.onyard.adapter.ReshootArrayAdapter;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.event.ReshootVehiclesRetrievedEvent;
import com.iaai.onyard.task.GetReshootVehiclesTask;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.squareup.otto.Subscribe;


public class ReshootListFragment extends SearchListFragment {

    @InjectView(R.id.reshoot_list)
    ListView mReshootListView;
    @InjectView(R.id.reshoot_list_title)
    TextView mTxtTitle;
    @InjectView(R.id.reshoot_list_title_line)
    View mTitleLine;

    private GetReshootVehiclesTask mGetReshootsTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_reshoot_list, container, false);
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
    public void onReshootVehiclesRetrieved(ReshootVehiclesRetrievedEvent event) {
        try {
            final Activity activity = getActivity();
            if (activity != null) {
                final ReshootArrayAdapter adapter = new ReshootArrayAdapter(activity,
                        event.getReshootList());
                mReshootListView.setAdapter(adapter);
                mReshootListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            if (getActivity() != null && !mIsItemLoading) {
                                mIsItemLoading = true;
                                showProgressDialog();
                                final VehicleInfo vehicle = (VehicleInfo) parent.getAdapter().getItem(position);

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
    protected void repopulateList() {
        super.repopulateList();

        final Activity activity = getActivity();
        if (activity != null) {
            mGetReshootsTask = new GetReshootVehiclesTask();
            mGetReshootsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    activity.getApplicationContext(), getEventBus());
        }
    }

    @Override
    protected void setListVisibility() {
        try {
            if (mReshootListView.getCount() > 0) {
                mReshootListView.setVisibility(View.VISIBLE);
                mTxtTitle.setVisibility(View.VISIBLE);
                mTitleLine.setVisibility(View.VISIBLE);
            }
            else {
                mReshootListView.setVisibility(View.GONE);
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
        if (mGetReshootsTask != null) {
            mGetReshootsTask.cancel(true);
        }
    }
}
