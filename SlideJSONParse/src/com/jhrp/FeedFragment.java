package com.jhrp;

import java.util.ArrayList;

import com.jhrp.R;
import com.jhrp.feed.FeedManager;
import com.jhrp.feed.FeedObject;
import com.jhrp.image.ImageFetcher;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 *	JHP 08/08/2013
 *	This fragment holds all the information regarding the feed.
 */

public class FeedFragment extends Fragment {

  	public static final String TAG=FeedFragment.class.getSimpleName();

  	//Work stuff
  	//TODO put on separate UIless fragment
  	private static FeedManager feedManager;
  	public static FeedAdapter feedAdapter;
  	public static ArrayList<FeedObject> feedObjects;
    public static ImageFetcher mImageFetcher;
    
    //UI stuff
    public static ListView feedObjects_lv;
    public static ListView feedObjects_lv_left;
    public static ListView feedObjects_lv_right;
    
    //Other variables
    public static boolean isLoading=false;
    private int previousTotal=0;
    private static int threshold=4;
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setRetainInstance(true);
        
        feedObjects= new ArrayList<FeedObject>();

        feedManager = new FeedManager();
        feedManager.setOffset(0);
        feedManager.first=true;
        feedAdapter = new FeedAdapter(getActivity(),R.layout.feed_list_item,feedObjects);
        
        
//        feedManager.requestUpdate(FeedFragment.this, feedObjects.size());
//        isLoading=true;

        mImageFetcher = new ImageFetcher(getActivity());
        
    }
 
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	final View v = inflater.inflate(R.layout.feed_fragment, container, false);
        feedObjects_lv = (ListView) v.findViewById(R.id.feed_lv);
        feedObjects_lv.setAdapter(feedAdapter);

        feedObjects_lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) { TODO improve fling by disabling the load of images
//                    mImageFetcher.setPauseWork(true);
//                } else {
//                    mImageFetcher.setPauseWork(false);
//                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {

            	if (isLoading) {
					if (totalItemCount > previousTotal) {
                        isLoading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!isLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + threshold)){
                	isLoading=true;
                	feedManager.requestUpdate(FeedFragment.this, totalItemCount); // Call feed worker request
                	feedAdapter.notifyDataSetChanged();
                }
            }
        });

        return v;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }
    
    public void refresh(){
	    if(feedObjects.size()<10 || FeedManager.getOffset()==0){
        	feedManager.requestUpdate(this, feedObjects.size());
        	feedAdapter.notifyDataSetChanged();
        	isLoading=true;
        } else
	    	isLoading=false;
	    
	    if(feedManager.lastOffset==FeedManager.getOffset())
	    	feedManager.lastOffset-=10;
	    
	    
	}
    

}