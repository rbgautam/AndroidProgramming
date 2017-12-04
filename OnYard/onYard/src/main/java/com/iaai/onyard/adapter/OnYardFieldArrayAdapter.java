package com.iaai.onyard.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.classes.OnYardField;

public class OnYardFieldArrayAdapter extends ArrayAdapter<OnYardField> {

    @InjectView(R.id.row_field_name)
    TextView mTxtFieldName;
    @InjectView(R.id.row_field_value)
    TextView mTxtFieldValue;
    @InjectView(R.id.row_status_image)
    ImageView mStatusImage;

    private ArrayList<OnYardField> mOnYardFieldList;
    private int mSelectedPos;
    protected boolean mCommitAttempted;

    public OnYardFieldArrayAdapter(Context context, ArrayList<? extends OnYardField> fields) {
        super(context, R.layout.onyard_field_row, (ArrayList<OnYardField>) fields);
        mOnYardFieldList = (ArrayList<OnYardField>) fields;
        mSelectedPos = -1;
        mCommitAttempted = false;
    }

    public void setCommitAttempted(boolean commitAttempted) {
        mCommitAttempted = commitAttempted;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        View rowView = convertView;

        if (rowView == null) {
            final LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.onyard_field_row, parent, false);
            ButterKnife.inject(this, rowView);
            holder = new ViewHolder();

            holder.txtName = mTxtFieldName;
            holder.txtValue = mTxtFieldValue;
            holder.image = mStatusImage;
            rowView.setTag(holder);
        }
        else {
            holder = (ViewHolder) rowView.getTag();
        }

        final OnYardField field = mOnYardFieldList.get(position);

        holder.txtName.setText(field.getName());
        switch (field.getInputType()) {
            case LIST:
                initListFieldViews(holder.image, holder.txtValue, field, parent.getContext());
                break;
            case CHECKBOX:
                initCheckboxFieldViews(holder.image, holder.txtValue, field, parent.getContext());
                break;
            case NUMERIC:
                initNumericFieldViews(holder.image, holder.txtValue, field, parent.getContext());
                break;
            case ALPHANUMERIC:
                initAlphanumericFieldViews(holder.image, holder.txtValue, field,
                        parent.getContext());
                break;
            case TEXT:
                initTextFieldViews(holder.image, holder.txtValue, field, parent.getContext());
                break;
            default:
                break;
        }

        if (position == mSelectedPos) {
            final Resources resources = parent.getContext().getResources();
            holder.txtName.setTextColor(resources.getColor(R.color.default_white));
            holder.txtName.setTypeface(null, Typeface.BOLD);
            holder.txtValue.setTextColor(resources.getColor(R.color.default_white));
            holder.txtValue.setTypeface(null, Typeface.BOLD);
            rowView.setBackgroundColor(resources.getColor(R.color.checkin_selected_row));
        }
        else {
            final Resources resources = parent.getContext().getResources();
            holder.txtName.setTextColor(resources.getColor(R.color.dark_gray));
            holder.txtName.setTypeface(null, Typeface.NORMAL);
            holder.txtValue.setTextColor(resources.getColor(R.color.dark_gray));
            holder.txtValue.setTypeface(null, Typeface.NORMAL);
            rowView.setBackgroundColor(resources.getColor(R.color.default_white));
        }

        return rowView;
    }

    protected void initListFieldViews(ImageView imageView, TextView textView, OnYardField field,
            Context context) {
        if (field.hasSelection()) {
            textView.setText(field.getSelectedOption().getDisplayName());
            imageView.setImageResource(R.drawable.checkmark);
        }
        else {
            textView.setText(context.getString(R.string.no_value_entered));
            if (mCommitAttempted && field.isRequired()) {
                imageView.setImageResource(R.drawable.exclamation);
            }
            else {
                imageView.setImageResource(R.drawable.question);
            }
        }
    }

    protected void initCheckboxFieldViews(ImageView imageView, TextView textView,
            OnYardField field, Context context) {
        initListFieldViews(imageView, textView, field, context);
    }

    protected void initNumericFieldViews(ImageView imageView, TextView textView, OnYardField field,
            Context context) {
        initTextFieldViews(imageView, textView, field, context);
    }

    protected void initAlphanumericFieldViews(ImageView imageView, TextView textView,
            OnYardField field, Context context) {
        initTextFieldViews(imageView, textView, field, context);
    }

    protected void initTextFieldViews(ImageView imageView, TextView textView, OnYardField field,
            Context context) {
        if (field.hasSelection()) {
            textView.setText(field.getEnteredValue());
            imageView.setImageResource(R.drawable.checkmark);
        }
        else {
            textView.setText(context.getString(R.string.no_value_entered));
            if (mCommitAttempted && field.isRequired()) {
                imageView.setImageResource(R.drawable.exclamation);
            }
            else {
                imageView.setImageResource(R.drawable.question);
            }
        }
    }

    public void refreshList(ArrayList<? extends OnYardField> fields) {
        mOnYardFieldList = (ArrayList<OnYardField>) fields;
        notifyDataSetChanged();
    }

    public void setSelectedPos(int selectedPos) {
        mSelectedPos = selectedPos;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public TextView txtName;
        public TextView txtValue;
        public ImageView image;
    }
}
