package com.jhrp.ui;

import com.jhrp.ui.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 
 * SeekArc.java
 * 
 * This is a class that functions much like a SeekBar but
 * follows a circle path instead of a straight line.
 * 
 * @author Neil Davies
 * 
 */
public class SeekArc extends View {

	private static final String TAG = SeekArc.class.getSimpleName();
	private static int INVALID_PROGRESS_VALUE = -1;
	// The initial rotational offset -90 means we start at 12 o'clock
	private final int mAngleOffset = -90;

	private static final String fertileWindow = "Fertile Window";
	
	private static final String periodWindow = "The Period";
	
	private static SpannableString fertileSpan;
	
	private static SpannableString periodSpan;
	
	/**
	 * The Drawable for the seek arc thumbnail
	 */
	private Drawable mThumb;
	
	/**
	 * The Maximum value that this SeekArc can be set to
	 */
	private int mMax = 31;
	
	/**
	 * The Current value that the SeekArc is set to
	 */
	private int mProgress = 1;
		
	/**
	 * The Current value that the Period is set to
	 */
	private int mProgressPeriodStart = 1;
	
	/**
	 * The Current value that the Period is set to
	 */
	private int mProgressPeriodEnd = 1;
	
	/**
	 * The Current value that the Fertile is set to
	 */
	private int mProgressFertileStart = 1;
	
	/**
	 * The Current value that the Fertile is set to
	 */
	private int mProgressFertileEnd = 1;
	
	/**
	 * The width of the progress line for this SeekArc
	 */
	private int mProgressWidth = 30;
	
	/**
	 * The Width of the background arc for the SeekArc 
	 */
	private int mArcWidth = 10;
	
	/**
	 * The Angle to start drawing this Arc from
	 */
	private int mStartAngle = 0;
	
	/**
	 * The Angle through which to draw the arc (Max is 360)
	 */
	private int mSweepAngle = 360;
	
	/**
	 * The rotation of the SeekArc- 0 is twelve o'clock
	 */
	private int mRotation = 0;
	
	/**
	 * Give the SeekArc rounded edges
	 */
	private boolean mRoundedEdges = true;
	
	/**
	 * Enable touch inside the SeekArc
	 */
	private boolean mTouchInside = true;
	
	/**
	 * Will the progress increase clockwise or anti-clockwise
	 */
	private boolean mClockwise = true;
	
	/**
	 * Write tags inside
	 */
	private boolean mWriteIn = true;
	
	/**
	 * Highlight a certain progress value
	 */
	private int mMarkProgress = 0;

	// Internal variables
	private int mArcRadius = 0;
	private Paint mPaintText= new Paint();
	private float mProgressSweep = 0;
	private float mProgressPeriodSweepStart = 0;
	private float mProgressPeriodSweepEnd = 0;
	private float mProgressFertileSweepStart = 0;
	private float mProgressFertileSweepEnd = 0;
	private RectF mArcRect = new RectF();
	private Paint mArcPaint;
	private Paint mProgressPaint;
	private Paint mProgressFertilePaint;
	private Paint mProgressPeriodPaint;
	private int mTranslateX;
	private int mTranslateY;
	private int mThumbXPos;
	private int mThumbYPos;
	private double mTouchAngle;
	private float mTouchIgnoreRadius;
	private OnSeekArcChangeListener mOnSeekArcChangeListener;
	

	public interface OnSeekArcChangeListener {

		/**
		 * Notification that the progress level has changed. Clients can use the
		 * fromUser parameter to distinguish user-initiated changes from those
		 * that occurred programmatically.
		 * 
		 * @param seekArc
		 *            The SeekArc whose progress has changed
		 * @param progress
		 *            The current progress level. This will be in the range
		 *            0..max where max was set by
		 *            {@link ProgressArc#setMax(int)}. (The default value for
		 *            max is 100.)
		 * @param fromUser
		 *            True if the progress change was initiated by the user.
		 */
		void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser);

