package com.iaai.onyard.view;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.EditText;

import com.iaai.onyard.activity.BaseActivity;
import com.iaai.onyard.event.KeyboardBackPressedEvent;


public class OnYardEditText extends EditText {

    public OnYardEditText(Context context) {
        super(context);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final BaseActivity activity = (BaseActivity) getContext();
            activity.getEventBus().post(new KeyboardBackPressedEvent());
        }

        return false;
    }

}
