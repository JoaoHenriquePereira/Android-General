package com.jhrp.assist;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import com.jhrp.assist.blob.ColorBlobDetector;
import com.jhrp.assist.ui.MainView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Main activity starts immediately with the last set used (if first time run tutorial)
 * 
 * @author João Pereira
 * @e-mail joaohrpereira@gmail.com
 */

public class MainActivity extends Activity implements CvCameraViewListener2, OnTouchListener {
    private static final String TAG = "MainActivity";

    //OpenCV
    private MainView mOpenCvCameraView;
    private List<String> mFiltersList;
    private MenuItem[] mFiltersMenuItems;
    private SubMenu mFiltersMenu;

    private boolean              mIsColorSelected = true;
    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private ColorBlobDetector    mDetector;
    private Scalar               CONTOUR_COLOR;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.surface_view);

        mOpenCvCameraView = (MainView) findViewById(R.id.surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        CONTOUR_COLOR = new Scalar(255,0,0,255);
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	
    	mRgba = inputFrame.rgba();

        if (mIsColorSelected) {//TODO
        	/*mDetector.process(mRgba);
            List<MatOfPoint> contours = mDetector.getContours();
            Log.e(TAG, "Contours count: " + contours.size());
            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

            Mat colorLabel = mRgba.submat(4, 68, 4, 68);
            colorLabel.setTo(mBlobColorRgba);

            Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
            mSpectrum.copyTo(spectrumLabel);*/
            
        }

        return mRgba;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*List<String> effects = mOpenCvCameraView.getEffectList();

        if (effects == null) {
            Log.e(TAG, "Color effects are not supported by device!");
            return true;
        }

        mColorEffectsMenu = menu.addSubMenu("Color Effect");
        mEffectMenuItems = new MenuItem[effects.size()];

        int idx = 0;
        ListIterator<String> effectItr = effects.listIterator();
        while(effectItr.hasNext()) {
           String element = effectItr.next();
           mEffectMenuItems[idx] = mColorEffectsMenu.add(1, idx, Menu.NONE, element);
           idx++;
        }*/
    	menu.add("Manage Tags");
    	
    	int idx = 0;
        mFiltersMenu = menu.addSubMenu("Filters");
        mFiltersList = Arrays.asList("fds","1","2");//mOpenCvCameraView.getResolutionList();
        mFiltersMenuItems = new MenuItem[mFiltersList.size()];

        ListIterator<String> resolutionItr = mFiltersList.listIterator();
        idx = 0;
        while(resolutionItr.hasNext()) {
            String element = resolutionItr.next();
            mFiltersMenuItems[idx] = mFiltersMenu.add(2, idx, Menu.NONE, element);
                    //Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
            idx++;
        }
        
        
        //mFiltersMenu = menu.addSubMenu("Filters");
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0)
        {
            /*mOpenCvCameraView.setEffect((String) item.getTitle());
            Toast.makeText(this, mOpenCvCameraView.getEffect(), Toast.LENGTH_SHORT).show();*/
        	Intent intent = new Intent(this, ManageTagsActivity.class);
        	startActivity(intent);
        }
        else if (item.getGroupId() == 2)
        {
            /*int id = item.getItemId();
            Size resolution = mResolutionList.get(id);
            mOpenCvCameraView.setResolution(resolution);
            resolution = mOpenCvCameraView.getResolution();
            String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
            Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();*/
        }

        return true;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG,"onTouch event");
     /*   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = Environment.getExternalStorageDirectory().getPath() +
                               "/sample_picture_" + currentDateandTime + ".jpg";
        mOpenCvCameraView.takePicture(fileName);
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();*/
        return false;
    }
}
