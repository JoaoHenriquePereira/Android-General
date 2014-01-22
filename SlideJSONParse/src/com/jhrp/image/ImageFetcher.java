package com.jhrp.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import com.jhrp.R;
import android.os.AsyncTask;
 
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
 
/**
 *	JHP 08/08/2013
 *	This class handles the cache, if no image is in cache, calls web api.
 */

public class ImageFetcher {
     
	private static final String TAG="ImageFetcher";
	
    private CacheManager memoryCache=new CacheManager();
    private FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private static final int MESSAGE_CLEAR = 0;
    private static final int MESSAGE_CLOSE = 3;
    private boolean pauseWork=false; 
    
    public ImageFetcher(Context context){
        fileCache=new FileCache(context);
    }
     
    final int stub_id=R.drawable.user_default;
    public void DisplayImage(String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=null;
        try{//TODO Pause work on fling to better process the images
        	bitmap=memoryCache.get(url);
        } catch (Exception e){
        	e.printStackTrace();
        }
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
        {
//        	if(!pauseWork){
	            queuePhoto(url, imageView);
	            imageView.setImageResource(stub_id);
//        	}
        }
    }
         
    private void queuePhoto(String url, ImageView imageView)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        PhotosLoader task = new PhotosLoader(p);
	    task.execute();
    }
     
    private Bitmap getBitmap(String url) 
    {
        File f=fileCache.getFile(url);

        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
         
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable e){
           if(e instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }
 
    //Decoding image and scaling it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
     
    //Task
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
        }
    }
     
    class PhotosLoader extends AsyncTask<Void, Void, Void> {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
         
        @Override
		protected Void doInBackground(Void ... params) {
            if(imageViewReused(photoToLoad))
                return null;
            Bitmap bmp=getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad))
                return null;
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
            return null;
        }
    }
     
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    protected class CacheAsyncTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            switch ((Integer)params[0]) {
                case MESSAGE_CLEAR:
                    clearCacheInternal();
                    break;
                case MESSAGE_CLOSE:
                    closeCacheInternal();
                    break;
            }
            return null;
        }
    }
    

    protected void clearCacheInternal() {
        if (memoryCache != null) {
        	memoryCache.clear();
        	fileCache.clear();
        }
    }

    protected void closeCacheInternal() {
        if (memoryCache != null) {
        	memoryCache.close();
            memoryCache = null;
        }
    }
    
    public void clearCache() {
        new CacheAsyncTask().execute(MESSAGE_CLEAR);
    }

    public void closeCache() {
        new CacheAsyncTask().execute(MESSAGE_CLOSE);
    }
     
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }
    
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
            	int count=is.read(bytes, 0, buffer_size);
            	if(count==-1)
            		break;
            	os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

	public boolean isPauseWork() {
		return pauseWork;
	}

	public void setPauseWork(boolean pauseWork) {
		this.pauseWork = pauseWork;
	}
 
}