package com.iaai.onyard.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.iaai.onyardproviderapi.classes.AuctionScheduleInfo;

public class AuctionDateArrayAdapter extends ArrayAdapter<AuctionScheduleInfo> {

    private final ArrayList<AuctionScheduleInfo> mAuctionScheduleList;

    public AuctionDateArrayAdapter(Context context, ArrayList<AuctionScheduleInfo> auctions) {
        super(context, android.R.layout.simple_spinner_dropdown_item, auctions);
        mAuctionScheduleList = auctions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        if (row == null) {
            final LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        final AuctionScheduleInfo item = mAuctionScheduleList.get(position);

        final TextView date = (TextView) row.findViewById(android.R.id.text1);
        date.setText(item.getAuctionDateString());
        date.setTextSize(19);

        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    public int getAuctionDatePos(Long auctionDate) {
        for (int index = 0; index < mAuctionScheduleList.size(); index++) {
            if (mAuctionScheduleList.get(index).getAuctionDate().equals(auctionDate)) {
                return index;
            }
        }

        return -1;
    }
}
