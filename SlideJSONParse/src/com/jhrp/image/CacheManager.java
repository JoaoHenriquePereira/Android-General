package com.jhrp.image;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jhrp.BuildConfig;

import android.graphics.Bitmap;
import android.util.Log;

/**
 *	JHP 08/08/2013
 *	CacheManager maps and controls current cache.
 */

public class CacheManager {
 
    private static final String TAG = "CacheManager";
    
    private Map<String, Bitmap> cache=Collections.synchronizedMap(
            new LinkedHashMap<String, Bitmap>(10,1.5f,true));//Last argument true for LRU ordering
    private long size=0;									
    private long limit=1000000;
 
    public CacheManager(){
        //use 25% of available memory
        setLimit(Runtime.getRuntime().maxMemory()/4);
    }
     
    public void setLimit(long new_limit){
        limit=new_limit;
        if(BuildConfig.DEBUG)
        	Log.i(TAG, "Cache will use up to "+limit/1024./1024.+"MB");
    }
 
    
    public Bitmap get(String id){
        try{
            if(!cache.containsKey(id))
                return null;
            return cache.get(id);
        }catch(NullPointerException ex){
            ex.printStackTrace();//NullPointerException TODO
            return null;
        }
    }
 
    public void put(String id, Bitmap bitmap){
        try{
            if(cache.containsKey(id))
                size-=getSizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            size+=getSizeInBytes(bitmap);
            checkSize();
        }catch(Throwable th){
            th.printStackTrace();
        }
    }
     
    private void checkSize() {
    	if(BuildConfig.DEBUG)
    		Log.i(TAG, "cache size="+size+" length="+cache.size());
        if(size>limit){
            Iterator<Entry<String, Bitmap>> iter=cache.entrySet().iterator();//least recently accessed item will be the first one iterated  
            while(iter.hasNext()){
                Entry<String, Bitmap> entry=iter.next();
                size-=getSizeInBytes(entry.getValue());
                iter.remove();
                if(size<=limit)
                    break;
            }
            if(BuildConfig.DEBUG)
            	Log.i(TAG, "Clean cache. New size "+cache.size());
        }
    }
 
    public void clear() {
        try{
            cache.clear();
            size=0;
        }catch(NullPointerException ex){
            ex.printStackTrace();//NullPointerException TODO
        }
    }
    
    public void close() { 
        cache=null;            
        size=0;
    }
 
    long getSizeInBytes(Bitmap bitmap) {
        if(bitmap==null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}