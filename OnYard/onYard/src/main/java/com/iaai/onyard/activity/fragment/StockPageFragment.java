package com.iaai.onyard.activity.fragment;

import android.app.Activity;



public abstract class StockPageFragment extends PageFragment {

    public abstract boolean isSaveAllowed();

    public abstract boolean isVerifyRequired();

    @Override
    public void updateOptionsMenu() {
        super.updateOptionsMenu();

        try {
            final Activity activity = getActivity();
            if (activity != null && activity.getActionBar() != null) {
                activity.invalidateOptionsMenu();
            }
        }
        catch (final Exception e) {
            logError(e);
        }
    }

    @Override
    protected boolean isActionBarTitleBold() {
        return false;
    }

    @Override
    protected boolean isActionBarSubtitleBold() {
        return true;
    }
}
