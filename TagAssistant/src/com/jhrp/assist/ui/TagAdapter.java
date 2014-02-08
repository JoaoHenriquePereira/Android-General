package com.jhrp.assist.ui;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Scalar;

import com.jhrp.assist.R;
import com.jhrp.assist.db.TagGroupModel;
import com.jhrp.assist.utils.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This class maps the information on the listview.
 * 
 * @author João Pereira
 * @e-mail joaohrpereira@gmail.com
 */


public class TagAdapter extends ArrayAdapter<TagGroupModel> {
	private static final String TAG="TagAdapter";
	
	private ArrayList<TagGroupModel> objects;
    private ImageView tagColor;
    private TextView tagName;
    
	public TagAdapter(Context context, int textViewResourceId,
			List<TagGroupModel> objects) {
		super(context, textViewResourceId, objects);
		this.objects=(ArrayList<TagGroupModel>) objects;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){

		View v = convertView;

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.tag_listitem_view, null);
		}

		if(objects.size()>0){
			final TagGroupModel i = objects.get(position);
		
			if (i != null) {
				tagName = (TextView) v.findViewById(R.id.tagName);
				tagColor = (ImageView) v.findViewById(R.id.tagColor);
			}
			Paint t = new Paint();
			Scalar x = Utils.convertStringToScalar(i.getRgba());
			t.setARGB((int)x.val[3], (int)x.val[0], (int)x.val[1], (int)x.val[2]);
			
			tagColor.setBackgroundColor(t.getColor());
			tagName.setText(i.getTagName());
		}

		return v;

	}

	public static float convertPixelsToDp(float px, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / (metrics.densityDpi / 160f);
	    return dp;
	}
	
    public static float convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi/160f);
	    return px;
	}

}