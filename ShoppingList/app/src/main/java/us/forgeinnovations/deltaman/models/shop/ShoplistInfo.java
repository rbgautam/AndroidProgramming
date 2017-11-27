package us.forgeinnovations.deltaman.models.shop;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import us.forgeinnovations.deltaman.notes.ModuleInfo;

/**
 * Created by 19680608Deltaman.
 */

public final class ShoplistInfo implements Parcelable {
    private final String mShoppingListId;
    private final String mShoppingListName;
    private final List<ModuleInfo> mItems;

    public ShoplistInfo(String courseId, String title, List<ModuleInfo> products) {
        mShoppingListId = courseId;
        mShoppingListName = title;
        mItems = products;
    }

    private ShoplistInfo(Parcel parcel) {
        mShoppingListName =parcel.readString();
        mShoppingListId = parcel.readString();
        mItems = new ArrayList<ModuleInfo>();
                parcel.readTypedList(mItems, ModuleInfo.CREATOR );

    }

    public String getCourseId() {
        return mShoppingListId;
    }

    public String getTitle() {
        return mShoppingListName;
    }

    public List<ModuleInfo> getModules() {
        return mItems;
    }

    public boolean[] getModulesCompletionStatus() {
        boolean[] status = new boolean[mItems.size()];

        for(int i = 0; i < mItems.size(); i++)
            status[i] = mItems.get(i).isComplete();

        return status;
    }

    public void setModulesCompletionStatus(boolean[] status) {
        for(int i = 0; i < mItems.size(); i++)
            mItems.get(i).setComplete(status[i]);
    }

    public ModuleInfo getModule(String moduleId) {
        for(ModuleInfo moduleInfo : mItems) {
            if(moduleId.equals(moduleInfo.getModuleId()))
                return moduleInfo;
        }
        return null;
    }

    @Override
    public String toString() {
        return mShoppingListName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShoplistInfo that = (ShoplistInfo) o;

        return mShoppingListId.equals(that.mShoppingListId);

    }

    @Override
    public int hashCode() {
        return mShoppingListId.hashCode();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mShoppingListName);
        parcel.writeString(mShoppingListId);
        parcel.writeTypedList(mItems);
    }

    public static final Creator<ShoplistInfo> CREATOR =
            new Creator() {
                @Override
                public Object createFromParcel(Parcel parcel) {
                    ShoplistInfo courseInfo =  new ShoplistInfo(parcel);
                    return courseInfo;
                }

                @Override
                public Object[] newArray(int size) {
                    return new Object[size];
                }
            };
}
