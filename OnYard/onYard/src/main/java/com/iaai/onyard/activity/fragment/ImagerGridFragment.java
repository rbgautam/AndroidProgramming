package com.iaai.onyard.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.activity.CameraActivity;
import com.iaai.onyard.activity.ImageReviewActivity;
import com.iaai.onyard.adapter.ImagerThumbnailAdapter;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.application.OnYard.ImageMode;
import com.iaai.onyard.application.OnYard.IntentExtraKey;
import com.iaai.onyard.event.ThumbnailGridScrolledEvent;
import com.iaai.onyard.event.ThumbnailGridScrolledEvent.ScrollDirection;


public class ImagerGridFragment extends BaseFragment {

    @InjectView(R.id.imager_thumb_grid)
    GridView mImageThumbGrid;
    private int mLastVisiblePosition;

    private ImageMode mImageMode;
    public static ImagerGridFragment newInstance(ImageMode mode) {
        final ImagerGridFragment fragment = new ImagerGridFragment();
        final Bundle args = new Bundle();
        args.putSerializable(IntentExtraKey.IMAGE_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_imager_grid, container,
                    false);
            ButterKnife.inject(this, view);

            mImageMode = (ImageMode) getArguments().getSerializable(IntentExtraKey.IMAGE_MODE);
            initThumbnails();

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return container;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            mImageThumbGrid.invalidateViews();
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    private void initThumbnails() {
        if (getActivity() != null) {
            mImageThumbGrid.setAdapter(new ImagerThumbnailAdapter(getSessionData()
                    .getOnYardImageData(mImageMode)));

            mLastVisiblePosition = mImageThumbGrid.getFirstVisiblePosition();
            mImageThumbGrid.setOnItemClickListener(onThumbnailClick());

            mImageThumbGrid.setOnScrollListener(onGridScroll());
        }
    }

    private OnScrollListener onGridScroll() {
        return new OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {

                final int currentFirstVisPos = view.getFirstVisiblePosition();
                if (getActivity() != null && currentFirstVisPos > mLastVisiblePosition) {
                    getEventBus().post(new ThumbnailGridScrolledEvent(ScrollDirection.UP));
                }
                mLastVisiblePosition = currentFirstVisPos;
            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }

        };

    }

    /**
     * Opens up ImageReviewActivity when a thumbnail is pressed.
     * 
     * @return
     */
    private OnItemClickListener onThumbnailClick() {
        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                try {
                    final Activity activity = getActivity();
                    if (activity != null) {
                        final int imageSeq = getSessionData().getOnYardImageData(mImageMode)
                                .getAllImageSequences().get(position);

                        final Intent intent = getSessionData().getOnYardImageData(mImageMode)
                                .isImageTaken(imageSeq) ? new Intent(activity,
                                        ImageReviewActivity.class) : new Intent(activity,
                                                CameraActivity.class);

                                intent.putExtra(IntentExtraKey.REVIEW_IMAGE_SEQUENCE,
                                        (int) mImageThumbGrid.getItemIdAtPosition(position));
                                intent.putExtra(IntentExtraKey.IMAGE_MODE, mImageMode);
                                startActivity(intent);
                    }
                }
                catch (final Exception e) {
                    showErrorDialog(ErrorMessage.PAGE_LOAD);
                    logError(e);
                }
            }
        };
    }


}
