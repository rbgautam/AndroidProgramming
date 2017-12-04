package com.iaai.onyard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.iaai.onyard.R;
import com.iaai.onyard.adapter.ImageReviewPageAdapter;
import com.iaai.onyard.application.OnYard.ImageMode;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.dialog.ErrorDialogFragment;
import com.iaai.onyard.transformer.ShakePageTransformer;
import com.iaai.onyard.utility.LogHelper;

/**
 * Allows the user to review an image and delete the specific image.
 */
public class ImageReviewActivity extends BaseActivity {
    private static final String ERROR_DIALOG_FRAGMENT_TAG = "error_dialog";
    private static final String INITIALIZATION_ERROR_MESSAGE = "There was an error initializing the Review screen.";
    private int mReviewImageSequence;
    private ImageReviewPageAdapter mImageAdapter;
    private ViewPager mViewPager;
    private ImageMode mImageMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_image_review);

            final Intent intent = getIntent();

            mReviewImageSequence = intent.getIntExtra(IntentExtraKey.REVIEW_IMAGE_SEQUENCE, 1);

            if (getIntent().getExtras().containsKey(IntentExtraKey.IMAGE_MODE)) {
                mImageMode = (ImageMode) getIntent().getExtras().getSerializable(
                        IntentExtraKey.IMAGE_MODE);
            }
            else {
                mImageMode = ImageMode.STANDARD;
            }

            mImageAdapter = new ImageReviewPageAdapter(getSupportFragmentManager(),
                    getSessionData(),
                    mImageMode);
            final int index = mImageAdapter.getPrimaryIndex(mReviewImageSequence);
            mViewPager = (ViewPager) findViewById(R.id.image_review_pager);
            mViewPager.setAdapter(mImageAdapter);
            mViewPager.setCurrentItem(index);
            if (getSessionData().getOnYardImageData(mImageMode).getNumImagesTaken() > 1) {
                mViewPager.setPageTransformer(true, new ShakePageTransformer(mViewPager));
            };
        }catch(final Exception e){
            final ErrorDialogFragment dialog = ErrorDialogFragment
                    .newInstance(INITIALIZATION_ERROR_MESSAGE);
            dialog.show(getSupportFragmentManager(), ERROR_DIALOG_FRAGMENT_TAG);
            LogHelper.logError(getApplicationContext(), e, this.getClass().getSimpleName());
        }
    }
}