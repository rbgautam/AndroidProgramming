package com.iaai.onyard.activity.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.adapter.EnhancementArrayAdapter;
import com.iaai.onyard.adapter.OnYardFieldArrayAdapter;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.classes.EnhancementField;
import com.iaai.onyard.classes.OnYardField;
import com.iaai.onyard.classes.OnYardFieldOption;
import com.iaai.onyard.event.EnhancementFieldOptionChosenEvent;
import com.iaai.onyard.event.SaveConditionsChangedEvent;
import com.iaai.onyard.event.SessionDataCreatedEvent;
import com.squareup.otto.Subscribe;

public class StockEnhancementPageFragment extends StockPageListFragment {

    @InjectView(R.id.stock_enhancement_list)
    ListView mEnhancementList;
    @InjectView(R.id.stock_enhancement_input_frame)
    FrameLayout mInputFrame;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_stock_enhancement, container,
                    false);
            ButterKnife.inject(this, view);

            initFragments();
            initList();

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return container;
        }
    }

    @Subscribe
    public void onSaveConditionsChanged(SaveConditionsChangedEvent event) {
        try {
            final Activity activity = getActivity();
            if (activity != null) {
                activity.invalidateOptionsMenu();
            }
        }
        catch (final Exception e) {
            showErrorDialog(ErrorMessage.CHECKIN);
            logError(e);
        }
    }

    @Override
    @Subscribe
    public void onSessionDataCreated(SessionDataCreatedEvent event) {
        try {
            if (getActivity() != null) {
                if (getSessionData().wasEnhancementCommitted()) {
                    dismissProgressDialog();
                    initList();

                    getEventBus().post(new SaveConditionsChangedEvent());
                }
            }
        }
        catch (final Exception e) {
            logError(e);
            showErrorDialog(ErrorMessage.CHECKIN);
        }
    }

    @Subscribe
    public void onEnhancementOptionChosen(EnhancementFieldOptionChosenEvent event) {
        try {
            if (getActivity() != null) {
                getSessionData().getEnhancementData().setSelectedOption(getListSelectedPos(),
                        event.getOption());
                refreshList();
                clearListFocus();

                getEventBus().post(new SaveConditionsChangedEvent());
            }
        }
        catch (final Exception e) {
            logError(e);
            showErrorDialog(ErrorMessage.CHECKIN);
        }
    }

    @Override
    protected int getActionBarIconId() {
        return R.drawable.ic_action_enhancement;
    }

    @Override
    protected String getActionBarTitle() {
        return "Enhancements";
    }

    @Override
    protected int getActionBarColorId() {
        return R.color.enhancements_purple;
    }

    @Override
    public Fragment getOnActivityResultFragment() {
        return null;
    }

    @Override
    protected String getActionBarSubTitle() {
        try {
            if (getActivity() != null) {
                return getSessionData().getVehicleInfo().getStockNumber();
            }
            else {
                return "";
            }
        }
        catch (final Exception e) {
            logWarning(e);
            return null;
        }
    }

    @Override
    public void onBecomeCurrentPage() {}

    @Override
    public boolean isSaveAllowed() {
        if (getActivity() != null) {
            return getSessionData().isEnhancementCommitAllowed();
        }
        else {
            return false;
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

    @Override
    public boolean isVerifyRequired() {
        return false;
    }

    @Override
    protected ListView getListView() {
        return mEnhancementList;
    }

    @Override
    protected FrameLayout getInputFrame() {
        return mInputFrame;
    }

    @Override
    protected boolean shouldDisplayList() {
        return true;
    }

    @Override
    protected ArrayList<? extends OnYardField> getFieldList() {
        if (getActivity() != null) {
            return getSessionData().getEnhancementData().getEnhancementFields();
        }
        else {
            return new ArrayList<EnhancementField>();
        }
    }

    @Override
    protected OnYardFieldArrayAdapter getListAdapter() {
        final Activity activity = getActivity();
        if (activity != null) {
            return new EnhancementArrayAdapter(activity, getFieldList());
        }
        else {
            return null;
        }
    }

    @Override
    protected int getDetailsFrameId() {
        return R.id.stock_enhancement_vehicle_details_frame;
    }

    @Override
    protected int getInputFrameId() {
        return mInputFrame.getId();
    }

    @Override
    protected void prepopulateFields() {}

    @Override
    protected OnYardFieldOption getGuessedOption(int selectedPos) {
        return null;
    }

    @Override
    protected InputFragment getInputFragment() {
        return new EnhancementInputFragment();
    }
}
