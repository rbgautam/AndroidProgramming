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
import com.iaai.onyard.adapter.AuctionSearchArrayAdapter;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.event.SetSaleAuctionVehiclesRetrievedEvent;
import com.iaai.onyard.task.GetAuctionVehiclesTask;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.squareup.otto.Subscribe;

public class SetSaleAuctionListFragment extends SearchListFragment {

    @InjectView(R.id.setsale_search_list)
    ListView mSetSaleListView;
    @InjectView(R.id.setsale_list_title)
    TextView mTxtTitle;
    @InjectView(R.id.setsale_list_title_line)
    View mTitleLine;

    private GetAuctionVehiclesTask mGetVehiclesTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_setsale_list, container, false);
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
    public void onSetSaleAuctionVehiclesRetrieved(SetSaleAuctionVehiclesRetrievedEvent event) {
        try {
            final Activity activity = getActivity();
            if (activity != null) {
                final AuctionSearchArrayAdapter adapter = new AuctionSearchArrayAdapter(activity,
                        event.getAuctionVehiclesList());
                mSetSaleListView.setAdapter(adapter);
                mSetSaleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
        catch (final Exception e) {
            logWarning(e);
        }
    }

    @Override
    protected void repopulateList() {
        super.repopulateList();

        final Activity activity = getActivity();
        if (activity != null) {
            mSetSaleListView.setAdapter(null);
            mGetVehiclesTask = new GetAuctionVehiclesTask();
            mGetVehiclesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    activity.getApplicationContext(), getEventBus(),
                    new OnYardPreferences(activity)
            .getSelectedAuctionDate());
        }
    }

    @Override
    protected void setListVisibility() {
        try {
            if (mSetSaleListView.getCount() > 0) {
                mSetSaleListView.setVisibility(View.VISIBLE);
                mTxtTitle.setVisibility(View.VISIBLE);
                mTitleLine.setVisibility(View.VISIBLE);
            }
            else {
                mSetSaleListView.setVisibility(View.GONE);
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
