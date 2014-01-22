package com.jhrp.image;

import java.io.File;
import java.net.URLEncoder;

import android.content.Context;
 
/**
 *	JHP 08/08/2013
 *	This class handles IO operations when storing the files.
 */

public class FileCache {
     
	private static final String TAG="FileCache";
	
    private File cacheDir;
    private static final String folderName="tunedin";
    
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),folderName);
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
     
    public File getFile(String url){
    	String filename=String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;
         
    }
     
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }
    
    public void close(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
        files=null;
        cacheDir=null;
    }
 
}