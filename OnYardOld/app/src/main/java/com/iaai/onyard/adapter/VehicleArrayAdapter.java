package com.iaai.onyard.adapter;

import com.iaai.onyard.classes.VehicleInfo;

import com.iaai.onyard.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Class that enables creation of ListView with custom row layout.
 */
public class VehicleArrayAdapter extends ArrayAdapter<VehicleInfo> {
	
	/**
	 * Array containing details about all vehicles contained in the adapter.
	 */
	private final VehicleInfo[] vehicles;

	/**
	 * Constructor that saves array of VehicleInfo objects to be displayed.
	 * 
	 * @param context The current context.
	 * @param objects The array of VehicleInfo objects to be displayed.
	 */
	public VehicleArrayAdapter(Context context, VehicleInfo[] objects) {
		super(context, R.layout.vehicle_list_row, objects);
		vehicles = objects;
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
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.vehicle_list_row, null, true);
			holder = new ViewHolder();
			
			holder.txtStockNum = (TextView) rowView.findViewById(R.id.stock_number_row);
			holder.txtVIN = (TextView) rowView.findViewById(R.id.vin_row);
			holder.txtYearMakeModel = (TextView) rowView.findViewById(R.id.year_make_model_row);
			
			rowView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) rowView.getTag();
		}

		VehicleInfo vehicle = vehicles[position];
		
		holder.txtStockNum.setText(vehicle.getStockNumber());
		holder.txtVIN.setText(vehicle.getVIN());
		holder.txtYearMakeModel.setText(vehicle.getYearMakeModel());
		
		return rowView;
	}
	
	/**
	 * Class used to store data in the row view, so that we don't have to re-inflate all views
	 * in each row every time.
	 */
	static class ViewHolder
	{
		public TextView txtStockNum;
		public TextView txtVIN;
		public TextView txtYearMakeModel;
	}
}
