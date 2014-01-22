package com.jhrp;

import java.util.ArrayList;
import com.jhrp.R;
import com.jhrp.HorizontalScroll.SizeCallback;
import com.jhrp.feed.FeedManager;
import com.jhrp.receiver.WifiReceiver;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 *	JHP 08/08/2013
 *	Main Activity which manages the fragments and synchronizes with the Wifi Receiver.
 */

public class MainActivity extends FragmentActivity {
	
	private static final String TAG = MainActivity.class.getSimpleName();

	//UI stuff
	private HorizontalScroll slideView;
	private View menuView;
	private View appView;
  	private static ListView otherObjects_lv;
  	private static DisplayMetrics metrics;
  	private static ImageView menuViewButton;
  	private static RelativeLayout.LayoutParams listViewParameters;
  	private static FeedFragment feedFragment;
  	private static NoDataFragment nodataFragment;
  	private static MyPageFragment mypageFragment;
  	private static final double slidePercent=0.3;
  	private static FragmentManager fragManager;
  	public boolean isVisible;
  	private static String currentFragment=MyPageFragment.TAG;
  	
  	//Others
  	private static ArrayList<String> options;
  	private WifiReceiver wifiReceiver = null;
  	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isVisible=false;
        
        fragManager = getSupportFragmentManager();
        
        options=new ArrayList<String>();
		options.add("Feed");
		options.add("Me");

		LayoutInflater inflater = LayoutInflater.from(this);
	    slideView = (HorizontalScroll) inflater.inflate(R.layout.slide_view, null);
	    setContentView(slideView);

	    menuView = inflater.inflate(R.layout.slide_menu, null);
	    appView = inflater.inflate(R.layout.activity_main, null);
	    ViewGroup tabBar = (ViewGroup) appView.findViewById(R.id.slidingPanel);

	    otherObjects_lv = (ListView) menuView.findViewById(R.id.otherobjects_lv);
	    menuViewButton = (ImageView) tabBar.findViewById(R.id.menuViewButton);
			
	    menuViewButton.setOnClickListener(new ClickListenerForSliding(slideView, menuView));

