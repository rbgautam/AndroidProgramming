package com.iaai.onyard.activity.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.iaai.onyard.R;
import com.iaai.onyard.application.OnYard.ErrorMessage;
import com.iaai.onyard.event.VehicleDetailsUpdatedEvent;
import com.iaai.onyardproviderapi.classes.VehicleInfo;
import com.squareup.otto.Subscribe;

public class VehicleDetailsFragment extends BaseFragment {

    @InjectView(R.id.txt_vin)
    TextView mTxtVin;
    @InjectView(R.id.txt_year_make_model)
    TextView mTxtYmm;
    @InjectView(R.id.txt_color)
    TextView mTxtColor;
    @InjectView(R.id.txt_provider)
    TextView mTxtProvider;
    @InjectView(R.id.txt_location)
    TextView mTxtLocation;
    @InjectView(R.id.txt_damage)
    TextView mTxtDamage;
    @InjectView(R.id.txt_status)
    TextView mTxtStatus;
    @InjectView(R.id.txt_sale_doc)
    TextView mTxtSaleDoc;
    @InjectView(R.id.txt_run_drive)
    TextView mTxtRunAndDrive;
    @InjectView(R.id.txt_auction_date)
    TextView mTxtAuctionDate;
    @InjectView(R.id.collapse_button)
    ImageView mBtnCollapse;
    @InjectView(R.id.collapsible_details)
    View mCollapsibleDetails;
    @InjectView(R.id.layout_vehicle_details)
    View mDetailsView;

    private boolean mIsCollapsing;

    private static final String RUN_AND_DRIVE_TEXT = "R&D";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            final View view = inflater.inflate(R.layout.fragment_vehicle_details, container, false);
            ButterKnife.inject(this, view);

            initializeUi();

            return view;
        }
        catch (final Exception e) {
            showFatalErrorDialog(ErrorMessage.PAGE_LOAD);
            logError(e);
            return container;
        }
    }

    private void initializeUi() {
        if (getActivity() != null) {
            final VehicleInfo vehicle = getSessionData().getVehicleInfo();

            if (vehicle.getVIN() != null && !vehicle.getVIN().equals("")) {
                mTxtVin.setText(vehicle.getVIN());
            }
            if (vehicle.getColorDescription() != null && !vehicle.getColorDescription().equals("")) {
                mTxtColor.setText(vehicle.getColorDescription() + " ");
            }
            if (vehicle.isRunAndDrive()) {
                mTxtRunAndDrive.setText(RUN_AND_DRIVE_TEXT);
            }
            mTxtYmm.setText(vehicle.getYearMakeModel());
            mTxtYmm.setVisibility(View.VISIBLE);
            mTxtProvider.setText(vehicle.getSalvageProviderName());
            mTxtDamage.setText(vehicle.getDamage());
            if (vehicle.getSaleDocTypeDescription() != null
                    && !vehicle.getSaleDocTypeDescription().equals("")) {
                mTxtSaleDoc.setText(vehicle.getSaleDocTypeDescription());
            }
            if (vehicle.getAisle() != null && !vehicle.getAisle().equals("")) {
                mTxtLocation.setText(vehicle.getAisle() + " - " + vehicle.getStall());
            }
            mTxtStatus.setText(vehicle.getStatusDescription());
            if (vehicle.getAuctionDate() != 0) {
                mTxtAuctionDate.setText(vehicle.getAuctionDateString());
            }

            mIsCollapsing = false;
            mDetailsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCollapsibleDetails.getVisibility() == View.GONE) {
                        expandDetails();
                    }
                    else {
                        collapseDetails();
                    }
                }
            });
        }
    }

    @Subscribe
    public void onVehicleDetailsUpdate(VehicleDetailsUpdatedEvent event) {
        try {
            initializeUi();
        }
        catch (final Exception e) {
            logWarning(e);
        }
    }

    public void collapseDetails() {
        if (!mIsCollapsing) {
            mIsCollapsing = true;
            final int finalHeight = mCollapsibleDetails.getHeight();
            final ValueAnimator animator = slideAnimator(finalHeight, 0);

            animator.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsCollapsing = false;
                    mCollapsibleDetails.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationStart(Animator animation) {
                }
            });

            animator.start();

            mBtnCollapse.setImageResource(R.drawable.ic_action_expand);
        }
    }

    public void expandDetails() {
        mCollapsibleDetails.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        mCollapsibleDetails.measure(widthSpec, heightSpec);
        final ValueAnimator animator = slideAnimator(0, mCollapsibleDetails.getMeasuredHeight());
        animator.start();

        mBtnCollapse.setImageResource(R.drawable.ic_action_collapse);
    }

    private ValueAnimator slideAnimator(int start, int end) {
        final ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // Update Height
                final int value = (Integer) valueAnimator.getAnimatedValue();
                final ViewGroup.LayoutParams layoutParams = mCollapsibleDetails.getLayoutParams();
                layoutParams.height = value;
                mCollapsibleDetails.setLayoutParams(layoutParams);
            }
        });

        return animator;
    }
}
