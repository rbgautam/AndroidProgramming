package com.iaai.onyard.activity.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.iaai.onyard.application.OnYard;

public abstract class PageFragment extends BaseFragment {

    public abstract void onBecomeCurrentPage();

    public abstract Fragment getOnActivityResultFragment();

    protected abstract int getActionBarIconId();

    protected abstract String getActionBarTitle();

    protected abstract String getActionBarSubTitle();

    protected abstract int getActionBarColorId();

    protected abstract boolean isActionBarTitleBold();

    protected abstract boolean isActionBarSubtitleBold();

    public void hideSoftKeyboard() {
        final Activity activity = getActivity();
        if (activity != null) {
            if (activity.getActionBar() != null) {
                final InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getApplicationWindowToken(), 0);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public void updateOptionsMenu() {
        final Activity activity = getActivity();
        if (activity != null) {
            if (activity.getActionBar() != null) {
                final ActionBar actionBar = activity.getActionBar();

                actionBar.setTitle(getActionBarTitle());
                setActionBarTitleBold(isActionBarTitleBold());

                actionBar.setSubtitle(getActionBarSubTitle());
                setActionBarSubtitleBold(isActionBarSubtitleBold());

                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
                        getActionBarColorId())));
                actionBar.setIcon(getResources().getDrawable(getActionBarIconId()));
            }
        }
    }

    private void setActionBarTitleBold(boolean bold) {
        final Activity activity = getActivity();
        if (activity != null) {
            final int titleId = getResources().getIdentifier(OnYard.ACTION_BAR_TITLE_ID, "id",
                    "android");
            final TextView titleView = (TextView) activity.findViewById(titleId);

            if (titleView != null) {
                if (bold) {
                    titleView.setTypeface(null, Typeface.BOLD);
                }
                else {
                    titleView.setTypeface(null, Typeface.NORMAL);
                }
            }
        }
    }

    private void setActionBarSubtitleBold(boolean bold) {
        final Activity activity = getActivity();
        if (activity != null) {
            final int titleId = getResources().getIdentifier(OnYard.ACTION_BAR_SUBTITLE_ID, "id",
                    "android");
            final TextView subtitleView = (TextView) activity.findViewById(titleId);

            if (subtitleView != null) {
                if (bold) {
                    subtitleView.setTypeface(null, Typeface.BOLD);
                }
                else {
                    subtitleView.setTypeface(null, Typeface.NORMAL);
                }
            }
        }
    }
}
