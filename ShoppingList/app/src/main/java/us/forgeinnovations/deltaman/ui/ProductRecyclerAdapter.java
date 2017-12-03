package us.forgeinnovations.deltaman.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import us.forgeinnovations.deltaman.notes.NoteInfo;

/**
 * Created by deltaman on 12/2/2017.
 */

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ViewHolder>  {
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final List<NoteInfo> mNotes;
    public ProductRecyclerAdapter(Context mContext, List<NoteInfo> mNotes) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mNotes = mNotes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View productView = mLayoutInflater.inflate(R.layout.item_product_list ,parent, false);
        return new ViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NoteInfo note =  mNotes.get(position);
        holder.mTextTitle.setText(note.getCourse().getTitle());
        holder.mTextDesc.setText(note.getTitle());
        holder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView mTextTitle;
        public final TextView mTextDesc;
        private int mCurrentPosition;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView)itemView.findViewById(R.id.textTitle);
            mTextDesc = (TextView) itemView.findViewById(R.id.textDesc);

            itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent launchProdDetails  = new Intent(mContext,ShoppingActivity.class);
                        launchProdDetails.putExtra(ShoppingActivity.ITEM_POSITION, mCurrentPosition);

                        mContext.startActivity(launchProdDetails);
                    }
                }
            );

        }



    }
}

