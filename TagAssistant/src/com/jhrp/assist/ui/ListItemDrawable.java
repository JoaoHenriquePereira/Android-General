package com.jhrp.assist.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * The ListItemDrawable is the ui element that represents each tag color
 * 
 * @author Jo�o Pereira
 * @e-mail joaohrpereira@gmail.com
 */

public class ListItemDrawable extends View {
    private Paint paint = new Paint();

    public ListItemDrawable(Context context) {
        super(context);            
        //this.fillColor = i;
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        canvas.drawRect(30, 30, 80, 80, paint);
        paint.setStrokeWidth(0);
        paint.setColor(Color.RED);
        canvas.drawRect(33, 60, 77, 77, paint );
    }

}