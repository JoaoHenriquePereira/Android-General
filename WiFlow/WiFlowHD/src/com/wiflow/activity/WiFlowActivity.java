package com.wiflow.activity;

import java.io.IOException;
import com.google.ads.*;

import org.apache.http.client.ClientProtocolException;

import com.wiflowhd.activity.R;
import com.wiflow.network.NetworkControl;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class WiFlowActivity extends Activity {
	
	//Buttons
	private static Button ok_Button;
	private static Button back_Button;
	private static ToggleButton start_service_Button;
	private static Button wifi_Button;
	
	//Views
	private static TextView status_Textview;
	private static EditText password_Edit;
	private static EditText user_Edit;
	
	//Credentials
	private static SharedPreferences settings;
	private static SharedPreferences.Editor editor;

	//Google Ads
	private AdView adView;
	
	public static WiFlowActivity wa;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Create the Google adView
        adView = new AdView(this, AdSize.IAB_LEADERBOARD, "a150e09406b7cb0");
        
        // Lookup the main LinearLayout 
        LinearLayout layout = (LinearLayout)findViewById(R.id.adslayout);

        // Add the adView to it
        layout.addView(adView);

        // Initiate a generic request to load it with an ad
        adView.loadAd(new AdRequest());
        
        // TEST ADS Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device.
//        AdRequest adRequest = new AdRequest();
//        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
        
        // Start loading the ad in the background.
//        adView.loadAd(adRequest);
        
        settings = getSharedPreferences("WiFlowFile", 0);
        editor=settings.edit();
        
        status_Textview=(TextView)findViewById(R.id.status_textview);
        wifi_Button=(Button)findViewById(R.id.wifi_button);

        final FrameLayout network_Linearlayout=(FrameLayout)findViewById(R.id.llmain_body_status);
        network_Linearlayout.setOnClickListener(new View.OnClickListener(){

			public void onClick(View arg0) {
				try {
					verifyConnection(false);
				} catch (ClientProtocolException e) {	
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        });
        
        final Button settings_Button = (Button)findViewById(R.id.settings_button);
		settings_Button.setOnClickListener(new View.OnClickListener(){
		
			public void onClick(View v) {

				final Dialog dialog = new Dialog(WiFlowActivity.this);
		        dialog.setContentView(R.layout.dialog_settings);
		        dialog.setTitle("Settings");
		        dialog.setCancelable(true);

		        final CheckBox force_connect_Checkbox=(CheckBox)dialog.findViewById(R.id.force_connect_chk);
		        final CheckBox notification_Checkbox=(CheckBox)dialog.findViewById(R.id.notification_chk);
		        final CheckBox ring_tone_Checkbox=(CheckBox)dialog.findViewById(R.id.ringtone_chk);
		        final CheckBox vibrate_Checkbox=(CheckBox)dialog.findViewById(R.id.vibrate_chk);
		        
				if(settings.getBoolean("force_connect_setting", false))
					force_connect_Checkbox.setChecked(true);
				else
					force_connect_Checkbox.setChecked(false);
				
				if(settings.getBoolean("notification_setting", true))
					notification_Checkbox.setChecked(true);
				else
					notification_Checkbox.setChecked(false);
				
				if(settings.getBoolean("ring_tone_setting", false))
					ring_tone_Checkbox.setChecked(true);
				else
					ring_tone_Checkbox.setChecked(false);
				
				if(settings.getBoolean("vibrate_setting", false))
					vibrate_Checkbox.setChecked(true);
				else
					vibrate_Checkbox.setChecked(false);

		        //cancel button
		        back_Button = (Button) dialog.findViewById(R.id.back_button);
		        back_Button.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		            	dialog.dismiss();
		            }
		        });
		        
		        //save button
		        ok_Button = (Button) dialog.findViewById(R.id.ok_button);
		        ok_Button.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
//		            	SharedPreferences settings = getSharedPreferences("WiFlowFile", 0);
//		        	    SharedPreferences.Editor editor = settings.edit();
		        	    editor.putBoolean("force_connect_setting", force_connect_Checkbox.isChecked());
		        	    editor.putBoolean("notification_setting", notification_Checkbox.isChecked());
		        	    editor.putBoolean("ring_tone_setting", ring_tone_Checkbox.isChecked());
		        	    editor.putBoolean("vibrate_setting", vibrate_Checkbox.isChecked());
		        	    editor.commit();
		            	dialog.dismiss();
		            }
		        });
		        
		        try{
		        	dialog.show();
		        }
		        catch (Exception IGNORED){}
			}
		});

		final Button configure1_Button = (Button)findViewById(R.id.configure1_button);
		if(settings.getString("configure1_user", "").equals("") || settings.getString("configure1_pass", "").equals(""))
			configure1_Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_next_nav));
		else
			configure1_Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_next_av));
		
		final RelativeLayout panel1_Relativelayout=(RelativeLayout)findViewById(R.id.rlmain_body_network1);
		
		panel1_Relativelayout.setOnClickListener(new View.OnClickListener(){
		
			public void onClick(View v) {

				final Dialog dialog = new Dialog(WiFlowActivity.this);
		        dialog.setContentView(R.layout.dialog_credentials_fon);
		        dialog.setTitle("Credentials");
		        dialog.setCancelable(true);

		        user_Edit=(EditText)dialog.findViewById(R.id.user_edit);
		        password_Edit=(EditText)dialog.findViewById(R.id.password_edit);
		        
		        user_Edit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		        
		        user_Edit.setText(settings.getString("configure1_user", ""));
		        password_Edit.setText(settings.getString("configure1_pass", ""));
		        
		        //cancel button
		        back_Button = (Button) dialog.findViewById(R.id.back_button);
		        back_Button.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		            	dialog.dismiss();
		            }
		        });
		        
		        final RadioButton radioPL_Button=(RadioButton)dialog.findViewById(R.id.radioPL);
		        final RadioButton radioPT_Button=(RadioButton)dialog.findViewById(R.id.radioPT);

		        if(!settings.getString("configure1_type", "gn").equals("pl"))
		        	radioPT_Button.setChecked(true);
		        else
		        	radioPL_Button.setChecked(true);
		        
		        //save button
		        ok_Button = (Button) dialog.findViewById(R.id.ok_button);
		        ok_Button.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {

		        	    String configure1_type="gn";
				        if(radioPL_Button.isChecked())
				        	configure1_type="pl";
				        if(radioPT_Button.isChecked())
				        	configure1_type="pt";

		        	    editor.putString("configure1_user", user_Edit.getText().toString());
		        	    editor.putString("configure1_pass", password_Edit.getText().toString());
		        	    editor.putString("configure1_type", configure1_type);
		        	    editor.commit();
		        	    
		        	    if(settings.getString("configure1_user", "").equals("") || settings.getString("configure1_pass", "").equals(""))
		        			configure1_Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_next_nav));
		        		else
		        			configure1_Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_next_av));
		        	    
		            	dialog.dismiss();
		            }
		        });
		        
		        try{
		        	dialog.show();
		        }
		        catch (Exception IGNORED){}
			}
		});
		
		

		final Button configure2_Button = (Button)findViewById(R.id.configure2_button);
		if(settings.getString("configure2_user", "").equals("") || settings.getString("configure2_pass", "").equals(""))
			configure2_Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_next_nav));
		else
			configure2_Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_next_av));
		
		final RelativeLayout panel2_Relativelayout=(RelativeLayout)findViewById(R.id.rlmain_body_network2);
		
		panel2_Relativelayout.setOnClickListener(new View.OnClickListener(){
		
			public void onClick(View v) {

				final Dialog dialog = new Dialog(WiFlowActivity.this);
		        dialog.setContentView(R.layout.dialog_credentials);
		        dialog.setTitle("Credentials");
		        dialog.setCancelable(true);

		        user_Edit=(EditText)dialog.findViewById(R.id.user_edit);
		        password_Edit=(EditText)dialog.findViewById(R.id.password_edit);
		        
		        user_Edit.setText(settings.getString("configure2_user", ""));
		        password_Edit.setText(settings.getString("configure2_pass", ""));
		        
		        //cancel button
		        back_Button = (Button) dialog.findViewById(R.id.back_button);
		        back_Button.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		            	dialog.dismiss();
		            }
		        });
		        
		        //save button
		        ok_Button = (Button) dialog.findViewById(R.id.ok_button);
		        ok_Button.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		        	    editor.putString("configure2_user", user_Edit.getText().toString());
		        	    editor.putString("configure2_pass", password_Edit.getText().toString());
		        	    editor.commit();
		        	    
		        	    if(settings.getString("configure2_user", "").equals("") || settings.getString("configure2_pass", "").equals(""))
		        			configure2_Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_next_nav));
		        		else
		        			configure2_Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_next_av));
		        	    
		            	dialog.dismiss();
		            }
		        });

		        try{
		        	dialog.show();
		        }
		        catch (Exception IGNORED){}
			}
		});
    }    
    
    public void onResume(){
    	super.onResume();
    	
    	try {
			verifyConnection(true);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }
    
    @Override
    public void onDestroy() {
      if (adView != null) {
        adView.destroy();
      }
      super.onDestroy();
    }
    
    //Function to turn on and off our network control thread
    public void manageNetworkControl(boolean intention){
    	if(intention){
    		startService(new Intent(WiFlowActivity.this, NetworkControl.class));
	        editor.putBoolean("service_on", true);
	        editor.commit();
    	} else {
	    	stopService(new Intent(WiFlowActivity.this, NetworkControl.class));
            editor.putBoolean("service_on", false);
            editor.commit();
    	}
    }

    //Function to hide/show Service Buttons
    private void manageServiceButtons() {
     
    	start_service_Button=(ToggleButton)findViewById(R.id.start_service_button);

	    if(settings.getBoolean("service_on", false)){
	  		start_service_Button.setChecked(true);
	  		manageNetworkControl(false);
			try {
				Thread.sleep(1*500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			manageNetworkControl(true);
	  	} else {
	  		start_service_Button.setChecked(false);
	  	}
    	
  		start_service_Button.setOnClickListener(new View.OnClickListener(){
	      	public void onClick(View v) {
	      		if(start_service_Button.isChecked()){
		            manageNetworkControl(true);
	      		} else {
	      			Toast.makeText(WiFlowActivity.this, R.string.toast_shutService, Toast.LENGTH_SHORT).show();
	      			manageNetworkControl(false);
	      		}
	      	}
		});
    }
    
    //Function to hide/show Service Buttons
    private void refreshServiceButtons() {
     
    	start_service_Button=(ToggleButton)findViewById(R.id.start_service_button);
    	
    	if(settings.getBoolean("service_on", false)){
  			start_service_Button.setChecked(true);
  		} else {
  			start_service_Button.setChecked(false);
  		}

  		start_service_Button.setOnClickListener(new View.OnClickListener(){
	      	public void onClick(View v) {
	      		if(start_service_Button.isChecked()){
		            manageNetworkControl(true);
	      		} else {
	      			manageNetworkControl(false);
	      		}
	      	}
		});
    }
    
	public void verifyConnection(boolean direct) throws ClientProtocolException, IOException {
		final ConnectivityManager conMgr =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        final WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        if(wifiManager.isWifiEnabled()){
        	wifi_Button.setBackgroundResource(R.drawable.wifi_on_g);
        }
        else{
        	wifi_Button.setBackgroundResource(R.drawable.wifi_on);
        }
        
    	wifi_Button.setOnClickListener(new View.OnClickListener(){
    		public void onClick(View v) {
    			if(wifiManager.isWifiEnabled()){
    				wifiManager.setWifiEnabled(false);
    	      		wifi_Button.setBackgroundResource(R.drawable.wifi_on);
    	      	}
    	      	else{
    	      		wifiManager.setWifiEnabled(true);
    	      		wifi_Button.setBackgroundResource(R.drawable.wifi_on_g);
    	      		Toast.makeText(WiFlowActivity.this, R.string.toast_wifiOn, Toast.LENGTH_SHORT).show();
    	      	}
    	   }
    	});
    		
    	if (activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
    		final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
             
            String networkName="Connected to "+wifiInfo.getSSID();
            
            if(wifiInfo.getSSID()!=null){
	            if(!wifiInfo.getSSID().contains("FON") && !wifiInfo.getSSID().equals("PT-WIFI")) {
	            	status_Textview.setTextColor(getResources().getColor(R.color.main_red));	
	            }
	            
	            /**
	             * SSID Belgium: Fon-belgacom, login same as PT apparently
	             * SSID UK: BTFON / BTOpezone-H / BTWiFi-with-FON, need to choose Fon account before entering user and pass
	             * SSID France: SFR WiFi FON / Neuf WiFi FON, login same as PT apparently
	             * SSID Poland: FON_NETIA_FREE_INTERNET, need to choose "Others" as Operator
	             * SSID Portugal: FON_ZON_FREE_INTERNET
	             */
	            else if(wifiInfo.getSSID().contains("FON") || wifiInfo.getSSID().contains("BTOpenzone-") || wifiInfo.getSSID().contains("Fon-")){
	            	status_Textview.setTextColor(getResources().getColor(R.color.main_green_dark));
	            	networkName="Connected to FON";
	            }else if(wifiInfo.getSSID().equals("PT-WIFI")){
	            	status_Textview.setTextColor(getResources().getColor(R.color.main_green_dark));
	            }
	            status_Textview.setText(networkName);
            }
        } else {
        	status_Textview.setTextColor(getResources().getColor(R.color.main_grey));
            status_Textview.setText("Not connected");
        } 
    	if(direct)
    		manageServiceButtons();
    	else
    		refreshServiceButtons();
	}	
}