package com.iaai.onyard.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.iaai.onyard.utility.DataHelper;
import com.iaai.onyard.utility.LogHelper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Custom view used for finger-drawing on a photo background.
 */
public class DrawView extends View implements OnTouchListener {
	
    /**
     * The list of all points on the screen that the user has touched.
     */
    private List<Point> mPoints = new ArrayList<Point>();
    /**
     * Object containing information about the paint with which to perform
     * drawing.
     */
    private Paint mPaint = new Paint();
    /**
     * The path of photo to set as the background image when drawing.
     */
    private String mBackgroundImagePath;

    /**
     * Constructor. Creates view and initializes settings.
     * 
     * @param context The current context.
     */
    public DrawView(Context context)
    {
        super(context);

        initializeDrawView(context);
    }
    
    /**
     * Constructor. Creates view and initializes settings.
     * 
     * @param context The current context.
     * @param attrs The view attributes.
     */
    public DrawView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initializeDrawView(context);
    }
    
    /**
     * Perform initialization of the view - set photo background and prepare view
     * for drawing.
     * 
     * @param context The current context.
     */
    private void initializeDrawView(Context context)
    {
        Cursor imageCursor = null;
        try
        {
        	setFocusable(true);
            setFocusableInTouchMode(true);
            setDrawingCacheEnabled(true);
            
            mBackgroundImagePath = DataHelper.getImageStorageDir(this.getContext()).getPath();
            	
            File newestFile = DataHelper.getNewestFileInDirectory(mBackgroundImagePath);

            Bitmap bmp = BitmapFactory.decodeFile(newestFile.getPath());
            int photoWidth = bmp.getWidth();
            int photoHeight = bmp.getHeight();
            
            if(photoWidth > photoHeight)
            {
    	        Matrix matrix = new Matrix();
    	        matrix.postRotate(90);
    	        Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, 
    	        		photoWidth, photoHeight, matrix, false);
    	        setBackgroundDrawable(new BitmapDrawable(rotatedBMP));
            }
            else
            {
            	setBackgroundDrawable(new BitmapDrawable(bmp));
            }
            
            this.setOnTouchListener(this);
    
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(3);
        }
        finally
        {
        	if(imageCursor != null)
        		imageCursor.close();
        }
    }
    
    /**
     * Save the background photo with drawing included. Overwrites the photo in
     * its original location.
     * @throws FileNotFoundException 
     */
	public void savePhoto() throws FileNotFoundException
    {
		getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, 
				new FileOutputStream(DataHelper.getNewestFileInDirectory(mBackgroundImagePath)));
    }
    
    /**
     * Set the drawing color based on the specified color string.
     * 
     * @param color The string containing the name of the desired color.
     */
    public void setDrawColor(String color)
    {
	    	if (color.equals("Black"))
	    		mPaint.setColor(Color.BLACK);
	    	else if (color.equals("Blue"))
	    		mPaint.setColor(Color.BLUE);
	    	else if (color.equals("Cyan"))
	    		mPaint.setColor(Color.CYAN);
	    	else if (color.equals("Dark Gray"))
	    		mPaint.setColor(Color.DKGRAY);
	    	else if (color.equals("Gray"))
	    		mPaint.setColor(Color.GRAY);
	    	else if (color.equals("Green"))
	    		mPaint.setColor(Color.GREEN);
	    	else if (color.equals("Light Gray"))
	    		mPaint.setColor(Color.LTGRAY);
	    	else if (color.equals("Magenta"))
	    		mPaint.setColor(Color.MAGENTA);
	    	else if (color.equals("Red"))
	    		mPaint.setColor(Color.RED);
	    	else if (color.equals("White"))
	    		mPaint.setColor(Color.WHITE);
	    	else if (color.equals("Yellow"))
	    		mPaint.setColor(Color.YELLOW);
    }

    /**
     * Perform drawing by connecting the points that the user has marked through
     * touch.
     * 
     * @param canvas The canvas on which to draw.
     */
    @Override
    public void onDraw(Canvas canvas) 
    {
    	Point prevPoint = null;

    	for (Point point : mPoints) 
    	{
    		if(point.color != mPaint.getColor())
    			mPaint.setColor(point.color);

    		if(prevPoint != null && prevPoint.endTouch == false)
    			canvas.drawLine(prevPoint.x, prevPoint.y, point.x, point.y, mPaint);
    		else
    			canvas.drawCircle(point.x, point.y, 0.5f, mPaint);
    		prevPoint = point;
    	}
    }

    /**
     * Create a point describing the event and add it to the list of touch points.
     * 
     * Called when a touch event is dispatched to a view.
	 * 
	 * @param view The view the touch event has been dispatched to. 
	 * @param event The MotionEvent object containing full information about the event. 
	 * @return True if the listener has consumed the event, false otherwise. 
     */
    public boolean onTouch(View view, MotionEvent event) 
    {
        Point point = new Point();
        point.x = event.getX();
        point.y = event.getY();
        point.color = mPaint.getColor();
        
        if(event.getAction() == MotionEvent.ACTION_UP)
        	point.endTouch = true;
        else
        	point.endTouch = false;
        
        mPoints.add(point);
        invalidate();
        LogHelper.logVerbose("DrawView point: " + point);
        return true;
    }
}

/**
 * Class describing a point that has been touched.
 */
class Point 
{
	/**
	 * The x-coordinate of the point.
	 */
	float x;
	/**
	 * The y-coordinate of the point.
	 */
	float y;
	/**
	 * Flag indicating whether or not this was the point at which the user
	 * removed their finger from the screen. True if this is the case.
	 */
	boolean endTouch;
	/**
	 * The color with which to draw the point.
	 */
	int color;

	@Override
	public String toString() {
		return x + ", " + y;
	}
}
