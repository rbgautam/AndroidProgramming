package com.iaai.onyard.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.iaai.onyard.activity.fragment.ImageReviewFragment;
import com.iaai.onyard.application.OnYard.ImageMode;
import com.iaai.onyard.session.OnYardSessionData;

/**
 * Adapter that deals with ImageReviewFragments and allows sliding from image to image on the
 * ImageReviewActivity. The adapter will be called by a ViewPager.
 */
public class ImageReviewPageAdapter extends FragmentStatePagerAdapter {
    private final ArrayList<Integer> mImageArr;
    private final ImageMode mImageMode;

    /**
     * Constructor that takes a fragment manager and imagerdata.
     * 
     * @param fm Fragment manager to add fragments
     * @param data Information to be passed to the fragments
     */
    public ImageReviewPageAdapter(FragmentManager fm, OnYardSessionData data, ImageMode imageMode) {
        super(fm);
        mImageMode = imageMode;
        mImageArr = data.getOnYardImageData(mImageMode).getTakenImageSequences();
    }

    /**
     * Creates the fragment in the next or previous position.
     * 
     * @param position The next or previous position.
     */
    @Override
    public Fragment getItem(int position) {
        final ImageReviewFragment imageFrag = ImageReviewFragment.newInstance(mImageMode,
                mImageArr.get(position));

        return imageFrag;
    }

    /**
     * Gets the number of fragments in the adapter.
     * 
     * @return number of ImageReviewFragments
     */
    @Override
    public int getCount() {
        return mImageArr.size();
    }

    /**
     * Gets the caption for current position.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return ((ImageReviewFragment) getItem(position)).getImageCaption();
    }

    /**
     * Gets the index for the number passed in. It will return -1 if the index does not exist. This
     * is used in conjunction with a ViewPager's setCurrentItem(int position).
     * 
     * @param num The number being searched for in the index.
     * @return position of the
     */
    public int getPrimaryIndex(int num) {
        return mImageArr.indexOf(num);
    }
}
