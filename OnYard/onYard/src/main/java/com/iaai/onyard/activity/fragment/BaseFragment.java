package com.iaai.onyard.activity.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.iaai.onyard.application.OnYard.FragmentTag;
import com.iaai.onyard.application.OnYardApplication;
import com.iaai.onyard.dialog.ErrorDialogFragment;
import com.iaai.onyard.dialog.FatalErrorDialogFragment;
import com.iaai.onyard.dialog.ProgressDialogFragment;
import com.iaai.onyard.event.ToggleProgressDialogEvent;
import com.iaai.onyard.session.OnYardSessionData;
import com.iaai.onyard.utility.LogHelper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;


public abstract class BaseFragment extends Fragment {

    private ProgressDialogFragment mProgressDialog;

    @Override
    public void onResume() {
        super.onResume();

        getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getActivity() != null) {
            getEventBus().unregister(this);
        }
        dismissProgressDialog();
    }

    protected Bus getEventBus() {
        return getApplicationObject().getEventBus();
    }

    protected OnYardSessionData getSessionData() {
        return getApplicationObject().getSessionData();
    }

    protected void setSessionData(OnYardSessionData sessionData) {
        getApplicationObject().setSessionData(sessionData);
    }

    protected void createSessionData(String stockNumber) {
        getApplicationObject().createSessionData(stockNumber);
    }

    private OnYardApplication getApplicationObject() {
        final Activity activity = getActivity();
        if (activity != null) {
            return (OnYardApplication) activity.getApplicationContext();
        }
        else {
            return null;
        }
    }

    protected void showErrorDialog(String message) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            final ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(message);
            dialog.show(activity.getSupportFragmentManager(), FragmentTag.ERROR_DIALOG);
        }
    }

    protected void showErrorDialog(String message, String title) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            final ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(message, title);
            dialog.show(activity.getSupportFragmentManager(), FragmentTag.ERROR_DIALOG);
        }
    }

    protected void showFatalErrorDialog(String message) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            final FatalErrorDialogFragment dialog = FatalErrorDialogFragment.newInstance(message);
            dialog.show(activity.getSupportFragmentManager(), FragmentTag.FATAL_ERROR_DIALOG);
        }
    }

    protected void logError(Exception e) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            LogHelper
            .logError(activity.getApplicationContext(), e, this.getClass()
                    .getSimpleName());
        }
    }

    protected void logWarning(Exception e) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            LogHelper.logWarning(activity.getApplicationContext(), e, this.getClass()
                    .getSimpleName());
        }
    }

    protected void logInfo(String message) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            LogHelper.logInfo(activity.getApplicationContext(), message, this.getClass()
                    .getSimpleName());
        }
    }

    protected void showProgressDialog() {
        dismissProgressDialog();

        final FragmentActivity activity = getActivity();
        if (activity != null) {
            mProgressDialog = new ProgressDialogFragment();
            mProgressDialog.show(activity.getSupportFragmentManager(), FragmentTag.PROGRESS_DIALOG);
        }
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Subscribe
    public void toggleProgressDialog(ToggleProgressDialogEvent event) {
        if (event.shouldShowDialog()) {
            showProgressDialog();
        }
        else {
            dismissProgressDialog();
        }
    }
}
