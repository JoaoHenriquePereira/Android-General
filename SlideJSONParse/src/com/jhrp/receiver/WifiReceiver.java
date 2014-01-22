package com.jhrp.receiver;

import com.jhrp.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *	JHP 08/08/2013
 *	This class handles the intentfilter broadcast of connection changes
 */

public class WifiReceiver extends BroadcastReceiver{

	private static final String TAG="WifiReceiver";
	
	private MainActivity mainActivity = null;  
	
	@Override
    public void onReceive(Context context, Intent intent) {
		try{
			if(mainActivity.isVisible)
				mainActivity.loadFeed();
		}
		catch (Exception IGNORED){}//TODO investigate
    }
	
	public void setMainActivityHandler(MainActivity main){
		mainActivity = main;
	}  
}