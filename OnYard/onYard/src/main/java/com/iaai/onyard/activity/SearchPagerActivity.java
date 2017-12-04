package com.iaai.onyard.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import leadtools.RasterSupport;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.WindowManager;
import butterknife.ButterKnife;

import com.iaai.onyard.R;
import com.iaai.onyard.activity.fragment.SearchCheckinPageFragment;
import com.iaai.onyard.activity.fragment.SearchEnhancementsPageFragment;
import com.iaai.onyard.activity.fragment.SearchGeneralPageFragment;
import com.iaai.onyard.activity.fragment.SearchImagerPageFragment;
import com.iaai.onyard.activity.fragment.SearchLocationPageFragment;
import com.iaai.onyard.activity.fragment.SearchSetSalePageFragment;
import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.application.OnYard.FragmentTag;
import com.iaai.onyard.application.OnYard.InfoMessage;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.application.OnYard.NavDrawerItemType;
import com.iaai.onyard.application.OnYard.NavDrawerTitle;
import com.iaai.onyard.application.OnYard.SearchMode;
import com.iaai.onyard.classes.NavDrawerItem;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.dialog.LoginDialogFragment;
import com.iaai.onyard.dialog.LogoutConfirmDialogFragment;
import com.iaai.onyard.event.ClearSearchFieldEvent;
import com.iaai.onyard.event.FirstSyncCompleteEvent;
import com.iaai.onyard.event.LoginCompleteEvent;
import com.iaai.onyard.event.LogoutCompleteEvent;
import com.iaai.onyard.event.NavDrawerItemClickedEvent;
import com.iaai.onyard.event.SessionDataCreatedEvent;
import com.iaai.onyard.event.ToggleProgressDialogEvent;
import com.iaai.onyard.sync.SyncHelper;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.squareup.otto.Subscribe;

public class SearchPagerActivity extends PagerActivity {

    private static class PageFragments {

        private static final int SEARCH_GENERAL_POSITION = 0;
        private static final int SEARCH_IMAGER_POSITION = 1;
        private static final int SEARCH_CHECKIN_POSITION = 2;
        private static int SEARCH_ENHANCEMENTS_POSITION = 3;
        private static int SEARCH_LOCATION_POSITION = 4;
        private static int SEARCH_SETSALE_POSITION = 5;
    }

    private static class NavDrawerLoggedOut {
        private static final int HOME_POSITION = 0;
        private static final int SETTINGS_POSITION = 1;
        private static final int LOG_IN_POSITION = 2;
    }

    private static class NavDrawerLoggedIn {

        private static final int HOME_POSITION = 0;
        private static final int IMAGER_POSITION = 1;
        private static final int CHECKIN_POSITION = 2;
        private static int ENHANCEMENTS_POSITION = 3;
        private static int LOCATION_POSITION = 4;
        private static int SETSALE_POSITION = 5;
        private static int SETTINGS_POSITION = 6;
        private static int LOG_OUT_POSITION = 7;
    }

    private boolean mReturningWithResult;
    private int mRequestCode;
    private int mResultCode;
    private Intent mData;

