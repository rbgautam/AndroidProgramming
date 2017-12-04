package com.iaai.onyard.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyardproviderapi.classes.HolidayInfo;
import com.iaai.onyardproviderapi.classes.VehicleInfo;

/**
 * Class that enables creation of ListView with custom row layout.
 */
public class CheckinSearchArrayAdapter extends ArrayAdapter<VehicleInfo> {

    @InjectView(R.id.checkin_stock_row)
    TextView mTxtStockNumber;
    @InjectView(R.id.checkin_year_make_model_row)
    TextView mTxtYmm;
    @InjectView(R.id.checkin_provider_row)
    TextView mTxtProvider;
    @InjectView(R.id.checkin_vin_row)
    TextView mTxtVin;

    // TFS ticket# 61163 - Using TextView instead of the ImageView to display the icon and the text
    // together in one view
    @InjectView(R.id.checkin_elapsedHrs_row)
    TextView mTxtElapsedHrs;

    /**
     * Array containing details about all vehicles contained in the adapter.
     */
    private final ArrayList<VehicleInfo> mCheckinList;

    /**
     * Constructor that saves array of VehicleInfo objects to be displayed.
     * 
     * @param context The current context.
     * @param objects The array of VehicleInfo objects to be displayed.
     */
    public CheckinSearchArrayAdapter(Context context, ArrayList<VehicleInfo> objects,
            ArrayList<HolidayInfo> holidayObj) {
        super(context, R.layout.checkin_search_list_row, objects);
        mCheckinList = objects;
    }

    /**
     * Populate and return custom inflated layout corresponding to one row of the ListView.
     * 
     * @param position The position of the item within the adapter's data set of the item whose view
     *            we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;

        if (rowView == null) {
            final LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.checkin_search_list_row, parent, false);
            ButterKnife.inject(this, rowView);
            holder = new ViewHolder();

            holder.txtStock = mTxtStockNumber;
            holder.txtYMM = mTxtYmm;
            holder.txtProvider = mTxtProvider;
            holder.txtVin = mTxtVin;
            holder.txtElapsedhrs = mTxtElapsedHrs;
            rowView.setTag(holder);
        }
        else {
            holder = (ViewHolder) rowView.getTag();
        }

        final VehicleInfo checkinVehicle = mCheckinList.get(position);

        holder.txtStock.setText(checkinVehicle.getStockNumber());
        holder.txtYMM.setText(checkinVehicle.getYearMakeModel());
        holder.txtProvider.setText(checkinVehicle.getSalvageProviderName());
        holder.txtVin.setText(checkinVehicle.getVIN());


        // final int elapsedHrs = 12;
        final int elapsedHrs = (int) checkinVehicle.getElaspsedTime();
        // getElapsedHRinfo(checkinVehicle);
        setElapsedHrInfo(holder, getContext(), elapsedHrs);

        return rowView;
    }



    /**
     * Function sets the value of the TextView for elapsed hrs and changes the Icon according to
     * number of hours elapsed
     * 
     * @param holder class used to hold the Row View info
     * @param context current context of the ArrayAdapter
     */
    private static void setElapsedHrInfo(ViewHolder holder, Context context, int elapsedHrs) {
        final String elapsedTxt = String.format("%d Hrs", elapsedHrs);

        Log.d("setelapsed", elapsedTxt);

        int iconImg = 0;

        if (elapsedHrs >= 8 && elapsedHrs <= 12) {
            iconImg = R.drawable.limp_icon;
        }
        else
            if (elapsedHrs > 12) {

                iconImg = R.drawable.himp_icon;
            }

        if (iconImg > 0) {
            final Drawable img = context.getResources().getDrawable(iconImg);
            img.setBounds(0, 0, img.getMinimumWidth() - 20, img.getMinimumHeight() - 20);
            holder.txtElapsedhrs.setCompoundDrawables(null, img, null, null);
            holder.txtElapsedhrs.setCompoundDrawablePadding(-10);
            // holder.txtElapsedhrs.setCompoundDrawablesRelativeWithIntrinsicBounds(null, img,
            // null,null);
        }
        else {
            holder.txtElapsedhrs.setCompoundDrawables(null, null, null, null);
        }

        holder.txtElapsedhrs.setText(elapsedTxt);
        // holder.txtElapsedhrs
    }

    /**
     * Class used to store data in the row view, so that we don't have to re-inflate all views in
     * each row every time.
     */
    static class ViewHolder {

        public TextView txtStock;
        public TextView txtYMM;
        public TextView txtProvider;
        public TextView txtVin;
        public TextView txtElapsedhrs;
    }
}