		/**
		 * Notification that the user has started a touch gesture. Clients may
		 * want to use this to disable advancing the seekbar.
		 * 
		 * @param seekArc
		 *            The SeekArc in which the touch gesture began
		 */
		void onStartTrackingTouch(SeekArc seekArc);

		/**
		 * Notification that the user has finished a touch gesture. Clients may
		 * want to use this to re-enable advancing the seekarc.
		 * 
		 * @param seekArc
		 *            The SeekArc in which the touch gesture began
		 */
		void onStopTrackingTouch(SeekArc seekArc);
	}

	public SeekArc(Context context) {
		super(context);
		init(context, null, 0);
	}

	public SeekArc(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, R.attr.seekArcStyle);
	}

	public SeekArc(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}
	
	

	private void init(Context context, AttributeSet attrs, int defStyle) {

		final Resources res = getResources();
		float density = context.getResources().getDisplayMetrics().density;

		// Defaults, may need to link this into theme settings
		int arcColor = res.getColor(R.color.progress_gray);
		int progressColor = res.getColor(android.R.color.holo_blue_light);
		int progressColorFertile = res.getColor(R.color.fertile_blue);
		int progressColorPeriod = res.getColor(R.color.period_red);
		int thumbHalfheight = 0;
		int thumbHalfWidth = 0;
		mThumb = res.getDrawable(R.drawable.seek_arc_control_selector);
		// Convert progress width to pixels for current density
		mProgressWidth = (int) (mProgressWidth * density);

		
		if (attrs != null) {
			// Attribute initialization
			final TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.SeekArc, defStyle, 0);

			Drawable thumb = a.getDrawable(R.styleable.SeekArc_thumb);
			if (thumb != null) {
				mThumb = thumb;
			}

			
			
			thumbHalfheight = (int) mThumb.getIntrinsicHeight() / 2;
			thumbHalfWidth = (int) mThumb.getIntrinsicWidth() / 2;
			mThumb.setBounds(-thumbHalfWidth, -thumbHalfheight, thumbHalfWidth,
					thumbHalfheight);

			mMax = a.getInteger(R.styleable.SeekArc_max, mMax);
			mProgress = a.getInteger(R.styleable.SeekArc_progress, mProgress);
			mProgressWidth = a.getInteger(
					R.styleable.SeekArc_progressWidth, mProgressWidth);
			mArcWidth = a.getInteger(R.styleable.SeekArc_arcWidth, mArcWidth);
			mStartAngle = a.getInt(R.styleable.SeekArc_startAngle, mStartAngle);
			mSweepAngle = a.getInt(R.styleable.SeekArc_sweepAngle, mSweepAngle);
			mRotation = a.getInt(R.styleable.SeekArc_rotation, mRotation);
			mRoundedEdges = a.getBoolean(R.styleable.SeekArc_roundEdges,
					mRoundedEdges);
			mTouchInside = a.getBoolean(R.styleable.SeekArc_touchInside,
					mTouchInside);
			mClockwise = a.getBoolean(R.styleable.SeekArc_clockwise,
					mClockwise);
			mWriteIn = a.getBoolean(R.styleable.SeekArc_writeIn,
					mWriteIn);
			
			mMarkProgress = a.getInteger(R.styleable.SeekArc_markProgress, mMarkProgress);
			
			
			mProgressFertileStart = a.getInt(R.styleable.SeekArc_progressFertileStart, mProgressFertileStart);
			mProgressFertileEnd = a.getInt(R.styleable.SeekArc_progressFertileEnd, mProgressFertileEnd);
			mProgressPeriodStart = a.getInt(R.styleable.SeekArc_progressPeriodStart, mProgressPeriodStart);
			mProgressPeriodEnd = a.getInt(R.styleable.SeekArc_progressPeriodEnd, mProgressPeriodEnd);
			
			mProgressFertileSweepStart = (float) mProgressFertileStart / mMax * mSweepAngle;
			mProgressFertileSweepEnd = (float) mProgressFertileEnd / mMax * mSweepAngle;
			mProgressPeriodSweepStart = (float) mProgressPeriodStart / mMax * mSweepAngle;
			mProgressPeriodSweepEnd = (float) mProgressPeriodEnd / mMax * mSweepAngle;//CLUE

			arcColor = a.getColor(R.styleable.SeekArc_arcColor, arcColor);
			progressColor = a.getColor(R.styleable.SeekArc_progressColor,
					progressColor);

			a.recycle();
		}
		
		mProgress--;
		mProgress = (mProgress > mMax) ? mMax : mProgress;
		mProgress = (mProgress < 0) ? 1 : mProgress;

		mSweepAngle = (mSweepAngle > 360) ? 360 : mSweepAngle;
		mSweepAngle = (mSweepAngle < 0) ? 0 : mSweepAngle;

		mStartAngle = (mStartAngle > 360) ? 0 : mStartAngle;
		mStartAngle = (mStartAngle < 0) ? 0 : mStartAngle;

		mArcPaint = new Paint();
		mArcPaint.setColor(arcColor);
		mArcPaint.setAntiAlias(true);
		mArcPaint.setStyle(Paint.Style.STROKE);
		mArcPaint.setStrokeWidth(mArcWidth);
		//mArcPaint.setAlpha(45);

		mProgressPaint = new Paint();
		mProgressPaint.setColor(progressColor);
		mProgressPaint.setAntiAlias(true);
		mProgressPaint.setStyle(Paint.Style.STROKE);
		mProgressPaint.setStrokeWidth(mProgressWidth);
		
		mProgressFertilePaint = new Paint();
		mProgressFertilePaint.setColor(progressColorFertile);
		mProgressFertilePaint.setAntiAlias(true);
		mProgressFertilePaint.setStyle(Paint.Style.STROKE);
		mProgressFertilePaint.setStrokeWidth(mProgressWidth);
		
		mProgressPeriodPaint = new Paint();
		mProgressPeriodPaint.setColor(progressColorPeriod);
		mProgressPeriodPaint.setAntiAlias(true);
		mProgressPeriodPaint.setStyle(Paint.Style.STROKE);
		mProgressPeriodPaint.setStrokeWidth(mProgressWidth);
		
		mPaintText= new Paint();
		mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
	    mPaintText.setStyle(Paint.Style.FILL_AND_STROKE);
	    mPaintText.setColor(getResources().getColor(android.R.color.white));
	    mPaintText.setTextSize(25f);
	    mPaintText.setTypeface(Typeface.DEFAULT_BOLD);
	    
	    //Spacing between letters inside
	    StringBuilder builder = new StringBuilder();
  	  	for(int i = 0; i < fertileWindow.length(); i++) {
          	builder.append(fertileWindow.charAt(i));
          	if(i+1 < fertileWindow.length()) {
        	  	builder.append("\u00A0");
         	}
      	}
      	
  	  	fertileSpan = new SpannableString(builder.toString());
      	if(builder.toString().length() > 1) {
          	for(int i = 1; i < builder.toString().length(); i+=2) {
          		fertileSpan.setSpan(new ScaleXSpan((1+1)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
          	}
      	}
      	
      	builder = new StringBuilder();
      	for(int i = 0; i < periodWindow.length(); i++) {
          	builder.append(periodWindow.charAt(i));
          	if(i+1 < periodWindow.length()) {
        	  	builder.append("\u00A0");
         	}
      	}
      	
      	periodSpan = new SpannableString(builder.toString());
      	if(builder.toString().length() > 1) {
          	for(int i = 1; i < builder.toString().length(); i+=2) {
          		periodSpan.setSpan(new ScaleXSpan((1+1)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
          	}
      	}

		if (mRoundedEdges) {
			mArcPaint.setStrokeCap(Paint.Cap.ROUND);
			mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
			
			mProgressFertilePaint.setStrokeCap(Paint.Cap.ROUND);
			mProgressPeriodPaint.setStrokeCap(Paint.Cap.ROUND);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {		
		if(!mClockwise) {
			canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY() );
		}

		// Draw the arcs
		final int arcStart = mStartAngle + mAngleOffset + mRotation;
		final int arcSweep = mSweepAngle;
		
		//Draw triangle
		Path path = new Path();
		path.moveTo(0, -1);//Aponta
		path.lineTo(1, 0);
		path.lineTo(-1, 0);
		path.close();
		
		float x_arrow = (float)(mArcRect.centerX());
		float y_arrow = (float)(mArcRect.top);
		
		Matrix mMatrix = new Matrix();
		RectF bounds = new RectF();
		path.computeBounds(bounds, true);
		mMatrix.postRotate(90, 
		                   (bounds.right + bounds.left)/2, 
		                   (bounds.bottom + bounds.top)/2);
		path.transform(mMatrix);
		path.offset(x_arrow, y_arrow);
		canvas.drawPath(path, mArcPaint);
		
		canvas.drawArc(mArcRect, arcStart, arcSweep, false, mArcPaint);
		
		canvas.drawArc(mArcRect, arcStart, mProgressSweep, false,
				mProgressPaint);
		
		canvas.drawArc(mArcRect,mProgressFertileSweepStart + 270, mProgressFertileSweepEnd-mProgressFertileSweepStart, false,
					mProgressFertilePaint);
	    
		canvas.drawArc(mArcRect, mProgressPeriodSweepStart + 270, mProgressPeriodSweepEnd-mProgressPeriodSweepStart, false,
					mProgressPeriodPaint);

			
		//Highlight progress
		if(mMarkProgress > -1){
			float a = (float) (mMarkProgress+1) / mMax * mSweepAngle + 270;
			float r = mArcRect.top-mArcRect.centerY();
			float x = (float) (r*Math.cos(a));
			float y = (float) (r*Math.sin(a));
			//Log.e("-","r "+r+ " a "+a+ " bot "+mArcRect.bottom+ " y "+y);
			canvas.drawCircle( x+mTranslateX , y+mTranslateY , 22 , mArcPaint );
		}
		
		if(mWriteIn){
			//Draw inner text
			Path mTextPath = new Path();
			
			if(mProgressFertileSweepStart < 90)
				mTextPath.addArc(mArcRect, mProgressFertileSweepStart + 270, mProgressFertileSweepEnd-mProgressFertileSweepStart);
			else
				mTextPath.addArc(mArcRect, mProgressFertileSweepEnd + 270, mProgressFertileSweepStart-mProgressFertileSweepEnd);
			
			canvas.drawTextOnPath(String.valueOf(fertileSpan), mTextPath, 0, 8, mPaintText);
			mTextPath.reset();
			
			if(mProgressPeriodSweepStart < 90)
				mTextPath.addArc(mArcRect, mProgressPeriodSweepStart + 270, mProgressPeriodSweepEnd-mProgressPeriodSweepStart);
			else
				mTextPath.addArc(mArcRect, mProgressPeriodSweepEnd+ 270, mProgressPeriodSweepStart-mProgressPeriodSweepEnd);
			
			canvas.drawTextOnPath(String.valueOf(periodSpan), mTextPath, 0, 8, mPaintText);
	    }

		// Draw the thumb nail
		canvas.translate(mTranslateX -mThumbXPos, mTranslateY -mThumbYPos);
		mThumb.draw(canvas);		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		final int height = getDefaultSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		final int width = getDefaultSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int min = Math.min(width, height);
		float top = 0;
		float left = 0;
		int arcDiameter = 0;

		mTranslateX = (int) (width * 0.5f);
		mTranslateY = (int) (height * 0.5f);
		
		arcDiameter = min - getPaddingLeft();
		mArcRadius = arcDiameter / 2;
		top = height / 2 - (arcDiameter / 2);
		left = width / 2 - (arcDiameter / 2);
		mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);
	
		int arcStart = (int)mProgressSweep + mStartAngle  + mRotation + 90;
		mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(arcStart)));
		mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(arcStart)));
		
		setTouchInSide(mTouchInside);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onStartTrackingTouch();
			updateOnTouch(event);
			break;
		case MotionEvent.ACTION_MOVE:
			updateOnTouch(event);
			break;
		case MotionEvent.ACTION_UP:
			onStopTrackingTouch();
			setPressed(false);
			break;
		case MotionEvent.ACTION_CANCEL:
			onStopTrackingTouch();
			setPressed(false);

			break;
		}

		return true;
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		if (mThumb != null && mThumb.isStateful()) {
			int[] state = getDrawableState();
			mThumb.setState(state);
		}
		invalidate();
	}

	private void onStartTrackingTouch() {
		if (mOnSeekArcChangeListener != null) {
			mOnSeekArcChangeListener.onStartTrackingTouch(this);
		}
	}

	private void onStopTrackingTouch() {
		if (mOnSeekArcChangeListener != null) {
			mOnSeekArcChangeListener.onStopTrackingTouch(this);
		}
	}

	private void updateOnTouch(MotionEvent event) {
		boolean ignoreTouch = ignoreTouch(event.getX(), event.getY());
		if (ignoreTouch) {
			return;
		}
		setPressed(true);
		mTouchAngle = getTouchDegrees(event.getX(), event.getY());
		int progress = getProgressForAngle(mTouchAngle);
		onProgressRefresh(progress, true);
	}

	private boolean ignoreTouch(float xPos, float yPos) {
		boolean ignore = false;
		float x = xPos - mTranslateX;
		float y = yPos - mTranslateY;

		float touchRadius = (float) Math.sqrt(((x * x) + (y * y)));
		if (touchRadius < mTouchIgnoreRadius) {
			ignore = true;
		}
		return ignore;
	}

	private double getTouchDegrees(float xPos, float yPos) {
		float x = xPos - mTranslateX;
		float y = yPos - mTranslateY;
		//invert the x-coord if we are rotating anti-clockwise
		x= (mClockwise) ? x:-x;
		// convert to arc Angle
		double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI / 2)
				- Math.toRadians(mRotation));
		if (angle < 0) {
			angle = 360 + angle;
		}
		angle -= mStartAngle;
		return angle;
	}

	private int getProgressForAngle(double angle) {
		int touchProgress = (int) Math.round(valuePerDegree() * angle);

		touchProgress = (touchProgress < 0) ? INVALID_PROGRESS_VALUE
				: touchProgress;
		touchProgress = (touchProgress > mMax) ? INVALID_PROGRESS_VALUE
				: touchProgress;
		return touchProgress;
	}

	private float valuePerDegree() {
		return (float) mMax / mSweepAngle;
	}

	private void onProgressRefresh(int progress, boolean fromUser) {
		updateProgress(progress, fromUser);
	}

	private void updateThumbPosition() {
		int thumbAngle = (int) (mStartAngle + mProgressSweep + mRotation + 90);
		mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
		mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
	}
	
	private void updateFertilePeriodPosition() {//CLUE
		mProgressFertileSweepStart = (float) (mProgressFertileStart+1) / mMax * mSweepAngle;
		mProgressFertileSweepEnd = (float)  (mProgressFertileEnd+1) / mMax * mSweepAngle;
		mProgressPeriodSweepStart = (float) (mProgressPeriodStart+1) / mMax * mSweepAngle;
		mProgressPeriodSweepEnd = (float) (mProgressPeriodEnd+1) / mMax * mSweepAngle;
	}
	
	
	private void updateProgress(int progress, boolean fromUser) {

		if (progress == INVALID_PROGRESS_VALUE) {
			return;
		}

		if (mOnSeekArcChangeListener != null) {
			mOnSeekArcChangeListener
					.onProgressChanged(this, progress, fromUser);
		}
		
		progress = (progress > mMax) ? mMax : progress;
		progress = (mProgress < 0) ? 1 : progress;

		
		mProgress = progress;
		mProgressSweep = (float) progress / mMax * mSweepAngle;

		updateThumbPosition();

		invalidate();
	}
	
	public void updateFertilePeriodProgress() {
		updateFertilePeriodPosition();
		invalidate();
	}

	/**
	 * Sets a listener to receive notifications of changes to the SeekArc's
	 * progress level. Also provides notifications of when the user starts and
	 * stops a touch gesture within the SeekArc.
	 * 
	 * @param l
	 *            The seek bar notification listener
	 * 
	 * @see SeekArc.OnSeekBarChangeListener
	 */
	public void setOnSeekArcChangeListener(OnSeekArcChangeListener l) {
		mOnSeekArcChangeListener = l;
	}

	public void setProgress(int progress) {
		updateProgress(progress, false);
	}

	public int getProgressWidth() {
		return mProgressWidth;
	}

	public void setProgressWidth(int mProgressWidth) {
		this.mProgressWidth = mProgressWidth;
		mProgressPaint.setStrokeWidth(mProgressWidth);
		mProgressPeriodPaint.setStrokeWidth(mProgressWidth);
		mProgressFertilePaint.setStrokeWidth(mProgressWidth);
	}
	
	public int getArcWidth() {
		return mArcWidth;
	}

	public void setArcWidth(int mArcWidth) {
		this.mArcWidth = mArcWidth;
		mArcPaint.setStrokeWidth(mArcWidth);
	}
	public int getArcRotation() {
		return mRotation;
	}

	public void setArcRotation(int mRotation) {
		this.mRotation = mRotation;
		updateThumbPosition();
	}

	public int getStartAngle() {
		return mStartAngle;
	}

	public void setStartAngle(int mStartAngle) {
		this.mStartAngle = mStartAngle;
		updateThumbPosition();
	}

	public int getSweepAngle() {
		return mSweepAngle;
	}

	public void setSweepAngle(int mSweepAngle) {
		this.mSweepAngle = mSweepAngle;
		updateThumbPosition();
	}
	
	public void setRoundedEdges(boolean isEnabled) {
		mRoundedEdges = isEnabled;
		if (mRoundedEdges) {
			mArcPaint.setStrokeCap(Paint.Cap.ROUND);
			mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
			mProgressFertilePaint.setStrokeCap(Paint.Cap.ROUND);
			mProgressPeriodPaint.setStrokeCap(Paint.Cap.ROUND);
		} else {
			mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
			mProgressPaint.setStrokeCap(Paint.Cap.SQUARE);
			mProgressFertilePaint.setStrokeCap(Paint.Cap.SQUARE);
			mProgressPeriodPaint.setStrokeCap(Paint.Cap.SQUARE);
		}
	}
	
	public void setTouchInSide(boolean isEnabled) {
		int thumbHalfheight = (int) mThumb.getIntrinsicHeight() / 2;
		int thumbHalfWidth = (int) mThumb.getIntrinsicWidth() / 2;
		mTouchInside = isEnabled;
		if (mTouchInside) {
			mTouchIgnoreRadius = (float) mArcRadius / 4;
		} else {
			// Don't use the exact radius makes interaction too tricky
			mTouchIgnoreRadius = mArcRadius
					- Math.min(thumbHalfWidth, thumbHalfheight);
		}
	}
	
	public void setClockwise(boolean isClockwise) {
		mClockwise = isClockwise;
	}
	
	public void setWriteIn(boolean isWriteIn) {
		mWriteIn = isWriteIn;
	}
	
	public void setMarkProgress(int mMarkProgress) {
		this.mMarkProgress = mMarkProgress;
		invalidate();
	}

	public int getProgressPeriodStart() {
		return mProgressPeriodStart;
	}

	public void setProgressPeriodStart(int mProgressPeriodStart) {
		this.mProgressPeriodStart = mProgressPeriodStart;
	}

	public int getProgressPeriodEnd() {
		return mProgressPeriodEnd;
	}

	public void setProgressPeriodEnd(int progressPeriodEnd) {
		this.mProgressPeriodEnd = progressPeriodEnd;
	}

	public int getProgressFertileStart() {
		return mProgressFertileStart;
	}

	public void setProgressFertileStart(int progressFertileStart) {
		this.mProgressFertileStart = progressFertileStart;
	}

	public int getProgressFertileEnd() {
		return mProgressFertileEnd;
	}

	public void setProgressFertileEnd(int progressFertileEnd) {
		this.mProgressFertileEnd = progressFertileEnd;
	}
}
