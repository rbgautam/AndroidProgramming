package com.iaai.onyard.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.iaai.onyard.application.OnYard.NavDrawerItemType;

public class NavDrawerItem implements Parcelable {

    private final String mName;
    private final int mImageResourceId;
    private final boolean mIsBold;
    private final NavDrawerItemType mType;

    public NavDrawerItem(String itemName, int imgResID, boolean isBold, NavDrawerItemType type) {
        mName = itemName;
        mImageResourceId = imgResID;
        mIsBold = isBold;
        mType = type;
    }

    public NavDrawerItem(Parcel in) {
        mName = in.readString();
        mImageResourceId = in.readInt();
        mIsBold = in.readInt() == 1;
        mType = (NavDrawerItemType) in.readSerializable();
    }

    public String getName() {
        return mName;
    }

    public int getImgResID() {
        return mImageResourceId;
    }

    public boolean isBold() {
        return mIsBold;
    }

    public NavDrawerItemType getType() {
        return mType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public NavDrawerItem createFromParcel(Parcel in) {
            return new NavDrawerItem(in);
        }

        @Override
        public NavDrawerItem[] newArray(int size) {
            return new NavDrawerItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeInt(mImageResourceId);
        out.writeInt(mIsBold ? 1 : 0);
        out.writeSerializable(mType);
    }
}
