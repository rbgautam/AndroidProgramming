package com.iaai.onyard.activity.fragment;

import java.util.ArrayList;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.classes.OnYardFieldOption;
import com.iaai.onyard.event.KeyboardBackPressedEvent;
import com.iaai.onyard.event.SetSaleFieldOptionChosenEvent;
import com.iaai.onyard.event.SetSaleInputEnteredEvent;
import com.squareup.otto.Subscribe;

public class SetSaleInputFragment extends InputFragment {

    @Override
    @Subscribe
    public void onKeyboardBackPressed(KeyboardBackPressedEvent event) {
        try {
            hideSoftKeyboard();
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    @Override
    protected OnItemClickListener getListItemClickListener(final ArrayList<OnYardFieldOption> optionsList) {
        return new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    final OnYardFieldOption clickedOption = optionsList.get(position);
                    if (getActivity() != null) {
                        getEventBus().post(new SetSaleFieldOptionChosenEvent(clickedOption));
                    }
                }
                catch (final Exception e) {
                    showFatalErrorDialog(ErrorMessage.SET_SALE);
                    logError(e);
                }
            }
        };
    }

    @Override
    protected OnEditorActionListener getEnterKeyListener() {
        return new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        final EditText editText = (EditText) v;

                        if (getActivity() != null) {
                            getEventBus().post(
                                    new SetSaleInputEnteredEvent(editText.getText().toString().trim()));
                        }

                        return true;
                    }
                    else {
                        return false;
                    }
                }
                catch (final Exception e) {
                    showFatalErrorDialog(ErrorMessage.CHECKIN);
                    logError(e);
                    return false;
                }

            }
        };
    }
}