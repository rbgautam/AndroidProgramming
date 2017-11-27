package us.forgeinnovations.deltaman.notes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by 19680608Deltaman.
 */

public final class CourseInfo implements Parcelable {
    private final String mCourseId;
    private final String mTitle;
    private final List<ProductInfo> mModules;

    public CourseInfo(String courseId, String title, List<ProductInfo> modules) {
        mCourseId = courseId;
        mTitle = title;
        mModules = modules;
    }

    private CourseInfo(Parcel parcel) {
        mTitle =parcel.readString();
        mCourseId = parcel.readString();
        mModules = new ArrayList<ProductInfo>();
                parcel.readTypedList(mModules, ProductInfo.CREATOR);

    }

    public String getCourseId() {
        return mCourseId;
    }

    public String getTitle() {
        return mTitle;
    }

    public List<ProductInfo> getModules() {
        return mModules;
    }

    public boolean[] getModulesCompletionStatus() {
        boolean[] status = new boolean[mModules.size()];

        for(int i=0; i < mModules.size(); i++)
            status[i] = mModules.get(i).isComplete();

        return status;
    }

    public void setModulesCompletionStatus(boolean[] status) {
        for(int i=0; i < mModules.size(); i++)
            mModules.get(i).setComplete(status[i]);
    }

    public ProductInfo getModule(String moduleId) {
        for(ProductInfo productInfo : mModules) {
            if(moduleId.equals(productInfo.getModuleId()))
                return productInfo;
        }
        return null;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseInfo that = (CourseInfo) o;

        return mCourseId.equals(that.mCourseId);

    }

    @Override
    public int hashCode() {
        return mCourseId.hashCode();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mCourseId);
        parcel.writeTypedList(mModules);
    }

    public static final Creator<CourseInfo> CREATOR =
            new Creator() {
                @Override
                public Object createFromParcel(Parcel parcel) {
                    CourseInfo courseInfo =  new CourseInfo(parcel);
                    return courseInfo;
                }

                @Override
                public Object[] newArray(int size) {
                    return new Object[size];
                }
            };
}
