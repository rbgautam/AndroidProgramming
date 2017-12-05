package us.forgeinnovations.deltaman.ui;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import us.forgeinnovations.deltaman.notes.CourseInfo;
import us.forgeinnovations.deltaman.notes.NoteInfo;

/**
 * Created by deltaman on 12/2/2017.
 */

public class ShoplistRecyclerAdapter extends RecyclerView.Adapter<ShoplistRecyclerAdapter.ViewHolder>  {
    private final Context mContext;
    private final List<CourseInfo> mCourses;
    private final LayoutInflater mLayoutInflater;

    public ShoplistRecyclerAdapter(Context context, List<CourseInfo> courses) {
        mContext = context;
        mCourses = courses;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_shopping_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CourseInfo course = mCourses.get(position);
        holder.mTextCourse.setText(course.getTitle());
        holder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextCourse;
        public int mCurrentPosition;

        public ViewHolder(final View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.textShopList);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v,mCourses.get(mCurrentPosition).getTitle(),Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}

