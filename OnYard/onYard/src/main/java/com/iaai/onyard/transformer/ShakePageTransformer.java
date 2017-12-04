package com.iaai.onyard.transformer;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.iaai.onyard.R;

public class ShakePageTransformer implements PageTransformer {

    private final ViewPager mImagePager;

    public ShakePageTransformer(ViewPager pager) {
        super();
        mImagePager = pager;
    }

    @Override
    public void transformPage(View view, float position) {
        view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.

            view.setAlpha(0);

        }
        else
            if (position == 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                if (mImagePager.getCurrentItem() == mImagePager.getAdapter().getCount() - 1
                        || mImagePager.getCurrentItem() == 0) {
                    final Animation shake = AnimationUtils.loadAnimation(view.getContext(),
                            R.anim.move_image);

                    view.startAnimation(shake);

                }
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            }
            else
                if (position < 0) { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    if (mImagePager.getCurrentItem() == 0) {
                        final Animation shake = AnimationUtils.loadAnimation(view.getContext(),
                                R.anim.move_image);

                        view.startAnimation(shake);

                    }
                    view.setAlpha(1);
                    view.setTranslationX(0);
                    view.setScaleX(1);
                    view.setScaleY(1);

                }
                else
                    if (position <= 1) { // (0,1]
                        if (mImagePager.getCurrentItem() == mImagePager.getAdapter().getCount() - 1) {
                            final Animation shake = AnimationUtils.loadAnimation(view.getContext(),
                                    R.anim.move_image);

                            view.startAnimation(shake);

                        }
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

}
