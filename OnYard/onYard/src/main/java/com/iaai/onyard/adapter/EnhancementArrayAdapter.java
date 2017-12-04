package com.iaai.onyard.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.iaai.onyard.R;
import com.iaai.onyard.classes.OnYardField;

public class EnhancementArrayAdapter extends OnYardFieldArrayAdapter {

    public EnhancementArrayAdapter(Context context, ArrayList<? extends OnYardField> fields) {
        super(context, fields);
    }

    @Override
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
                if (field.isRequired()) {
                    imageView.setImageResource(R.drawable.exclamation);
                }
                else {
                    imageView.setImageResource(R.drawable.question);
                }
            }
        }
    }
}
