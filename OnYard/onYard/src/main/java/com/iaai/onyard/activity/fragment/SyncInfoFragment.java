package com.iaai.onyard.activity.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard.Broadcast;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.event.ConnectivityCheckedEvent;
import com.iaai.onyard.sync.SyncHelper;
import com.iaai.onyard.task.CheckConnectivityTask;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.squareup.otto.Subscribe;


public class SyncInfoFragment extends BaseFragment {

    private static final int MAX_UPDATE_FREQUENCY_MS = 500;

    @InjectView(R.id.sync_last_updated)
    TextView mTxtLastSync;

    @InjectView(R.id.sync_status)
    TextView mTxtSyncStatus;

    @InjectView(R.id.sync_images_pending)
    TextView mTxtSyncImagesPending;

    @InjectView(R.id.sync_images_delay)
    TextView mTxtSyncImagesDelay;

    @InjectView(R.id.sync_checkin_pending)
    TextView mTxtCheckinPending;

    @InjectView(R.id.sync_enhancement_pending)
    TextView mTxtEnhancementPending;

    @InjectView(R.id.sync_location_pending)
    TextView mTxtLocationPending;

    @InjectView(R.id.sync_setsale_pending)
    TextView mTxtSetSalePending;

    @InjectView(R.id.sync_button)
    Button mBtnSyncNow;

    private long mSyncInfoLastUpdateTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_sync_info, container, false);
            ButterKnife.inject(this, view);

            mBtnSyncNow.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        final Activity activity = getActivity();
                        if (activity != null) {
                            new CheckConnectivityTask().execute(activity.getApplicationContext(),
                                    getEventBus());
                        }
                    }
                    catch (final Exception e) {
                        logWarning(e);
                    }
                }
            });

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return container;
        }
    }

    @Override
    public void onResume() {
        try {
            super.onResume();
            final Activity activity = getActivity();
            if (activity != null) {
                activity.registerReceiver(mSyncCompletedReceiver,
                        new IntentFilter(Broadcast.SYNC_COMPLETED));
                activity.registerReceiver(mUpdateSyncInfoReceiver,
                        new IntentFilter(Broadcast.UPDATE_SYNC_INFO));
            }
            updateSyncInfo("", false);
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
        }
    }

    @Override
    public void onPause() {
        try {
            final Activity activity = getActivity();
            if (activity != null) {
                activity.unregisterReceiver(mSyncCompletedReceiver);
                activity.unregisterReceiver(mUpdateSyncInfoReceiver);
            }
            super.onPause();
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
        }
    }

    private void updateSyncInfo(String syncStat, boolean forceUpdate) {
        mTxtSyncStatus.setText(syncStat);

        final long currentTime = System.currentTimeMillis();
        final Activity activity = getActivity();

        if (activity != null) {
            if (forceUpdate || currentTime - mSyncInfoLastUpdateTime > MAX_UPDATE_FREQUENCY_MS) {
                mSyncInfoLastUpdateTime = currentTime;

                String mTxt = getResources().getString(R.string.time_since_last_sync)
                        + SyncHelper.getTimeSinceLastSync(activity.getContentResolver());
                mTxtLastSync.setText(mTxt);

                mTxt = SyncHelper.getPendingSyncCount(activity.getContentResolver(),
                        OnYardContract.IMAGER_APP_ID)
                        + getResources().getString(R.string.images_pending_sync);
                mTxtSyncImagesPending.setText(mTxt);

                if (!SyncHelper.isInSyncWindow(activity.getContentResolver(),
                        DataHelper.getBranchTimeZone(activity))) {
                    final String start = SyncHelper.getNextSyncWindowStart(activity);
                    mTxt = getResources().getString(R.string.images_sync_delay) + start;
                    mTxtSyncImagesDelay.setText(mTxt);
                    mTxtSyncImagesDelay.setTextColor(Color.RED);
                    mTxtSyncImagesDelay.setVisibility(View.VISIBLE);
                }
                else {
                    mTxtSyncImagesDelay.setVisibility(View.GONE);
                }

                mTxt = SyncHelper.getPendingSyncCount(activity.getContentResolver(),
                        OnYardContract.CHECKIN_APP_ID)
                        + getResources().getString(R.string.checkins_pending_sync);
                mTxtCheckinPending.setText(mTxt);

                mTxt = SyncHelper.getPendingSyncCount(activity.getContentResolver(),
                        OnYardContract.ENHANCEMENT_APP_ID)
                        + getResources().getString(R.string.enhancements_pending_sync);
                mTxtEnhancementPending.setText(mTxt);

                mTxt = SyncHelper.getPendingSyncCount(activity.getContentResolver(),
                        OnYardContract.LOCATION_APP_ID)
                        + getResources().getString(R.string.locations_pending_sync);
                mTxtLocationPending.setText(mTxt);

                mTxt = SyncHelper.getPendingSyncCount(activity.getContentResolver(),
                        OnYardContract.SETSALE_APP_ID)
                        + getResources().getString(R.string.setsales_pending_sync);
                mTxtSetSalePending.setText(mTxt);
            }

            if (SyncHelper.isCurrentlySyncing(activity)) {
                disableSyncButton();
            }
            else {
                enableSyncButton();
            }
        }
    }

    private final BroadcastReceiver mSyncCompletedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                // Sleep on main thread to let sync finish before updating sync info
                Thread.sleep(100);

                final String syncStatus = intent
                        .getStringExtra(IntentExtraKey.SYNC_BROADCAST_MESSAGE);
                updateSyncInfo(syncStatus, true);
            }
            catch (final Exception e) {
                logWarning(e);
            }
        }
    };

    private final BroadcastReceiver mUpdateSyncInfoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                updateSyncInfo("",
                        intent.getBooleanExtra(IntentExtraKey.FORCE_SYNC_INFO_UPDATE, false));
            }
            catch (final Exception e) {
                logWarning(e);
            }
        }
    };

    @Subscribe
    public void onConnectivityChecked(ConnectivityCheckedEvent event) {
        try {
            final Activity activity = getActivity();
            if (activity != null) {
                if (event.isConnected()) {
                    SyncHelper.requestOnDemandSync(activity.getApplicationContext());
                    disableSyncButton();
                }
                else {
                    updateSyncInfo(getResources().getString(R.string.sync_failed_msg), false);
                }
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    private void enableSyncButton() {
        mBtnSyncNow.setText(R.string.sync_button_caption);
        mBtnSyncNow.setEnabled(true);
    }

    private void disableSyncButton() {
        mBtnSyncNow.setText(R.string.syncing_caption);
        mBtnSyncNow.setEnabled(false);
    }
}

