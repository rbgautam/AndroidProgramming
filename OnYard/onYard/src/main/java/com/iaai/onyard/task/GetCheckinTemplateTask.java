package com.iaai.onyard.task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.iaai.onyard.application.OnYard.OnYardFieldInputType;
import com.iaai.onyard.classes.CheckinField;
import com.iaai.onyard.listener.GetCheckinTemplateListener;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyardproviderapi.classes.CheckinFieldInfo;
import com.iaai.onyardproviderapi.contract.OnYardContract;

/**
 * AsyncTask to get the appropriate checkin template. The template is returned as an ArrayList of
 * CheckinField objects once they have been extracted. Parameters for execute:
 * <P>
 * Param 0: context - Context <br>
 * Param 1: salvage type - Integer <br>
 * </P>
 * 
 * @author wferguso
 */

public class GetCheckinTemplateTask extends AsyncTask<Object, Void, ArrayList<CheckinField>> {

    private final WeakReference<GetCheckinTemplateListener> mWeakListener;

    public GetCheckinTemplateTask(GetCheckinTemplateListener listener) {
        mWeakListener = new WeakReference<GetCheckinTemplateListener>(listener);
    }

    @Override
    protected ArrayList<CheckinField> doInBackground(Object... params) {
        try {
            final Context context = (Context) params[0];
            final int salvageType = (Integer) params[1];

            return getTemplate(context.getContentResolver(), salvageType);
        }
        catch (final Exception e) {
            LogHelper.logError((Context) params[0], e, this.getClass().getSimpleName());
        }
        return new ArrayList<CheckinField>();
    }

    @Override
    protected void onPostExecute(ArrayList<CheckinField> fieldList) {
        if (mWeakListener != null && mWeakListener.get() != null) {
            mWeakListener.get().onCheckinTemplateRetrieved(fieldList);
        }
    }

    public static ArrayList<CheckinField> getTemplate(ContentResolver contentResolver,
            int salvageType) {
        Cursor queryResult = null;
        try {
            queryResult = contentResolver.query(OnYardContract.CheckinField.CHECKIN_TEMPLATE_URI,
                    null,
                    OnYardContract.CheckinTemplate.COLUMN_NAME_SALVAGE_TYPE + "=?",
                    new String[] { String.valueOf(salvageType) }, null);

            final ArrayList<CheckinField> fieldList = new ArrayList<CheckinField>();

            if (queryResult == null || !queryResult.moveToFirst()) {
                return fieldList;
            }

            CheckinField field;
            do {
                field = new CheckinField(new CheckinFieldInfo(queryResult), contentResolver,
                        salvageType);

                if (!(field.getInputType() == OnYardFieldInputType.LIST && field.getOptions().isEmpty())) {
                    fieldList.add(field);
                }
            }
            while (queryResult.moveToNext());

            return fieldList;
        }
        finally {
            if (queryResult != null)
            {
                queryResult.close();
            }
        }
    }
}
