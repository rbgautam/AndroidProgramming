package com.iaai.onyard.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyardproviderapi.classes.VehicleInfo;

public class LocationSearchArrayAdapter extends ArrayAdapter<VehicleInfo> {

    @InjectView(R.id.location_stock)
    TextView mTxtStockNumber;
    @InjectView(R.id.location_year_make_model)
    TextView mTxtYmm;
    @InjectView(R.id.location_provider)
    TextView mTxtProvider;
    @InjectView(R.id.location_sale_doc)
    TextView mTxtSaleDocType;
    @InjectView(R.id.location_run_drive)
    TextView mTxtRunDrive;
    @InjectView(R.id.location_location)
    TextView mTxtLocation;

    ArrayList<VehicleInfo> mLocationVehicleInfo;

    public LocationSearchArrayAdapter(Context context, ArrayList<VehicleInfo> objects) {
        super(context, R.layout.location_search_list_row, objects);
        mLocationVehicleInfo = objects;
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
            rowView = inflater.inflate(R.layout.location_search_list_row, parent, false);
            ButterKnife.inject(this, rowView);
            holder = new ViewHolder();

            holder.txtStock = mTxtStockNumber;
            holder.txtYMM = mTxtYmm;
            holder.txtProvider = mTxtProvider;
            holder.txtSaleDocType = mTxtSaleDocType;
            holder.txtRunDrive = mTxtRunDrive;
            holder.txtLocation = mTxtLocation;
            rowView.setTag(holder);
        }
        else {
            holder = (ViewHolder) rowView.getTag();
        }

        mLocationVehicleInfo.get(position);

        holder.txtStock.setText("");
        holder.txtYMM.setText("");
        holder.txtProvider.setText("");
        holder.txtSaleDocType.setText("");
        holder.txtRunDrive.setText("");
        holder.txtLocation.setText("");

        final VehicleInfo vehicle = mLocationVehicleInfo.get(position);
        holder.txtStock.setText(vehicle.getStockNumber());
        holder.txtYMM.setText(vehicle.getYearMakeModel());
        holder.txtProvider.setText(vehicle.getSalvageProviderName());
        holder.txtSaleDocType.setText(vehicle.getSaleDocTypeDescription());
        holder.txtRunDrive.setText(vehicle.isRunAndDrive() ? "R&D" : "");
        if (vehicle.getAisle() != null && !vehicle.getAisle().equals("")) {
            holder.txtLocation.setText(vehicle.getAisle() + " - " + vehicle.getStall());
        }

        return rowView;
    }

    /**
     * Class used to store data in the row view, so that we don't have to re-inflate all views in
     * each row every time.
     */
    static class ViewHolder {
        public TextView txtStock;
        public TextView txtYMM;
        public TextView txtProvider;
        public TextView txtSaleDocType;
        public TextView txtRunDrive;
        public TextView txtLocation;
    }
}
