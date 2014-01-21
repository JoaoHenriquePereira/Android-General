package com.jhrp.ui;

import java.util.ArrayList;

import com.jhrp.ui.HorizontalScroll.SizeCallback;
import com.jhrp.ui.R;

import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
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

public class MainActivity extends FragmentActivity {
	
	private static final String TAG = MainActivity.class.getSimpleName();

  	private HorizontalScroll slideView;
	private View menuView;
	private View appView;
  	
  	private static ListView otherObjects_lv;
  	private static DisplayMetrics metrics;
  	private static ImageView menuViewButton;
  	private static RelativeLayout.LayoutParams listViewParameters;

  	private static final double slidePercent=0.3;
  	private static ImageView helpViewButton;
  	
  	private static CalendarFragment calendarFragment;
  	
  	//Others
  	private static ArrayList<String> options;
  	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        options=new ArrayList<String>();
		options.add("Calendar");
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
	    
	    //Slide only the percent we define
	    slideView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu((int) ((metrics.widthPixels)*(1-slidePercent))));

		//Initialize
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

	    helpViewButton = (ImageView) tabBar.findViewById(R.id.helpViewButton);
	    helpViewButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(MainActivity.this,HelpActivity.class);
            	startActivity(intent);
            }
        });
		
		
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
					FragmentManager fm = getSupportFragmentManager();
			        calendarFragment = (CalendarFragment)fm.findFragmentByTag(CalendarFragment.TAG);
			        if (calendarFragment == null) {
			            calendarFragment = new CalendarFragment();
			        }
			        fm.beginTransaction().replace(R.id.slidingPanel, calendarFragment, CalendarFragment.TAG).commit();
				} else{
					FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE); 
					ft.replace(R.id.slidingPanel, new MyPageFragment());
					ft.commit(); 
				}
			}
		});

		/** Start with CalendarFragment*/
		FragmentManager fm = getSupportFragmentManager();
        calendarFragment = (CalendarFragment)fm.findFragmentByTag(CalendarFragment.TAG);
        if (calendarFragment == null) {
            calendarFragment = new CalendarFragment();
        }
        fm.beginTransaction().replace(R.id.slidingPanel, calendarFragment, CalendarFragment.TAG).commit();
	}
 
    
    /**
     * The Sliding Drawer handling 
     */
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
}