package us.forgeinnovations.deltaman.models.shop;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * Created by Deltaman.
 */

public final class ItemInfo implements Parcelable {
    private ProductInfo mCourse;
    private String mTitle;
    private String mText;

    public ItemInfo(ProductInfo course, String title, String text) {
        mCourse = course;
        mTitle = title;
        mText = text;
    }

    private ItemInfo(Parcel parcel) {
        mCourse = parcel.readParcelable(ProductInfo.class.getClassLoader());
        mTitle =  parcel.readString();
        mText = parcel.readString();
    }

    public ProductInfo getCourse() {
        return mCourse;
    }

    public void setCourse(ProductInfo course) {
        mCourse = course;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    private String getCompareKey() {
        return mCourse.getModuleId () + "|" + mTitle + "|" + mText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemInfo that = (ItemInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mCourse,0);
        parcel.writeString(mTitle);
        parcel.writeString(mText);
    }

    public static final Creator<ItemInfo> CREATOR =
            new Creator() {
                @Override
                public Object createFromParcel(Parcel parcel) {
                    ItemInfo noteInfo =  new ItemInfo(parcel);
                    return noteInfo;
                }

                @Override
                public Object[] newArray(int size) {
                    return new Object[size];
                }
            };
}
