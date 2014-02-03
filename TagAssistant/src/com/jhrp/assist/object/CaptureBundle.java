package com.jhrp.assist.object;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import com.jhrp.assist.blob.ColorBlobDetector;

/**
 * Object passed between Capture fragment and its' manager activity
 * 
 * @author João Pereira
 * @e-mail joaohrpereira@gmail.com
 */

public class CaptureBundle {
	
	private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private ColorBlobDetector    mDetector;
    
	public Mat getmRgba() {
		return mRgba;
	}
	public void setmRgba(Mat mRgba) {
		this.mRgba = mRgba;
	}
	public Scalar getmBlobColorRgba() {
		return mBlobColorRgba;
	}
	public void setmBlobColorRgba(Scalar mBlobColorRgba) {
		this.mBlobColorRgba = mBlobColorRgba;
	}
	public ColorBlobDetector getmDetector() {
		return mDetector;
	}
	public void setmDetector(ColorBlobDetector mDetector) {
		this.mDetector = mDetector;
	}
}
