package com.jhrp;

import java.util.ArrayList;
import java.util.List;
import com.jhrp.R;
import com.jhrp.feed.FeedObject;

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

public class FeedAdapter extends ArrayAdapter<FeedObject>
{
	private static final String TAG = FeedAdapter.class.getSimpleName();
	private ArrayList<FeedObject> objects;
	private Context context;
	float imageWidth;
    private ImageView obj_poster;
    private ImageView obj_avatar;
    private TextView obj_title;
    private ImageView obj_type;
    private TextView obj_comment;
    
    //Strings
    private static final String favorite_1=" has added \"";
    private static final String favorite_2="\" as a favorite.";
    private static final String rating_1=" has rated \"";
    private static final String tunedin_1=" has connected to \"";
    private static final String comment_1=" commented on \"";
    private static final String full_stop="\".";
    private static final String quote="\"";
    
	public FeedAdapter(Context context, int textViewResourceId,
			List<FeedObject> objects) {
		super(context, textViewResourceId, objects);
		this.objects=(ArrayList<FeedObject>) objects;
		this.context=context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){

		View v = convertView;

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.feed_list_item, null);
		}

		if(objects.size()>0){
			final FeedObject i = objects.get(position);
		
			if (i != null) {
	//TODO
				
				obj_poster = (ImageView) v.findViewById(R.id.feedobj_poster);
				obj_title = (TextView) v.findViewById(R.id.feedobj_title);
				obj_type = (ImageView) v.findViewById(R.id.feedobj_type);
				obj_avatar = (ImageView) v.findViewById(R.id.feedobj_avatar);
//				obj_comment = (TextView) v.findViewById(R.id.feedobj_comment);
				obj_poster.setOnClickListener(new View.OnClickListener() {
	
			        @Override
			        public void onClick(View v) {
			        	Toast.makeText(context, "Poster clicked", Toast.LENGTH_SHORT).show();
			        }
			    });
				
				
			    

				if(i.type.equals("favorite")){
					obj_type.setImageResource(R.drawable.favorite);
					StringBuilder strBuild= new StringBuilder();
					strBuild.append(i.user.username);
					strBuild.append(favorite_1);
					strBuild.append(i.show.title);
					strBuild.append(favorite_2);
					obj_title.setText(strBuild.toString());
				}
				else if(i.type.equals("rating")){
					switch(i.rating){
						case 1: obj_type.setImageResource(R.drawable.star1);
								break;
						case 2: obj_type.setImageResource(R.drawable.star2);
								break;
						case 3: obj_type.setImageResource(R.drawable.star3);
								break;
						case 4: obj_type.setImageResource(R.drawable.star4);
								break;
						case 5: obj_type.setImageResource(R.drawable.star5);
								break;
						default: obj_type.setImageResource(R.drawable.star);
								break;
					}
					StringBuilder strBuild= new StringBuilder();
					strBuild.append(i.user.username);
					strBuild.append(rating_1);
					strBuild.append(i.show.title);
					strBuild.append(full_stop);
					obj_title.setText(strBuild.toString());
				}
				else if(i.type.equals("tunedin")){
					obj_type.setImageResource(R.drawable.wheel);
					StringBuilder strBuild= new StringBuilder();
					strBuild.append(i.user.username);
					strBuild.append(tunedin_1);
					strBuild.append(i.show.title);
					strBuild.append(full_stop);
					obj_title.setText(strBuild.toString());
				}
				else { //It's a comment
					obj_type.setImageResource(R.drawable.comment);
					StringBuilder strBuild= new StringBuilder();
					strBuild.append(i.user.username);
					strBuild.append(comment_1);
					strBuild.append(i.show.title);
					strBuild.append(full_stop);
					obj_title.setText(strBuild.toString());
//					
//					strBuild= new StringBuilder();
//					strBuild.append(quote);
//					strBuild.append(i.comment);
//					strBuild.append(full_stop);
//					
//					obj_comment.setText(strBuild.toString());
					v.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							Toast.makeText(context, "Summon the comment!", Toast.LENGTH_SHORT).show();
						}
					});
				}
				
				
			}

			
	        FeedFragment.mImageFetcher.DisplayImage(i.show.images.poster.w120, obj_poster);
	        FeedFragment.mImageFetcher.DisplayImage(i.user.avatar.w80, obj_avatar);
	        obj_type.getLayoutParams().height = obj_avatar.getLayoutParams().height;//(int) convertPixelsToDp(obj_avatar.getLayoutParams().height,(Activity)context);
			obj_type.getLayoutParams().width = obj_avatar.getLayoutParams().width;//(int) convertPixelsToDp(obj_avatar.getLayoutParams().width,(Activity)context);
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