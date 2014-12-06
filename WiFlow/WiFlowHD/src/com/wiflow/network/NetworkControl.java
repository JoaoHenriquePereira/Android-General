package com.wiflow.network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyStore;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.wiflowhd.activity.R;
import com.wiflow.activity.WiFlowActivity;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore.Audio;
import android.util.Log;

public class NetworkControl extends IntentService
{
        //Wifi management
        private static WifiManager wifi;
    	private static BroadcastReceiver receiver;
    	
    	//Minimum supported wifi strenght
    	private static final int min_wifi_level = -80;
    	
    	//Debug Flag
    	private static final boolean DEBUG = true;

    	//Credentials
    	private static SharedPreferences settings;
    	
    	//Supported networks (, "boingo hotspot")
    	private static final String[] supported_SSID= new String[]{"fon_zon_free_internet","pt-wifi","fon_belgacom","telenethomespot","telenethotspot"};

    	//Service update timer in seconds
    	private static final int time_int=5;
    	
    	//Log
    	private static final String LOG="NETWORK CONTROL";

        @Override
        public IBinder onBind(Intent intent) 
        {
        	return null;
        }

		public NetworkControl() {
			super("com.wiflow.network.NetworkControl");
		}

		@Override
		protected void onHandleIntent(Intent arg0) {
			try
	        {
				//Get saved credentials
		        settings = getSharedPreferences("WiFlowFile", 0);
		        
		        //Get WiFi service context
		        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		        //Register Receiver
		        registerReceiver(receiver, new IntentFilter(
		        				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		        
	            final boolean isWifiEnabled = wifi.isWifiEnabled();
	            
	            if(DEBUG)
	            	Log.e(LOG,"START FUNCTION LOOP");

	            //Verify if WiFi is enabled
	            if (isWifiEnabled){
            		//wifi is enabled
	            	if(DEBUG)
	            		Log.e(LOG,"WiFi is enabled");
            		
            		//Get info on the current connection 
     	            final WifiInfo wifiInfo = wifi.getConnectionInfo();
     	            
     	            final boolean isConnected = isConnected(wifiInfo);
	   	  
            		//Verify if this device is connected to some network
    	            if (isConnected) {
	
    	            	final boolean isSupported = isSupported(wifiInfo.getSSID());
    	            	final boolean isOnline = isOnline();
    	            	final int signalStrength = wifiInfo.getRssi();
    	            	final String SSID = wifiInfo.getSSID();
    	            	
    	            	if(DEBUG)
    	            		Log.e(LOG,"Connected to: " +  wifiInfo.getSSID());
    	            	
    	            	if(DEBUG)
    	            		Log.e(LOG,"SIGNAL STRENGTH: " +  signalStrength);
    	            	
    	            	//Verify if network is supported
    	            	if(isSupported){
    	            		if(DEBUG)
    	            			Log.e(LOG,"Connected to Supported Network");
    	            		
    	            		//Verify if device is Online; if so, we will check signal strenght to see if it worth searching a new network to connect to
    	            		if(isOnline){
    	            			if(DEBUG)
    	            				Log.e(LOG,"Online");
    	            			
    	            			if(signalStrength < min_wifi_level){
    	            				if(DEBUG)
    	            					Log.e(LOG,"Online on Weak Signal");
    	            				
    	            			}
    	            		
    	            		//Device is connected to a supported Network but is Offline, so we will try to authenticate the user
    	            		}else{
    	            			if(DEBUG)
    	            				Log.e(LOG,"Offline");
    	            			
    	            			loginSupported(SSID);
    	            		}
    	            	//Device is connected to a non-supported Network	
    	            	}else{
    	            		if(DEBUG)
    	            			Log.e(LOG,"Connected to Non-Supported Network");
    	            		
    	            		//Device is Offline in a non-supported Network
    	            		if(!isOnline){
    	            			
    	            			//Search for access points and collect available WiFi
    	    	                wifi.startScan();                    
    	    	                final List<ScanResult> available_wifis = getAvailableWiFi();
    	    	                
    	    	                switch(numSuppNetworks(available_wifis)){
    	    	                	case 0: break;
    	    	                	
    	    	                	//If there is 1 supported network available connect to it
    	    	                	case 1: connectSup(getBestSupSignal(available_wifis));
    	    	                			break;
    	    	                	
    	    	                	//If there is more than 1 supported network available send user a notification to choose which one to connect to	
    	    	                	default: break;
    	    	                		
    	    	                }
    	            		}
    	            		
    	            	}
    	            //Device is not connected to any Network	
    	            }else{
    	            	if(DEBUG)
    	            		Log.e(LOG,"Not Connected");
    	            	
    	            	//Search for access points and collect available WiFi
    	                wifi.startScan();                    
    	                final List<ScanResult> available_wifis = getAvailableWiFi();
    	                
    	                switch(numSuppNetworks(available_wifis)){
		                	case 0: break;
		                	
		                	//If there is 1 supported network available connect to it
		                	case 1: connectSup(getBestSupSignal(available_wifis));
		                			break;
		                	
		                	//If there is more than 1 supported network available send user a notification to choose which one to connect to	
		                	default: break;
	                		
    	                }
    	            }
            		
            	}else{
            		//wifi is disabled
            		if(DEBUG)
            			Log.e(LOG,"WiFi is disabled");
            	}
    	
	        }catch(NullPointerException e){
				if(DEBUG)
					Log.d(LOG,"NullPointerException in BackgroundThread --> "+e.getMessage());
			}

			if(settings.getBoolean("service_on", false))
				calcNextUpdate();
		}

		private void calcNextUpdate()
		{
			Intent intent = new Intent(this, this.getClass());
			PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			// 15 second verification
			long currentTimeMillis = System.currentTimeMillis();
			long nextUpdateTimeMillis = currentTimeMillis + time_int * 1000;

			AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
		}

		//Check if a given Wifi is known by the phone
		public static boolean wifiIsKnown(ScanResult target_wifi){
			for (WifiConfiguration WC : wifi.getConfiguredNetworks()) {
				if(WC.SSID.replace("\"", "").equals(target_wifi.SSID))
					return true;
			}
			return false;
		}
		
          	
		public List<ScanResult> getAvailableWiFi(){
			List<ScanResult> available_wifis = new ArrayList<ScanResult>();
			try{
          		for (ScanResult result : wifi.getScanResults()) {
          			if(DEBUG)
          				Log.e(LOG,"AVAILABLE WIFI: "+ result.toString());
          			available_wifis.add(result);
          		}
          	}catch(Exception e){  
          		if(DEBUG)
          			Log.e(LOG, e.getMessage());
          	}
			return available_wifis;
		}
		
		//Method to know how many supported networks are in range
		public int numSuppNetworks(List<ScanResult> available_networks){
			int counter = 0;
			
			for(ScanResult result : available_networks)
				if(isSupported(result.SSID))
					counter++;
					
			return counter;
		}
		
		public ScanResult getBestSupSignal(List<ScanResult> available_networks){

            ScanResult bestSignal_supported = null;
	        for (ScanResult result : available_networks) {
	        	if(DEBUG)
	            	Log.e(LOG,"WIFI FOUND: "+result.toString());
	            	
	            	if ((bestSignal_supported == null
	                  || WifiManager.compareSignalLevel(bestSignal_supported.level, result.level) < 0) && isSupported(result.SSID))
	                bestSignal_supported = result;
	        }

            return bestSignal_supported;
		}
		
		public static boolean isSupported(String test){
			boolean result=false;
			final int sSSID_length=supported_SSID.length;
			for(int i=0;i<sSSID_length;i++){
				if(test.toLowerCase(Locale.getDefault()).contains(supported_SSID[i])){
					result=true;
					break;
				}
			}
			if(test.toLowerCase(Locale.getDefault()).contains("fon") && test.toLowerCase(Locale.getDefault()).contains("free_internet"))
//			if(test.toLowerCase(Locale.getDefault()).contains("fon"))	
			result=true;
				
			return result;
		}

		//********************************
		//NOTIFICATION FUNCTION
		//********************************
		
		private boolean notifyUser(int id){
			try{
		    	final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		    	final Notification notification = new Notification(R.drawable.logowiflow_24x24, "WiFlow", System.currentTimeMillis());

		    	if(settings.getBoolean("vibrate_setting", false))
		    		notification.vibrate= new long[]{100,200,300};
		    	if(settings.getBoolean("ring_tone_setting", false))
		    		notification.sound= Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");

		    	CharSequence contentTitle = "";
				CharSequence contentText = "";

		    	switch(id){
		
			    	//Service Stopped		
			    	case 1:	contentTitle = "Stopped Service";
			    			contentText = "WiFlow has been stopped either by you or Android OS due to inactivity";
			    			((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancel(3);
			    			break;
			    			
			    	//Router exchange to supported network	
			    	case 2:	contentTitle = "Wifi swap";
			    			contentText = "WiFlow changed your internet connection";
			    			break;
			    	
			    	//Service Started		
			    	case 3:	contentTitle = "Started Service";
			    			contentText = "WiFlow is now monitoring your connections";
			    			notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
			    			break;
			    			
					//Credentials failed to login on service FON
			    	case 4:	contentTitle = "Login failed";
			    			contentText = "Your credentials failed to login on FON";
			    			break;
			    			
			    	//Credentials failed to login on service PT-WIFI	
			    	case 5:	contentTitle = "Login failed";
			    			contentText = "Your credentials failed to login on PT-WIFI";
			    			break;
			    			
			    	//Credentials failed to login on service Telenet
			    	case 6:	contentTitle = "Login failed";
			    			contentText = "Your credentials failed to login on Telenet";
			    			break;
			    			
			    	//Credentials failed to login on service Boingo	
			    	case 7:	contentTitle = "Login failed";
			    			contentText = "Fon server timedout and didn't respond";
			    			break;
			    			
			    	//Login failed unknown reason
			    	case 8:	contentTitle = "Login failed";
			    			contentText = "An unexpected error, signal may be weak";
			    			break;
			    			
			    	default:break;
				}
		
		    	final Intent notificationIntent = new Intent(this, WiFlowActivity.class);
		    	final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		    	notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
		    	
		    	mNotificationManager.notify(id, notification);
		    	
			}
			catch(NullPointerException e){
				if(DEBUG)
					Log.e(LOG,e.getMessage());
				return false;
			}
			return true;
		}

		//******************************
		//CONNECTION FUNCTIONS
		//*****************************
		
		//Function to connect to Memorized Wifi
		private void enableKnownWifi(ScanResult target_wifi){
			try{
				//Searches the target wifi network in the known networks
				for (WifiConfiguration WC : wifi.getConfiguredNetworks()) {
						 //if the network is found in the known networks section and has not the same SSID that the currently connected one, connect
						 if(WC.SSID.replace("\"", "").equals(target_wifi.SSID) ){
							 
							if(DEBUG)
								Log.e(LOG,"PREPARE TO CONNECT TO: "+  WC.SSID);
										
							wifi.enableNetwork(WC.networkId, true);
							
							//Give time to enable the network
							Thread.sleep(1*500);
						 }
				}	
			}catch(InterruptedException e){ 
				if(DEBUG)
					Log.e(LOG, e.getMessage()); 
			}
		}
		
		//Function to create WifiConfig for non Memorized Wifi and enable it
		private static void firstEnableWifi(ScanResult target_wifi){
			
			try{
				//Config New Open Connections that are supported
				final WifiConfiguration conf = new WifiConfiguration();
				conf.SSID = "\"" + target_wifi.SSID + "\"";
				conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				wifi.saveConfiguration();
				wifi.enableNetwork(wifi.addNetwork(conf), true);
				//Give time to enable network
				Thread.sleep(1*500);
			}catch(InterruptedException e){ 
				if(DEBUG)
					Log.e(LOG, e.getMessage()); 
			}
		}
		
		//Connect to a given supported network
		public void connectSup(ScanResult target_wifi){
//			
			//Check the known wifi's list and find the matching SSID wifi
			if(wifiIsKnown(target_wifi))
				enableKnownWifi(target_wifi);
			//If it is the first time using this network, add it to the phone and connect
			else
				firstEnableWifi(target_wifi);
			
		}
		
		//Verify if connected
		private boolean isConnected(WifiInfo info){
            final ConnectivityManager conMgr =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            
            if (activeNetwork != null && activeNetwork.isConnected() && activeNetwork.isAvailable() && 
            		info.getSupplicantState().toString() != "SCANNING" && info.getSSID() != null && info.getIpAddress()!=0)
            	return true;
            else 
            	return false;  
		}
		
		public void loginSupported(String SSID){
			
			final WifiInfo wifiInfo = wifi.getConnectionInfo();
			/** TODO
			 * Para adicionar os novos servicos
			 * 	
			 * SSID Belgium: Fon-belgacom, login same as PT apparently
			 * SSID UK: BTFON / BTOpezone-H / BTWiFi-with-FON, need to choose Fon account before entering user and pass
			 * SSID France: SFR WiFi FON / Neuf WiFi FON, login same as PT apparently
			 * SSID Poland: FON_NETIA_FREE_INTERNET, need to choose "Others" as Operator
			 * SSID Portugal: FON_ZON_FREE_INTERNET
			 * */
			
			final String redirectedURL = getCurrentHostURL("http://www.apple.com/");
			if((SSID.contains("FON") || SSID.contains("BTOpenzone-") || SSID.contains("Fon-")) && isConnected(wifiInfo)){
				if(DEBUG)
					Log.e(LOG,"ENTREI NO FON LOGIN SUPPORTED");

				try {
					Thread.sleep(1*500);
				} catch(InterruptedException e){ 
					if(DEBUG)
						Log.e(LOG, e.getMessage()); 
				}
				
//				  if(WiFlowActivity.country.toLowerCase().equals("gn") || ) //general PT, Belgium, France...
//				      connectFON_GN(httpdata[1]);
//				  else if(WiFlowActivity.country.toLowerCase().equals("pl"))
//				      connectFON_PL(httpdata[1]);
//				  else if(WiFlowActivity.country.equals("UK"))
//				      connectFON_UK(httpdata[1]);
				
				 if(SSID.toLowerCase(Locale.getDefault()).contains("netia_free_internet")) 
					try {	
						if(DEBUG)
							Log.e(LOG,"ENTREI NO NETIA FREE INTERNET LOGIN SUPPORTED");
						connectFON_PL(redirectedURL);
					}catch(Exception e){
						if(DEBUG)
							e.printStackTrace();
					}
				else if(SSID.toLowerCase(Locale.getDefault()).contains("fon_belgacom"))
					try{
						if(DEBUG)
							Log.e(LOG,"ENTREI NO FON BELGACOM LOGIN SUPPORTED");
						connectFON_BC(redirectedURL);
					}catch(Exception e){
						if(DEBUG)
							e.printStackTrace();
					}
				
				else if(SSID.toLowerCase(Locale.getDefault()).contains("zon_free"))
					try {
						if(DEBUG)
							Log.e(LOG,"ENTREI NO ZON FON LOGIN SUPPORTED");
						connectFON_GN(redirectedURL);//ZON FON PT
					}catch(Exception e){
						if(DEBUG)
							e.printStackTrace();
					}
			}else if(SSID.toLowerCase(Locale.getDefault()).equals("pt-wifi") && isConnected(wifiInfo)){
				if(DEBUG)
					Log.e(LOG,"ENTREI NO PTWIFI LOGIN SUPPORTED");
				//Get redirected URL
				try {
					connectPTWIFI(redirectedURL);
				}catch(Exception e){
					if(DEBUG)
						e.printStackTrace();
				}
			}else if((SSID.toLowerCase(Locale.getDefault()).contains("telenethomespot") || SSID.toLowerCase(Locale.getDefault()).contains("telenethotspot")) && isConnected(wifiInfo)){
				if(DEBUG)
					Log.e(LOG,"ENTREI NO TELENET LOGIN SUPPORTED");
				try {
					connect_Telenet(redirectedURL);
				} catch(Exception e){
					if(DEBUG)
						e.printStackTrace();
				}
			}
		}
		
		//Get the current URL+Host from a HTTPGET
		public static String getCurrentHostURL(String start_url){
			String result="";
			final HttpClient httpClient = new DefaultHttpClient(); 
			httpClient.getParams().setParameter("http.protocol.allow-circular-redirects", true);
			final HttpGet httpget = new HttpGet(start_url);
			
			try {
		        final HttpContext context = new BasicHttpContext(); 
		        final HttpResponse response = httpClient.execute(httpget, context);
		        //Boingo Detection
		        final String responseBody = EntityUtils.toString(response.getEntity());
		        if(responseBody.toUpperCase().contains("BOINGO"))
		        	return "boingo";
		        
		        final HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
		        final HttpHost currentHost = (HttpHost)  context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		        
		        final String currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI());
		        
		        if(DEBUG)
		        	Log.e(LOG, "CURRENT URL -> " + currentUrl);
      
		        result = currentUrl;
			} catch (ClientProtocolException e) {
			    if(DEBUG)
			    	Log.e(LOG, e.getMessage());
			} catch (IOException e) {
				if(DEBUG)
					Log.e(LOG, e.getMessage());
			}
			return result;
		}
		
		
		
		//Check if the connection is redirecting you to zon or pt wifi login pages
				public boolean isOnline() {
					
					if(isConnected(wifi.getConnectionInfo())){
						return getCurrentHostURL("http://www.apple.com/").equals("http://www.apple.com/");
				        
					}else
			        	return false;	
				}

				public class MyHttpClient extends DefaultHttpClient {
					 
				    final Context context;
				 
				    public MyHttpClient(Context context) {
				        this.context = context;
				    }
				 
				    @Override
				    protected ClientConnectionManager createClientConnectionManager() {
				        SchemeRegistry registry = new SchemeRegistry();
				        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
				        // Register for port 443 our SSLSocketFactory with our keystore
				        // to the ConnectionManager
				        registry.register(new Scheme("https", newSslSocketFactory(), 443));
				        return new SingleClientConnManager(getParams(), registry);
				    }
				 
				    private SSLSocketFactory newSslSocketFactory() {
				        try {
				            // Get an instance of the Bouncy Castle KeyStore format
				            KeyStore trusted = KeyStore.getInstance("BKS");

				            SSLSocketFactory sf = new SSLSocketFactory(trusted);
				            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
				            return sf;
				        } catch (Exception e) {
				            throw new AssertionError(e);
				        }
				    }
				}
				
				
				public boolean connectFON_BC(String redirectedURL) {
					
					boolean result=false;
					final String user_saved = settings.getString("configure1_user", "");
				    final String pass_saved = settings.getString("configure1_pass", "");
				    
				    //Get Notification Preferences Status
		            final boolean enable_notification = settings.getBoolean("notification_setting", false);
				    
		            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

		            DefaultHttpClient client = new DefaultHttpClient();
		            //Trust Self signed SSL
		            SchemeRegistry registry = new SchemeRegistry();
		            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		            registry.register(new Scheme("https", socketFactory, 443));
		            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
		            DefaultHttpClient httpclient = new DefaultHttpClient(mgr, client.getParams());

		            // Set verifier     
		            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		            if(DEBUG)
		            	Log.e("NOTIFICATION","CONNECT FON "+enable_notification);
		            
				    if(!user_saved.equals("") || !pass_saved.equals("")){

				    	//Needed to handle the 2 redirections verified in FON ZON
				    	httpclient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);
				    
				    	//Username and Password formatting
				    	final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);   
					    nameValuePairs.add(new BasicNameValuePair("login[user]", user_saved));
					    nameValuePairs.add(new BasicNameValuePair("login[pass]", pass_saved));
					    nameValuePairs.add(new BasicNameValuePair("commit", "Login"));
					    
					    //Redirections Treatment
					    try {
					    	// Create a local instance of cookie store
					        CookieStore cookieStore = new BasicCookieStore();

					        // Create local HTTP context
					        HttpContext localContext = new BasicHttpContext();
					        // Bind custom cookie store to the local context
					        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

					        HttpGet httpget = new HttpGet("http://www.apple.com"); 

					        // Pass local context as a parameter
					        HttpResponse response_init = client.execute(httpget, localContext);
					        Thread.sleep(1*1000);
//						
//					    	String etc=EntityUtils.toString(response_init.getEntity());
//					    	writeMem(response_init.getStatusLine().getStatusCode()+"\n CARALHO DO GET 2"+etc);
//					    
//					    	HttpUriRequest currentReq = (HttpUriRequest) localContext.getAttribute( 
//					                ExecutionContext.HTTP_REQUEST);
//					        HttpHost currentHost = (HttpHost)  localContext.getAttribute( 
//					                ExecutionContext.HTTP_TARGET_HOST);
//					        String currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI());
//					        
//					        writeMem("CURRENT URL "+currentUrl+" comparacao "+redirectedURL);

					        String next_url="https://belgacom.portal.fon.com/en/login/processLogin";
					    	HttpPost httppost1 = new HttpPost(next_url);
				        	httppost1.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				     
				        	HttpResponse response= httpclient.execute(httppost1,localContext);
			            	final String responseBody = EntityUtils.toString(response.getEntity());
			            	writeMem("FIRST POST ANSWER FONBC: " + responseBody+ "\n\npostedin "+next_url+ " \n\ncode"+response.getStatusLine().getStatusCode());
				        	
				        	if(responseBody.contains("url=")){ //Everything according to plan
				        		next_url=responseBody.substring(responseBody.indexOf("url=")+4,responseBody.indexOf("\"/>"));
			        			httpget = new HttpGet(next_url.replace("amp;", "")); 
			        			writeMem("POSTING FINAL "+next_url.replace("amp;", ""));
				        		response_init = httpclient.execute(httpget, localContext);
				        		writeMem("LAST POST ANSWER FONBC: " + EntityUtils.toString(response_init.getEntity())+ "\n\npostedin "+next_url+ " \n\ncode"+response_init.getStatusLine().getStatusCode());
				        	}
				        	else{ //Something failed should verify what but will return false
				        		notifyUser(8);//Something failed 
				        		return false;
				        	}
				        		
				        	return true;
				        } catch (ClientProtocolException e) {
				        	if(DEBUG)
				        		e.printStackTrace();
				        } catch (IOException e) {
				        	if(e.toString().contains("NoHttpResponseException"))
				        		notifyUser(7);//No response
				        	else
				        		if(DEBUG)
				        			e.printStackTrace();
				        } catch (Exception e){
				        	if(DEBUG){
					        	Log.e(LOG, e.getMessage()+ " GENERAL EXCEPTION");
								e.printStackTrace();
				        	}
							
				        }
				    }else{
				    	if(DEBUG)
				    		Log.e(LOG,"MISSING USERNAME OR PASSWORD FON");
		                if(enable_notification)
		                	notifyUser(4);
				    }
					return result;
				}
				
				public boolean connectFON_GN(String first_url) {
					
					boolean result=false;
					final String user_saved = settings.getString("configure1_user", "");
				    final String pass_saved = settings.getString("configure1_pass", "");
				    
				    //Get Notification Preferences Status
		            final boolean enable_notification = settings.getBoolean("notification_setting", false);
				    
		            if(DEBUG)
		            	Log.e("NOTIFICATION","CONNECT FON "+enable_notification);
		            
				    if(!user_saved.equals("") || !pass_saved.equals("")){
				    	
				    	final HttpClient httpclient = new DefaultHttpClient();
				    	//Needed to handle the 2 redirections verified in FON ZON
				    	httpclient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);
				    	
				    	//Username and Password formatting
				    	final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);   
				    	//New ZON src code
					    nameValuePairs.add(new BasicNameValuePair("USERNAME", user_saved));
					    nameValuePairs.add(new BasicNameValuePair("PASSWORD", pass_saved));
					    
					    if(DEBUG)
					    	Log.e(LOG,"USER E PASS: " + nameValuePairs.toString());
					    
					    try {
					        //First post to the URL we have when we first try to access a website and are redirected to FON website
					        final HttpPost httppost1 = new HttpPost("http://www.apple.com");
					        
					        if(DEBUG)
					        	Log.e(LOG,"URL: " + first_url); 	
				        	
					        final HttpResponse response1 = httpclient.execute(httppost1);	
					        final String responseBody1 = EntityUtils.toString(response1.getEntity());
					        
					        if(DEBUG)
					        	Log.e(LOG,"FIRST POST ANSWER: " + responseBody1);
					        
					        String second_url = responseBody1.substring(responseBody1.indexOf("<div class=\"login\""), responseBody1.indexOf("<label for=\"text\">Username</label>"));
					        second_url = second_url.substring(second_url.indexOf("https://"), second_url.indexOf("tab=2")+5);
					        
					        if(DEBUG)
					        	Log.e(LOG,"SECOND URL: " + second_url);
					        
					        final HttpPost httppost2 = new HttpPost(second_url);
					        httppost2.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					        final HttpResponse response2 = httpclient.execute(httppost2);	
					        final String responseBody2 = EntityUtils.toString(response2.getEntity());
					        
					        if(DEBUG)
					        	Log.e(LOG,"SECOND RESPONSE: " + responseBody2);
					        
					        //Notification for wrong User or Login
					        if(!responseBody2.contains("You're connected!")){
					        	if(DEBUG)
					        		Log.e(LOG,"FALHOU LOGIN FON");
					        	
					        	//Authentication fail notification
				                if(enable_notification)
				                	notifyUser(4);
					        }
					        	
				            result=true;
				        } catch (ClientProtocolException e) {
				        	if(DEBUG)
				        		Log.e(LOG, e.getMessage());
				        } catch (IOException e) {
				        	if(e.toString().contains("NoHttpResponseException"))
				        		notifyUser(7);//No response
				        	else
				        		if(DEBUG)
				        			e.printStackTrace();
				        } catch (Exception e){
				        	if(DEBUG){
								Log.e(LOG, e.getMessage()+ " GENERAL EXCEPTION");
								e.printStackTrace();
				        	}
				        	
				        }
				    }else{
				    	if(DEBUG)
				    		Log.e(LOG,"MISSING USERNAME OR PASSWORD FON");
				    	 //Authentication fail notification
		                if(enable_notification)
		                	notifyUser(4);
				    }
				    
