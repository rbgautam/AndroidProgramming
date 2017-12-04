package com.iaai.onyard.camera;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.iaai.onyard.R;
import com.iaai.onyard.adapter.ImageReviewPageAdapter;
import com.iaai.onyard.utility.LogHelper;


/**
 * When the ViewPager is at the beginning or end page, the image will bounce back. This class
 * assumes it is being called by ImageReviewActivity.
 */
public class BouncePageTransformer implements PageTransformer {

    private static final int CURRENT_PAGE = 0;
    private static final int RIGHT_PAGE = 1;
    private static final int LEFT_PAGE = -1;
    private static final int POSITIVE_BOUNCE_DISTANCE = 400;
    private static final int NEGATIVE_BOUNCE_DISTANCE = -400;
    ImageReviewPageAdapter mReviewPageAdapter;
    ViewPager mImagePager;


    /**
     * Constructor that keeps track what image is being displayed by a ViewPager.
     * 
     * @param pager ViewPager being kept track of.
     */
    public BouncePageTransformer(ViewPager pager) {
        super();
        mImagePager = pager;
    }

    /**
     * Changes the First and last Page to bounce when they are touched.
     * 
     * @param view Current view
     * @param position Page position -1 means page to the left, 1 means page to the right
     */
    @Override
    public void transformPage(View view, float position) {

        if (position < LEFT_PAGE) { // [-Infinity,-1)
            // This page is way off-screen to the left.

            view.setAlpha(0);

        }
        else
            if (position == CURRENT_PAGE) { // [-1,0]
                ImageView reviewImage = null;
                try {
                    reviewImage = (ImageView) view.findViewById(R.id.full_img);
                }
                catch (final NullPointerException e) {
                    LogHelper.logError(view.getContext(), e, this.getClass().getSimpleName());
                }
                // Use the default slide transition when moving to the left page
                if (mImagePager.getCurrentItem() == 0) {

                    try{
                        bounceImage(reviewImage, POSITIVE_BOUNCE_DISTANCE);
                    }
                    catch( final Exception e){
                        LogHelper.logError(view.getContext(), e, this.getClass().getSimpleName());

                    }

                }
                else
                    if (mImagePager.getCurrentItem() == mImagePager.getAdapter().getCount()
                    - RIGHT_PAGE) {
                        try{
                            bounceImage(reviewImage, NEGATIVE_BOUNCE_DISTANCE);
                        }
                        catch( final Exception e){
                            LogHelper.logError(view.getContext(), e, this.getClass()
                                    .getSimpleName());

                        }
                    }

                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            }
            else
                if (position < CURRENT_PAGE) { // [-1,0]
                    // Use the default slide transition when moving to the left page

                    view.setAlpha(1);
                    view.setTranslationX(0);
                    view.setScaleX(1);
                    view.setScaleY(1);

                }
                else
                    if (position <= RIGHT_PAGE) { // (0,1]

                        view.setAlpha(1);
                        view.setTranslationX(0);
                        view.setScaleX(1);
                        view.setScaleY(1);


                    }
                    else { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        view.setAlpha(0);
                    }
    }

    /**
     * Private image
     * 
     * @param reviewImage
     * @param xPosition
     */
    private void bounceImage(ImageView reviewImage, int xPosition) {
        Animation move = new TranslateAnimation(0, xPosition, 0, 0);

        move.setDuration(500);
        reviewImage.startAnimation(move);
        move = new TranslateAnimation(xPosition, 0, 0, 0);
        move.setDuration(500);
        reviewImage.startAnimation(move);
    }





}
