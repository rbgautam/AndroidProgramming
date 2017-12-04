package com.iaai.onyard.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.iaai.onyard.R;
import com.iaai.onyard.activity.fragment.StockCheckinPageFragment;
import com.iaai.onyard.activity.fragment.StockDetailsPageFragment;
import com.iaai.onyard.activity.fragment.StockEnhancementPageFragment;
import com.iaai.onyard.activity.fragment.StockImagerPageFragment;
import com.iaai.onyard.activity.fragment.StockLocationPageFragment;
import com.iaai.onyard.activity.fragment.StockPageFragment;
import com.iaai.onyard.activity.fragment.StockSetSalePageFragment;
import com.iaai.onyard.application.OnYard.DialogTitle;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.application.OnYard.FragmentTag;
import com.iaai.onyard.application.OnYard.InfoMessage;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.application.OnYard.NavDrawerItemType;
import com.iaai.onyard.application.OnYard.NavDrawerTitle;
import com.iaai.onyard.application.OnYard.SearchMode;
import com.iaai.onyard.application.OnYard.SetSaleId;
import com.iaai.onyard.classes.NavDrawerItem;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.classes.SaveAction;
import com.iaai.onyard.classes.SetSaleField;
import com.iaai.onyard.dialog.CancelConfirmDialogFragment;
import com.iaai.onyard.event.CancelStockEvent;
import com.iaai.onyard.event.NavDrawerItemClickedEvent;
import com.iaai.onyard.event.SaveConditionsChangedEvent;
import com.iaai.onyard.event.VehicleDetailsUpdatedEvent;
import com.iaai.onyard.session.SetSaleData;
import com.iaai.onyard.utility.AuthenticationHelper;
import com.iaai.onyard.utility.ImageDirHelper;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import static com.google.android.gms.common.GooglePlayServicesClient.*;

