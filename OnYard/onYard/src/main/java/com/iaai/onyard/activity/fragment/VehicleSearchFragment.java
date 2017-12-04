package com.iaai.onyard.activity.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.activity.VehicleListActivity;
import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.application.OnYard.SearchMode;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.classes.SearchHelper;
import com.iaai.onyard.dialog.ErrorDialogFragment;
import com.iaai.onyard.event.ClearSearchFieldEvent;
import com.iaai.onyard.performancetest.Timer;
import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyardproviderapi.classes.SelectionClause;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;
import com.squareup.otto.Subscribe;


public class VehicleSearchFragment extends BaseFragment {

    @InjectView(R.id.txt_stock_search)
    EditText mSearchField;
    @InjectView(R.id.btn_vehicle_search)
    Button mSearchButton;
    @InjectView(R.id.btn_vehicle_voice_search)
    Button mVoiceButton;
    @InjectView(R.id.btn_vehicle_barcode_search)
    Button mScanButton;

    /**
     * Request code sent to speech recognition activity and returned when activity sends result
     * back.
     */
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
    /**
     * Request code sent to barcode scanner activity and returned when activity sends result back.
     */
    private static final int SCAN_BARCODE_REQUEST_CODE = 2;

    private static final String SEARCH_ERROR_MESSAGE = "There was an error while searching. Please try again.";
    private static final String TOO_MANY_RESULTS_ERROR_MESSAGE = "There were more than " + OnYard.MAX_LIST_STOCKS + " stocks found " +
            "that matched your search criteria. Please narrow down your search.";
    private static final String SPEECH_RECOGNITION_ERROR_MESSAGE = "There was an error while performing speech recognition. " +
            "Please try again.";
    private static final String SCAN_LOAD_ERROR_MESSAGE = "There was an error while loading the barcode scanner. " +
            "Please try again.";
    private static final String NO_STOCKS_FOUND_MESSAGE = "There were no stocks found that match your search "
            + "criteria. Please try again.";

    private static final String ERROR_DIALOG_FRAGMENT_TAG = "error_dialog";
    private static final String MODE_BUNDLE_KEY = "mode";
    private static final int KEYCODE_SCAN_HARDWARE_BUTTON = 303;

    private SearchMode mSearchMode;
    public static VehicleSearchFragment newInstance(SearchMode mode) {
        final VehicleSearchFragment frag = new VehicleSearchFragment();

        final Bundle args = new Bundle();
        args.putSerializable(MODE_BUNDLE_KEY, mode);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_vehicle_search, container, false);
        mSearchMode = (SearchMode) getArguments().getSerializable(MODE_BUNDLE_KEY);

        ButterKnife.inject(this, view);

        mSearchField.setOnKeyListener(onEnterPerformSearch());
        mSearchButton.setOnClickListener(onVehicleSearchButtonClick());
        mVoiceButton.setOnClickListener(onVoiceSearchButtonClick());
        mScanButton.setOnClickListener(onScanBarcodeButtonClick());

        mSearchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if previous length is 0 and new length > 1, perform search
                if (s != null && start == 0 && before == 0 && count > 1) {
                    performSearch();
                }
            }
        });

        if (OnYard.isDeviceFzX1()) {
            mScanButton.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Subscribe
    public void onClearSearchField(ClearSearchFieldEvent event) {
        mSearchField.setText("");
    }

    /**
     * If a result is returned from either the voice search or barcode scan activities,
     * automatically perform a vehicle search on said result. If a result is returned from the
     * camera activity, update the camera button accordingly. Called when an activity you launched
     * exits, giving you the requestCode you started it with, the resultCode it returned, and any
     * additional data from it. The resultCode will be RESULT_CANCELED if the activity explicitly
     * returned that, didn't return any result, or crashed during its operation.
     * 
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *            allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its
     *            setResult().
     * @param data An Intent, which can return result data to the caller (various data can be
     *            attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                final ArrayList<String> matches = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                String voiceResult = matches.get(0);
                final String voiceResultNoWhitespace = voiceResult.replace(" ", "");

                if (DataHelper.isWordOnlyNumeric(voiceResultNoWhitespace)) {
                    voiceResult = voiceResultNoWhitespace;
                }

                mSearchField.setText("");
                mSearchField.setText(voiceResult);
            }
            if (requestCode == SCAN_BARCODE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                final String contents = data.getStringExtra("SCAN_RESULT");

                mSearchField.setText("");
                mSearchField.setText(contents);
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
        catch (final Exception e) {
            showSearchErrorDialog();
            logError(e);
        }
    }

    /**
     * Get click listener for voice search button. When it is clicked, construct intent and begin
     * voice recognition activity, expecting a text value in return.
     * 
     * @return The onClickListener for the voice search button.
     */
    private View.OnClickListener onVoiceSearchButtonClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (canRecognizeSpeechInput()) {
                        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                        // Specify the calling package to identify your application
                        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                                .getPackage().getName());
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                                "Say a Make, Model, Stock Number, or VIN");
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

                        // Specify how many results you want to receive. The results will be sorted
                        // where the first result is the one with higher confidence.
                        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

                        getParentFragment().startActivityForResult(intent,
                                VOICE_RECOGNITION_REQUEST_CODE);
                    }
                }
                catch (final Exception e) {
                    showSpeechRecognitionErrorDialog();
                    logError(e);
                }
            }
        };
    }

    /**
     * Get click listener for barcode scanner button. When it is clicked, construct intent and begin
     * barcode scan activity, expecting a text value in return.
     * 
     * @return The onClickListener for the barcode scanner button.
     */
    private View.OnClickListener onScanBarcodeButtonClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    final Intent intent = new Intent(OnYard.ZXING_SCAN_ACTION);
                    intent.putExtra("SCAN_MODE", "ONE_D_MODE");
                    getParentFragment().startActivityForResult(intent, SCAN_BARCODE_REQUEST_CODE);
                }
                catch (final Exception e) {
                    showScanErrorDialog();
                    logError(e);
                }
            }
        };
    }

    /**
     * Get key listener for search textview. When enter key is pressed, perform search.
     * 
     * @return The onKeyListener for the barcode scanner button.
     */
    private View.OnKeyListener onEnterPerformSearch() {
        return new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                try {
                    if (event.getAction() == KeyEvent.ACTION_DOWN
                            && keyCode == KeyEvent.KEYCODE_ENTER) {
                        performSearch();
                        return true;
                    }
                    if (event.getAction() == KeyEvent.ACTION_DOWN
                            && keyCode == KEYCODE_SCAN_HARDWARE_BUTTON) {
                        mSearchField.setText("");
                        return true;
                    }
                    return false;
                }
                catch (final Exception e) {
                    showSearchErrorDialog();
                    logError(e);
                    return false;
                }
            }
        };
    }

    /**
     * Get click listener for search button. When it is clicked, perform search.
     * 
     * @return The onClickListener for the search button.
     */
    private View.OnClickListener onVehicleSearchButtonClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    performSearch();
                }
                catch (final Exception e) {
                    showSearchErrorDialog();
                    logError(e);
                }
            }
        };
    }

    /**
     * Determine whether the system offers speech recognition.
     * 
     * @return True if speech recognition available, false otherwise.
     */
    private boolean canRecognizeSpeechInput() {
        final Activity activity = getActivity();
        if (activity != null) {
            final List<ResolveInfo> activities = activity.getPackageManager()
                    .queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

            if (activities.size() > 0) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    /**
     * Perform a vehicle search on the text value contained in the search field. Display the search
     * progress dialog while this happens.
     */
    private void performSearch() {
        try {
            final String searchVal = mSearchField.getText().toString().trim();

            if (searchVal.trim().equals("")) {
                showNoStocksFoundDialog();
            }
            else {
                showProgressDialog();
                sendSearchResultIntent(searchVal);
            }
        }
        catch (final Exception e) {
            showSearchErrorDialog();
            logError(e);
        }
    }

    /**
     * Query the Vehicles database for stock numbers that match the search value.
     * 
     * @param searchVal The search value.
     * @return A cursor containing the query results.
     */
    private Cursor getVehicleSearchStocks(String searchVal) {
        final Activity activity = getActivity();
        if (activity != null) {
            final SelectionClause selection = new SearchHelper(searchVal).getVehicleSearchSelection();

            if (mSearchMode == SearchMode.CHECKIN) {
                selection.append("AND",
                        DataHelper.getStatusFilterClause(VehicleInfo.getCheckinStatusCodes()));
            }
            if (mSearchMode == SearchMode.SETSALE) {
                selection.append("AND",
                        DataHelper.getStatusFilterClause(VehicleInfo.getSetSaleStatusCodes()));

                final OnYardPreferences preferences = new OnYardPreferences(activity);
                selection.append("AND",
                        DataHelper.getAuctionDateFilterClause(preferences.getSelectedAuctionDate()));
                selection.append("AND",
                        DataHelper.getAdminBranchFilterClause(preferences.getEffectiveBranchNumber()));
            }

            return activity.getContentResolver().query(OnYardContract.Vehicles.CONTENT_URI,
                    new String[] { OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER },
                    selection.getSelection(), selection.getSelectionArgs(), null);
        }
        else {
            return null;
        }
    }

    /**
     * Determine a course of action based on the number of vehicles (x) found in the search. If x=0:
     * show a dialog stating that no vehicles were found. If x=1: construct an intent and send the
     * user to a new VehicleDetailsActivity, destroying this one in the process. If
     * max_list_stocks>x>1: construct an intent and send the user to a list containing all stocks
     * found. If x>max_list_stocks: show a dialog stating that too many vehicles were found.
     * 
     * @param searchVal The value on which to perform the vehicle search.
     */
    private void sendSearchResultIntent(String searchVal) {
        final Timer t = new Timer("MainActivity.performSearch");
        t.start();

        final Activity activity = getActivity();
        if (activity != null) {
            final Cursor queryResult = getVehicleSearchStocks(searchVal);
            activity.startManagingCursor(queryResult);
            final int resultCount = queryResult.getCount();

            switch (resultCount) {
                case 0:
                    showNoStocksFoundDialog();
                    dismissProgressDialog();
                    break;

                case 1:
                    queryResult.moveToFirst();
                    final String stockNum = queryResult.getString(queryResult
                            .getColumnIndex(OnYardContract.Vehicles.COLUMN_NAME_STOCK_NUMBER));

                    createSessionData(stockNum);
                    break;

                default:
                    if (resultCount > OnYard.MAX_LIST_STOCKS) {
                        showTooManyStocksFoundDialog();
                        dismissProgressDialog();
                    }
                    else {
                        final Intent intent = new Intent(activity, VehicleListActivity.class);
                        intent.putExtra(IntentExtraKey.SEARCH_MODE, mSearchMode);
                        intent.putExtra(IntentExtraKey.SEARCH_VAL, searchVal);
                        startActivity(intent);
                    }
                    break;
            }
        }

        t.end();
        t.logVerbose();
    }

    private void showSearchErrorDialog() {
        final ErrorDialogFragment dialog = ErrorDialogFragment
                .newInstance(SEARCH_ERROR_MESSAGE);
        dialog.show(getChildFragmentManager(), ERROR_DIALOG_FRAGMENT_TAG);
    }

    private void showNoStocksFoundDialog() {
        String message = NO_STOCKS_FOUND_MESSAGE;
        final Activity activity = getActivity();
        if (activity != null) {
            switch (mSearchMode) {
                case CHECKIN:
                    message = activity.getString(R.string.checkin_search_no_stocks);
                    break;
                case GENERAL:
                case IMAGER:
                case LOCATION:
                    message = NO_STOCKS_FOUND_MESSAGE;
                    break;
                case SETSALE:
                    message = activity.getString(R.string.setsale_search_no_stocks);
                    break;
                default:
                    break;
            }
        }

        final ErrorDialogFragment dialog = ErrorDialogFragment
                .newInstance(message);
        dialog.show(getChildFragmentManager(), ERROR_DIALOG_FRAGMENT_TAG);
    }

    private void showSpeechRecognitionErrorDialog() {
        final ErrorDialogFragment dialog = ErrorDialogFragment
                .newInstance(SPEECH_RECOGNITION_ERROR_MESSAGE);
        dialog.show(getChildFragmentManager(), ERROR_DIALOG_FRAGMENT_TAG);
    }

    private void showScanErrorDialog() {
        final ErrorDialogFragment dialog = ErrorDialogFragment
                .newInstance(SCAN_LOAD_ERROR_MESSAGE);
        dialog.show(getChildFragmentManager(), ERROR_DIALOG_FRAGMENT_TAG);
    }

    private void showTooManyStocksFoundDialog() {
        final ErrorDialogFragment dialog = ErrorDialogFragment
                .newInstance(TOO_MANY_RESULTS_ERROR_MESSAGE);
        dialog.show(getChildFragmentManager(), ERROR_DIALOG_FRAGMENT_TAG);
    }
}
