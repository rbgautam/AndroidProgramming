package com.iaai.onyard.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.activity.fragment.ThumbnailFragment;
import com.iaai.onyard.adapter.ImagerThumbnailAdapter;
import com.iaai.onyard.application.OnYard;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.application.OnYard.ImageMode;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.camera.CameraPreview;
import com.iaai.onyard.camera.LevelLineView;
import com.iaai.onyard.camera.OnYardRecognitionListener;
import com.iaai.onyard.classes.OnYardCamera;
import com.iaai.onyard.classes.OnYardPreferences;
import com.iaai.onyard.classes.Resolution;
import com.iaai.onyard.dialog.ErrorDialogFragment;
import com.iaai.onyard.event.ChangeCameraParamsEvent;
import com.iaai.onyard.event.ImageSavedEvent;
import com.iaai.onyard.event.VoiceRecognitionEvent;
import com.iaai.onyard.session.OnYardImageData;
import com.iaai.onyard.session.OnYardSessionData;
import com.iaai.onyard.task.LoadByteArrayImageTask;
import com.iaai.onyard.utility.LogHelper;
import com.iaai.onyard.utility.ResolutionHelper;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
/**
 * Allows the user to see the camera preview
 */
public class CameraActivity extends BaseActivity implements PictureCallback,
ShutterCallback, AutoFocusCallback {

    private static final String INITIALIZATION_ERROR_MESSAGE = "There was an error initializing the camera.";
    private static final String PAUSE_ERROR_MESSAGE = "There was an error pausing the camera.";
    private static final String IMAGE_SAVE_ERROR_MESSAGE = "There was an error while saving the photo. The photo could not be saved.";
    private static final String IMAGE_CAPTURE_ERROR_MESSAGE = "There was an error while taking the photo. The photo could not be taken.";
    private static final String ERROR_DIALOG_FRAGMENT_TAG = "error_dialog";
    private static final String CAM_PARAMS_DIALOG_FRAGMENT_TAG = "camera_params_dialog";
    private static final String CANNOT_FOCUS_MESSAGE = "Can't focus. Please try again.";
    private static final int ANIMATION_DURATION = 300;
    private static final float ZOOM_CONTROL_ENABLED = 0.5F;
    private static final float ZOOM_CONTROL_DISABLED = 0.15F;
    private static final boolean ZOOM_CONTROL_VISIBLE = true;
    private static final boolean ZOOM_CONTROL_INVISIBLE = false;
    private static final int ZOOM_CONTROL_DELAY = 100;
    private static int LEVEL_LINE_THRESHOLD = 3;
    private static int MAX_ANDROID_JPEG_QUALITY = 100;
    private static int LEVEL_LINE_LEVEL_COLOR = Color.GREEN;
    private static int LEVEL_LINE_NOT_LEVEL_COLOR = Color.RED;
    private static final int REFOCUS_TIMER_MS = 2000;

    @InjectView(R.id.camera_preview)
    FrameLayout mCameraPreviewFrame;
    @InjectView(R.id.button_capture)
    ImageButton mCaptureButton;
    @InjectView(R.id.flash_button)
    ImageButton mFlashButton;
    @InjectView(R.id.focus_button)
    ImageButton mFocusButton;
    @InjectView(R.id.zoom_in)
    ImageButton mZoomInButton;
    @InjectView(R.id.zoom_out)
    ImageButton mZoomOutButton;
    @InjectView(R.id.navigation_overlay_layout)
    View mNavOverlay;
    @InjectView(R.id.CarCaption)
    Button mCaptionButton;
    @InjectView(R.id.camera_full_image)
    ImageView mFullImage;

    private LevelLineView mLevelViewGreen;
    private LevelLineView mLevelViewRed;
    private SensorEventListener mSensorEventListener;

    private OnYardRecognitionListener mRecognitionListener;
    private OnYardCamera mCamera;
    private boolean mCaptureButtonPressed;

    private CameraPreview mPreview;
    private ImageView mOverlayView;

    private int mImageSequence;

    private SpeechRecognizer mSpeechRecognizer;

    private MediaPlayer mShootSound = null;
    private MediaPlayer mCannotFocusSound = null;

    private int mPreviewWidth;
    private int mPreviewHeight;

    private ImageMode mImageMode;
    private Toast mCannotFocusToast;

    private final Runnable mReFocusRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                mCamera.enableFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.autoFocus(CameraActivity.this);
            }
            catch (final Exception e) {
                logError(e);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_camera);
            ButterKnife.inject(this);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            final Bundle intentExtras = getIntent().getExtras();

            if (intentExtras != null && intentExtras.containsKey(IntentExtraKey.IMAGE_MODE)) {
                mImageMode = (ImageMode) intentExtras.getSerializable(
                        IntentExtraKey.IMAGE_MODE);
            }
            else {
                mImageMode = ImageMode.STANDARD;
            }

            if (intentExtras != null
                    && intentExtras.containsKey(IntentExtraKey.REVIEW_IMAGE_SEQUENCE)) {
                mImageSequence = intentExtras.getInt(IntentExtraKey.REVIEW_IMAGE_SEQUENCE);
            }
            else {
                mImageSequence = getSessionData().getOnYardImageData(mImageMode)
                        .getFirstUntakenImageSequence();
            }

            mCannotFocusToast = Toast.makeText(this, CANNOT_FOCUS_MESSAGE, Toast.LENGTH_SHORT);
            mCannotFocusToast.setGravity(Gravity.CENTER, 0, 0);
        }
        catch (final Exception e) {
            showFatalErrorDialog(INITIALIZATION_ERROR_MESSAGE);
            logError(e);
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            initializeViews();

            if (isCameraFrameInflated()) {
                sizeCameraViews();
                onCameraReady();
            }

            try {
                toggleMediaSounds(false);
                listenForVoiceIfEnabled();
                toggleCaptureButton(true);
            }
            catch (final Exception e) {
                LogHelper.logWarning(getApplicationContext(), e, this.getClass().getSimpleName());
            }
        }
        catch (final Exception e) {
            showFatalErrorDialog(INITIALIZATION_ERROR_MESSAGE);
            logError(e);
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            cleanUp();
        }
        catch (final Exception e) {
            showFatalErrorDialog(PAUSE_ERROR_MESSAGE);
            logError(e);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            cleanUp();
        }
        catch (final Exception e) {
            showFatalErrorDialog(PAUSE_ERROR_MESSAGE);
            logError(e);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            switch (keyCode)
            {
                case KeyEvent.KEYCODE_FOCUS:
                {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0
                            && mCaptureButton != null) {
                        return mCaptureButton.performClick();
                    }
                    break;
                }
                case KeyEvent.KEYCODE_BACK:
                {
                    if (isImageNavOverlayShowing()) {
                        setNavOverlayVisibility(View.INVISIBLE);
                        setZoomControlVisibility(View.VISIBLE);
                        listenForVoiceIfEnabled();
                        return true;
                    }
                    break;
                }
            }
            return super.onKeyDown(keyCode, event);
        }
        catch (final Exception e) {
            logWarning(e);
            return false;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        try {
            super.onWindowFocusChanged(hasFocus);

            if (hasFocus && isCameraFrameInflated()) {
                sizeCameraViews();
                onCameraReady();
            }
        }
        catch (final Exception e) {
            showFatalErrorDialog(INITIALIZATION_ERROR_MESSAGE);
            logError(e);
        }
    }

    private void initializeCamera() {
        if (mCamera == null) {
            mCamera = new OnYardCamera(this);
        }
    }

    private void initializeCameraPreview() {
        mCaptureButton.setOnClickListener(captureImageClick());

        mFlashButton.setOnClickListener(flashOnClick());
        mFocusButton.setOnClickListener(focusOnClick());

        mZoomInButton.setOnTouchListener(zoomInTouch());
        mZoomOutButton.setOnTouchListener(zoomOutTouch());

        // Zoom controls
        try {
            // do not show zoom controls if navigation overlay is being displayed
            setCustomZoomControls(isImageNavOverlayShowing() ? ZOOM_CONTROL_INVISIBLE
                    : ZOOM_CONTROL_VISIBLE);
        }
        catch (final Exception e) {
            LogHelper.logWarning(getApplicationContext(), e, this.getClass().getSimpleName());
        }

        // Create Preview view and set it as content of activity
        mPreview = new CameraPreview(this, mCamera, mCameraPreviewFrame);

        mCameraPreviewFrame.removeAllViews();
        mCameraPreviewFrame.addView(mPreview);
    }

    private void initializeViews() {
        setNavOverlayVisibility(View.INVISIBLE);

        initializeCamera();
        initializeCameraPreview();

        mCaptionButton.setText(getSessionData().getOnYardImageData(mImageMode).getImageCaption(
                mImageSequence));
        mCaptionButton.setOnClickListener(showNavigationOverlay());

        if (mCamera.isFlashSupported()) {
            mFlashButton.setImageResource(mCamera.getFlashModeDrawableId(this));
            mFlashButton.invalidate();
        }
        else {
            mFlashButton.setVisibility(View.GONE);
        }

        mOverlayView = new ImageView(this);
    }

    private void sizeCameraViews() {
        final int currFrameWidth = mCameraPreviewFrame.getWidth();
        final int currFrameHeight = mCameraPreviewFrame.getHeight();

        final Resolution previewRes = ResolutionHelper.getLargest4by3Res(currFrameWidth,
                currFrameHeight);
        // load cached dims if portrait
        if (currFrameHeight > currFrameWidth) {
            previewRes.setHeight(mPreviewHeight);
            previewRes.setWidth(mPreviewWidth);
        }
        final boolean previewDimsChanged = mPreview.getWidth() != previewRes.getWidth()
                && mPreview.getHeight() != previewRes.getHeight();

        if (previewDimsChanged) {
            final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(previewRes.getWidth(),
                    previewRes.getHeight());
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            mPreview.setLayoutParams(lp);

            mPreviewHeight = previewRes.getHeight();
            mPreviewWidth = previewRes.getWidth();
        }
    }

    private void cleanUp() {
        releaseCamera();
        releaseLevelLineListener();
        releaseSpeechRecognizer();
        toggleMediaSounds(true);
        releaseMediaPlayer();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        if (mPreview != null) {
            mPreview.getHolder().removeCallback(mPreview);
        }
        mOverlayView = null;
    }

    private void releaseMediaPlayer() {
        if (mShootSound != null) {
            mShootSound.reset();
            mShootSound.release();
        }
        if (mCannotFocusSound != null) {
            mCannotFocusSound.reset();
            mCannotFocusSound.release();
        }

        mShootSound = null;
        mCannotFocusSound = null;
    }

    private void releaseSpeechRecognizer() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
        }
        mSpeechRecognizer = null;
        mRecognitionListener = null;
        enableSystemSounds();
    }

    private void releaseLevelLineListener() {
        try {
            unregisterLevelLineListener();
            mSensorEventListener = null;
            if (mLevelViewGreen != null) {
                mLevelViewGreen.setVisibility(View.GONE);
                mLevelViewGreen = null;
            }
            if (mLevelViewRed != null) {
                mLevelViewRed.setVisibility(View.GONE);
                mLevelViewRed = null;
            }
        }
        catch (final Exception e) {
            LogHelper.logWarning(getApplicationContext(), e, this.getClass().getSimpleName());
        }
    }

    private void onCameraReady() {
        if (mLevelViewGreen == null && mLevelViewRed == null) {
            mLevelViewGreen = new LevelLineView(this, 0, mPreviewHeight / 2, mPreviewWidth - 1,
                    mPreviewHeight / 2, LEVEL_LINE_LEVEL_COLOR);
            mLevelViewRed = new LevelLineView(this, 0, mPreviewHeight / 2, mPreviewWidth - 1,
                    mPreviewHeight / 2, LEVEL_LINE_NOT_LEVEL_COLOR);
            mLevelViewGreen.setVisibility(View.INVISIBLE);
            mLevelViewRed.setVisibility(View.INVISIBLE);

            mCameraPreviewFrame.addView(mLevelViewGreen);
            mCameraPreviewFrame.addView(mLevelViewRed);

            setCameraParameters();
        }
    }

    private void setNavOverlayVisibility(int visibility) {
        setViewVisibility(mNavOverlay, visibility);
    }

    private void setZoomControlVisibility(int visibility) {
        setViewVisibility(findViewById(R.id.zoom_controls_layout), visibility);
    }

    private void setViewVisibility(View view, int visibility) {
        view.setVisibility(visibility);
    }

    private boolean isImageNavOverlayShowing() {
        return mNavOverlay.getVisibility() == View.VISIBLE;
    }

    private void setCameraParameters() {
        final OnYardImageData imageData = getSessionData().getOnYardImageData(mImageMode);

        // disable native shutter sound
        try {
            mCamera.disableShutterSound();
        }
        catch (final Exception e) {
            logWarning(e);
        }

        // JPEG quality
        try {
            // Setting JPEG quality to 100 when image is taken - compression by 3rd party library
            // takes place at a later point
            mCamera.setJpegQuality(MAX_ANDROID_JPEG_QUALITY);
        }
        catch (final Exception e) {
            showFatalErrorDialog(INITIALIZATION_ERROR_MESSAGE);
            logError(e);
        }

        // Resolution
        try
        {
            mCamera.setImageResolution(imageData.getMinImageRes(mImageSequence));
        }
        catch (final Exception e) {
            showFatalErrorDialog(INITIALIZATION_ERROR_MESSAGE);
            logError(e);
        }

        // Overlay
        try {
            mCameraPreviewFrame.removeView(mOverlayView);
            if (imageData.hasOverlayImage(mImageSequence)) {
                Picasso.with(this).load(imageData.getOverlayImageResourceId(this, mImageSequence))
                .resize(mPreviewWidth, mPreviewHeight).centerCrop().into(mOverlayView);

                mCameraPreviewFrame.addView(mOverlayView);
                mCameraPreviewFrame.invalidate();
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }

        // level line
        try {
            final boolean isLevelLineEnabled = imageData.isLevelLineEnabled(mImageSequence);
            if (isLevelLineEnabled) {
                startLevelLineListener();
            }
            else {
                stopLevelLineListener();
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }

        // Focus
        try {
            if (mCamera.isFocusSupported()) {
                final String newFocusMode = imageData.getDefaultFocusMode(mImageSequence);
                mCamera.enableFocusMode(newFocusMode);

                mFocusButton.setImageResource(mCamera.getFocusModeDrawableId(this));
                mFocusButton.invalidate();

                if (OnYard.isKitKat()
                        && newFocusMode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    mFocusButton.post(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                mCamera.enableFocusMode(newFocusMode);
                            }
                            catch (final Exception e) {
                                logWarning(e);
                            }
                        }
                    });
                }
            }
            else {
                mFocusButton.setVisibility(View.GONE);
            }
        }
        catch (final Exception e) {
            logError(e);
        }

        // Zoom
        try {
            mCamera.resetZoom();
        }
        catch (final Exception e) {
            logWarning(e);
        }

        if (OnYard.isDeviceCN51()) {
            mCamera.setRotation(180);
        }
    }


    private void setCustomZoomControls(boolean bVisible) {
        if (mCamera.isMinZoom()) {
            toggleZoomButton(mZoomInButton, true);
            toggleZoomButton(mZoomOutButton, false);
        }
        else
            if (mCamera.isMaxZoom()) {
                toggleZoomButton(mZoomInButton, false);
                toggleZoomButton(mZoomOutButton, true);
            }
            else {
                toggleZoomButton(mZoomInButton, true);
                toggleZoomButton(mZoomOutButton, true);
            }
        setZoomControlVisibility(bVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private void toggleZoomButton(ImageButton ib, boolean toEnable) {
        ib.setAlpha(toEnable ? ZOOM_CONTROL_ENABLED : ZOOM_CONTROL_DISABLED);
    }

    private boolean isCameraFrameInflated() {
        return mCameraPreviewFrame.getHeight() != 0 && mCameraPreviewFrame.getWidth() != 0;
    }

    /**
     * Listener to capture Image
     * 
     * @return
     */
    private View.OnClickListener captureImageClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (isCaptureEnabled()) {
                        toggleCaptureButton(false);
                        // Hack for the Autofocus callback not getting received
                        if (mCamera
                                .isFocusModeEnabled(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                            mCaptureButton.postDelayed(mReFocusRunnable, REFOCUS_TIMER_MS);
                        }

                        if (mCamera.isFocusModeEnabled(Camera.Parameters.FOCUS_MODE_AUTO)
                                || mCamera.isFocusModeEnabled(Camera.Parameters.FOCUS_MODE_MACRO)
                                || mCamera
                                .isFocusModeEnabled(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                            mCaptureButtonPressed = true;
                            mCamera.autoFocus(CameraActivity.this);
                        }
                        else {
                            mCamera.takePicture(CameraActivity.this, CameraActivity.this);
                        }
                    }
                }
                catch (final Exception e) {
                    showErrorDialog(IMAGE_CAPTURE_ERROR_MESSAGE);
                    logError(e);
                    toggleCaptureButton(true);
                }
            }
        };
    }
    /**
     * Listeners for both single touch and touch-and-hold gestures
     */
    private OnTouchListener zoomInTouch() {
        return new View.OnTouchListener() {

            private Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            if (mHandler != null) {
                                return true;
                            }
                            mHandler = new Handler();
                            // first touch to execute immediately
                            mHandler.postDelayed(mAction, 50);
                            break;
                        case MotionEvent.ACTION_UP:
                            if (mHandler == null) {
                                return true;
                            }
                            mHandler.removeCallbacks(mAction);
                            mHandler = null;
                            break;
                    }
                    return false;
                }
                catch (final Exception e) {
                    logError(e);
                    return false;
                }
            }

            Runnable mAction = new Runnable() {

                @Override
                public void run() {
                    try {
                        doZoomIn();
                        mHandler.postDelayed(this, ZOOM_CONTROL_DELAY);
                    }
                    catch (final Exception e) {
                        logError(e);
                    }
                }
            };
        };
    }

    private OnTouchListener zoomOutTouch() {
        return new View.OnTouchListener() {

            private Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            if (mHandler != null) {
                                return true;
                            }
                            mHandler = new Handler();
                            // first touch to execute immediately
                            mHandler.postDelayed(mAction, 0);
                            break;
                        case MotionEvent.ACTION_UP:
                            if (mHandler == null) {
                                return true;
                            }
                            mHandler.removeCallbacks(mAction);
                            mHandler = null;
                            break;
                    }
                    return false;
                }
                catch (final Exception e) {
                    logError(e);
                    return false;
                }
            }

            Runnable mAction = new Runnable() {

                @Override
                public void run() {
                    try {
                        doZoomOut();
                        mHandler.postDelayed(this, ZOOM_CONTROL_DELAY);
                    }
                    catch (final Exception e) {
                        logError(e);
                    }
                }
            };
        };
    }

    private OnClickListener flashOnClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (isCaptureEnabled()) {
                        toggleFlashMode();
                    }
                }
                catch (final Exception e) {
                    logError(e);
                }
            }
        };
    }

    private OnClickListener focusOnClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (isCaptureEnabled()) {
                        toggleFocusMode();
                    }
                }
                catch (final Exception e) {
                    logError(e);
                }
            }
        };
    }
    /**
     * Zooming in by one zoom level
     */
    private void doZoomIn() {
        mCamera.zoomIn();
        setCustomZoomControls(ZOOM_CONTROL_VISIBLE);
    }

    /**
     * Zooming out by one zoom level
     */
    private void doZoomOut() {
        mCamera.zoomOut();
        setCustomZoomControls(ZOOM_CONTROL_VISIBLE);
    }

    /**
     * Toggle camera flash mode
     */
    private void toggleFlashMode() {
        mFlashButton.setImageResource(mCamera.toggleFlashMode(this));
        mFlashButton.invalidate();
    }

    /**
     * Toggle camera focus mode
     */
    private void toggleFocusMode() {
        mFocusButton.setImageResource(mCamera.toggleFocusMode(this));
        mFocusButton.invalidate();
    }

    /**
     * If all required images were taken, start Image Save activity. If there are more image(s) to
     * take, prepare for next image.
     */
    @Subscribe
    public void onImageSaved(ImageSavedEvent event) {
        try {
            dismissProgressDialog();
            if (getSessionData().getOnYardImageData(mImageMode).areAllRequiredImagesTaken(false)) {
                finish();
            }
            else {
                // make sure image was successfully saved in file.
                // If not display an error message and stay on the same image
                if (!event.isSuccessful()) {
                    showErrorDialog("Image could not be captured. Please try again.");
                }

                imageToThumbnail(R.id.camera_full_image, R.id.camera_layout);

                showThumbnail(mImageSequence);

                mImageSequence = getSessionData().getOnYardImageData(mImageMode)
                        .getNextUntakenImageSequence(
                                mImageSequence);

                previewNextImage();

                toggleCaptureButton(true);
                mCamera.startPreview();
            }
        }
        catch (final Exception e) {
            final ErrorDialogFragment dialog = ErrorDialogFragment
                    .newInstance(IMAGE_SAVE_ERROR_MESSAGE);
            dialog.show(getSupportFragmentManager(), ERROR_DIALOG_FRAGMENT_TAG);
            LogHelper.logError(getApplicationContext(), e, this.getClass().getSimpleName());
        }
    }

    private void previewNextImage() {
        setCameraParameters();
        setCustomZoomControls(ZOOM_CONTROL_VISIBLE);

        mCaptionButton.setText(getSessionData().getOnYardImageData(mImageMode).getImageCaption(
                mImageSequence));
    }

    private OnClickListener showNavigationOverlay() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GridView thumbView;
                try {
                    setNavOverlayVisibility(View.VISIBLE);
                    setZoomControlVisibility(View.INVISIBLE);
                    releaseSpeechRecognizer();

                    thumbView = (GridView) findViewById(R.id.navigation_overlay_grid);
                    thumbView.setAdapter(new ImagerThumbnailAdapter(getSessionData()
                            .getOnYardImageData(mImageMode)));
                    thumbView.setOnItemClickListener(onNavigationThumbnailClick());
                }
                catch (final Exception e) {
                    logError(e);
                }
            }

            private OnItemClickListener onNavigationThumbnailClick() {
                return new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        try {
                            final int imageSeq = getSessionData().getOnYardImageData(mImageMode)
                                    .getAllImageSequences()
                                    .get(position);

                            if (getSessionData().getOnYardImageData(mImageMode).isImageTaken(
                                    imageSeq)) {
                                // go to image review
                                final Intent intent = new Intent(CameraActivity.this,
                                        ImageReviewActivity.class);

                                intent.putExtra(IntentExtraKey.REVIEW_IMAGE_SEQUENCE, imageSeq);
                                intent.putExtra(IntentExtraKey.IMAGE_MODE, mImageMode);
                                startActivity(intent);
                            }
                            else {
                                // go to camera preview for the selected image
                                mImageSequence = imageSeq;
                                previewNextImage();
                                setNavOverlayVisibility(View.INVISIBLE);
                                setZoomControlVisibility(View.VISIBLE);
                                listenForVoiceIfEnabled();
                            }
                        }
                        catch (final Exception e) {
                            logError(e);
                        }
                    }
                };
            }
        };
    }

    /**
     * Shows the thumbnail in the camera activity.
     * 
     * @param imageOrder
     */
    private void showThumbnail(int imageOrder) {
        final ThumbnailFragment thumbFrag = (ThumbnailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.camera_thumb_fragment);
        thumbFrag.displayThumbnail(mImageMode, imageOrder);
    }

    private void toggleCaptureButton(boolean enable) {
        try {
            mCaptureButton.setEnabled(enable);
            mCaptureButton.setImageDrawable(enable ? getResources().getDrawable(
                    R.drawable.camera_button) : getResources().getDrawable(
                            R.drawable.camera_button_pressed));
        }
        catch (final Exception e) {
            logError(e);
        }
    }

    private boolean isCaptureEnabled() {
        return mCaptureButton.isEnabled();
    }

    private void createShutterSound() {
        mShootSound = MediaPlayer.create(this, R.raw.camera_shutter_click);

        mShootSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    toggleMediaSounds(false);
                    listenForVoiceIfEnabled();
                }
                catch (final Exception e) {
                    logWarning(e);
                }
            }
        });
    }

    // playing custom shutter sound
    private void playShutterSound() {
        if (mShootSound != null) {
            releaseSpeechRecognizer();
            toggleMediaSounds(true);
            mShootSound.start();
        }
    }

    private void createNoFocusSound() {
        mCannotFocusSound = MediaPlayer.create(this, R.raw.cant_focus_sound);

        mCannotFocusSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    toggleMediaSounds(false);
                    listenForVoiceIfEnabled();
                }
                catch (final Exception e) {
                    logWarning(e);
                }
            }
        });
    }

    // playing custom 'auto focusing failed' sound
    private void playNoFocusSound() {
        if (mCannotFocusSound != null) {
            releaseSpeechRecognizer();
            toggleMediaSounds(true);
            mCannotFocusSound.start();
        }
    }

    /**
     * animation: transition of full size image to a thumbnail
     * 
     * @param idFrom - ID of the full size image view
     * @param idTo - ID of a thumbnail image view
     */
    public void imageToThumbnail(int idFrom, int idTo) {
        try {
            final AnimationSet animation = new AnimationSet(false);

            final View viewFrom = findViewById(idFrom);
            final View viewTo = findViewById(idTo);

            // calculate starting an ending bounds
            final Rect startBounds = new Rect();
            final Rect finalBounds = new Rect();
            final Point offset = new Point();

            viewFrom.getGlobalVisibleRect(startBounds);
            viewTo.getGlobalVisibleRect(finalBounds, offset);

            final float finalScale = (float) finalBounds.width() / startBounds.width();

            final TranslateAnimation transAnimation = new TranslateAnimation(startBounds.left,
                    finalBounds.right, startBounds.top, finalBounds.bottom);

            transAnimation.setDuration(ANIMATION_DURATION);

            final ScaleAnimation scaleAnimation = new ScaleAnimation(1, finalScale, 1, finalScale);
            scaleAnimation.setDuration(ANIMATION_DURATION);
            animation.addAnimation(scaleAnimation);
            animation.addAnimation(transAnimation);

            animation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationEnd(Animation animation) {
                    try {
                        viewFrom.clearAnimation();
                        viewFrom.setVisibility(View.INVISIBLE);
                    }
                    catch (final Exception e) {
                        logWarning(e);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {}

                @Override
                public void onAnimationStart(Animation animation) {
                    try {
                        // hide thumbnail of previous image
                        viewFrom.setVisibility(View.VISIBLE);
                    }
                    catch (final Exception e) {
                        logWarning(e);
                    }
                }
            });
            viewFrom.startAnimation(animation);
        }
        catch (final Exception e) {
            logError(e);
        }
    }

    private boolean isCamParamsDialogShowing() {
        return getSupportFragmentManager().findFragmentByTag(CAM_PARAMS_DIALOG_FRAGMENT_TAG) != null;
    }

    private void listenForVoiceIfEnabled() {
        if (new OnYardPreferences(this).getIsVoiceCommandEnabled() && !isImageNavOverlayShowing()) {
            listenForVoice();
        }
        else {
            releaseSpeechRecognizer();
        }
    }

    private void listenForVoice() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
        }

        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.iaai.onyard");
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());

            if (mRecognitionListener == null) {
                mRecognitionListener = new OnYardRecognitionListener(getEventBus());
            }

            mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
            muteSystemSounds();
            mSpeechRecognizer.startListening(intent);
        }
    }

    private void muteSystemSounds() {
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
    }

    private void enableSystemSounds() {
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
    }

    @Subscribe
    public void changeCameraParams(ChangeCameraParamsEvent event) {
        try {
            switch (event.getChange()) {
                case VOICE_COMMANDS_DISABLE:
                    onVoiceCommandsDisable();
                    break;
                case VOICE_COMMANDS_ENABLE:
                    onVoiceCommandsEnable();
                    break;
                default:
                    break;
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    private void onVoiceCommandsEnable() {
        try {
            listenForVoice();
            new OnYardPreferences(this).setIsVoiceCommandEnabled(true);
        }
        catch (final Exception e) {
            LogHelper.logWarning(getApplicationContext(), e, this.getClass().getSimpleName());
        }
    }

    private void onVoiceCommandsDisable() {
        releaseSpeechRecognizer();
        new OnYardPreferences(this).setIsVoiceCommandEnabled(false);
    }

    private void toggleMediaSounds(boolean soundOn) {
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        final int mVolumeLevel = soundOn ? audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC) : 0;

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeLevel,
                        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            showProgressDialog();

            // inflate image view for animation
            new LoadByteArrayImageTask(mFullImage, data, 640, 480).execute();

            final OnYardSessionData sessionData = getSessionData();
            sessionData.saveImage(mImageSequence, data, getApplicationContext(), getEventBus(),
                    mImageMode);
            setSessionData(sessionData);
        }
        catch (final Exception e) {
            showErrorDialog(IMAGE_SAVE_ERROR_MESSAGE);
            logError(e);
        }
    }

    @Override
    public void onShutter() {
        try {
            if (mShootSound == null) {
                createShutterSound();
            }
            playShutterSound();
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        try {
            mCaptureButton.removeCallbacks(mReFocusRunnable);
            if (mCaptureButtonPressed) {
                mCaptureButtonPressed = false;
                if (success) {
                    mCamera.takePicture(this, this);
                }
                else {
                    // play autofocus failure sound
                    if (mCannotFocusSound == null) {
                        createNoFocusSound();
                    }
                    playNoFocusSound();
                    toggleCaptureButton(true);

                    mCamera.enableFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    mFocusButton.setImageResource(mCamera.getFocusModeDrawableId(this));
                    mFocusButton.invalidate();

                    mCannotFocusToast.show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mCannotFocusToast != null) {
                                mCannotFocusToast.cancel();
                            }
                        }
                    }, 1000);
                }
            }
        }
        catch (final Exception e) {
            showErrorDialog(ErrorMessage.FOCUS_FAILED);
            logError(e);
            toggleCaptureButton(true);
        }
    }

    @Subscribe
    public void onVoiceRecognition(VoiceRecognitionEvent event) {
        try {
            switch (event.getResult()) {
                case SPEECH_RESULTS_FOUND:
                    onSpeechResults(event.getSpeechResults());
                    break;
                case READY_FOR_SPEECH:
                    onReadyForSpeech();
                    break;
                case SPEECH_ERROR:
                    onSpeechError(event.getErrorCode());
                    break;
                default:
                    break;
            }
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    private void onSpeechResults(Bundle results) {
        // do no keyword matching if cam params dialog is showing
        if (isCamParamsDialogShowing()) {
            listenForVoice();
            return;
        }

        // match keyword with action
        final ArrayList<String> voiceResults = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (voiceResults != null
                && voiceResults.contains(new OnYardPreferences(this).getCameraCaptureKeyword())) {
            mCaptureButton.performClick();
        }
        else {
            // re-enable listening
            listenForVoice();
        }

        LogHelper.logVerbose(voiceResults.toString());
    }

    private void onReadyForSpeech() {
        enableSystemSounds();
    }

    private void onSpeechError(int error) {
        listenForVoice();

        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                LogHelper.logDebug("Error: Audio Recording error");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                LogHelper.logDebug("Error: Other client side error");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                LogHelper.logDebug("Error: Insufficient permissions");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                LogHelper.logDebug("Error: Other network related error");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                LogHelper.logDebug("Error: Network operation timed out");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                LogHelper.logDebug("Error: No recognition result matched");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                // LogHelper.logDebug("Error: RecognitionService busy");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                LogHelper.logDebug("Error: Server sends error status");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                LogHelper.logDebug("Error: No speech input");
                break;
            default:
                break;
        }
    }

    private void startLevelLineListener() {
        mLevelViewGreen.setVisibility(View.VISIBLE);
        mLevelViewRed.setVisibility(View.VISIBLE);

        registerLevelLineListener();
    }

    private void stopLevelLineListener() {
        mLevelViewGreen.setVisibility(View.INVISIBLE);
        mLevelViewRed.setVisibility(View.INVISIBLE);

        unregisterLevelLineListener();
    }

    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     * Register sensor event listener for accelerometer
     * 
     * @param sensorManager
     * @param mEventListener
     */
    private void registerLevelLineListener() {
        if (mSensorEventListener == null) {
            mSensorEventListener = new SensorListener(mLevelViewGreen, mLevelViewRed, this);
        }
        final SensorManager sensorManager = getSensorManager();
        sensorManager.registerListener(mSensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterLevelLineListener() {
        final SensorManager sensorManager = getSensorManager();
        sensorManager.unregisterListener(mSensorEventListener);
    }

    private static class SensorListener implements SensorEventListener {
        private final WeakReference<LevelLineView> mGreen;
        private final WeakReference<LevelLineView> mRed;
        private final WeakReference<Context> mContext;

        public SensorListener(LevelLineView green, LevelLineView red, Context context) {
            mGreen = new WeakReference<LevelLineView>(green);
            mRed = new WeakReference<LevelLineView>(red);
            mContext = new WeakReference<Context>(context);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (mContext == null || mGreen == null || mRed == null || mContext.get() == null
                    || mGreen.get() == null || mRed.get() == null) {
                return;
            }
            final Context context = mContext.get();
            try {
                final LevelLineView green = mGreen.get();
                final LevelLineView red = mRed.get();
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
                        && (green.getVisibility() != View.INVISIBLE || red.getVisibility() != View.INVISIBLE)) {
                    final float aX = event.values[0];
                    final float aY = event.values[1];
                    final double angle = Math.atan2(aY, aX) / (Math.PI / 180);
                    green.rotate((float) angle);
                    red.rotate((float) angle);

                    if (Math.abs(angle) < LEVEL_LINE_THRESHOLD) {
                        green.setVisibility(View.VISIBLE);
                        red.setVisibility(View.INVISIBLE);
                    }
                    else {
                        green.setVisibility(View.INVISIBLE);
                        red.setVisibility(View.VISIBLE);
                    }
                }
            }
            catch (final Exception e) {
                LogHelper.logWarning(context.getApplicationContext(), e, this.getClass()
                        .getSimpleName());
            }
        }
    }
}
