package com.jhrp.feed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.jhrp.BuildConfig;
import com.jhrp.FeedFragment;

/**
 *	JHP 08/08/2013
 *	This class handles the management of loading the feed and parsing JSON
 */

public class FeedManager {
    
	private static final String TAG="FeedManager";
	
	//Jackson stuff
	private static ObjectMapper objectMapper = null;
    private static JsonFactory jsonFactory = null;
    
    //Default params
  	private static int limit=10;
  	private static int offset=0;

  	//Shameless Hardcoded url for the API
  	private static final String endPoint="http://INSERTJSONAPIHERE";

  	//Other stuff
    private static FeedMeta feedMeta = null;

    //Running first time
    public boolean first=true;
    public boolean isLoading=false;
    public int lastOffset = 0;
    
    //Strings
    private static final String TAG_META="meta";
    private static final String TAG_FEED="feed";

    //Put some fire on
    public FeedManager() {
        objectMapper = new ObjectMapper();
        jsonFactory = new JsonFactory();
        feedMeta = new FeedMeta();
        
    }

    public void requestUpdate(Fragment fragment, int total) {
    	
    	//if we already have all do nothing
    	if(offset>=feedMeta.total && !first)
    		return;

    	if(lastOffset==total && !first)
    		return; //Job in progress;
    	else 
    		lastOffset=total;
    	
    	if(offset+limit>feedMeta.total && !first){//If the limit is too big make it fit
    		limit=feedMeta.total-offset;
    	}
    	
    	FeedManagerTask task = new FeedManagerTask(fragment);
	    task.execute();
    }
	
	private static String BuildURL(){
	    List<NameValuePair> params = new LinkedList<NameValuePair>();
	    params.add(new BasicNameValuePair("limit", String.valueOf(getLimit())));
	    params.add(new BasicNameValuePair("offset", String.valueOf(getOffset())));
	    params.add(new BasicNameValuePair("cachekey", feedMeta.cachekey));
	    String paramString = URLEncodedUtils.format(params, "utf-8");
	    return endPoint + "?" + paramString;
	}
	
	public FeedMeta getMeta(){
    	return feedMeta;
    }

	public static int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		FeedManager.limit = limit;
	}

	public static int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		FeedManager.offset = offset;
	}
	
	private class FeedManagerTask extends AsyncTask<Void, Void, JSONObject> {
		
		private Context context;
	
		public FeedManagerTask(Fragment fragment) {
	        context=fragment.getActivity();
	        dialog = new ProgressDialog(context);
	    }
		
	    private ProgressDialog dialog;
	    /** application context. */
	    @Override
	    protected void onPreExecute() {
	    	if(first){
		        this.dialog.setMessage("Fetching your first activities :)");
		        this.dialog.show();
	    	}
	    }
	    
		@Override
		protected JSONObject doInBackground(Void ... params) {
			
			JSONObject result=null;
			String temp=null;
			
			
			try {     

				HttpClient client = new DefaultHttpClient();
		        temp = BuildURL();
		        HttpGet httpGet = new HttpGet(temp);
		        
		        HttpResponse response = client.execute(httpGet);
		        temp = EntityUtils.toString(response.getEntity());
		        
		        result = new JSONObject(temp);
		        client=null;
		        return result;
		    } catch (ClientProtocolException e) {
		        e.printStackTrace();
		    } catch (Exception e) {
		    	if(e instanceof JSONException){
		    		if(BuildConfig.DEBUG)
		    			Log.e(TAG,temp);	
		    		result=null;//TODO If JSONException happens, separate String objects and try to save as many as possible
		    					//AKA Improve parser
		    	}
		    	if(e instanceof HttpHostConnectException){ //Unreachable or refused
		    		result=null;
		    	}
		        e.printStackTrace();
		    }
			
			isLoading=false;
		    return result;
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			ArrayList<FeedObject> temp= new ArrayList<FeedObject>();
        	
        	try {
        		temp = objectMapper.readValue(jsonFactory.createJsonParser(result.getString(TAG_FEED)), new TypeReference<List<FeedObject>>(){});
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException IGNORED){} //Connection is down

        	
        	FeedFragment.feedObjects.addAll(temp);
        	offset=FeedFragment.feedObjects.size();
        	if(BuildConfig.DEBUG)
        		Log.i(TAG,"offset "+offset+ " limit "+limit);
        	temp=null;
			
        	if(first){
        		
	        	try {
					feedMeta = objectMapper.readValue(jsonFactory.createJsonParser(result.getString(TAG_META)), FeedMeta.class);
				} catch (JsonParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JsonMappingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException IGNORED){} //Connection is down
	        	first=false;
			}
        	
        	FeedFragment.feedAdapter.notifyDataSetChanged();
        	FeedFragment.isLoading=isLoading;
        	if (dialog.isShowing()) {
                dialog.dismiss();    
        	}
		}
	}
	
}
