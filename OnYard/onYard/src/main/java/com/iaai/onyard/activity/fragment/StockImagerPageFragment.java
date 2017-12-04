package com.iaai.onyard.activity.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.application.OnYard.FragmentTag;
import com.iaai.onyard.application.OnYard.ImageMode;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.event.SaveConditionsChangedEvent;
import com.iaai.onyard.event.SessionDataCreatedEvent;
import com.iaai.onyard.event.ThumbnailGridScrolledEvent;
import com.squareup.otto.Subscribe;


public class StockImagerPageFragment extends StockPageFragment {

    private static final String ENHANCEMENT_IMAGES_TAB_TITLE = "Enhancement";
    private static final String CHECK_IN_IMAGES_TAB_TITLE = "Check-In";
    private static final String RESHOOT_IMAGES_TAB_TITLE = "Reshoot";

    private VehicleDetailsFragment mDetailsFragment;

    @InjectView(R.id.imager_tab_host)
    FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_stock_imager, container, false);
            ButterKnife.inject(this, view);

            initFragments();
            initTabs();

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return container;
        }
    }

    private void initFragments() {
        mDetailsFragment = new VehicleDetailsFragment();
        getChildFragmentManager().beginTransaction()
        .replace(R.id.vehicle_details_frame, mDetailsFragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null && getSessionData().hasUnsavedData()) {
            mDetailsFragment.collapseDetails();
        }
    }

    @Subscribe
    public void onSessionDataCreated(SessionDataCreatedEvent event) {
        try {
            if (getActivity() != null) {
                if (getSessionData().wasImagerCommitted()) {
                    mTabHost.clearAllTabs();
                    initTabs();

                    getEventBus().post(new SaveConditionsChangedEvent());
                }
            }
        }
        catch (final Exception e) {
            logError(e);
            showErrorDialog(ErrorMessage.PAGE_LOAD);
        }
    }

    private void initTabs() {
        final Activity activity = getActivity();
        if (activity != null) {
            mTabHost.setup(activity, getChildFragmentManager(), android.R.id.tabcontent);
            createImagerTab();

            final boolean hasReshoots = getSessionData().getOnYardImageData(ImageMode.RESHOOT)
                    .getTotalNumberOfImages() > 0;
                    if (hasReshoots) {
                        createReshootTab();
                        mTabHost.setCurrentTabByTag(FragmentTag.RESHOOT_TAB);
                    }
                    else {
                        if (hasReshoots) {
                            mTabHost.setCurrentTabByTag(FragmentTag.RESHOOT_TAB);
                        }
                    }
        }
    }

    private void createReshootTab() {
        final Bundle reshootBundle = new Bundle();
        reshootBundle.putSerializable(IntentExtraKey.IMAGE_MODE, ImageMode.RESHOOT);
        mTabHost.addTab(
                mTabHost.newTabSpec(FragmentTag.RESHOOT_TAB).setIndicator(RESHOOT_IMAGES_TAB_TITLE),
                ImagerGridFragment.class, reshootBundle);
    }

    private void createImagerTab() {
        if (getActivity() != null) {
            final Bundle imagerBundle = new Bundle();
            imagerBundle.putSerializable(IntentExtraKey.IMAGE_MODE, ImageMode.STANDARD);
            final String imagerTabTitle = getSessionData().getVehicleInfo().hasImages() ? ENHANCEMENT_IMAGES_TAB_TITLE
                    : CHECK_IN_IMAGES_TAB_TITLE;
            mTabHost.addTab(mTabHost.newTabSpec(FragmentTag.IMAGER_TAB).setIndicator(imagerTabTitle),
                    ImagerGridFragment.class, imagerBundle);
        }
    }

    @Override
    public Fragment getOnActivityResultFragment() {
        return null;
    }

    @Override
    protected int getActionBarIconId() {
        return R.drawable.ic_action_camera;
    }

    @Override
    protected String getActionBarTitle() {
        return "Imager";
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
    protected int getActionBarColorId() {
        return R.color.imager_blue;
    }

    @Override
    public void onBecomeCurrentPage() {
    }

    @Subscribe
    public void onThumbnailGridScrolled(ThumbnailGridScrolledEvent event) {
        try {
            switch (event.getScrollDirection()) {
                case UP:
                    mDetailsFragment.collapseDetails();
                    break;
                case DOWN:
                    break;
                default:
                    break;
            }
        }
        catch (final Exception e) {
            logError(e);
        }
    }

    @Override
    public boolean isSaveAllowed() {
        try {
            if (getActivity() != null) {
                return getSessionData().isImagerCommitAllowed();
            }
            else {
                return false;
            }
        }
        catch (final Exception e) {
            logError(e);
            showFatalErrorDialog(ErrorMessage.SAVE);
            return false;
        }
    }

    @Override
    public boolean isVerifyRequired() {
        return false;
    }
}