	    final View[] children = new View[] { menuView, appView };
	    metrics = new DisplayMetrics();
	  	getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    int scrollToViewIdx = 1;
	    slideView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu((int) ((metrics.widthPixels)*(1-slidePercent))));

		//Initialize
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		otherObjects_lv = (ListView) findViewById(R.id.otherobjects_lv);
		listViewParameters = (RelativeLayout.LayoutParams) otherObjects_lv.getLayoutParams();
		listViewParameters.width = metrics.widthPixels;
		otherObjects_lv.setLayoutParams(listViewParameters);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.slide_list_item, options);
		otherObjects_lv.setAdapter(adapter);
		otherObjects_lv.setOnItemClickListener(new OnItemClickListener(){

			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				if(arg2==0){//Replace Feed Activity
					isVisible=true;
					loadFeed();
				} else{
					isVisible=false;
					if(mypageFragment!=null){
						if(!mypageFragment.isAdded()){
							replaceFragment(R.id.contentPanel, mypageFragment, MyPageFragment.TAG);
						}
					} else {
						mypageFragment= new MyPageFragment();
						replaceFragment(R.id.contentPanel, mypageFragment, MyPageFragment.TAG);
					}
				}
			}
		});
	
		//Post first screen and keep retained fragments
		if(currentFragment.equals(FeedFragment.TAG)){
			feedFragment=(FeedFragment) fragManager.findFragmentByTag(FeedFragment.TAG);
			if(feedFragment!=null){
				loadFeed();
			}
		}
		else {
			mypageFragment=(MyPageFragment) fragManager.findFragmentByTag(MyPageFragment.TAG);
			if(mypageFragment!=null)
				replaceFragment(R.id.contentPanel, mypageFragment, MyPageFragment.TAG);
			else{
				mypageFragment= new MyPageFragment();
				replaceFragment(R.id.contentPanel, mypageFragment, MyPageFragment.TAG);
			}
			nodataFragment=(NoDataFragment) fragManager.findFragmentByTag(NoDataFragment.TAG);
			if(nodataFragment!=null){
				removeFragment(nodataFragment);
			}
		}
		
		//Register receiver
		wifiReceiver = new WifiReceiver();
		wifiReceiver.setMainActivityHandler(this);    
		IntentFilter callInterceptorIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(wifiReceiver,  callInterceptorIntentFilter);
    }
    
    public void loadFeed() {

    	feedFragment=(FeedFragment)fragManager.findFragmentByTag(FeedFragment.TAG);
    	nodataFragment=(NoDataFragment)fragManager.findFragmentByTag(NoDataFragment.TAG);
    	
    	if(isNetworkAvailable()){ //If we are online
    		if(feedFragment!=null){ //Was created before  
    			if(FeedFragment.feedObjects.size()>0){ //If it has something then let the fragment refresh
    				feedFragment.refresh();
    			}
    			else {//Otherwise recreate
    				removeFragment(feedFragment);
    				feedFragment= new FeedFragment();
    			}
    			replaceFragment(R.id.contentPanel, feedFragment, FeedFragment.TAG);
    		} else { //If the fragment is not there then replace it with one new
    			feedFragment= new FeedFragment();
				replaceFragment(R.id.contentPanel, feedFragment, FeedFragment.TAG);
    		}
    		if(nodataFragment!=null){//If the nodata fragment is there and it's online remove it
    			removeFragment(nodataFragment);
    			nodataFragment=null;
    		}
    	} else { //If we are offline
    		if( feedFragment!=null
    				&& FeedFragment.feedObjects.size()>0){ 	// But the fragment exists and 
    													 	// we have some information loaded make it visible
    			
    			replaceFragment(R.id.contentPanel, feedFragment, FeedFragment.TAG);
 
    		} if(nodataFragment==null) { 	// the fragment doesn't exist so we simply replace what we have 
    											 	// with the nodataFragment
        			nodataFragment= new NoDataFragment();
        			replaceFragment(R.id.contentPanel, nodataFragment, NoDataFragment.TAG);
    		}

    		if(nodataFragment==null) { //If the no data fragment is not displayed yet then add it
    			nodataFragment= new NoDataFragment();
    			addFragment(R.id.warningPanel, nodataFragment, NoDataFragment.TAG);
    		} 
    	}
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	unregisterReceiver(wifiReceiver);
    	feedFragment=null;
    	
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	if(isVisible)
    		loadFeed();
    }
    
    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        
        if(feedFragment!=null){
        	if(!isVisible)
        		currentFragment=MyPageFragment.TAG;
        	else
        		currentFragment=FeedFragment.TAG;
        }
    }
    
    
    @Override
    public void onStop() {
        super.onStop();
        
    }
    
    private void replaceFragment(int holderid, Fragment fragment, String tag){
    	FragmentTransaction ft = fragManager.beginTransaction();
    	if(isVisible){ //if it's visible
    		if(mypageFragment!=null){
    			removeFragment(mypageFragment);
    			mypageFragment=null;
    		}
    		
    		if(fragment!=null){ 
    			if(FeedFragment.feedObjects!=null){ //If we have something then just show them
    				ft.show(fragment);
    				if(!fragment.isAdded()){
    					ft.add(holderid, fragment,tag);
    				} 
    			}
    			else
    				ft.replace(holderid, fragment,tag);
    		} else {
    			ft.replace(holderid, fragment, tag);
    		}
    		currentFragment=FeedFragment.TAG;
    	} else {
    		if(fragment instanceof MyPageFragment){ //if it's my page fragment then we need to do some things
    			if(fragment.isAdded()){
    				currentFragment=MyPageFragment.TAG;
    				return;
    			}
    			
    			if(feedFragment!=null){
    				ft.hide(feedFragment);	//Hide what we already loaded
    				if(!fragment.isAdded()){
    					ft.add(holderid,fragment,tag);
    				}
    			} else { //Otherwise just replace
    				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE); 
    				ft.replace(holderid, fragment, tag);
    			}
    			
    			if(nodataFragment!=null){
    				ft.remove(nodataFragment);
    				nodataFragment=null;
				}
    			
    		} else {
    			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE); 
    			ft.replace(holderid, fragment, tag);
    		}
    	}
    	ft.commit();
    }
    
    private void addFragment(int holderid,Fragment fragment, String tag){
    	FragmentTransaction ft = fragManager.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE); 
		ft.add(holderid, fragment, tag);
		ft.commit();
    }
    
    private void removeFragment(Fragment fragment){
    	FragmentTransaction ft = fragManager.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE); 
		ft.remove(fragment);
		ft.commit();
    }

	private static class ClickListenerForSliding implements OnClickListener {
        HorizontalScrollView slideView;
        View menu;

        boolean menuOut = false;

        public ClickListenerForSliding(HorizontalScrollView scrollView, View menu) {
            super();
            this.slideView = scrollView;
            this.menu = menu;
        }

        @Override
        public void onClick(View v) {
            int menuWidth = menu.getMeasuredWidth();

            // Ensure menu is visible
            menu.setVisibility(View.VISIBLE);

            if (!menuOut) {
                // Scroll to 0 to reveal menu
                int left = 0;
                slideView.smoothScrollTo(left, 0);
            } else {
                // Scroll to menuWidth so menu isn't on screen.
                int left = menuWidth;
                slideView.smoothScrollTo(left, 0);
            }
            menuOut = !menuOut;
        }
    }
    
    private static class SizeCallbackForMenu implements SizeCallback {
        int slideWidth;

        public SizeCallbackForMenu(int slideWidth) {
            super();
            this.slideWidth = slideWidth;
        }

        @Override
        public void onGlobalLayout() {}

        @Override
        public void getViewSize(int idx, int w, int h, int[] dims) {
            dims[0] = w;
            dims[1] = h;
            final int menuIdx = 0;
            if (idx == menuIdx) {
                dims[0] = w - slideWidth;
            }
        }
    }
    
    //TODO Handle login pages that redirect traffic to them
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}