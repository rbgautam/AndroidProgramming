package com.iaai.onyard.activity.fragment;

import java.util.ArrayList;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.classes.OnYardFieldOption;
import com.iaai.onyard.event.EnhancementFieldOptionChosenEvent;
import com.iaai.onyard.event.KeyboardBackPressedEvent;
import com.squareup.otto.Subscribe;

public class EnhancementInputFragment extends InputFragment {

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
                    if (getActivity() != null) {
                        final OnYardFieldOption clickedOption = optionsList.get(position);
                        getEventBus().post(new EnhancementFieldOptionChosenEvent(clickedOption));
                    }
                }
                catch (final Exception e) {
                    showFatalErrorDialog(ErrorMessage.ENHANCEMENTS);
                    logError(e);
                }
            }
        };
    }

    @Override
    protected OnEditorActionListener getEnterKeyListener() {
        return null;
    }
}
