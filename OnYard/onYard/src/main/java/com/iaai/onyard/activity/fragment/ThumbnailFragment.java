package com.iaai.onyard.activity.fragment;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.activity.ImageReviewActivity;
import com.iaai.onyard.application.OnYard.ImageMode;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.session.OnYardImageData;
import com.squareup.picasso.Picasso;

/**
 * ThumbnailFragment used in CameraActivity to display image.
 */
public class ThumbnailFragment extends BaseFragment {

    @InjectView(R.id.thumbnailButton)
    ImageButton mThumbnailButton;
    @InjectView(R.id.thumbnailOverlay)
    ImageView mThumbnailOverlay;

    private static final int THUMBNAIL_WIDTH = 300;
    private static final int THUMBNAIL_HEIGHT = 225;
    private int mImageOrder;
    private ImageMode mImageMode;

    /**
     * Overrides the onCreateView which sets the layout of the fragment.
     * 
     * @param inflater Used to inflate the view
     * @param container Parent view
     * @param savedInstanceState previous saved state
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_thumbnail, container, false);

            ButterKnife.inject(this, view);
            mThumbnailButton.setOnClickListener(getReviewScreen());

            return view;
        }
        catch (final Exception e) {
            logError(e);
            return container;
        }
    }

    public void displayThumbnail(ImageMode imageMode, int imageOrder) {
        mImageMode = imageMode;
        mImageOrder = imageOrder;

        final Activity activity = getActivity();
        if (activity != null) {

            final OnYardImageData iData = getSessionData().getOnYardImageData(mImageMode);
            Picasso.with(activity)
            .load(new File(iData.getImagePath(
                    mImageOrder))).resize(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
                    .into(mThumbnailButton);


            if (iData.hasOverlayImage(imageOrder)) {
                Picasso.with(activity)
                .load(iData.getThumbOverlayImageResourceId(activity, imageOrder))
                .resize(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT).into(mThumbnailOverlay);
                mThumbnailOverlay.setVisibility(View.VISIBLE);
            }
            else {
                mThumbnailOverlay.setVisibility(View.INVISIBLE);
            }

        }
    }

    /**
     * Pulls up the ImageReviewActivity
     * 
     * @return button listener
     */
    private OnClickListener getReviewScreen() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getImageReview();
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {}

    /**
     * Opens the ImageReviewActivity and passes ImagerData and the Image number to the new activity.
     */
    private void getImageReview() {
        try {
            final Activity activity = getActivity();
            if (activity != null) {
                final Intent intent = new Intent(activity, ImageReviewActivity.class);
                intent.putExtra(IntentExtraKey.REVIEW_IMAGE_SEQUENCE, mImageOrder);
                intent.putExtra(IntentExtraKey.IMAGE_MODE, mImageMode);
                startActivity(intent);
            }
        }
        catch (final Exception e) {
            logError(e);
        }
    }
}