    private boolean mReturningFromStock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search);
            ButterKnife.inject(this);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            initializeOnYard();
            initializePager();
            initializeNavDrawer();
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.INITIALIZATION);
            logError(e);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final Bundle intentExtras = intent.getExtras();
        if (intentExtras != null && intentExtras.containsKey(IntentExtraKey.SEARCH_MODE)) {
            mReturningFromStock = true;
            switch ((SearchMode) intentExtras.get(IntentExtraKey.SEARCH_MODE)) {
                case GENERAL:
                    pageToPosition(PageFragments.SEARCH_GENERAL_POSITION);
                    break;
                case IMAGER:
                    pageToPosition(PageFragments.SEARCH_IMAGER_POSITION);
                    break;
                case CHECKIN:
                    pageToPosition(PageFragments.SEARCH_CHECKIN_POSITION);
                    break;
                case ENHANCEMENT:
                    pageToPosition(PageFragments.SEARCH_ENHANCEMENTS_POSITION);
                    break;
                case LOCATION:
                    pageToPosition(PageFragments.SEARCH_LOCATION_POSITION);
                    break;
                case SETSALE:
                    pageToPosition(PageFragments.SEARCH_SETSALE_POSITION);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onResumeFragments() {
        try {
            super.onResumeFragments();

            // we have to wait until fragment is resumed before calling onActivityResult
            if (mReturningWithResult) {
                getCurrentPage().getOnActivityResultFragment().onActivityResult(mRequestCode,
                        mResultCode, mData);
            }
            mReturningWithResult = false;

            if (mReturningFromStock) {
                getEventBus().post(new ClearSearchFieldEvent());
                mReturningFromStock = false;
            }
        }
        catch (final Exception e) {
            showErrorDialog(ErrorMessage.SEARCH_ON_ACTIVITY_RESULT);
            logWarning(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            // When request is coming from a fragment, requestCode has a fragment index in the
            // high-order bits. We must first identify that it came from a fragment, then mask
            // the high bits in order to get the actual requestCode.
            final int fragmentIndex = requestCode >> 16;
            if (fragmentIndex != 0) {
                mReturningWithResult = true;
                mRequestCode = requestCode & 65535;
                mResultCode = resultCode;
                mData = data;
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (!isNavDrawerVisible()) {
                    if (getCurrentPagePosition() != PageFragments.SEARCH_GENERAL_POSITION) {
                        pageToPosition(PageFragments.SEARCH_GENERAL_POSITION);
                        return true;
                    }
                }
            }

            return super.onKeyDown(keyCode, event);
        }
        catch (final Exception e) {
            logWarning(e);
            return false;
        }
    }

    @Subscribe
    public void toggleProgressDialog(ToggleProgressDialogEvent event) {
        if (event.shouldShowDialog()) {
            showProgressDialog();
        }
        else {
            dismissProgressDialog();
        }
    }

    @Subscribe
    public void onSessionDataCreated(SessionDataCreatedEvent event) {
        try {
            final OnYardPreferences preferences = new OnYardPreferences(this);
            final Intent intent = new Intent(this, StockPagerActivity.class);

            intent.putExtra(IntentExtraKey.SEARCH_MODE, SearchMode.GENERAL);

            if (getCurrentPagePosition() == PageFragments.SEARCH_IMAGER_POSITION) {
                intent.putExtra(IntentExtraKey.SEARCH_MODE, SearchMode.IMAGER);
            }
            if (getCurrentPagePosition() == PageFragments.SEARCH_CHECKIN_POSITION) {
                intent.putExtra(IntentExtraKey.SEARCH_MODE, SearchMode.CHECKIN);
            }
            if (getCurrentPagePosition() == PageFragments.SEARCH_ENHANCEMENTS_POSITION
                    && preferences.isEnhancementsEnabled()) {
                intent.putExtra(IntentExtraKey.SEARCH_MODE, SearchMode.ENHANCEMENT);
            }
            if (getCurrentPagePosition() == PageFragments.SEARCH_LOCATION_POSITION) {
                intent.putExtra(IntentExtraKey.SEARCH_MODE, SearchMode.LOCATION);
            }
            if (getCurrentPagePosition() == PageFragments.SEARCH_SETSALE_POSITION
                    && preferences.isSetSaleEnabled()) {
                intent.putExtra(IntentExtraKey.SEARCH_MODE, SearchMode.SETSALE);
            }

            startActivity(intent);
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.VEHICLE_DATA_LOAD);
            logError(e);
        }
    }

    @Subscribe
    public void onFirstSyncCompleted(FirstSyncCompleteEvent event) {
        initializePager();
        initializeNavDrawer();
    }

    @Subscribe
    public void onLoginComplete(LoginCompleteEvent event) {
        try {
            onAuthChange();
            if (SyncHelper.getLastDBUpdateTime(getApplicationContext()) == 0L) {
                showFirstSyncProgressDialog(InfoMessage.INITIAL_SYNC_IN_PROGRESS);
            }
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.LOGIN);
            logError(e);
        }
    }

    @Subscribe
    public void onLogoutComplete(LogoutCompleteEvent event) {
        try {
            switch (event.getResult()) {
                case SUCCESS:
                    onLogoutSuccessful();
                    break;
                case FAILURE:
                    onLogoutFailed();
                    break;
                case NO_NETWORK:
                    onLogoutFailed();
                    break;
                default:
                    onLogoutFailed();
                    break;
            }
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.LOGOUT);
            logError(e);
        }
    }

    @Subscribe
    public void onNavDrawerItemClick(NavDrawerItemClickedEvent event) {
        try {
            super.closeNavDrawer();
            if (AuthenticationHelper.isAnyUserLoggedIn(getContentResolver())) {
                if (event.getItemIndex() == NavDrawerLoggedIn.HOME_POSITION) {
                    pageToPosition(PageFragments.SEARCH_GENERAL_POSITION);
                    return;
                }
                if (event.getItemIndex() == NavDrawerLoggedIn.IMAGER_POSITION) {
                    pageToPosition(PageFragments.SEARCH_IMAGER_POSITION);
                    return;
                }
                if (event.getItemIndex() == NavDrawerLoggedIn.CHECKIN_POSITION) {
                    pageToPosition(PageFragments.SEARCH_CHECKIN_POSITION);
                    return;
                }
                if (event.getItemIndex() == NavDrawerLoggedIn.ENHANCEMENTS_POSITION) {
                    pageToPosition(PageFragments.SEARCH_ENHANCEMENTS_POSITION);
                    return;
                }
                if (event.getItemIndex() == NavDrawerLoggedIn.LOCATION_POSITION) {
                    pageToPosition(PageFragments.SEARCH_LOCATION_POSITION);
                    return;
                }
                if (event.getItemIndex() == NavDrawerLoggedIn.SETSALE_POSITION) {
                    pageToPosition(PageFragments.SEARCH_SETSALE_POSITION);
                    return;
                }
                if (event.getItemIndex() == NavDrawerLoggedIn.LOG_OUT_POSITION) {
                    final LogoutConfirmDialogFragment dialog = new LogoutConfirmDialogFragment();
                    dialog.show(getSupportFragmentManager(), FragmentTag.LOGOUT_DIALOG);
                    return;
                }
                if (event.getItemIndex() == NavDrawerLoggedIn.SETTINGS_POSITION) {
                    final Intent intent = new Intent(this, AccountPreferencesActivity.class);
                    startActivity(intent);
                    return;
                }
            }
            else {
                switch (event.getItemIndex()) {
                    case NavDrawerLoggedOut.HOME_POSITION:
                        pageToPosition(PageFragments.SEARCH_GENERAL_POSITION);
                        break;
                    case NavDrawerLoggedOut.LOG_IN_POSITION:
                        final LoginDialogFragment loginDialog = new LoginDialogFragment();
                        loginDialog.show(getSupportFragmentManager(), FragmentTag.LOGIN_DIALOG);
                        break;
                    case NavDrawerLoggedOut.SETTINGS_POSITION:
                        final Intent intent = new Intent(this, AccountPreferencesActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        }
        catch (final Exception e) {
            showErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
        }
    }

    /**
     * Perform necessary OnYard initialization. Ensure that sync account exists and perform on
     * demand sync if associated preference is enabled.
     * 
     * @throws IOException
     */
    private void initializeOnYard() throws IOException {
        final OnYardPreferences preferences = new OnYardPreferences(this);
        preferences.setDefaultSyncPrefs();

        SyncHelper.createOnYardAccount(getApplicationContext());
        SyncHelper.enableSync(getApplicationContext());
        SyncHelper.updateSyncInterval(getApplicationContext(),
                SyncHelper.getSyncIntervalFromDb(getApplicationContext()));

        // apply LeadTools license
        InputStream licenseStream = null;
        try {
            licenseStream = getAssets().open(OnYard.LEADTOOLS_LICENSE_FILENAME);
            RasterSupport.setLicense(licenseStream, OnYard.LEADTOOLS_LICENSE_KEY);
        }
        catch (final Exception e) {
            logError(e);
        }
        finally {
            if (licenseStream != null) {
                licenseStream.close();
            }
        }
    }

    private void onLogoutSuccessful() {
        onAuthChange();
    }

    private void onLogoutFailed() {
        dismissProgressDialog();
        showErrorDialog(ErrorMessage.LOGOUT_NO_NETWORK);
    }

    private void onAuthChange() {
        dismissProgressDialog();
        initializePager();
        invalidateOptionsMenu();
        initializeNavDrawer();
    }

    @Override
    protected List<Fragment> getLoggedInPagerFragments() {
        try {
            final List<Fragment> fragmentList = new ArrayList<Fragment>();
            final OnYardPreferences preferences = new OnYardPreferences(this);

            fragmentList
            .add(PageFragments.SEARCH_GENERAL_POSITION, new SearchGeneralPageFragment());
            fragmentList.add(PageFragments.SEARCH_IMAGER_POSITION, new SearchImagerPageFragment());
            fragmentList.add(PageFragments.SEARCH_CHECKIN_POSITION, new SearchCheckinPageFragment());

            PageFragments.SEARCH_ENHANCEMENTS_POSITION = preferences.isEnhancementsEnabled() ? 3
                    : -1;
            PageFragments.SEARCH_LOCATION_POSITION = Math.max(
                    PageFragments.SEARCH_CHECKIN_POSITION,
                    PageFragments.SEARCH_ENHANCEMENTS_POSITION) + 1;
            PageFragments.SEARCH_SETSALE_POSITION = preferences.isSetSaleEnabled() ? PageFragments.SEARCH_LOCATION_POSITION + 1
                    : -1;

            if (PageFragments.SEARCH_ENHANCEMENTS_POSITION >= 0) {
                fragmentList.add(PageFragments.SEARCH_ENHANCEMENTS_POSITION,
                        new SearchEnhancementsPageFragment());
            }
            fragmentList.add(PageFragments.SEARCH_LOCATION_POSITION,
                    new SearchLocationPageFragment());
            if (PageFragments.SEARCH_SETSALE_POSITION >= 0) {
                fragmentList
                .add(PageFragments.SEARCH_SETSALE_POSITION, new SearchSetSalePageFragment());
            }

            return fragmentList;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.INITIALIZATION);
            logError(e);
            return new ArrayList<Fragment>();
        }
    }

    @Override
    protected List<Fragment> getLoggedOutPagerFragments() {
        try {
            final List<Fragment> fragmentList = new ArrayList<Fragment>();

            fragmentList
            .add(PageFragments.SEARCH_GENERAL_POSITION, new SearchGeneralPageFragment());

            return fragmentList;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.INITIALIZATION);
            logError(e);
            return new ArrayList<Fragment>();
        }
    }

    @Override
    protected int getViewPagerId() {
        return R.id.activity_search_pager;
    }

    @Override
    protected int getActionBarMenuId() {
        return R.menu.menu_search;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.activity_search_drawer_layout;
    }

    @Override
    protected int getDrawerFrameId() {
        return R.id.activity_search_nav_drawer_frame;
    }

    @Override
    protected ArrayList<NavDrawerItem> getNavDrawerItemList() {
        try {
            final OnYardPreferences preferences = new OnYardPreferences(this);
            final ArrayList<NavDrawerItem> drawerList = new ArrayList<NavDrawerItem>();

            final NavDrawerItem homeItem = new NavDrawerItem(NavDrawerTitle.SEARCH_GENERAL,
                    R.drawable.ic_onyard_logo_dark,
                    getCurrentPagePosition() == PageFragments.SEARCH_GENERAL_POSITION,
                    NavDrawerItemType.PRIMARY);
            final NavDrawerItem imagerItem = new NavDrawerItem(NavDrawerTitle.SEARCH_IMAGER,
                    R.drawable.ic_action_camera_dark,
                    getCurrentPagePosition() == PageFragments.SEARCH_IMAGER_POSITION,
                    NavDrawerItemType.PRIMARY);
            final NavDrawerItem checkinItem = new NavDrawerItem(NavDrawerTitle.SEARCH_CHECKIN,
                    R.drawable.ic_action_paste_dark,
                    getCurrentPagePosition() == PageFragments.SEARCH_CHECKIN_POSITION,
                    NavDrawerItemType.PRIMARY);
            final NavDrawerItem enhancementItem = new NavDrawerItem(
                    NavDrawerTitle.SEARCH_ENHANCEMENTS, R.drawable.ic_action_enhancement_dark,
                    getCurrentPagePosition() == PageFragments.SEARCH_ENHANCEMENTS_POSITION,
                    NavDrawerItemType.PRIMARY);
            final NavDrawerItem locationItem = new NavDrawerItem(NavDrawerTitle.SEARCH_LOCATION,
                    R.drawable.ic_action_place_dark,
                    getCurrentPagePosition() == PageFragments.SEARCH_LOCATION_POSITION,
                    NavDrawerItemType.PRIMARY);
            final NavDrawerItem setSaleItem = new NavDrawerItem(NavDrawerTitle.SEARCH_SETSALE,
                    R.drawable.ic_action_go_to_today_dark,
                    getCurrentPagePosition() == PageFragments.SEARCH_SETSALE_POSITION,
                    NavDrawerItemType.PRIMARY);
            final NavDrawerItem settingsItem = new NavDrawerItem(NavDrawerTitle.SETTINGS,
                    R.drawable.ic_action_settings, false, NavDrawerItemType.SECONDARY);
            final NavDrawerItem logoutItem = new NavDrawerItem(NavDrawerTitle.LOG_OUT,
                    R.drawable.ic_action_cancel, false, NavDrawerItemType.SECONDARY);
            final NavDrawerItem loginItem = new NavDrawerItem(NavDrawerTitle.LOG_IN,
                    R.drawable.ic_action_accounts, false, NavDrawerItemType.SECONDARY);

            if (AuthenticationHelper.isAnyUserLoggedIn(getContentResolver())) {
                drawerList.add(NavDrawerLoggedIn.HOME_POSITION, homeItem);
                drawerList.add(NavDrawerLoggedIn.IMAGER_POSITION, imagerItem);
                drawerList.add(NavDrawerLoggedIn.CHECKIN_POSITION, checkinItem);

                NavDrawerLoggedIn.ENHANCEMENTS_POSITION = preferences.isEnhancementsEnabled() ? 3
                        : -1;
                NavDrawerLoggedIn.LOCATION_POSITION = Math.max(NavDrawerLoggedIn.CHECKIN_POSITION,
                        NavDrawerLoggedIn.ENHANCEMENTS_POSITION) + 1;
                NavDrawerLoggedIn.SETSALE_POSITION = preferences.isSetSaleEnabled() ? NavDrawerLoggedIn.LOCATION_POSITION + 1
                        : -1;
                NavDrawerLoggedIn.SETTINGS_POSITION = Math.max(NavDrawerLoggedIn.SETSALE_POSITION,
                        NavDrawerLoggedIn.LOCATION_POSITION) + 1;
                NavDrawerLoggedIn.LOG_OUT_POSITION = NavDrawerLoggedIn.SETTINGS_POSITION + 1;

                if (NavDrawerLoggedIn.ENHANCEMENTS_POSITION >= 0) {
                    drawerList.add(NavDrawerLoggedIn.ENHANCEMENTS_POSITION, enhancementItem);
                }
                drawerList.add(NavDrawerLoggedIn.LOCATION_POSITION, locationItem);
                if (NavDrawerLoggedIn.SETSALE_POSITION >= 0) {
                    drawerList.add(NavDrawerLoggedIn.SETSALE_POSITION, setSaleItem);
                }
                drawerList.add(NavDrawerLoggedIn.SETTINGS_POSITION, settingsItem);
                drawerList.add(NavDrawerLoggedIn.LOG_OUT_POSITION, logoutItem);
            }
            else {
                drawerList.add(NavDrawerLoggedOut.HOME_POSITION, homeItem);
                drawerList.add(NavDrawerLoggedOut.SETTINGS_POSITION, settingsItem);
                drawerList.add(NavDrawerLoggedOut.LOG_IN_POSITION, loginItem);
            }

            return drawerList;
        }
        catch (final Exception e) {
            showErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return new ArrayList<NavDrawerItem>();
        }
    }
}
