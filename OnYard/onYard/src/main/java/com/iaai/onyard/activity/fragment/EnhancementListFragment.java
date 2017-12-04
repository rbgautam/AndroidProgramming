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
import com.iaai.onyard.adapter.EnhancementSearchArrayAdapter;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.event.SalvageEnhancementsRetrievedEvent;
import com.iaai.onyard.task.GetPendingEnhancementsTask;
import com.iaai.onyardproviderapi.classes.SalvageEnhancementInfo;
import com.squareup.otto.Subscribe;


public class EnhancementListFragment extends SearchListFragment {

    @InjectView(R.id.enhancements_search_list)
    ListView mEnhancementsListView;
    @InjectView(R.id.enhancements_list_title)
    TextView mTxtTitle;
    @InjectView(R.id.enhancements_list_title_line)
    View mTitleLine;

    private GetPendingEnhancementsTask mGetEnhancementsTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_enhancements_list, container,
                    false);
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
    public void onEnhancementsRetrieved(SalvageEnhancementsRetrievedEvent event) {
        try {
            final Activity activity = getActivity();
            if (activity != null) {
                final EnhancementSearchArrayAdapter adapter = new EnhancementSearchArrayAdapter(
                        activity, event.getSalvageEnhancementsList());
                mEnhancementsListView.setAdapter(adapter);
                mEnhancementsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            if (getActivity() != null && !mIsItemLoading) {
                                mIsItemLoading = true;
                                showProgressDialog();
                                final SalvageEnhancementInfo salEnhancement = (SalvageEnhancementInfo) parent
                                        .getAdapter().getItem(position);
                                createSessionData(salEnhancement.getStockNumber());
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
            mGetEnhancementsTask = new GetPendingEnhancementsTask();
            mGetEnhancementsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    activity.getApplicationContext(), getEventBus());
        }
    }

    @Override
    protected void setListVisibility() {
        try {
            if (mEnhancementsListView.getCount() > 0) {
                mEnhancementsListView.setVisibility(View.VISIBLE);
                mTxtTitle.setVisibility(View.VISIBLE);
                mTitleLine.setVisibility(View.VISIBLE);
            }
            else {
                mEnhancementsListView.setVisibility(View.GONE);
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
        if (mGetEnhancementsTask != null) {
            mGetEnhancementsTask.cancel(true);
        }
    }
}
