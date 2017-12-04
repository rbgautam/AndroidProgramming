package com.iaai.onyard.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;

import com.iaai.onyard.R;
import com.iaai.onyard.adapter.VehicleArrayAdapter;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.application.OnYard.SearchMode;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.classes.SearchHelper;
import com.iaai.onyard.event.SessionDataCreatedEvent;
import com.iaai.onyard.task.GetPendingCountTask;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.SelectionClause;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.squareup.otto.Subscribe;

/**
 * Activity that contains a list of vehicles for the user to choose from.
 */
public class VehicleListActivity extends BaseListActivity {

    /**
     * Dialog indicating search error. Dialog closes when OK pressed.
     */
    private static final int SEARCH_ERROR_ID = 1;
    /**
     * Dialog showing a spinning animation and telling the user that the search is
     * being performed. Dialog closes when search is complete.
     */
    private static final int SEARCHING_ID = 2;

    private String mSearchCriteria;
    private SearchMode mSearchMode;
    private boolean mIsItemLoading;

    /**
     * Inflate Activity UI and set View initial values and listeners.
     * 
     * Called when the activity is starting.
     * 
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.vehicle_list);

            final Bundle intentExtras = getIntent().getExtras();

            if (intentExtras != null) {
                if (intentExtras.containsKey(IntentExtraKey.SEARCH_MODE)) {
                    mSearchMode = (SearchMode) getIntent().getExtras().getSerializable(
                            IntentExtraKey.SEARCH_MODE);
                }

                if (intentExtras.containsKey(IntentExtraKey.SEARCH_VAL)) {
                    final String searchVal = getIntent().getExtras().getString(
                            IntentExtraKey.SEARCH_VAL);
                    mSearchCriteria = searchVal;
                }
            }
            final VehicleInfo[] vehicles = getVehicleArrayFromCursor(getVehiclesBySearchVal(mSearchCriteria));

            final VehicleArrayAdapter adapter = new VehicleArrayAdapter(this, vehicles);
            setListAdapter(adapter);

            new GetPendingCountTask().execute(new Object[] { getApplicationContext(), this });
        }
        catch (final Exception e)
        {
            LogHelper.logError(getApplicationContext(), e, this.getClass().getSimpleName());
            showErrorDialogAndExit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mIsItemLoading = false;
    }

    /**
     * Get the vehicle that was clicked, construct an intent with the vehicle's
     * stock number as an extra, and send the user to the VehicleDetailsActivity.
     * 
     * Called when an item in the list is selected. Subclasses
     * should override. Subclasses can call getListView().getItemAtPosition(position)
     * if they need to access the data associated with the selected item.
     * 
     * @param l The ListView where the click happened.
     * @param v The view that was clicked within the ListView.
     * @param position The position of the view in the list.
     * @param id The row id of the item that was clicked.
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        try
        {
            if (!mIsItemLoading) {
                mIsItemLoading = true;
                showProgressDialog();
                super.onListItemClick(l, v, position, id);

                final VehicleInfo vehicle = (VehicleInfo) getListAdapter().getItem(position);

                createSessionData(vehicle.getStockNumber());
            }
        }
        catch (final Exception e)
        {
            LogHelper.logError(getApplicationContext(), e, this.getClass().getSimpleName());
            showErrorDialogAndExit();
        }
    }

    /**
     * If using showDialog(id), the activity will call through to this method the first
     * time, and hang onto it thereafter. Any dialog that is created by this method will
     * automatically be saved and restored, including whether it is showing.
     * 
     * @param id The id of the dialog.
     * @param args The dialog arguments provided to showDialog(int, Bundle).
     * @return The dialog. If null is returned, the dialog will not be created.
     */
    @Override
    protected Dialog onCreateDialog(int id, Bundle args)
    {
        try
        {
            AlertDialog dialog;
            AlertDialog.Builder builder;
            switch(id)
            {
                case SEARCHING_ID:
                    builder = new ProgressDialog.Builder(this);
                    builder.setMessage("Searching. Please wait...")
                    .setCancelable(true);
                    dialog = builder.create();
                    break;
                case SEARCH_ERROR_ID:
                    builder = new AlertDialog.Builder(this);
                    builder.setMessage("There was an error while searching. Please try again.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            VehicleListActivity.this.finish();
                        }
                    });
                    dialog = builder.create();
                    break;
                default:
                    dialog = null;
                    break;
            }
            return dialog;
        }
        catch (final Exception e)
        {
            LogHelper.logWarning(getApplicationContext(), e, this.getClass().getSimpleName());
            return new Dialog(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            final MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.menu_list, menu);

            final ActionBar actionBar = getActionBar();

            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
                    getActionBarColorId())));

            actionBar.setDisplayShowTitleEnabled(true);

            actionBar.setTitle(getListAdapter().getCount() + " Results Matching \""
                    + mSearchCriteria + "\"");

            return true;
        }
        catch (final Exception e) {
            LogHelper.logError(getApplicationContext(), e, this.getClass().getSimpleName());
            showErrorDialogAndExit();
            return false;
        }
    }

    private int getActionBarColorId() {
        switch (mSearchMode) {
            case GENERAL:
                return R.color.iaa_red;
            case IMAGER:
                return R.color.imager_blue;
            case CHECKIN:
                return R.color.checkin_green;
            case ENHANCEMENT:
                return R.color.enhancements_purple;
            case LOCATION:
                return R.color.location_yellow;
            case SETSALE:
                return R.color.setsale_orange;
            default:
                return R.color.iaa_red;
        }
    }

    /**
     * Override on the Camera button to do nothing.
     * 
     * @param keyCode
     * @param event
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_CAMERA) {
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
        catch (final Exception e) {
            LogHelper.logWarning(this, e, this.getClass().getSimpleName());
            return false;
        }
    }

    /**
     * Query the Vehicles database for details where corresponding VIN and/or stock number
     * contain the search value.
     * 
     * @param searchVal The search value.
     * @return A cursor containing the query results.
     */
    private Cursor getVehiclesBySearchVal(String searchVal)
    {
        final String detailsProj[] = new String[]{
                OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER,
                OnYardContract.Vehicles.COLUMN_NAME_VIN, OnYardContract.Vehicles.COLUMN_NAME_YEAR,
                OnYardContract.Vehicles.COLUMN_NAME_MAKE, OnYardContract.Vehicles.COLUMN_NAME_MODEL
        };

        final SelectionClause selection = new SearchHelper(searchVal).getVehicleSearchSelection();

        if (mSearchMode == SearchMode.CHECKIN) {
            selection.append("AND",
                    DataHelper.getStatusFilterClause(VehicleInfo.getCheckinStatusCodes()));
        }
        if (mSearchMode == SearchMode.SETSALE) {
            selection.append("AND",
                    DataHelper.getStatusFilterClause(VehicleInfo.getSetSaleStatusCodes()));

            final OnYardPreferences preferences = new OnYardPreferences(this);
            selection.append("AND",
                    DataHelper.getAuctionDateFilterClause(preferences.getSelectedAuctionDate()));
            selection.append("AND",
                    DataHelper.getAdminBranchFilterClause(preferences.getEffectiveBranchNumber()));
        }

        final Cursor queryResult = getContentResolver().query(
                OnYardContract.Vehicles.CONTENT_URI,
                detailsProj,
                selection.getSelection(),
                selection.getSelectionArgs(),
                null);

        startManagingCursor(queryResult);

        return queryResult;
    }

    /**
     * Construct an array of VehicleInfo objects from a cursor containing vehicle details.
     * 
     * @param cursor The cursor containing vehicle details.
     * @return The array of VehicleInfo objects.
     */
    private VehicleInfo[] getVehicleArrayFromCursor(Cursor cursor)
    {
        final VehicleInfo[] vehicles = new VehicleInfo[cursor.getCount()];
        int vehIndex = 0;

        cursor.moveToFirst();

        do
        {
            vehicles[vehIndex] = new VehicleInfo(cursor);

            vehIndex++;
        } while(cursor.moveToNext());

        return vehicles;
    }

    /**
     * Show a generic search error dialog. This activity will finish when "OK" is pressed.
     */
    private void showErrorDialogAndExit()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    showDialog(SEARCH_ERROR_ID);
                }
                catch (final Exception e)
                {
                    LogHelper.logWarning(getApplicationContext(), e, this.getClass()
                            .getSimpleName());
                    finish();
                }
            }
        });
    }

    @Subscribe
    public void onSessionDataCreated(SessionDataCreatedEvent event) {
        final Intent intent = new Intent(this, StockPagerActivity.class);
        intent.putExtra(IntentExtraKey.SEARCH_MODE, mSearchMode);
        startActivity(intent);
    }
}
