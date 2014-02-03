package com.jhrp.assist.ui;

import java.util.ArrayList;
import java.util.List;

import com.jhrp.assist.R;
import com.jhrp.assist.R.id;
import com.jhrp.assist.R.layout;
import com.jhrp.assist.db.TagGroupModel;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 *	JHP 08/08/2013
 *	This class maps the information on the listview.
 *	TODO make expandable boxes for comment
 */

public class TagAdapter extends ArrayAdapter<TagGroupModel>
{
	private static final String TAG="FeedAdapter";
	private ArrayList<TagGroupModel> objects;
	private Context context;
	private long id;
	//private String tagName;
	//private byte[] tagColor;
	
    private ImageView tagColor;
    private TextView tagName;
    
	public TagAdapter(Context context, int textViewResourceId,
			List<TagGroupModel> objects) {
		super(context, textViewResourceId, objects);
		this.objects=(ArrayList<TagGroupModel>) objects;
		this.context=context;
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

	       // FeedFragment.mImageFetcher.DisplayImage(i.show.images.poster.w120, obj_poster);
	       // FeedFragment.mImageFetcher.DisplayImage(i.user.avatar.w80, obj_avatar);
	       // obj_type.getLayoutParams().height = obj_avatar.getLayoutParams().height;//(int) convertPixelsToDp(obj_avatar.getLayoutParams().height,(Activity)context);
			//obj_type.getLayoutParams().width = obj_avatar.getLayoutParams().width;//(int) convertPixelsToDp(obj_avatar.getLayoutParams().width,(Activity)context);
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