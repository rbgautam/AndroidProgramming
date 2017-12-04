package com.iaai.onyard.activity.fragment;

import java.io.File;
import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.activity.CameraActivity;
import com.iaai.onyard.application.OnYard.ImageMode;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.session.OnYardSessionData;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

/**
 * ImageReviewFragment used in ImageReview Activity.
 */
public class ImageReviewFragment extends BaseFragment {

    private String mDisplayedImagePath;
    private static final String DISPLAYED_IMAGE_PATH_KEY = "displayed_image_path";
    private static final String IMAGE_NUMBER_KEY = "image_number_key";
    private static final String IMAGE_MODE_KEY = "image_mode";
    private static final String IMAGE_ERROR_MESSAGE = "Unable to load image in Image Review";
    private static final String INITIALIZATION_ERROR_MESSAGE = "Unable to load Image fragment Menu items.";
    private static final String IMAGE_DELETE_ERROR_MESSAGE = "Error deleting image from Image Review.";
    private int mReviewImageSequence;
    private ImageMode mImageMode;
    private static final int REVIEW_IMAGE_WIDTH = 640;
    private static final int REVIEW_IMAGE_HEIGHT = 480;

    @InjectView(R.id.full_img)
    ImageView mImage;

    /**
     * Initializes a new Review Image fragment fragment and sends ImagerData and an image sequence
     * as a bundle, which will allow the fragment to initialize the data later.
     * 
     * @param data The vehicle and image data
     * @param imageSeq The image sequence.
     * @return new ImageReviewFragment
     */
    public static ImageReviewFragment newInstance(ImageMode imageMode,
            int imageSeq) {
        final ImageReviewFragment imageReview = new ImageReviewFragment();
        final Bundle imgBundle = new Bundle();
        imgBundle.putSerializable(IMAGE_MODE_KEY, imageMode);
        imgBundle.putInt(IMAGE_NUMBER_KEY, imageSeq);
        imageReview.setArguments(imgBundle);
        return imageReview;
    }

    /**
     * Sets the information called in newInstance.
     */
    private void getPackages() {
        mReviewImageSequence = getArguments().getInt(IMAGE_NUMBER_KEY);
        mImageMode = (ImageMode) getArguments().getSerializable(IMAGE_MODE_KEY);
    }

    /**
     * Sets the view for ImageReviewFragment
     * 
     * @param inflater Inflates the view\
     * @param container Parent View
     * @param savedInstanceState Previous state
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            getPackages();
            setHasOptionsMenu(true);
            final View view = inflater.inflate(R.layout.fragment_image_review, container, false);
            ButterKnife.inject(this, view);

            displayImage();
            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(IMAGE_ERROR_MESSAGE);
            logError(e);
            return container;
        }


    }

    /**
     * When the activity is created, the ImageView will display the image.
     * 
     * @param savedInstanceState previous saved state
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            try {
                savedInstanceState.getString(DISPLAYED_IMAGE_PATH_KEY);
                displayImage();
            }
            catch (final Exception e) {
                showFatalErrorDialog(IMAGE_ERROR_MESSAGE);
                logError(e);
            }
        }

    }

    /**
     * Creates the action bar and inflates it with the caption and delete button.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        try {
            inflater.inflate(R.menu.image_review, menu);

            final Activity activity = getActivity();
            if (activity != null) {
                final ActionBar actionBar = activity.getActionBar();
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
                        R.color.imager_blue)));
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setTitle(getSessionData().getOnYardImageData(mImageMode).getImageCaption(
                        mReviewImageSequence));
            }
        }
        catch (final Exception e) {
            showFatalErrorDialog(INITIALIZATION_ERROR_MESSAGE);
            logError(e);
        }
    }

    /**
     * Allows the action bar buttons to be selected.
     * 
     * @param item Menu item being selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            // Handle presses on the action bar items
            if (item.getItemId() == R.id.action_delete) {
                final Activity activity = getActivity();
                if (activity != null) {
                    PicassoTools.clearCache(Picasso.with(activity));
                    deleteImage();
                    return true;
                }
                else {
                    return super.onOptionsItemSelected(item);
                }
            }
            return super.onOptionsItemSelected(item);
        }
        catch (final Exception e) {
            showErrorDialog(IMAGE_DELETE_ERROR_MESSAGE);
            logError(e);
            return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Will save the current instance of the fragment
     * 
     * @param outState previous bundle information
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mDisplayedImagePath != null) {
            outState.putString(DISPLAYED_IMAGE_PATH_KEY, mDisplayedImagePath);
        }
    }

    /**
     * Displays an image in the fragment.
     * 
     * @param imagePath
     */
    private void displayImage() throws IOException {
        final Activity activity = getActivity();
        if (activity != null) {
            mDisplayedImagePath = getSessionData().getOnYardImageData(mImageMode).getImagePath(
                    mReviewImageSequence);

            Picasso.with(activity).load(new File(mDisplayedImagePath))
            .resize(REVIEW_IMAGE_WIDTH, REVIEW_IMAGE_HEIGHT).into(mImage);
        }
    }

    /**
     * Deletes the Image when the delete button is pressed.
     */
    private void deleteImage() {
        final Activity activity = getActivity();
        if (activity != null) {
            final OnYardSessionData sessionData = getSessionData();
            sessionData.getOnYardImageData(mImageMode).deleteUncommittedImage(mReviewImageSequence,
                    activity.getApplicationContext());
            setSessionData(sessionData);

            final Intent finishIntent = new Intent(activity, CameraActivity.class);
            finishIntent.putExtra(IntentExtraKey.IMAGE_MODE, mImageMode);
            finishIntent.putExtra(IntentExtraKey.REVIEW_IMAGE_SEQUENCE, mReviewImageSequence);
            finishIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(finishIntent);
            activity.finish();
        }
    }

    public String getImageCaption() {
        if (getActivity() != null) {
            return getSessionData().getOnYardImageData(mImageMode)
                    .getImageCaption(mReviewImageSequence);
        }
        else {
            return "";
        }
    }
}
