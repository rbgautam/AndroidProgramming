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
import com.iaai.onyardproviderapi.classes.SalvageEnhancementInfo;

/**
 * Class that enables creation of ListView with custom row layout.
 */
public class EnhancementSearchArrayAdapter extends ArrayAdapter<SalvageEnhancementInfo> {

    @InjectView(R.id.enhancement_stock_row)
    TextView mTxtStockNumber;
    @InjectView(R.id.enhancement_year_make_model_row)
    TextView mTxtYmm;
    @InjectView(R.id.enhancement_provider_row)
    TextView mTxtProvider;
    @InjectView(R.id.enhancement_desc_row)
    TextView mTxtEnh;

    /**
     * Array containing details about all enhancements contained in the adapter.
     */
    private final ArrayList<SalvageEnhancementInfo> mEnhancementsList;

    /**
     * Constructor that saves array of EnhancementInfo objects to be displayed.
     * 
     * @param context The current context.
     * @param objects The array of SalvageEnhancementInfo objects to be displayed.
     */
    public EnhancementSearchArrayAdapter(Context context, ArrayList<SalvageEnhancementInfo> objects) {
        super(context, R.layout.enhancements_search_list_row, objects);
        mEnhancementsList = objects;
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
            rowView = inflater.inflate(R.layout.enhancements_search_list_row, parent, false);
            ButterKnife.inject(this, rowView);
            holder = new ViewHolder();

            holder.txtStock = mTxtStockNumber;
            holder.txtYMM = mTxtYmm;
            holder.txtProvider = mTxtProvider;
            holder.txtEnh = mTxtEnh;
            rowView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) rowView.getTag();
        }

        mEnhancementsList.get(position);

        final SalvageEnhancementInfo salEnhancement = mEnhancementsList.get(position);
        holder.txtStock.setText(salEnhancement.getStockNumber());
        holder.txtYMM.setText(salEnhancement.getYearMakeModel());
        holder.txtProvider.setText(salEnhancement.getSalvageProviderName());
        holder.txtEnh.setText(salEnhancement.getEnhancementDesc());

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
        public TextView txtEnh;
    }
}
