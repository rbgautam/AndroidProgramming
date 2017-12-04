package com.iaai.onyard.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

import com.iaai.onyard.utility.LogHelper;

public class LevelLineView extends ImageView {

    private Paint mPaint;
    private final int mThickness = 8;
    private int mStartX;
    private int mStartY;
    private int mStopX;
    private int mStopY;

    public LevelLineView(Context context) {
        super(context);
    }

    public LevelLineView(Context context, int startX, int startY, int stopX, int stopY, int color) {
        super(context);
        try {
            mPaint = new Paint();

            mPaint.setColor(color);
            mPaint.setStrokeWidth(mThickness);

            mStartX = startX;
            mStartY = startY;
            mStopX = stopX;
            mStopY = stopY;
        }
        catch (final Exception e) {
            LogHelper.logWarning(getContext(), e, this.getClass().getSimpleName());
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            canvas.drawLine(mStartX, mStartY, mStopX, mStopY, mPaint);
        }
        catch (final Exception e) {
            LogHelper.logWarning(getContext(), e, this.getClass().getSimpleName());
        }
    }

    public void rotate(float angle) {
        try {
            // first turn back to original middle line,
            // then rotate to direction opposite to device rotation to keep the line horizontal
            setRotation(-getRotation());
            setRotation(-angle);
        }
        catch (final Exception e) {
            LogHelper.logWarning(getContext(), e, this.getClass().getSimpleName());
        }
    }
}
