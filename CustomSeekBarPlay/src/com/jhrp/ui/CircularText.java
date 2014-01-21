package com.jhrp.ui;

import com.jhrp.ui.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class CircularText extends View {
    
	private SpannableString finalText;
	private Path mArc;
	private Paint mPaintText;

    public CircularText(Context context, AttributeSet attrs) {
  		super(context, attrs);
  		init(context, attrs);
  	}
  	
  	private void init(Context context, AttributeSet attrs) {

  		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
  		Display display = wm.getDefaultDisplay();
  		Point size;
  		int width=0;
  		
  		final String text= getResources().getString(R.string.calendar_title);
  		
  		if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
  			width = display.getWidth();
  		}else{
  			size = new Point();
  			display.getSize(size);
  			width = size.x;
  		}
  		
  	    mArc = new Path();
  	    RectF oval = new RectF(0,10,width-50,550);;
  	    mArc.addArc(oval, -138, 310);          
  	    mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
  	    mPaintText.setStyle(Paint.Style.FILL_AND_STROKE);
  	    mPaintText.setColor(getResources().getColor(R.color.grey_seekbar));
  	    mPaintText.setTextSize(50f);
  	      
	  	StringBuilder builder = new StringBuilder();
	  	for(int i = 0; i < text.length(); i++) {
	      	builder.append(text.charAt(i));
	      	if(i+1 < text.length()) {
	      		builder.append("\u00A0");
	        }
	    }
	    finalText = new SpannableString(builder.toString());
	    if(builder.toString().length() > 1) {
	    	for(int i = 1; i < builder.toString().length(); i+=2) {
	    		finalText.setSpan(new ScaleXSpan((1+1)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	        }
	    }
  	}

  	@Override
  	protected void onDraw(Canvas canvas) {
  		canvas.drawTextOnPath(String.valueOf(finalText), mArc, 0, 20, mPaintText);      
  	    invalidate();
  	}
}

