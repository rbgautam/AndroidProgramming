package us.forgeinnovations.deltaman.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import us.forgeinnovations.deltaman.notes.NoteInfo;
import us.forgeinnovations.deltaman.repository.ShopkeeperDatabaseContract;

import static us.forgeinnovations.deltaman.repository.ShopkeeperDatabaseContract.*;

/**
 * Created by deltaman on 12/2/2017.
 */

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ViewHolder>  {
    private final Context mContext;
    //private final List<NoteInfo> mNotes;
    private final LayoutInflater mLayoutInflater;

    private Cursor mNotes;

    public ProductRecyclerAdapter(Context context, Cursor notes) {
        mContext = context;
        mNotes = notes;
        mNotes.moveToFirst();
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_product_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        mNotes.moveToPosition(position);
        int courseidpos = mNotes.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        int notetitlepos = mNotes.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        holder.mTextCourse.setText(mNotes.getString(courseidpos));
        holder.mTextTitle.setText(mNotes.getString(notetitlepos));
        holder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        return mNotes.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextCourse;
        public final TextView mTextTitle;
        public int mCurrentPosition;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.textTitle);
            mTextTitle = (TextView) itemView.findViewById(R.id.textShopList);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ShoppingActivity.class);
                    intent.putExtra(ShoppingActivity.ITEM_POSITION , mCurrentPosition);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}

