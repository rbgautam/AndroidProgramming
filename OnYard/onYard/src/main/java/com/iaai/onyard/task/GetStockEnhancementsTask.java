package com.iaai.onyard.task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard.EnhancementId;
import com.iaai.onyard.classes.EnhancementField;
import com.iaai.onyard.listener.GetEnhancementFieldsListener;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.StockEnhancementInfo;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to get the appropriate enhancement fields. The template is returned as an ArrayList of
 * EnhancementField objects once they have been extracted. Parameters for execute:
 * <P>
 * Param 0: context - Context <br>
 * Param 1: vehicle info - VehicleInfo <br>
 * </P>
 * 
 * @author wferguso
 */

public class GetStockEnhancementsTask extends AsyncTask<Object, Void, ArrayList<EnhancementField>> {

    private final WeakReference<GetEnhancementFieldsListener> mWeakListener;

    public GetStockEnhancementsTask(GetEnhancementFieldsListener listener) {
        mWeakListener = new WeakReference<GetEnhancementFieldsListener>(listener);
    }

    @Override
    protected ArrayList<EnhancementField> doInBackground(Object... params) {
        try {
            final Context context = (Context) params[0];
            final VehicleInfo vehicle = (VehicleInfo) params[1];

            return getEnhancements(context.getContentResolver(), vehicle);
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[0], e, this.getClass().getSimpleName());
        }
        return new ArrayList<EnhancementField>();
    }

    @Override
    protected void onPostExecute(ArrayList<EnhancementField> fieldList) {
        if (mWeakListener != null && mWeakListener.get() != null) {
            mWeakListener.get().onEnhancementFieldsRetrieved(fieldList);
        }
    }

    private ArrayList<EnhancementField> getEnhancements(ContentResolver contentResolver,
            VehicleInfo vehicle) {
        Cursor queryResult = null;

        try {
            queryResult = contentResolver.query(Uri.withAppendedPath(
                    OnYardContract.SlaSalvageEnhancement.STOCK_ENHANCEMENT_URI_BASE,
                    vehicle.getStockNumber()), null, null, null, null);

            final ArrayList<EnhancementField> requiredFieldList = new ArrayList<EnhancementField>();
            final ArrayList<EnhancementField> nonRequiredFieldList = new ArrayList<EnhancementField>();

            if (queryResult == null || !queryResult.moveToFirst()) {
                return nonRequiredFieldList;
            }

            final boolean isCheckinEligible = vehicle.isCheckinEligible();
            EnhancementField field;
            do {
                field = new EnhancementField(new StockEnhancementInfo(queryResult));

                // hardcoded exclusion of certain enhancements if stock is ready for check-in
                if (isCheckinEligible) {
                    if (field.getId() == EnhancementId.CAR_START
                            || field.getId() == EnhancementId.DRIVE_THROUGH
                            || field.getId() == EnhancementId.RUN_AND_DRIVE) {
                        continue;
                    }
                }

                if (!field.getOptions().isEmpty()) {
                    if (field.isRequired()) {
                        requiredFieldList.add(field);
                    }
                    else {
                        nonRequiredFieldList.add(field);
                    }
                }
            }
            while (queryResult.moveToNext());

            final ArrayList<EnhancementField> combinedFieldList = new ArrayList<EnhancementField>();
            combinedFieldList.addAll(requiredFieldList);
            combinedFieldList.addAll(nonRequiredFieldList);

            return combinedFieldList;
        }
        finally {
            if (queryResult != null) {
                queryResult.close();
            }
        }
    }
}
