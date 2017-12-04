package com.iaai.onyard.dialog;

import android.app.DialogFragment;

import com.iaai.onyard.application.OnYardApplication;
import com.squareup.otto.Bus;

public class BaseDialogFragment2 extends DialogFragment {

    public BaseDialogFragment2() {

    }

    @Override
    public void onResume() {
        super.onResume();

        getEventBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getEventBus().unregister(this);
    }

    protected Bus getEventBus() {
        return getApplicationObject().getEventBus();
    }

    private OnYardApplication getApplicationObject() {
        return (OnYardApplication) getActivity().getApplicationContext();
    }
}