					return result;
				}
				

				public boolean connectFON_PL(String first_url) {
					
					boolean result=false;
					final String user_saved = settings.getString("configure1_user", "");
				    final String pass_saved = settings.getString("configure1_pass", "");
				    final String type_saved = settings.getString("configure1_type", "gn");
		            final boolean enable_notification = settings.getBoolean("notification_setting", false);
				     
				    if(!user_saved.equals("") || !pass_saved.equals("")){
				    	
				    	final HttpClient httpclient = new DefaultHttpClient();
				    	//Needed to handle the 2 redirections verified in FON ZON
				    	httpclient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);

				    	//Username and Password formatting
				    	final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);   
					    nameValuePairs.add(new BasicNameValuePair("login_email", user_saved));
					    nameValuePairs.add(new BasicNameValuePair("login_pass", pass_saved));
					    
					    if(type_saved.equals("pl"))
					    	nameValuePairs.add(new BasicNameValuePair("login_provider", "netia"));
					    else
					    	nameValuePairs.add(new BasicNameValuePair("login_provider", "netia_others"));
					    
					    //Redirections Treatment
					    try {
					        //First post to the URL we have when we first try to access a website and are redirected to FON website
					    	final HttpPost httppost1 = new HttpPost(first_url);
				        	httppost1.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				        	httpclient.execute(httppost1);
				        	final HttpResponse response1 = httpclient.execute(httppost1);	
				        	final String responseBody1 = EntityUtils.toString(response1.getEntity());
				        	if(DEBUG)
				        		Log.e(LOG,"FIRST POST ANSWER: " + responseBody1);
				        	
				        	//Second post in order to submit username and password to FON gateway and get encrypted data
				        	final HttpPost httppost2 = new HttpPost("https://www.fon.com/login/gateway/processLoginCustom");
					        httppost2.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					        final HttpResponse response = httpclient.execute(httppost2);	
					        final String responseBody = EntityUtils.toString(response.getEntity());
					        if(DEBUG)
					        	Log.e(LOG,"SECOND POST ANSWER: " + responseBody);

					        //Notification for wrong User or Login
					        if(responseBody.contains("<!DOCTYPE")){
					        	if(DEBUG)
					        		Log.e(LOG,"FALHOU LOGIN FON");
					        	 //Authentication fail notification
				                if(enable_notification)
				                	notifyUser(4);
				                
					        }else{
					        	String third_post_url ="";
					        	if(DEBUG)
					        		Log.e(LOG,"RESPONSE LOCATION: "+response.getHeaders("LOCATION")[0].toString());
					            //Build the url for the third post based on the redirection obtain in response to the second post
					        	if(response.getHeaders("LOCATION")[0].toString().contains("192.168."))
					        		third_post_url = response.getHeaders("LOCATION")[0].toString().replace("Location: ", "");
					        	else
					        		third_post_url= response.getHeaders("LOCATION")[0].toString().replace("Location: http://:/", "http://192.168.3.1/");
					        	
					        	if(DEBUG)
					        		Log.e(LOG,"THIRD POST URL: " + third_post_url);
					        	
					            //Third Post to make the authentication with the encrypted credentials
					        	final HttpPost httppost3 = new HttpPost(third_post_url);
					        	final HttpResponse response2 = httpclient.execute(httppost3);
					        	if(DEBUG){
					        		final String responseBody2 = EntityUtils.toString(response2.getEntity());
					            	Log.e(LOG,"RESPOSTA DO ULTIMO POST: " + responseBody2);
					        	}
					        }
				            result=true;
				        } catch (ClientProtocolException e) {
				        	if(DEBUG)
				        		Log.e(LOG, e.getMessage());
				        } catch (IOException e) {
				        	if(e.toString().contains("NoHttpResponseException"))
				        		notifyUser(7);//No response
				        	else
				        		if(DEBUG)
				        			e.printStackTrace();
				        } catch (Exception e){
				        	if(DEBUG){
					        	Log.e(LOG, e.getMessage()+ " GENERAL EXCEPTION");
								e.printStackTrace();
				        	}
				        }
					    
					    finally{

				        }
				        
				    }else{
				    	if(DEBUG)
				    		Log.e(LOG,"MISSING USERNAME OR PASSWORD FON");
				    	 //Authentication fail notification
		                if(enable_notification)
		                	notifyUser(4);
				    }
				    
					return result;
				}

				public boolean connectPTWIFI(String first_url){
			
					boolean result=false;
					final String user_saved = settings.getString("configure2_user", "");
				    final String pass_saved = settings.getString("configure2_pass", "");
		            final boolean enable_notification = settings.getBoolean("notification_setting", false);
				    
				    if(!user_saved.equals("") && !pass_saved.equals("")){
						final HttpClient httpclient = new DefaultHttpClient();
						final HttpPost httppost = new HttpPost("https://hotspot.ptwifi.pt/login?lg=pt"); 
					    // Add your data   
						final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);   
					    nameValuePairs.add(new BasicNameValuePair("username", user_saved));
					    nameValuePairs.add(new BasicNameValuePair("password", pass_saved));
					    try {
					    	
					        	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					        
					        	final HttpResponse response = httpclient.execute(httppost);
					        	final String responseBody = EntityUtils.toString(response.getEntity());
					            if(DEBUG)
					            	Log.e(LOG,"response " + responseBody);
				                
				                //Notification for wrong User or Login
						        if(!responseBody.toLowerCase(Locale.getDefault()).contains("authentication success")){
						        	if(DEBUG)
						        		Log.e(LOG,"FALHOU LOGIN PT-WIFI");
						        	
						        	//Authentication fail notification
					                if(enable_notification)
					                	notifyUser(5);
					                
						        }else if(responseBody.toLowerCase(Locale.getDefault()).contains("authentication success"))
						        	result=true;        
					            
					        } catch (ClientProtocolException e) {
					        	if(DEBUG)
					        		Log.e(LOG, e.getMessage());
					        } catch (IOException e) {
					        	if(e.toString().contains("NoHttpResponseException"))
					        		notifyUser(7);//No response
					        	else
					        		e.printStackTrace();
					    	}
					    	catch (Exception e){
								Log.e(LOG, e.getMessage()+ " GENERAL EXCEPTION");
								e.printStackTrace();
				        	
					    	}
				    }else{
				    	if(DEBUG)
				    		Log.e(LOG,"MISSING USERNAME OR PASSWORD PT-WIFI"); 
				    	
				    	notifyUser(5);
				    }
								    
				    return result;
				}
				
				private boolean connect_Telenet(String redirectedURL) {
					boolean result=false;
					final String user_saved = settings.getString("configure3_user", "");
				    final String pass_saved = settings.getString("configure3_pass", "");
				    
				    //Get Notification Preferences Status
		            final boolean enable_notification = settings.getBoolean("notification_setting", false);
				    
		            if(DEBUG)
		            	Log.e("NOTIFICATION","CONNECT TELENET "+enable_notification);
		            
				    if(!user_saved.equals("") || !pass_saved.equals("")){
				    	
				    	final HttpClient httpclient = new DefaultHttpClient();
				    	//Needed to handle the 2 redirections verified in FON ZON
				    	httpclient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);
//				    	httpclient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
				    	HttpProtocolParams.setUseExpectContinue(httpclient.getParams(), false);
				    	//Username and Password formatting
				    	final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);   
					    nameValuePairs.add(new BasicNameValuePair("userName", user_saved));
					    nameValuePairs.add(new BasicNameValuePair("passWord", pass_saved));
					    nameValuePairs.add(new BasicNameValuePair("terms", "on"));
					    
					    //Redirections Treatment
					    try {
					        //First post to the URL we have when we first try to access a website and are redirected to FON website
					        final HttpPost httppost1 = new HttpPost(redirectedURL);
				        	httppost1.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				        	httppost1.addHeader("Referer", redirectedURL);
				        	httppost1.addHeader("Content-Type","application/x-www-form-urlencoded");
				        	httppost1.addHeader("Connection","keep-alive");
				        	
				        	
				        	ResponseHandler<String> responseHandler = new BasicResponseHandler();
				        	String responseBody = httpclient.execute(httppost1, responseHandler);
				        	writeMem("FIRST POST ANSWER TELENET: " + responseBody);
				        	
				        	//String to post again
				        	String next_url="https://"+redirectedURL.split("/")[2]+"/portal/login.html";
				        	
				            if(responseBody.contains("<MessageType>100")){
				            	System.out.println("REDIRECTED!! "+redirectedURL);
				            	final HttpPost httppost2 = new HttpPost(next_url);
				            	httppost2.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				            	HttpResponse response= httpclient.execute(httppost2);
				            	final String responseBody1 = EntityUtils.toString(response.getEntity());
				            	writeMem("SECOND POST ANSWER TELENET: " + responseBody1);
				            }

				            result=true;
				        } catch (ClientProtocolException e) {
				        	if(DEBUG)
				        		Log.e(LOG, e.getMessage());
				        } catch (IOException e) {
				        	if(e.toString().contains("NoHttpResponseException"))
				        		notifyUser(7);//No response
				        	else
				        		if(DEBUG)
				        			e.printStackTrace();
				        } catch (Exception e){
				        	if(DEBUG){
					        	Log.e(LOG, e.getMessage()+ " GENERAL EXCEPTION");
								e.printStackTrace();
				        	}
				        }
				    }else{
				    	if(DEBUG)
				    		Log.e(LOG,"MISSING USERNAME OR PASSWORD FON");
				    	 //Authentication fail notification
		                if(enable_notification)
		                	notifyUser(6);
				    }
				    
					return result;
					
				}
							
				public boolean isSdReadable() {

				    boolean mExternalStorageAvailable = false;
				    try {
				        String state = Environment.getExternalStorageState();

				        if (Environment.MEDIA_MOUNTED.equals(state)) {
				            // We can read and write the media
				            mExternalStorageAvailable = true;
				            Log.i("isSdReadable", "External storage card is readable.");
				        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				            // We can only read the media
				            Log.i("isSdReadable", "External storage card is readable.");
				            mExternalStorageAvailable = true;
				        } else {
				            // Something else is wrong. It may be one of many other
				            // states, but all we need to know is we can neither read nor
				            // write
				            mExternalStorageAvailable = false;
				        }
				    } catch (Exception ex) {

				    }
				    return mExternalStorageAvailable;
				}
				
				private static void writeMem(String in){
			
			            try{
//			                Log.d(LOG, input + " threadnum "+ threadnum);
			                final File rootsd = Environment.getExternalStorageDirectory();
			                final File storage = new File(rootsd.getAbsolutePath() + "/wiflow.txt");
			                final FileWriter filewriter = new FileWriter(storage,true);
			                final BufferedWriter out = new BufferedWriter(filewriter);
			                final java.sql.Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			                out.write(timestamp.toString() + " ::: ");
			                out.write(in);
			                out.write("\n************************\n");
			                out.flush();
			                out.close();
			            }
			            catch(IOException e){
			                    Log.e(LOG,"Error writing "+e.getMessage());
			            }
			            return;
			        
				}
				
				
				@Override
		        public void onDestroy() 
		        {
		        	super.onDestroy();
		        }

				
		}