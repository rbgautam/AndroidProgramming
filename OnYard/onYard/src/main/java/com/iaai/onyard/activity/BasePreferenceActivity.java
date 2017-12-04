package com.iaai.onyard.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;

import com.iaai.onyard.application.OnYard.Broadcast;
import com.iaai.onyard.application.OnYard.FragmentTag;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.application.OnYardApplication;
import com.iaai.onyard.classes.UnauthorizedReason;
import com.iaai.onyard.dialog.ForcefulLogOutDialogFragment2;
import com.iaai.onyard.listener.GetPendingCountListener;
import com.iaai.onyard.sync.SyncHelper;
import com.iaai.onyard.task.GetPendingCountTask;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.LogHelper;
import com.squareup.otto.Bus;

public abstract class BasePreferenceActivity extends PreferenceActivity implements
GetPendingCountListener {

    private boolean mLoggedInAtPause = false;

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
    }

    @Override
    protected void onPause() {
        super.onPause();

        getEventBus().unregister(this);

        if (AuthenticationHelper.isAnyUserLoggedIn(getContentResolver())) {
            mLoggedInAtPause = true;
        }
        unregisterReceiver(mLogOutReceiver);
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
        final ForcefulLogOutDialogFragment2 dialog = ForcefulLogOutDialogFragment2
                .newInstance(message);
        dialog.show(getFragmentManager(), FragmentTag.FORCEFUL_LOGOUT_DIALOG);
    }

    private void performVoluntaryLogOut() {
        final Intent intent = new Intent(this, SearchPagerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    private final BroadcastReceiver mLogOutReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUserOut(intent);
        }
    };

    protected Bus getEventBus() {
        return getApplicationObject().getEventBus();
    }

    private OnYardApplication getApplicationObject() {
        return (OnYardApplication) getApplicationContext();
    }
}
