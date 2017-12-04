package com.iaai.onyard.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iaai.onyard.R;
import com.iaai.onyardproviderapi.classes.VehicleInfo;

/**
 * Class that enables creation of ListView with custom row layout.
 */
public class ReshootArrayAdapter extends ArrayAdapter<VehicleInfo> {

    /**
     * Array containing details about all vehicles contained in the adapter.
     */
    private final ArrayList<VehicleInfo> reshoots;

    /**
     * Constructor that saves array of VehicleInfo objects to be displayed.
     * 
     * @param context The current context.
     * @param objects The array of VehicleInfo objects to be displayed.
     */
    public ReshootArrayAdapter(Context context, ArrayList<VehicleInfo> objects) {
        super(context, R.layout.reshoot_list_row, objects);
        reshoots = objects;
    }

    /**
     * Populate and return custom inflated layout corresponding to one row of the ListView.
     * 
     * @param position The position of the item within the adapter's data set
     * of the item whose view we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        View rowView = convertView;

        if(rowView == null)
        {
            final LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.reshoot_list_row, parent, false);
            holder = new ViewHolder();

            holder.txtStock = (TextView) rowView.findViewById(R.id.reshoot_stock_row);
            holder.txtYMM = (TextView) rowView.findViewById(R.id.reshoot_year_make_model_row);
            holder.txtProvider = (TextView) rowView.findViewById(R.id.reshoot_provider_row);
            holder.txtStatus = (TextView) rowView.findViewById(R.id.reshoot_status_row);
            holder.txtLocation = (TextView) rowView.findViewById(R.id.reshoot_location);

            holder.linStatLocation = (LinearLayout) rowView
                    .findViewById(R.id.reshoot_stat_location_row);
            holder.linStockYMM = (LinearLayout) rowView.findViewById(R.id.reshoot_stock_vnn_row);
            rowView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) rowView.getTag();
        }

        final VehicleInfo reshootVehicle = reshoots.get(position);

        holder.txtStock.setText(reshootVehicle.getStockNumber());
        holder.txtYMM.setText(reshootVehicle.getYearMakeModel());
        holder.txtProvider.setText(reshootVehicle.getSalvageProviderName());
        holder.txtLocation.setText(reshootVehicle.getLocation());
        holder.txtStatus.setText(reshootVehicle.getStatusDescription());

        return rowView;
    }

    /**
     * Class used to store data in the row view, so that we don't have to re-inflate all views
     * in each row every time.
     */
    static class ViewHolder
    {
        public TextView txtStock;
        public TextView txtYMM;
        public TextView txtProvider;
        public TextView txtStatus;
        public TextView txtLocation;
        public LinearLayout linStatLocation;
        public LinearLayout linStockYMM;
    }
}
