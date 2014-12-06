package com.wiflow.activity;

import java.io.IOException;
import com.google.ads.*;
import com.google.ads.AdRequest.ErrorCode;

import org.apache.http.client.ClientProtocolException;

import com.wiflow.network.NetworkControl;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WiFlowActivity extends Activity implements AdListener {
	
	//Buttons
	private static Button ok_Button;
	private static Button back_Button;
	private static RelativeLayout start_service_Button;
	private static RelativeLayout start_wifi_Button;
	
	//Views
	private static TextView status_Textview;
	private static EditText password_Edit;
	private static EditText user_Edit;
	private static TextView wifiheader_Textview;
	private static TextView serviceheader_Textview;
	
	//Credentials
	private static SharedPreferences settings;
	private static SharedPreferences.Editor editor;

	//
	private NotificationManager mNotificationManager;
	//Google Ads
	private AdView adView;
	private InterstitialAd interstitial;
	private int intersticial_treshold = 20;

	private static final String LOG="WiFlow Activity";
	
	public static WiFlowActivity wa;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        wa = this;

        // Create the Google adView
//        adView = new AdView(this, AdSize.BANNER, "a150dc9f3b41d09");
        adView = new AdView(this, AdSize.SMART_BANNER, "a150e09406b7cb0");
//        AdView adView2 = new AdView(this, AdSize.,"a150dc9f3b41d09");
        // Lookup the main LinearLayout 
        LinearLayout layout = (LinearLayout)findViewById(R.id.adslayout);

        // Add the adView to it
        layout.addView(adView);

        // Initiate a generic request to load it with an ad
//        adView.loadAd(new AdRequest());

        // TEST ADS Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device.
        AdRequest adRequest = new AdRequest();
        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);            
        adView.loadAd(adRequest);
        
        settings = getSharedPreferences("WiFlowFile", 0);
        editor=settings.edit();
        
        status_Textview=(TextView)findViewById(R.id.status_textview);
        
    	start_wifi_Button = (RelativeLayout)findViewById(R.id.rlwifi_button);
    	start_service_Button = (RelativeLayout)findViewById(R.id.rlservice_button);
    	wifiheader_Textview = (TextView)findViewById(R.id.wifi_header);
    	serviceheader_Textview = (TextView)findViewById(R.id.service_header);
        
    	
        final Button settings_Button = (Button)findViewById(R.id.settings_button);
		settings_Button.setOnClickListener(new View.OnClickListener(){
		
			public void onClick(View v) {

				checkInterstitial();
				
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
		            	checkInterstitial();
		            	dialog.dismiss();
		            }
		        });
	

		        //save button
		        ok_Button = (Button) dialog.findViewById(R.id.ok_button);
		        ok_Button.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		            	checkInterstitial();
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
		
		
		final ImageView configure1_Button = (ImageView)findViewById(R.id.configure1_button);
		if(settings.getString("configure1_user", "").equals("") || settings.getString("configure1_pass", "").equals(""))
			configure1_Button.setBackgroundResource(R.drawable.ic_next_nav);
		else
			configure1_Button.setBackgroundResource(R.drawable.ic_next_av);
		
		final RelativeLayout panel1_Relativelayout=(RelativeLayout)findViewById(R.id.rlmain_body_network1);
		
		panel1_Relativelayout.setOnClickListener(new View.OnClickListener(){
			
			public void onClick(View v) {
				checkInterstitial();
				final Dialog dialog = new Dialog(WiFlowActivity.this);
		        dialog.setContentView(R.layout.dialog_credentials);
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
		            	checkInterstitial();
		            	dialog.dismiss();
		            }
		        });

		        //save button
		        ok_Button = (Button) dialog.findViewById(R.id.ok_button);
		        ok_Button.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		            	checkInterstitial();
		        	    editor.putString("configure1_user", user_Edit.getText().toString());
		        	    editor.putString("configure1_pass", password_Edit.getText().toString());
		        	    editor.commit();
		        	    
		        	    if(settings.getString("configure1_user", "").equals("") || settings.getString("configure1_pass", "").equals(""))
		        			configure1_Button.setBackgroundResource(R.drawable.ic_next_nav);
		        		else
		        			configure1_Button.setBackgroundResource(R.drawable.ic_next_av);
		        	    
		            	dialog.dismiss();
		            }
		        });
		        
		        try{
		        	dialog.show();
		        }
		        catch (Exception IGNORED){}
			}
		});

		final ImageView configure2_Button = (ImageView)findViewById(R.id.configure2_button);
		if(settings.getString("configure2_user", "").equals("") || settings.getString("configure2_pass", "").equals(""))
			configure2_Button.setBackgroundResource(R.drawable.ic_next_nav);
		else
			configure2_Button.setBackgroundResource(R.drawable.ic_next_av);
		
		final RelativeLayout panel2_Relativelayout=(RelativeLayout)findViewById(R.id.rlmain_body_network2);
		
		panel2_Relativelayout.setOnClickListener(new View.OnClickListener(){
		
			public void onClick(View v) {
				checkInterstitial();
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
		            	checkInterstitial();
		            	dialog.dismiss();
		            }
		        });
		        
		        //save button
		        ok_Button = (Button) dialog.findViewById(R.id.ok_button);
		        ok_Button.setOnClickListener(new OnClickListener() {
		            public void onClick(View v) {
		            	checkInterstitial();
		        	    editor.putString("configure2_user", user_Edit.getText().toString());
		        	    editor.putString("configure2_pass", password_Edit.getText().toString());
		        	    editor.commit();
		        	    
		        	    if(settings.getString("configure2_user", "").equals("") || settings.getString("configure2_pass", "").equals(""))
		        	    	configure2_Button.setBackgroundResource(R.drawable.ic_next_nav);
		        		else
		        			configure2_Button.setBackgroundResource(R.drawable.ic_next_av);
		        	    
		            	dialog.dismiss();
		            }
		        });
		        try{
		        	dialog.show();
		        }
		        catch (Exception IGNORED){}
			}
		});

		        final Button configure3_Button = (Button)findViewById(R.id.configure3_button);
				if(settings.getString("configure3_user", "").equals("") || settings.getString("configure3_pass", "").equals(""))
					configure3_Button.setBackgroundResource(R.drawable.ic_next_nav);
        		else
        			configure3_Button.setBackgroundResource(R.drawable.ic_next_av);
				
				final RelativeLayout panel3_Relativelayout=(RelativeLayout)findViewById(R.id.rlmain_body_network3);
				
				panel3_Relativelayout.setOnClickListener(new View.OnClickListener(){
				
					public void onClick(View v) {
						checkInterstitial();
						final Dialog dialog = new Dialog(WiFlowActivity.this);
				        dialog.setContentView(R.layout.dialog_credentials);
				        dialog.setTitle("Credentials");
				        dialog.setCancelable(true);

				        user_Edit=(EditText)dialog.findViewById(R.id.user_edit);
				        password_Edit=(EditText)dialog.findViewById(R.id.password_edit);
				        
				        user_Edit.setText(settings.getString("configure3_user", ""));
				        password_Edit.setText(settings.getString("configure3_pass", ""));
				        
				        //cancel button
				        back_Button = (Button) dialog.findViewById(R.id.back_button);
				        back_Button.setOnClickListener(new OnClickListener() {
				            public void onClick(View v) {
				            	checkInterstitial();
				            	dialog.dismiss();
				            }
				        });
				        
				        //save button
				        ok_Button = (Button) dialog.findViewById(R.id.ok_button);
				        ok_Button.setOnClickListener(new OnClickListener() {
				            public void onClick(View v) {
				            	checkInterstitial();
				        	    editor.putString("configure3_user", user_Edit.getText().toString());
				        	    editor.putString("configure3_pass", password_Edit.getText().toString());
				        	    editor.commit();
				        	    
				        	    if(settings.getString("configure3_user", "").equals("") || settings.getString("configure3_pass", "").equals(""))
				        	    	configure3_Button.setBackgroundResource(R.drawable.ic_next_nav);
				        		else
				        			configure3_Button.setBackgroundResource(R.drawable.ic_next_av);
				        	    
				            	dialog.dismiss();
				            }
				        });	
		        
		        try{
		        	dialog.show();
		        }
		        catch (Exception IGNORED){}
			}
		});
				
				
		final Button filter_Button = (Button)findViewById(R.id.filter_button);
		filter_Button.setOnClickListener(new View.OnClickListener(){
				
					public void onClick(View v) {
						checkInterstitial();
						final Dialog dialog = new Dialog(WiFlowActivity.this);
				        dialog.setContentView(R.layout.dialog_filter);
				        dialog.setTitle("Location");
				        dialog.setCancelable(true);

				        //cancel button
				        back_Button = (Button) dialog.findViewById(R.id.back_button);
				        back_Button.setOnClickListener(new OnClickListener() {
				            public void onClick(View v) {
				            	checkInterstitial();
				            	dialog.dismiss();
				            }
				        });
				        
//				        final RadioButton radioPL_Button=(RadioButton)dialog.findViewById(R.id.radioPL);
				        final RadioButton radioPT_Button=(RadioButton)dialog.findViewById(R.id.radioPT);
				        final RadioButton radioBE_Button=(RadioButton)dialog.findViewById(R.id.radioBE);

				        
//				    	radioPL_Button.setOnClickListener(new OnClickListener() {
//				            public void onClick(View v) {
//				            	radioPL_Button.setChecked(true);
//				            	radioPT_Button.setChecked(false);
//				            	radioBE_Button.setChecked(false);
//				            }
//				        });
				    	
				    	
				    	radioPT_Button.setOnClickListener(new OnClickListener() {
				            public void onClick(View v) {
				            	checkInterstitial();
//				            	radioPL_Button.setChecked(false);
				            	radioPT_Button.setChecked(true);
				            	radioBE_Button.setChecked(false);
				            }
				        });
				    	
				    	radioBE_Button.setOnClickListener(new OnClickListener() {
				            public void onClick(View v) {
				            	checkInterstitial();
//				            	radioPL_Button.setChecked(false);
				            	radioPT_Button.setChecked(false);
				            	radioBE_Button.setChecked(true);
				            }
				        });

				        String loc=settings.getString("configure1_type", "gn");
				        
				        if(loc.equals("pt"))
				        	radioPT_Button.setChecked(true);
//				        else if(loc.equals("pl"))
//				        	radioPL_Button.setChecked(true);
				        else if(loc.equals("be"))
				        	radioBE_Button.setChecked(true);
				   //     else
				     //   	radioGN_Button.setChecked(true);

				        final LinearLayout mainLinear=(LinearLayout)findViewById(R.id.mainlinear);
				        
				        //save button
				        ok_Button = (Button) dialog.findViewById(R.id.ok_button);
				        ok_Button.setOnClickListener(new OnClickListener() {
				            public void onClick(View v) {
				            	checkInterstitial();
				        	    String configure1_type="";
						        if(radioPT_Button.isChecked()){
						        	configure1_type="pt";
						        	
						        	mainLinear.removeView(panel3_Relativelayout);
						        	
						        	if(mainLinear.findViewById(R.id.configure2_button)==null)
						        		mainLinear.addView(panel2_Relativelayout);
						        	
						        	if(mainLinear.findViewById(R.id.configure1_button)==null)
						        		mainLinear.addView(panel1_Relativelayout);
						        	
						        	final TextView fon_textview = (TextView)findViewById(R.id.fon_textview);
						        	fon_textview.setText(R.string.fon_label);
						        }
//						        else if(radioPL_Button.isChecked()){
//						        	configure1_type="pl";	
//						        	mainLinear.removeView(panel2_Relativelayout);
//						        	mainLinear.removeView(panel3_Relativelayout);	
//						        	
//						        	if(mainLinear.findViewById(R.id.configure1_button)==null)
//						        		mainLinear.addView(panel1_Relativelayout);
//						        	
//						        	final TextView fon_textview = (TextView)findViewById(R.id.fon_textview);
//						        	fon_textview.setText(R.string.netia_label);
//						        }
						        else if(radioBE_Button.isChecked()){
						        	configure1_type="be";	
						        	mainLinear.removeView(panel1_Relativelayout);
						        	mainLinear.removeView(panel2_Relativelayout);
						        	
						        	if(mainLinear.findViewById(R.id.configure3_button)==null)
						        		mainLinear.addView(panel3_Relativelayout);
						        	
//						        	if(mainLinear.findViewById(R.id.configure1_button)==null)
//						        		mainLinear.addView(panel1_Relativelayout);
						        	
//						        	final TextView fon_textview = (TextView)findViewById(R.id.fon_textview);
//						        	fon_textview.setText(R.string.belgacom_label);
						        	
						        	final TextView telenet_textview = (TextView)findViewById(R.id.telenet_textview);
						        	telenet_textview.setText(R.string.telenet_label);
						        }
						        else{
						        	configure1_type="gn";

						        	mainLinear.removeView(panel1_Relativelayout);
						        	mainLinear.removeView(panel2_Relativelayout);
						        }

				        	    editor.putString("configure1_type", configure1_type);
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
	    if(settings.getBoolean("service_on", false)){
	  		start_service_Button.setBackgroundResource(R.layout.container_ontoggle);
	  		manageNetworkControl(false);
			try {
				Thread.sleep(1*500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			manageNetworkControl(true);
	  	} else {
	  		start_service_Button.setBackgroundResource(R.layout.container_offtoggle);//off
	  	}
    	
  		start_service_Button.setOnClickListener(new View.OnClickListener(){
	      	public void onClick(View v) {
	      		checkInterstitial();
	      		if(settings.getBoolean("service_on", false)){
	      			Toast.makeText(WiFlowActivity.this, R.string.toast_shutService, Toast.LENGTH_SHORT).show();
	      			manageNetworkControl(false);
	    	  		start_service_Button.setBackgroundResource(R.layout.container_offtoggle);//off
	    	  		serviceheader_Textview.setTextColor(getResources().getColor(R.color.main_title_txt));
	      		} else {
	      			Toast.makeText(WiFlowActivity.this, R.string.toast_ServiceOn, Toast.LENGTH_SHORT).show();
		            manageNetworkControl(true);
			  		start_service_Button.setBackgroundResource(R.layout.container_ontoggle);
	      		}
	      	}
		});
    }
    
    
	public void verifyConnection() throws ClientProtocolException, IOException {
		final ConnectivityManager conMgr =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        final WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        if(wifiManager.isWifiEnabled()){
        	start_wifi_Button.setBackgroundResource(R.layout.container_ontoggle);
        }
        else{
        	start_wifi_Button.setBackgroundResource(R.layout.container_offtoggle);//off
        	wifiheader_Textview.setTextColor(getResources().getColor(R.color.main_title_txt));
        }

    	start_wifi_Button.setOnClickListener(new View.OnClickListener(){
    		public void onClick(View v) {
    			checkInterstitial();
    			if(wifiManager.isWifiEnabled()){
    				wifiManager.setWifiEnabled(false);
    				start_wifi_Button.setBackgroundResource(R.layout.container_offtoggle);//off
    				wifiheader_Textview.getPaint().setShader(null);
    				wifiheader_Textview.setTextColor(getResources().getColor(R.color.main_title_txt));
    				
    	      	}
    	      	else{
    	      		wifiManager.setWifiEnabled(true);
    	      		Toast.makeText(WiFlowActivity.this, R.string.toast_wifiOn, Toast.LENGTH_SHORT).show();
    	      		start_wifi_Button.setBackgroundResource(R.layout.container_ontoggle);
    	      	}
    	   }
    	});
    	
    	if (activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
    		final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
             
            String networkName="Connected to "+wifiInfo.getSSID();
            
            if(wifiInfo.getSSID()!=null){
	            int supported=0;
            	if(!wifiInfo.getSSID().toUpperCase().contains("FON") && !wifiInfo.getSSID().toUpperCase().contains("PT-WIFI") && !wifiInfo.getSSID().toUpperCase().contains("TELENETHOMESPOT") && !wifiInfo.getSSID().toUpperCase().contains("TELENETHOTSPOT")) {
            		supported=1;
	            }
	            
	            /**
	             * SSID Belgium: Fon-belgacom, login same as PT apparently
	             * SSID UK: BTFON / BTOpezone-H / BTWiFi-with-FON, need to choose Fon account before entering user and pass
	             * SSID France: SFR WiFi FON / Neuf WiFi FON, login same as PT apparently
	             * SSID Poland: FON_NETIA_FREE_INTERNET, need to choose "Others" as Operator
	             * SSID Portugal: FON_ZON_FREE_INTERNET
	             */
	            else if(wifiInfo.getSSID().toUpperCase().contains("FON") || wifiInfo.getSSID().contains("BTOpenzone-") || wifiInfo.getSSID().contains("Fon-")){
	            	networkName="Connected to FON";
	            	supported=2;
	            }else if(wifiInfo.getSSID().toUpperCase().contains("PT-WIFI")){
	            	supported=2;
	            }else if(wifiInfo.getSSID().toUpperCase().contains("TELENETHOMESPOT") || wifiInfo.getSSID().toUpperCase().contains("TELENETHOTSPOT")){
	            	networkName="Connected to Telenet";
	            	supported=2;
	            }
	            status_Textview.setText(networkName);

	
            }
        } else {
            status_Textview.setText("Not connected");
        } 
    	manageServiceButtons();
	}	

	private void checkInterstitial(){
		Log.e(LOG,"Interstitial Counter: "+settings.getInt("interstitial", 0));
		if(settings.getInt("interstitial", 0)==intersticial_treshold){
			
			// Create the interstitial
		    interstitial = new InterstitialAd(this, "a150e09406b7cb0");

		    // TEST ADS Create an ad request. Check logcat output for the hashed device ID to
	        // get test ads on a physical device.
		    AdRequest adRequest = new AdRequest();
		    adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		    interstitial.loadAd(adRequest);
		    
		 // Initiate a generic request to load it with an ad
//	        interstitial.loadAd(new AdRequest());

		    // Set Ad Listener to use the callbacks below
		    interstitial.setAdListener(this);
			
			editor.putInt("interstitial", 0);
    	    editor.commit();
		}
		else{
	        editor.putInt("interstitial", settings.getInt("interstitial", 0)+1);
    	    editor.commit();
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
	
	   	try {
			verifyConnection();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
	}
	  
	@Override
	public void onPause(){
		super.onPause();
	}
	   
	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.destroy();
	    }

		try{mNotificationManager.cancel(1);}catch(Exception IGNORED){}
	    super.onDestroy();
	}

	public void onDismissScreen(Ad arg0) {
		
	}

	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		
	}

	public void onLeaveApplication(Ad arg0) {
		
	}

	public void onPresentScreen(Ad arg0) {
		
	}

	public void onReceiveAd(Ad arg0) {
		if (arg0 == interstitial) {
		      interstitial.show();
		}
	}

}