public class StockPagerActivity extends PagerActivity implements
ConnectionCallbacks,
OnConnectionFailedListener, LocationListener {

    private static class PageFragments {
        private static int STOCK_DETAILS_POSITION = 0;
        private static int STOCK_IMAGER_POSITION = 1;
        private static int STOCK_CHECKIN_POSITION = 2;
        private static int STOCK_ENHANCEMENT_POSITION = 3;
        private static int STOCK_LOCATION_POSITION = 4;
        private static int STOCK_SETSALE_POSITION = 5;
    }

    private static class NavDrawerLoggedOut {
        private static int DETAILS_POSITION = 0;
        private static int MAP_POSITION = 1;
        private static int CANCEL_POSITION = 2;
    }

    private static class NavDrawerLoggedIn {
        private static int DETAILS_POSITION = 0;
        private static int IMAGER_POSITION = 1;
        private static int CHECKIN_POSITION = 2;
        private static int ENHANCEMENT_POSITION = 3;
        private static int LOCATION_POSITION = 4;
        private static int SETSALE_POSITION = 5;
        private static int MAP_POSITION = 6;
        private static int CANCEL_POSITION = 7;
    }

    private boolean mReturningWithResult;
    private int mRequestCode;
    private int mResultCode;
    private Intent mData;

    private LocationRequest mLocationRequest;
    private LocationClient mLocationClient;
    private boolean mIsLocationClientConnecting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_stock);

            initializePager();
            initializeNavDrawer();

            final Bundle intentExtras = getIntent().getExtras();
            if (intentExtras != null && intentExtras.containsKey(IntentExtraKey.SEARCH_MODE)) {
                switch ((SearchMode) intentExtras.get(IntentExtraKey.SEARCH_MODE)) {
                    case GENERAL:
                        pageToPosition(PageFragments.STOCK_DETAILS_POSITION);
                        break;
                    case IMAGER:
                        pageToPosition(PageFragments.STOCK_IMAGER_POSITION);
                        break;
                    case CHECKIN:
                        pageToPosition(PageFragments.STOCK_CHECKIN_POSITION);
                        break;
                    case ENHANCEMENT:
                        pageToPosition(PageFragments.STOCK_ENHANCEMENT_POSITION);
                        break;
                    case LOCATION:
                        pageToPosition(PageFragments.STOCK_LOCATION_POSITION);
                        break;
                    case SETSALE:
                        pageToPosition(PageFragments.STOCK_SETSALE_POSITION);
                        break;
                    default:
                        break;
                }
            }

            PicassoTools.clearCache(Picasso.with(this));
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            stopLocationUpdates();
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            invalidateOptionsMenu();
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
        }

        try {
            if (getCurrentPagePosition() == PageFragments.STOCK_LOCATION_POSITION
                    || getCurrentPagePosition() == PageFragments.STOCK_SETSALE_POSITION) {
                startLocationUpdates();
            }
        }
        catch (final Exception e) {
            logWarning(e);
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
        }
        catch (final Exception e) {
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
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK: {
                    if (getCurrentPagePosition() == PageFragments.STOCK_CHECKIN_POSITION
                            && ((StockCheckinPageFragment) getCurrentPage()).onBackPressed()
                            || getCurrentPagePosition() == PageFragments.STOCK_ENHANCEMENT_POSITION
                            && ((StockEnhancementPageFragment) getCurrentPage()).onBackPressed()
                            || getCurrentPagePosition() == PageFragments.STOCK_SETSALE_POSITION
                            && ((StockSetSalePageFragment) getCurrentPage()).onBackPressed()) {
                        return true;
                    }
                    if (getSessionData().hasUnsavedData()) {
                        final CancelConfirmDialogFragment dialog = new CancelConfirmDialogFragment();
                        dialog.show(getSupportFragmentManager(), FragmentTag.CANCEL_CONFIRM_DIALOG);
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
    public void onStockCancel(CancelStockEvent event) {
        try {
            cancelStockSelection();
            ImageDirHelper.deleteAllUnsavedImages(getApplicationContext());
        }
        catch (final Exception e) {
            logError(e);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            super.onCreateOptionsMenu(menu);

            final MenuItem itemSave = menu.findItem(R.id.action_save);
            final MenuItem itemVerify = menu.findItem(R.id.action_verify);

            final StockPageFragment currentPage = (StockPageFragment) getCurrentPage();
            if (currentPage != null) {
                if (currentPage.isSaveAllowed()) {
                    itemSave.setVisible(true);
                    itemVerify.setVisible(false);
                }
                else {
                    itemSave.setVisible(false);
                    if (currentPage.isVerifyRequired()) {
                        itemVerify.setVisible(true);
                    }
                    else {
                        itemVerify.setVisible(false);
                    }
                }
            }

            return true;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == R.id.action_verify || item.getItemId() == R.id.action_save) {
                final SaveAction saveAction = new SaveAction(item.getItemId(),
                        getCurrentPagePosition() == PageFragments.STOCK_SETSALE_POSITION);

                getSessionData().commitData(getApplicationContext(), getLastKnownLocation(),
                        saveAction);
                navigatePostCommit(saveAction);
                return true;
            }
            else {
                return super.onOptionsItemSelected(item);
            }
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.SAVE);
            logError(e);
            return false;
        }
    }

    @Subscribe
    public void onSaveConditionsChanged(SaveConditionsChangedEvent event) {
        try {
            invalidateOptionsMenu();
        }
        catch (final Exception e) {
            showErrorDialog(ErrorMessage.CHECKIN);
            logError(e);
        }
    }

    @Subscribe
    public void onNavDrawerItemClick(NavDrawerItemClickedEvent event) {
        try {
            super.closeNavDrawer();
            if (AuthenticationHelper.isAnyUserLoggedIn(getContentResolver())) {
                if (event.getItemIndex() == NavDrawerLoggedIn.DETAILS_POSITION) {
                    pageToPosition(PageFragments.STOCK_DETAILS_POSITION);
                }
                else
                    if (event.getItemIndex() == NavDrawerLoggedIn.IMAGER_POSITION) {
                        pageToPosition(PageFragments.STOCK_IMAGER_POSITION);
                    }
                    else
                        if (event.getItemIndex() == NavDrawerLoggedIn.CHECKIN_POSITION) {
                            pageToPosition(PageFragments.STOCK_CHECKIN_POSITION);
                        }
                        else
                            if (event.getItemIndex() == NavDrawerLoggedIn.ENHANCEMENT_POSITION) {
                                pageToPosition(PageFragments.STOCK_ENHANCEMENT_POSITION);
                            }
                            else
                                if (event.getItemIndex() == NavDrawerLoggedIn.LOCATION_POSITION) {
                                    pageToPosition(PageFragments.STOCK_LOCATION_POSITION);
                                }
                                else
                                    if (event.getItemIndex() == NavDrawerLoggedIn.SETSALE_POSITION) {
                                        pageToPosition(PageFragments.STOCK_SETSALE_POSITION);
                                    }
                                    else
                                        if (event.getItemIndex() == NavDrawerLoggedIn.MAP_POSITION) {
                                            final Intent intent = new Intent(this, VehicleMapActivity.class);
                                            startActivity(intent);
                                        }
                                        else
                                            if (event.getItemIndex() == NavDrawerLoggedIn.CANCEL_POSITION) {
                                                if (getSessionData().hasUnsavedData()) {
                                                    final CancelConfirmDialogFragment dialog = new CancelConfirmDialogFragment();
                                                    dialog.show(getSupportFragmentManager(),
                                                            FragmentTag.CANCEL_CONFIRM_DIALOG);
                                                }
                                                else {
                                                    cancelStockSelection();
                                                }
                                            }
                                            else {}
            }
            else {
                if (event.getItemIndex() == NavDrawerLoggedOut.DETAILS_POSITION) {
                    pageToPosition(PageFragments.STOCK_DETAILS_POSITION);
                }
                else
                    if (event.getItemIndex() == NavDrawerLoggedOut.MAP_POSITION) {
                        final Intent intent = new Intent(this, VehicleMapActivity.class);
                        startActivity(intent);
                    }
                    else
                        if (event.getItemIndex() == NavDrawerLoggedOut.CANCEL_POSITION) {
                            if (getSessionData().hasUnsavedData()) {
                                final CancelConfirmDialogFragment dialog = new CancelConfirmDialogFragment();
                                dialog.show(getSupportFragmentManager(),
                                        FragmentTag.CANCEL_CONFIRM_DIALOG);
                            }
                            else {
                                cancelStockSelection();
                            }
                        }
                        else {}
            }
        }
        catch (final Exception e) {
            showErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
        }
    }

    private void startLocationUpdates() {
        if (mLocationClient == null || !mIsLocationClientConnecting
                && !mLocationClient.isConnected()) {
            mIsLocationClientConnecting = true;

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(2000);
            mLocationRequest.setFastestInterval(500);

            mLocationClient = new LocationClient(this, this, this);
            mLocationClient.connect();
        }
    }

    private void stopLocationUpdates() {
        if (mLocationClient != null) {
            if (mLocationClient.isConnected()) {
                mLocationClient.removeLocationUpdates(this);
            }
            mLocationClient.disconnect();
        }
    }

    private void cancelStockSelection() {
        final Intent intent = new Intent(this, SearchPagerActivity.class);
        if (getCurrentPagePosition() == PageFragments.STOCK_DETAILS_POSITION) {
            intent.putExtra(IntentExtraKey.SEARCH_MODE, SearchMode.GENERAL);
        }
        else
            if (getCurrentPagePosition() == PageFragments.STOCK_IMAGER_POSITION) {
                intent.putExtra(IntentExtraKey.SEARCH_MODE, SearchMode.IMAGER);
            }
            else
                if (getCurrentPagePosition() == PageFragments.STOCK_CHECKIN_POSITION) {
                    intent.putExtra(IntentExtraKey.SEARCH_MODE, SearchMode.CHECKIN);
                }
                else
                    if (getCurrentPagePosition() == PageFragments.STOCK_ENHANCEMENT_POSITION) {
                        intent.putExtra(IntentExtraKey.SEARCH_MODE, SearchMode.ENHANCEMENT);
                    }
                    else
                        if (getCurrentPagePosition() == PageFragments.STOCK_LOCATION_POSITION) {
                            intent.putExtra(IntentExtraKey.SEARCH_MODE, SearchMode.LOCATION);
                        }
                        else
                            if (getCurrentPagePosition() == PageFragments.STOCK_SETSALE_POSITION) {
                                intent.putExtra(IntentExtraKey.SEARCH_MODE, SearchMode.SETSALE);
                            }
                            else {
                                intent.putExtra(IntentExtraKey.SEARCH_MODE, SearchMode.GENERAL);
                            }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    private void navigatePostCommit(SaveAction saveAction) {
        final boolean imagerIncomplete = getSessionData().isImagerIncomplete();
        final boolean checkinIncomplete = getSessionData().isCheckinIncomplete();
        final boolean locationIncomplete = getSessionData().isLocationIncomplete();

        final boolean setSaleCouldNotCommit = getSessionData().isSetSaleCommitAllowed()
                && getSessionData().shouldTrySetSaleCommit(saveAction)
                && !getSessionData().wasSetSaleCommitted();

        if (!imagerIncomplete && !checkinIncomplete && !locationIncomplete
                && !setSaleCouldNotCommit) {
            cancelStockSelection();
        }
        else {
            // recreate completed pages
            if (getSessionData().wasImagerCommitted()) {
                getSessionData().recreateImagerData(getApplicationContext());
            }
            if (getSessionData().wasCheckinCommitted()) {
                getSessionData().recreateCheckinData(getApplicationContext());
            }
            if (getSessionData().wasEnhancementCommitted()) {
                getSessionData().recreateEnhancementData();
            }
            if (getSessionData().wasLocationCommitted()) {
                getSessionData().recreateLocationData();
            }
            if (getSessionData().wasSetSaleCommitted()) {
                getSessionData().recreateSetSaleData(getApplicationContext());
            }

            getEventBus().post(new VehicleDetailsUpdatedEvent());

            // page to appropriate position
            final int currentPagePos = getCurrentPagePosition();
            if (currentPagePos == PageFragments.STOCK_IMAGER_POSITION) {
                if (setSaleCouldNotCommit) {
                    pageToInvalidSetSale();
                }
                else {
                    if(checkinIncomplete) {
                        pageToIncompleteCheckin();
                    }
                    else {
                        if(locationIncomplete) {
                            pageToIncompleteLocation();
                        }
                    }
                }
                return;
            }

            if(currentPagePos == PageFragments.STOCK_CHECKIN_POSITION) {
                if(setSaleCouldNotCommit) {
                    pageToInvalidSetSale();
                }
                else {
                    if(locationIncomplete) {
                        pageToIncompleteLocation();
                    }
                    else {
                        if (imagerIncomplete) {
                            pageToIncompleteImager();
                        }
                    }
                }
                return;
            }

            if (currentPagePos == PageFragments.STOCK_ENHANCEMENT_POSITION) {
                if(setSaleCouldNotCommit) {
                    pageToInvalidSetSale();
                }
                else {
                    if (locationIncomplete) {
                        pageToIncompleteLocation();
                    }
                    else {
                        if(imagerIncomplete) {
                            pageToIncompleteImager();
                        }
                        else {
                            if (checkinIncomplete) {
                                pageToIncompleteCheckin();
                            }
                        }
                    }
                    return;
                }
            }

            if(currentPagePos == PageFragments.STOCK_LOCATION_POSITION) {
                if (setSaleCouldNotCommit) {
                    pageToInvalidSetSale();
                }
                else {
                    if(imagerIncomplete) {
                        pageToIncompleteImager();
                    }
                    else {
                        if(checkinIncomplete) {
                            pageToIncompleteCheckin();
                        }
                    }
                }
                return;
            }

            if (currentPagePos == PageFragments.STOCK_SETSALE_POSITION) {
                if (setSaleCouldNotCommit) {
                    showInvalidSetSaleDialog();
                }
                else {
                    if (imagerIncomplete) {
                        pageToIncompleteImager();
                    }
                    else {
                        if (checkinIncomplete) {
                            pageToIncompleteCheckin();
                        }
                        else {
                            if (locationIncomplete) {
                                pageToIncompleteLocation();
                            }
                        }
                    }
                }
                return;
            }
        }
    }

    private void pageToIncompleteImager() {
        pageToPosition(PageFragments.STOCK_IMAGER_POSITION);
        showErrorDialog(ErrorMessage.INCOMPLETE_IMAGER, DialogTitle.INCOMPLETE_DATA);
    }

    private void pageToIncompleteCheckin() {
        pageToPosition(PageFragments.STOCK_CHECKIN_POSITION);
        showErrorDialog(ErrorMessage.INCOMPLETE_CHECKIN, DialogTitle.INCOMPLETE_DATA);
    }

    private void pageToIncompleteLocation() {
        pageToPosition(PageFragments.STOCK_LOCATION_POSITION);
        showErrorDialog(ErrorMessage.INCOMPLETE_LOCATION, DialogTitle.INCOMPLETE_DATA);
    }

    private void pageToInvalidSetSale() {
        pageToPosition(PageFragments.STOCK_SETSALE_POSITION);
        showInvalidSetSaleDialog();
    }

    private void showInvalidSetSaleDialog() {
        final String stockNumUsingLaneItem = getSessionData().getStockNumUsingLaneItem(
                getApplicationContext());
        final String thisStockNumber = getSessionData().getVehicleInfo().getStockNumber();
        final SetSaleData setSaleData = getSessionData().getSetSaleData();
        final int currentAuctionNumber = Integer.parseInt(setSaleData
                .getFieldById(SetSaleId.AUCTION_NUMBER).getSelectedOption().getValue());
        final int currentAuctionItemSeqNumber = Integer.parseInt(setSaleData.getFieldById(
                SetSaleId.AUCTION_ITEM_SEQUENCE_NUMBER).getEnteredValue());

        if (stockNumUsingLaneItem.equals(thisStockNumber)) {
            // show popup stating that lane/item is used by this stock
            showErrorDialog(String.format(InfoMessage.LANE_ITEM_ASSIGNED_CURRENT_STOCK,
                    SetSaleField.getAuctionLaneFromNumber(currentAuctionNumber),
                    currentAuctionItemSeqNumber),
                    DialogTitle.LANE_ITEM_ASSIGNED);
        }
        else {
            // show popup stating that lane/item is used by different stock
            showErrorDialog(String.format(InfoMessage.LANE_ITEM_ASSIGNED_DIFFERENT_STOCK,
                    SetSaleField.getAuctionLaneFromNumber(currentAuctionNumber),
                    currentAuctionItemSeqNumber, stockNumUsingLaneItem),
                    DialogTitle.LANE_ITEM_ASSIGNED);
        }
    }

    @Override
    protected List<Fragment> getLoggedInPagerFragments() {
        try {
            final OnYardPreferences preferences = new OnYardPreferences(this);
            final List<Fragment> fragmentList = new ArrayList<Fragment>();

            PageFragments.STOCK_DETAILS_POSITION = 0;
            PageFragments.STOCK_IMAGER_POSITION = 1;
            PageFragments.STOCK_CHECKIN_POSITION = getSessionData().getVehicleInfo()
                    .isCheckinEligible() ? 2 : -1;
            PageFragments.STOCK_ENHANCEMENT_POSITION = preferences.isEnhancementsEnabled() ? Math
                    .max(PageFragments.STOCK_IMAGER_POSITION, PageFragments.STOCK_CHECKIN_POSITION) + 1
                    : -1;
            PageFragments.STOCK_LOCATION_POSITION = Math.max(PageFragments.STOCK_IMAGER_POSITION,
                    Math.max(PageFragments.STOCK_CHECKIN_POSITION,
                            PageFragments.STOCK_ENHANCEMENT_POSITION)) + 1;
            PageFragments.STOCK_SETSALE_POSITION = shouldCreateSetSaleFragment()
                    && preferences.isSetSaleEnabled() ? PageFragments.STOCK_LOCATION_POSITION + 1
                            : -1;

            fragmentList.add(PageFragments.STOCK_DETAILS_POSITION, new StockDetailsPageFragment());
            fragmentList.add(PageFragments.STOCK_IMAGER_POSITION, new StockImagerPageFragment());
            if (PageFragments.STOCK_CHECKIN_POSITION >= 0) {
                fragmentList.add(PageFragments.STOCK_CHECKIN_POSITION,
                        new StockCheckinPageFragment());
            }
            if (PageFragments.STOCK_ENHANCEMENT_POSITION >= 0) {
                fragmentList.add(PageFragments.STOCK_ENHANCEMENT_POSITION,
                        new StockEnhancementPageFragment());
            }
            fragmentList
            .add(PageFragments.STOCK_LOCATION_POSITION, new StockLocationPageFragment());
            if (PageFragments.STOCK_SETSALE_POSITION >= 0) {
                fragmentList.add(PageFragments.STOCK_SETSALE_POSITION,
                        new StockSetSalePageFragment());
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

            fragmentList.add(PageFragments.STOCK_DETAILS_POSITION, new StockDetailsPageFragment());

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
        return R.id.activity_stock_pager;
    }

    @Override
    protected int getActionBarMenuId() {
        return R.menu.menu_stock;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.activity_stock_drawer_layout;
    }

    @Override
    protected int getDrawerFrameId() {
        return R.id.activity_stock_nav_drawer_frame;
    }

    @Override
    protected ArrayList<NavDrawerItem> getNavDrawerItemList() {
        try {
            final OnYardPreferences preferences = new OnYardPreferences(this);
            final ArrayList<NavDrawerItem> drawerList = new ArrayList<NavDrawerItem>();

            final NavDrawerItem detailsItem = new NavDrawerItem(NavDrawerTitle.STOCK_DETAILS,
                    R.drawable.ic_action_view_as_list,
                    getCurrentPagePosition() == PageFragments.STOCK_DETAILS_POSITION,
                    NavDrawerItemType.PRIMARY);
            final NavDrawerItem imagerItem = new NavDrawerItem(NavDrawerTitle.STOCK_IMAGER,
                    R.drawable.ic_action_camera_dark,
                    getCurrentPagePosition() == PageFragments.STOCK_IMAGER_POSITION,
                    NavDrawerItemType.PRIMARY);
            final NavDrawerItem checkinItem = new NavDrawerItem(NavDrawerTitle.STOCK_CHECKIN,
                    R.drawable.ic_action_paste_dark,
                    getCurrentPagePosition() == PageFragments.STOCK_CHECKIN_POSITION,
                    NavDrawerItemType.PRIMARY);
            final NavDrawerItem enhancementItem = new NavDrawerItem(
                    NavDrawerTitle.STOCK_ENHANCEMENTS, R.drawable.ic_action_enhancement_dark,
                    getCurrentPagePosition() == PageFragments.STOCK_ENHANCEMENT_POSITION,
                    NavDrawerItemType.PRIMARY);
            final NavDrawerItem locationItem = new NavDrawerItem(NavDrawerTitle.STOCK_LOCATION,
                    R.drawable.ic_action_place_dark,
                    getCurrentPagePosition() == PageFragments.STOCK_LOCATION_POSITION,
                    NavDrawerItemType.PRIMARY);
            final NavDrawerItem setSaleItem = new NavDrawerItem(NavDrawerTitle.STOCK_SETSALE,
                    R.drawable.ic_action_go_to_today_dark,
                    getCurrentPagePosition() == PageFragments.STOCK_SETSALE_POSITION,
                    NavDrawerItemType.PRIMARY);
            final NavDrawerItem mapItem = new NavDrawerItem(NavDrawerTitle.MAP,
                    R.drawable.ic_action_map, false, NavDrawerItemType.PRIMARY);
            final NavDrawerItem cancelItem = new NavDrawerItem(NavDrawerTitle.CANCEL,
                    R.drawable.ic_action_undo, false, NavDrawerItemType.SECONDARY);

            if (AuthenticationHelper.isAnyUserLoggedIn(getContentResolver())) {
                NavDrawerLoggedIn.DETAILS_POSITION = 0;
                NavDrawerLoggedIn.IMAGER_POSITION = 1;
                NavDrawerLoggedIn.CHECKIN_POSITION = getSessionData().getVehicleInfo()
                        .isCheckinEligible() ? 2 : -1;
                NavDrawerLoggedIn.ENHANCEMENT_POSITION = preferences.isEnhancementsEnabled() ? Math
                        .max(NavDrawerLoggedIn.IMAGER_POSITION, NavDrawerLoggedIn.CHECKIN_POSITION) + 1
                        : -1;
                NavDrawerLoggedIn.LOCATION_POSITION = Math.max(PageFragments.STOCK_IMAGER_POSITION,
                        Math.max(NavDrawerLoggedIn.CHECKIN_POSITION,
                                NavDrawerLoggedIn.ENHANCEMENT_POSITION)) + 1;
                NavDrawerLoggedIn.SETSALE_POSITION = shouldCreateSetSaleFragment()
                        && preferences.isSetSaleEnabled() ? NavDrawerLoggedIn.LOCATION_POSITION + 1
                                : -1;
                NavDrawerLoggedIn.MAP_POSITION = Math.max(NavDrawerLoggedIn.LOCATION_POSITION,
                        NavDrawerLoggedIn.SETSALE_POSITION) + 1;
                NavDrawerLoggedIn.CANCEL_POSITION = NavDrawerLoggedIn.MAP_POSITION + 1;

                drawerList.add(NavDrawerLoggedIn.DETAILS_POSITION, detailsItem);
                drawerList.add(NavDrawerLoggedIn.IMAGER_POSITION, imagerItem);
                if (NavDrawerLoggedIn.CHECKIN_POSITION >= 0) {
                    drawerList.add(NavDrawerLoggedIn.CHECKIN_POSITION, checkinItem);
                }
                if (NavDrawerLoggedIn.ENHANCEMENT_POSITION >= 0) {
                    drawerList.add(NavDrawerLoggedIn.ENHANCEMENT_POSITION, enhancementItem);
                }
                drawerList.add(NavDrawerLoggedIn.LOCATION_POSITION, locationItem);
                if (NavDrawerLoggedIn.SETSALE_POSITION >= 0) {
                    drawerList.add(NavDrawerLoggedIn.SETSALE_POSITION, setSaleItem);
                }
                drawerList.add(NavDrawerLoggedIn.MAP_POSITION, mapItem);
                drawerList.add(NavDrawerLoggedIn.CANCEL_POSITION, cancelItem);
            }
            else {
                NavDrawerLoggedOut.DETAILS_POSITION = 0;
                NavDrawerLoggedOut.MAP_POSITION = 1;
                NavDrawerLoggedOut.CANCEL_POSITION = 2;

                drawerList.add(NavDrawerLoggedOut.DETAILS_POSITION, detailsItem);
                drawerList.add(NavDrawerLoggedOut.MAP_POSITION, mapItem);
                drawerList.add(NavDrawerLoggedOut.CANCEL_POSITION, cancelItem);
            }

            return drawerList;
        }
        catch (final Exception e) {
            showErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return new ArrayList<NavDrawerItem>();
        }
    }

    private boolean shouldCreateSetSaleFragment() {
        return getSessionData().getVehicleInfo().isSetSaleEligible(
                Integer.parseInt(new OnYardPreferences(this).getEffectiveBranchNumber()));
    }

    @Override
    public void onPageSelected(int newPosition) {
        super.onPageSelected(newPosition);

        try {
            if (newPosition == PageFragments.STOCK_LOCATION_POSITION
                    || newPosition == PageFragments.STOCK_SETSALE_POSITION) {
                startLocationUpdates();
            }
            else {
                stopLocationUpdates();
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            setCurrentLocation(location);
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    @Override
    public void onDisconnected() {}

    @Override
    public void onConnected(Bundle dataBundle) {
        try {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
            mIsLocationClientConnecting = false;
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {}
}
