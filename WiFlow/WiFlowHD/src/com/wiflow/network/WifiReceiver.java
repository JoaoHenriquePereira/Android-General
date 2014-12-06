package com.wiflow.network;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.wiflow.activity.WiFlowActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver{

	private static SharedPreferences settings;
	private static final String LOG="WIFI RECEIVER";
	
	@Override
    public void onReceive(Context context, Intent intent) {
        //here, check that the network connection is available. If yes, start your service. If not, stop your service.
	   
		try {
			WiFlowActivity.wa.verifyConnection(false);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			Log.e(LOG, e.getMessage()+ " GENERAL EXCEPTION");
			e.printStackTrace();
		}
		
		try{
		    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo info = cm.getActiveNetworkInfo();
	        settings = context.getSharedPreferences("WiFlowFile", 0);
	        if (info != null) {
	            if (info.isConnected()) {
	        	    if(settings.getBoolean("service_on", false)){
		        	    context.startService(new Intent(context, NetworkControl.class));
	        	    }
	            }
	        }	
		}
		catch(Exception e){
			Log.e(LOG, e.getMessage()+ " GENERAL EXCEPTION");
			e.printStackTrace();
		}
    }
}