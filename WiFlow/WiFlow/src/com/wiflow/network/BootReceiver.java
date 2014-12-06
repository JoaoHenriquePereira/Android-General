package com.wiflow.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver{

	private static SharedPreferences settings;
	private static final String LOG="BOOT RECEIVER";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try{
			settings = context.getSharedPreferences("WiFlowFile", 0);
			if(settings.getBoolean("service_on", false)){
				Intent serviceIntent = new Intent();
				serviceIntent.setAction("com.wiflow.network.NetworkControl");
				context.startService(serviceIntent);	
			}
		}
		catch(Exception e){
			Log.e(LOG, e.getMessage()+ " GENERAL EXCEPTION");
			e.printStackTrace();
		}
	}
}