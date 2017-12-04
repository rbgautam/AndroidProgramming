package com.iaai.onyard.classes;

import com.iaai.onyard.R;


public class SaveAction {

    private final boolean mSavePressed;
    private final boolean mVerifyPressed;
    private final boolean mOnSetSalePage;

    public SaveAction(int actionBarItemId, boolean onSetSalePage)
    {
        mSavePressed = actionBarItemId == R.id.action_save ? true : false;
        mVerifyPressed = actionBarItemId == R.id.action_verify ? true : false;
        mOnSetSalePage = onSetSalePage;
    }

    public boolean wasSavePressed() {
        return mSavePressed;
    }

    public boolean wasVerifyPressed() {
        return mVerifyPressed;
    }

    public boolean wasOnSetSalePage() {
        return mOnSetSalePage;
    }
}
