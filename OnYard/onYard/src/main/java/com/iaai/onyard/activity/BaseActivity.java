package com.iaai.onyard.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.iaai.onyard.application.OnYard.Broadcast;
import com.iaai.onyard.application.OnYard.DialogTitle;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.application.OnYard.FragmentTag;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.application.OnYardApplication;
import com.iaai.onyard.classes.UnauthorizedReason;
import com.iaai.onyard.dialog.BaseDialogFragment;
import com.iaai.onyard.dialog.ErrorDialogFragment;
import com.iaai.onyard.dialog.FatalErrorDialogFragment;
import com.iaai.onyard.dialog.ForcefulLogOutDialogFragment;
import com.iaai.onyard.dialog.ProgressDialogFragment;
import com.iaai.onyard.event.FirstSyncCompleteEvent;
import com.iaai.onyard.listener.GetPendingCountListener;
import com.iaai.onyard.session.OnYardSessionData;
import com.iaai.onyard.sync.SyncHelper;
import com.iaai.onyard.task.GetPendingCountTask;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.LogHelper;
import com.squareup.otto.Bus;

public abstract class BaseActivity extends FragmentActivity implements GetPendingCountListener {

    private boolean mLoggedInAtPause = false;
    private ProgressDialogFragment mProgressDialog;
    private ProgressDialogFragment mFirstSyncProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            new GetPendingCountTask().execute(new Object[] { getApplicationContext(), this });
        }
        catch (final Exception e) {
            LogHelper.logError(getApplicationContext(), e, this.getClass().getSimpleName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getEventBus().register(this);

        if (mLoggedInAtPause && !AuthenticationHelper.isAnyUserLoggedIn(getContentResolver())) {
            performForcefulLogOut(new UnauthorizedReason().getUserFriendlyMessage());
            mLoggedInAtPause = false;
        }
        registerReceiver(mLogOutReceiver, new IntentFilter(Broadcast.LOGOUT));
        registerReceiver(mSyncCompletedReceiver, new IntentFilter(Broadcast.SYNC_COMPLETED));
        if (!SyncHelper.isCurrentlySyncing(this)) {
            dismissFirstSyncProgressDialog();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        getEventBus().unregister(this);

        if (AuthenticationHelper.isAnyUserLoggedIn(getContentResolver())) {
            mLoggedInAtPause = true;
        }
        unregisterReceiver(mLogOutReceiver);
        unregisterReceiver(mSyncCompletedReceiver);
        dismissProgressDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_FOCUS:
                case KeyEvent.KEYCODE_CAMERA: {
                    return true;
                }
            }
            return super.onKeyDown(keyCode, event);
        }
        catch (final Exception e) {
            logWarning(e);
            return false;
        }
    }

    @Override
    public void onPendingCountRetrieved(int pendingCount) {
        try {
            SyncHelper.updateSyncNotification(getApplicationContext(), pendingCount, false);
        }
        catch (final Exception e) {
            LogHelper.logWarning(getApplicationContext(), e, this.getClass().getSimpleName());
        }
    }

    public Bus getEventBus() {
        return getApplicationObject().getEventBus();
    }

    protected OnYardSessionData getSessionData() {
        return getApplicationObject().getSessionData();
    }

    protected void setSessionData(OnYardSessionData sessionData) {
        getApplicationObject().setSessionData(sessionData);
    }

    protected Location getLastKnownLocation() {
        return getApplicationObject().getLastKnownLocation();
    }

    protected void setCurrentLocation(Location location) {
        getApplicationObject().setCurrentLocation(location);
    }

    private OnYardApplication getApplicationObject() {
        return (OnYardApplication) getApplicationContext();
    }

    protected void showErrorDialog(String message) {
        final ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(message);
        showDialogAllowStateLoss(dialog, FragmentTag.ERROR_DIALOG);
    }

    protected void showErrorDialog(String message, String title) {
        final ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(message, title);
        showDialogAllowStateLoss(dialog, FragmentTag.ERROR_DIALOG);
    }

    protected void showFatalErrorDialog(String message) {
        final FatalErrorDialogFragment dialog = FatalErrorDialogFragment.newInstance(message);
        showDialogAllowStateLoss(dialog, FragmentTag.FATAL_ERROR_DIALOG);
    }

    private void showDialogAllowStateLoss(BaseDialogFragment dialog_fragment, String fragment_tag) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(dialog_fragment, fragment_tag);
        ft.commitAllowingStateLoss();
    }

    protected void logError(Exception e) {
        LogHelper.logError(getApplicationContext(), e, this.getClass().getSimpleName());
    }

    protected void logWarning(Exception e) {
        LogHelper.logWarning(getApplicationContext(), e, this.getClass().getSimpleName());
    }

    protected void logInfo(String message) {
        LogHelper.logInfo(getApplicationContext(), message, this.getClass().getSimpleName());
    }

    protected void showProgressDialog() {
        dismissProgressDialog();

        mProgressDialog = new ProgressDialogFragment();
        mProgressDialog.show(getSupportFragmentManager(), FragmentTag.PROGRESS_DIALOG);
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    protected void showFirstSyncProgressDialog(String message) {
        dismissFirstSyncProgressDialog();

        mFirstSyncProgressDialog = ProgressDialogFragment.newInstance(message);
        mFirstSyncProgressDialog.show(getSupportFragmentManager(),
                FragmentTag.FIRST_SYNC_PROGRESS_DIALOG);
    }

    protected void dismissFirstSyncProgressDialog() {
        if (mFirstSyncProgressDialog != null) {
            mFirstSyncProgressDialog.dismiss();
            mFirstSyncProgressDialog = null;
            getEventBus().post(new FirstSyncCompleteEvent());
        }
    }

    private void LogUserOut(Intent intent) {
        if (intent.hasExtra(IntentExtraKey.VOLUNTARY_LOGOUT)) {
            final boolean isLogoutVoluntary = intent.getBooleanExtra(
                    IntentExtraKey.VOLUNTARY_LOGOUT,
                    false);
            if (isLogoutVoluntary) {
                performVoluntaryLogOut();
            }
            else {
                performForcefulLogOut(intent.getStringExtra(IntentExtraKey.LOGOUT_MESSAGE));
            }
        }
        else {
            performForcefulLogOut(intent.getStringExtra(IntentExtraKey.LOGOUT_MESSAGE));
        }
    }

    private void performForcefulLogOut(String message) {
        if (message == null) {
            message = "";
        }
        final ForcefulLogOutDialogFragment dialog = ForcefulLogOutDialogFragment
                .newInstance(message);
        dialog.show(getSupportFragmentManager(), FragmentTag.FORCEFUL_LOGOUT_DIALOG);
    }

    private void performVoluntaryLogOut() {
        final Intent intent = new Intent(this, SearchPagerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    private final BroadcastReceiver mSyncCompletedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            dismissFirstSyncProgressDialog();
            if (intent.hasExtra(IntentExtraKey.SYNC_SUCCESSFUL)) {
                final boolean isSyncSuccessful = intent.getBooleanExtra(
                        IntentExtraKey.SYNC_SUCCESSFUL, true);
                if (!isSyncSuccessful) {
                    if (SyncHelper.getLastDBUpdateTime(getApplicationContext()) == 0L) {
                        showErrorDialog(ErrorMessage.FAILED_INITIAL_SYNC,
                                DialogTitle.FAILED_INITIAL_SYNC);
                    }
                }
            }
        }
    };

    private final BroadcastReceiver mLogOutReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUserOut(intent);
        }
    };
}
