package com.iaai.onyard.adapter;

import java.io.File;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iaai.onyard.R;
import com.iaai.onyard.session.OnYardImageData;
import com.iaai.onyard.utility.LogHelper;
import com.squareup.picasso.Picasso;

/**
 * Adapter to add thumbnails to navigation overlay GridView in Camera activity
 */
public class ImagerThumbnailAdapter extends BaseAdapter {

    private final OnYardImageData mImageData;
    private static final int THUMBNAIL_WIDTH = 140;
    private static final int THUMBNAIL_HEIGHT = 105;

    /**
     * Constructor that takes an Imagerdata.
     * 
     * @param context The application context.
     * @param data The ImagerData information.
     */
    public ImagerThumbnailAdapter(OnYardImageData data) {
        mImageData = data;
    }

    /**
     * @return The number of required images.
     */
    @Override
    public int getCount() {
        return mImageData.getTotalNumberOfImages();
    }

    /**
     * Does nothing. Returns null
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Gets the picture number at the position in the view. Used for when a picture is clicked.
     * 
     * @param position picture location
     */
    @Override
    public long getItemId(int position) {
        return mImageData.getAllImageSequences().get(position);
    }

    /**
     * Initiates each thumbnail in each position of the view.
     * 
     * @param position Place in view.
     * @param convertView View being added to.
     * @param parent Parent view.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        // (position > 5) is a hack to fix thumbnail images being duplicated when vehicle details
        // pane collapses... ImageViews 6-9 will never be reused.
        if (convertView == null || position > 5) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_thumbnail,
                    parent, false);
            holder = new ViewHolder();
            holder.thumbPic = (ImageView) convertView.findViewById(R.id.grid_thumbnail);
            holder.thumbOver = (ImageView) convertView.findViewById(R.id.grid_overlay);
            holder.caption = (TextView) convertView.findViewById(R.id.grid_caption);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            final int imageSeq = mImageData.getAllImageSequences().get(position);
            final String newImagePath = mImageData.getImagePath(imageSeq);
            if (newImagePath != null) {
                Picasso.with(parent.getContext()).load(new File(newImagePath))
                .placeholder(R.drawable.no_image).error(R.drawable.no_image)
                .resize(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT).into(holder.thumbPic);

                if (mImageData.hasOverlayImage(imageSeq)) {
                    Picasso.with(parent.getContext())
                            .load(mImageData.getThumbOverlayImageResourceId(parent.getContext(),
                                    imageSeq)).resize(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
                            .into(holder.thumbOver);

                    holder.thumbOver.setVisibility(View.VISIBLE);
                }
            }
            else {
                holder.thumbPic.setImageResource(R.drawable.no_image);
                holder.thumbOver.setVisibility(View.INVISIBLE);
            }

            holder.caption.setText(mImageData.getImageCaption(imageSeq));
        }
        catch (final Exception e) {
            LogHelper.logError(parent.getContext().getApplicationContext(), e, this.getClass()
                    .getSimpleName());
        }

        return convertView;
    }

    /**
     * Class to hold the caption and thumbPic.
     */
    private static class ViewHolder {
        TextView caption;
        ImageView thumbPic;
        ImageView thumbOver;
    }
}
