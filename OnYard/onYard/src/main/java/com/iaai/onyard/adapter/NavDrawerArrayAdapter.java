package com.iaai.onyard.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iaai.onyard.R;
import com.iaai.onyard.classes.NavDrawerItem;

public class NavDrawerArrayAdapter extends ArrayAdapter<NavDrawerItem> {

    private final Context mContext;
    private final ArrayList<NavDrawerItem> mDrawerItemList;
    private final int mLayoutResourceId;

    public NavDrawerArrayAdapter(Context context, int layoutResourceID,
            ArrayList<NavDrawerItem> listItems) {
        super(context, layoutResourceID, listItems);
        mContext = context;
        mDrawerItemList = listItems;
        mLayoutResourceId = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder drawerHolder;

        if (view == null) {
            final LayoutInflater inflater = LayoutInflater.from(mContext);
            drawerHolder = new ViewHolder();

            view = inflater.inflate(mLayoutResourceId, parent, false);
            drawerHolder.icon = (ImageView) view.findViewById(R.id.drawer_icon);
            drawerHolder.text = (TextView) view.findViewById(R.id.drawer_itemName);

            view.setTag(drawerHolder);

        }
        else {
            drawerHolder = (ViewHolder) view.getTag();
        }

        final NavDrawerItem drawerItem = mDrawerItemList.get(position);

        if (drawerItem.getImgResID() != 0) {
            drawerHolder.icon.setImageDrawable(view.getResources()
                    .getDrawable(drawerItem.getImgResID()));
        }
        drawerHolder.text.setText(drawerItem.getName());
        if (drawerItem.isBold()) {
            drawerHolder.text.setTypeface(null, Typeface.BOLD);
        }
        else {
            drawerHolder.text.setTypeface(null, Typeface.NORMAL);
        }

        return view;
    }

    private static class ViewHolder
    {
        public ImageView icon;
        public TextView text;
    }
}
