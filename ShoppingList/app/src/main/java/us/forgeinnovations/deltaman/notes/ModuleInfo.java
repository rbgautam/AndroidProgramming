package us.forgeinnovations.deltaman.notes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Deltaman.
 */

public final class ModuleInfo implements Parcelable{
    private final String mModuleId;
    private final String mTitle;
    private boolean mIsComplete = false;

    public ModuleInfo(String moduleId, String title) {
        this(moduleId, title, false);
    }

    public ModuleInfo(String moduleId, String title, boolean isComplete) {
        mModuleId = moduleId;
        mTitle = title;
        mIsComplete = isComplete;
    }

    private ModuleInfo(Parcel parcel) {
        mTitle = parcel.readString();
        mModuleId = parcel.readString();
        mIsComplete = parcel.readByte()== 0x1?true:false;
    }

    public String getModuleId() {
        return mModuleId;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean isComplete() {
        return mIsComplete;
    }

    public void setComplete(boolean complete) {
        mIsComplete = complete;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleInfo that = (ModuleInfo) o;

        return mModuleId.equals(that.mModuleId);
    }

    @Override
    public int hashCode() {
        return mModuleId.hashCode();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mModuleId);
        parcel.writeByte((byte)(mIsComplete?1:0));
    }

    public final static Parcelable.Creator<ModuleInfo> CREATOR =
            new Creator<ModuleInfo>() {
                @Override
                public ModuleInfo createFromParcel(Parcel parcel) {
                    ModuleInfo moduleInfo =  new ModuleInfo(parcel);
                    return moduleInfo;
                }

                @Override
                public ModuleInfo[] newArray(int size) {
                    return new ModuleInfo[size];
                }
            };
}
