package com.iaai.onyard.activity.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.iaai.onyard.application.OnYard.Broadcast;
import com.iaai.onyard.application.OnYard.ErrorMessage;


public abstract class SearchListFragment extends BaseFragment {

    protected boolean mIsItemLoading;

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
            repopulateList();

            mIsItemLoading = false;
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
            cancelAsyncTask();
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

    protected abstract void cancelAsyncTask();

    protected void repopulateList() {
        cancelAsyncTask();
    }

    protected abstract void setListVisibility();

    private final BroadcastReceiver mSyncReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                repopulateList();
            }
            catch (final Exception e) {
                logWarning(e);
            }
        }
    };
}
