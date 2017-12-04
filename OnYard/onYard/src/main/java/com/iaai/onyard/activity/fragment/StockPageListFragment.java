package com.iaai.onyard.activity.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.iaai.onyard.adapter.OnYardFieldArrayAdapter;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.classes.OnYardField;
import com.iaai.onyard.classes.OnYardFieldOption;
import com.iaai.onyard.event.SessionDataCreatedEvent;


public abstract class StockPageListFragment extends StockPageFragment {

    private OnYardFieldArrayAdapter mListAdapter;
    private InputFragment mInputFragment;
    private VehicleDetailsFragment mDetailsFragment;
    private int mSelectedPos;
    private int mFirstVisiblePosition;
    private final Runnable mForceScrollToSelectedField = new Runnable() {

        @Override
        public void run() {
            final ListView list = getListView();
            if (mSelectedPos > list.getLastVisiblePosition()) {
                list.setSelection(mSelectedPos);
            }
            list.smoothScrollToPositionFromTop(mSelectedPos, mListViewItemHeight == null ? 200
                    : (int) (mListViewItemHeight * 0.8));
        }
    };
    protected Integer mListViewItemHeight;
    protected int mNumVisibleListItems;

    public boolean onBackPressed() {
        try {
            final FrameLayout inputFrame = getInputFrame();
            if (inputFrame != null && inputFrame.getVisibility() == View.VISIBLE) {
                inputFrame.setVisibility(View.GONE);
                return true;
            }
            else {
                return false;
            }
        }
        catch (final Exception e) {
            logWarning(e);
            return false;
        }
    }

    protected void initFragments() {
        mDetailsFragment = new VehicleDetailsFragment();
        getChildFragmentManager().beginTransaction()
        .replace(getDetailsFrameId(), mDetailsFragment).commit();

        mInputFragment = getInputFragment();
        getChildFragmentManager().beginTransaction()
        .replace(getInputFrameId(), mInputFragment).commit();
    }

    protected void initList() {
        final ListView list = getListView();
        final ArrayList<OnYardField> fieldList = (ArrayList<OnYardField>) getFieldList();

        mListAdapter = getListAdapter();

        list.setAdapter(mListAdapter);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mFirstVisiblePosition = list.getFirstVisiblePosition();
        list.setOnScrollListener(getOnListScrollListener());
        list.setOnItemClickListener(getOnItemClickListener());

        prepopulateFields();

        if (!shouldDisplayList() || fieldList.isEmpty()) {
            list.setVisibility(View.GONE);
        }

        mListViewItemHeight = getListViewItemHeight(list);
    }

    protected void setAdapterCommitAttempted(boolean attempted) {
        if (mListAdapter != null) {
            mListAdapter.setCommitAttempted(attempted);
        }
    }

    private OnScrollListener getOnListScrollListener() {
        return new OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                try {
                    final int currentFirstVisPos = view.getFirstVisiblePosition();
                    if (currentFirstVisPos > mFirstVisiblePosition) {
                        mDetailsFragment.collapseDetails();
                    }
                    mFirstVisiblePosition = currentFirstVisPos;
                }
                catch (final Exception e) {
                    logWarning(e);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {}
        };
    }

    private OnItemClickListener getOnItemClickListener() {
        return new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    setSelectedFieldPos(position);

                    mDetailsFragment.collapseDetails();

                    final OnYardField selectedField = getFieldList().get(mSelectedPos);
                    final ListView list = getListView();
                    mInputFragment.createInputFields(selectedField, getGuessedOption(mSelectedPos),
                            getScreenHeightPixels());
                    getInputFrame().setVisibility(View.VISIBLE);

                    list.post(mForceScrollToSelectedField);
                    if (mSelectedPos > getFieldList().size() - 5) {
                        list.postDelayed(mForceScrollToSelectedField, 400);
                    }
                }
                catch (final Exception e) {
                    logError(e);
                    showErrorDialog(ErrorMessage.CHECKIN);
                }
            }
        };
    }

    private Integer getListViewItemHeight(ListView list) {
        try {
            final View childView = mListAdapter.getView(0, null, list);
            childView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            return childView.getMeasuredHeight();
        }
        catch (final Exception e) {
            return null;
        }
    }

    private int getScreenHeightPixels() {
        final Activity activity = getActivity();
        if (activity != null) {
            final Display display = activity.getWindowManager().getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);

            return size.y;
        }
        else {
            return 1000;
        }
    }

    protected void clearListFocus() {
        getListView().setItemChecked(-1, true);
        mListAdapter.setSelectedPos(-1);
        getInputFrame().setVisibility(View.GONE);
    }

    protected void refreshList() {
        if (mListAdapter != null) {
            mListAdapter.refreshList(getFieldList());
        }
    }

    protected void goToNextField() {
        final ListView list = getListView();
        if (mSelectedPos == list.getCount() - 1) {
            hideSoftKeyboard();
        }
        else {
            setSelectedFieldPos(mSelectedPos + 1);

            if (getFieldList().get(mSelectedPos).hasSelection()) {
                goToNextField();
            }
            else {
                final View field = mListAdapter.getView(mSelectedPos, null, list);
                list.performItemClick(field, mSelectedPos, field.getId());
            }
        }
    }

    protected void goToField(int position) {
        final ListView list = getListView();
        if (mSelectedPos + 1 < list.getCount()) {
            setSelectedFieldPos(position);
            final View field = mListAdapter.getView(mSelectedPos, null, list);
            list.performItemClick(field, mSelectedPos, field.getId());
        }
        else {
            hideSoftKeyboard();
        }
    }

    @Override
    public void hideSoftKeyboard() {
        try {
            super.hideSoftKeyboard();
            onBackPressed();

            if (mInputFragment != null) {
                mInputFragment.onSoftKeyboardHidden();
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    protected void setSelectedFieldPos(int selectedPos) {
        mSelectedPos = selectedPos;
        mListAdapter.setSelectedPos(mSelectedPos);
    }

    protected void forceScrollToSelectedField() {
        getListView().postDelayed(mForceScrollToSelectedField, 100);
    }

    @Override
    public void onBecomeCurrentPage() {
        if (mListAdapter != null) {
            refreshList();
        }
    }

    protected int getListSelectedPos() {
        return mSelectedPos;
    }

    @Override
    public Fragment getOnActivityResultFragment() {
        return mInputFragment;
    }

    protected abstract ListView getListView();

    protected abstract FrameLayout getInputFrame();

    protected abstract boolean shouldDisplayList();

    protected abstract ArrayList<? extends OnYardField> getFieldList();

    protected abstract OnYardFieldArrayAdapter getListAdapter();

    protected abstract InputFragment getInputFragment();

    protected abstract int getDetailsFrameId();

    protected abstract int getInputFrameId();

    protected abstract void onSessionDataCreated(SessionDataCreatedEvent event);

    protected abstract void prepopulateFields();

    protected abstract OnYardFieldOption getGuessedOption(int selectedPos);
}
