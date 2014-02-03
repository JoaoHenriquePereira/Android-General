package com.jhrp.assist;

import java.sql.SQLException;

import com.jhrp.assist.CaptureColorFragment.OnCaptureColor;
import com.jhrp.assist.SetsFragment.OnDataPass;
import com.jhrp.assist.db.SetDAO;
import com.jhrp.assist.db.TagGroupDAO;
import com.jhrp.assist.object.CaptureBundle;
import com.jhrp.assist.object.InterBundle;
import com.jhrp.assist.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**TODO
 * 
 * Add 2 listviews: 1 for sets, 1 for colors of sets tags
 * 		"Add set" last item on set listview
 * 		"Add tag" last item on tags listview
 * 		Slide left or right delete
 *   	Short click configure color
 *   	Long click change tag name
 */
public class ManageTagsActivity extends FragmentActivity implements SetsFragment.OnDataPass, TagsFragment.OnDataPass,OnCaptureColor {
	private static final String TAG = "ManageTagsActivity";
	
	//Buttons
	private static Button backButton;
	
	//Credentials
	private static SetDAO mSetDAO;
	private static TagGroupDAO mTagGroupDAO;

	//Fragment
  	private static FragmentManager fragManager;
  	private static SetsFragment mSetsFragment;
  	private static TagsFragment mTagsFragment;
  	private static CaptureColorFragment mCaptureColorFragment;
  	
  	private int mState = 0;
	private InterBundle mInterBundle;
	private CaptureBundle mCaptureBundle;
	private boolean fromCapture = false;
	private String mNewTag;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.managetags_view);
        
        fragManager = getSupportFragmentManager();
        
        //Initialize stuff
        mSetDAO = new SetDAO(this);
        mTagGroupDAO = new TagGroupDAO(this);
        
        try {
			mSetDAO.open();
			mTagGroupDAO.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	switch(mState){
            		case 0:
            			Intent intent= new Intent(ManageTagsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                    	finish(); 
            			break;
            		case 1: 
            			mState--;
            			replaceState();
            			break;
            		case 2: 
            			mState--;
            			replaceState();
            			break;
            		default:
            			mState=0;
            			replaceState();
            			break;
            	}
            }
        });

        //TODO
        //this.deleteDatabase("sets.db");
        
        mSetsFragment = new SetsFragment();
        
        FragmentTransaction ft = fragManager.beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out);
     
        ft.replace(R.id.contentLayout, mSetsFragment, SetsFragment.TAG);
        ft.addToBackStack(null);
        
        // Start the animated transition.
        ft.commit();
    }

	@Override
	public void onDataPass(InterBundle i) {
		mState = i.getState();
		mInterBundle = i;
		replaceState();
	}
	
	@Override
	public void onDataPass(String i) {
		mState=2;
		mNewTag=i;
		replaceState();
	}
	
	@Override
	public void onCaptureColor(CaptureBundle i) {
		mCaptureBundle = i;
		fromCapture = true;
		mState = 1;
		replaceState();
	}
	
	public void replaceState(){
		FragmentTransaction ft = fragManager.beginTransaction();
		ft.setCustomAnimations(R.anim.fragment_anim_in, R.anim.fragment_anim_out);
	    
		switch(mState){
			case 0:
				fromCapture = false;
				if(mSetsFragment == null)
					mSetsFragment = new SetsFragment();
				ft.replace(R.id.contentLayout, mSetsFragment, SetsFragment.TAG);
		        ft.addToBackStack(null);
		        ft.commit();
				break;//SetsFragment
				
			case 1:
				Bundle bundle = new Bundle();
				if(!fromCapture){
					if(mTagsFragment == null){
						mTagsFragment = new TagsFragment();
						bundle.putInt("setnum", mInterBundle.getClickedItem());
						bundle.putLong("settgid", mInterBundle.getClickedItemTGId());
						bundle.putString("setname", mInterBundle.getClickedItemName());
						mTagsFragment.setArguments(bundle);
					} else {
						bundle = mTagsFragment.getArguments();
						bundle.putInt("setnum", mInterBundle.getClickedItem());
						bundle.putLong("settgid", mInterBundle.getClickedItemTGId());
						bundle.putString("setname", mInterBundle.getClickedItemName());
					}
				} else { //Save new color in DB
					mTagGroupDAO.createTagGroup(mNewTag, 
							mInterBundle.getClickedItemTGId(), 
							Utils.convertScalarToString(mCaptureBundle.getmBlobColorRgba()), 
							Utils.convertScalarToString(mCaptureBundle.getmBlobColorHsv()));
				}
				
				fromCapture = false;
		        ft.replace(R.id.contentLayout, mTagsFragment, TagsFragment.TAG);
		        ft.addToBackStack(null);
		        ft.commit();
				break;//TagsFragment
			case 2:
				fromCapture = true;
				if(mCaptureColorFragment == null)
					mCaptureColorFragment = new CaptureColorFragment();
		        
				ft.replace(R.id.contentLayout, mCaptureColorFragment, CaptureColorFragment.TAG);
		        ft.addToBackStack(null);
		        ft.commit();
				break;//CaptureColorFragment
		}
	}
    
	public static SetDAO getmSetDAO() {
		return mSetDAO;
	}

	public static void setmSetDAO(SetDAO mSetDAO) {
		ManageTagsActivity.mSetDAO = mSetDAO;
	}

	public static TagGroupDAO getmTagGroupDAO() {
		return mTagGroupDAO;
	}

	public static void setmTagGroupDAO(TagGroupDAO mTagGroupDAO) {
		ManageTagsActivity.mTagGroupDAO = mTagGroupDAO;
	}
    
	@Override
	public void onResume(){
		super.onResume();
		
		try {
			mSetDAO.open();
			mTagGroupDAO.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	  
	@Override
	public void onPause(){
		super.onPause();
	}
	   
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    //mSetDAO.close();
		//mTagGroupDAO.close();
	}

	
}