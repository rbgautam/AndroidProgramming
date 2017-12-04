package com.iaai.onyard.activity.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView.OnEditorActionListener;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.classes.OnYardField;
import com.iaai.onyard.classes.OnYardFieldOption;
import com.iaai.onyard.event.KeyboardBackPressedEvent;
import com.iaai.onyard.filter.InputFilterAlphaNumeric;
import com.iaai.onyard.filter.InputFilterMinMax;
import com.iaai.onyard.filter.InputFilterText;
import com.iaai.onyard.view.OnYardEditText;

public abstract class InputFragment extends BaseFragment {

    @InjectView(R.id.input_layout)
    LinearLayout mLayout;

    protected OnYardField mCurrentField;
    private boolean mIsKeyboardShowing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_input, container, false);
            ButterKnife.inject(this, view);

            mIsKeyboardShowing = false;
            view.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            final Activity activity = getActivity();
                            if (activity != null) {
                                final InputMethodManager mgr = (InputMethodManager) activity
                                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (mgr.isAcceptingText()) {
                                    mIsKeyboardShowing = true;
                                }
                                else {
                                    mIsKeyboardShowing = false;
                                }
                            }
                        }
                    });

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return null;
        }
    }

    public void createInputFields(OnYardField field, OnYardFieldOption guessedOption,
            int screenHeightPixels) {
        resetLayout();
        mCurrentField = field;

        switch (mCurrentField.getInputType()) {
            case LIST:
            case CHECKBOX:
                createListView(guessedOption);
                break;
            case NUMERIC:
                createNumericEditText();
                break;
            case ALPHANUMERIC:
                createAlphaNumericEditText();
                break;
            case TEXT:
                createEditText();
                break;
            default:
                break;
        }
    }

    protected int getDesiredListInputHeight(ListView list) {
        try {
            final View childView = list.getAdapter().getView(0, null, list);
            childView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            // height of input fragment should be such that 3 fields and part of a fourth is visible
            return childView.getMeasuredHeight() * 7;
        }
        catch (final Exception e) {
            return 600;
        }
    }

    protected void createEditText() {
        final Activity activity = getActivity();
        if (activity != null) {
            final EditText editText = new OnYardEditText(activity);
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editText.setFilters(new InputFilter[] {
                    new InputFilter.LengthFilter(mCurrentField.getMaxLength()), new InputFilterText() });

            populateEditText(editText);
            mLayout.addView(editText);
        }
    }

    protected void createAlphaNumericEditText() {
        final Activity activity = getActivity();
        if (activity != null) {
            final EditText editText = new OnYardEditText(activity);
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editText.setFilters(new InputFilter[] {
                    new InputFilter.LengthFilter(mCurrentField.getMaxLength()),
                    new InputFilterAlphaNumeric() });

            populateEditText(editText);
            mLayout.addView(editText);
        }
    }

    protected void createNumericEditText() {
        final Activity activity = getActivity();
        if (activity != null) {
            final EditText editText = new OnYardEditText(activity);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setFilters(new InputFilter[] { new InputFilterMinMax(mCurrentField.getMinValue(),
                    mCurrentField.getMaxValue()) });

            populateEditText(editText);
            mLayout.addView(editText);
        }
    }

    protected void populateEditText(EditText editText) {
        showSoftKeyboard();
        editText.requestFocus();
        editText.setOnEditorActionListener(getEnterKeyListener());

        if (mCurrentField.hasSelection()) {
            editText.setText(mCurrentField.getEnteredValue());
            editText.setSelection(editText.getText().length());
        }
    }

    public abstract void onKeyboardBackPressed(KeyboardBackPressedEvent event);

    protected abstract OnItemClickListener getListItemClickListener(
            final ArrayList<OnYardFieldOption> optionsList);

    protected abstract OnEditorActionListener getEnterKeyListener();

    protected void createListView(OnYardFieldOption guessedOption) {
        final Activity activity = getActivity();
        if (activity != null) {
            hideSoftKeyboard();

            final ArrayList<OnYardFieldOption> optionsList = mCurrentField.getOptions();
            final ArrayList<String> displayValuesList = new ArrayList<String>();
            for (int index = 0; index < optionsList.size(); index++) {
                displayValuesList.add(optionsList.get(index).getDisplayName());
            }

            final ListView list = new ListView(activity);
            list.setStackFromBottom(true);
            list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

            list.setAdapter(new ArrayAdapter<String>(activity, R.layout.field_option_row,
                    displayValuesList) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    try {
                        final View view = super.getView(position, convertView, parent);
                        if (mCurrentField.hasSelection()
                                && position == mCurrentField.getSelectedIndex()) {
                            view.setBackgroundResource(R.color.field_input_selected);
                        }
                        else {
                            view.setBackgroundResource(R.color.transparent);
                        }
                        return view;
                    }
                    catch (final Exception e) {
                        showErrorDialog(ErrorMessage.PAGE_LOAD);
                        logWarning(e);
                        return null;
                    }
                }
            });

            list.setOnItemClickListener(getListItemClickListener(optionsList));

            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    getDesiredListInputHeight(list));
            list.setLayoutParams(params);

            mLayout.addView(list);

            if (mCurrentField.hasSelection()) {
                list.setSelection(mCurrentField.getSelectedIndex());
            }
            else {
                list.setSelection(0);

                if (guessedOption != null) {
                    final int guessedInd = displayValuesList.indexOf(guessedOption.getDisplayName());
                    if (guessedInd > 0) {
                        list.setSelection(guessedInd - 1);
                    }
                }
            }
        }
    }

    protected void resetLayout() {
        mLayout.removeAllViews();
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        mLayout.setLayoutParams(params);
    }

    protected void toggleSoftKeyboard() {
        final Activity activity = getActivity();
        if (activity != null) {
            final InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(mLayout.getApplicationWindowToken(),
                    InputMethodManager.SHOW_FORCED, 0);
        }
    }

    protected void showSoftKeyboard() {
        if (!mIsKeyboardShowing) {
            toggleSoftKeyboard();
        }
    }

    protected void hideSoftKeyboard() {
        final Activity activity = getActivity();
        if (activity != null) {
            final InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mLayout.getApplicationWindowToken(), 0);
            onSoftKeyboardHidden();
        }
    }

    protected void onSoftKeyboardHidden() {
        mIsKeyboardShowing = false;
    }
}
