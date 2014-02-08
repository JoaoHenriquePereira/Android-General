package com.jhrp.assist.object;

import org.opencv.core.Scalar;

/**
 * Object passed between Capture fragment and its' manager activity
 * 
 * @author João Pereira
 * @e-mail joaohrpereira@gmail.com
 */

public class CaptureBundle {
	
	private Scalar mBlobColorHsv;
    private Scalar mBlobColorRgba;
    
	public Scalar getmBlobColorHsv() {
		return mBlobColorHsv;
	}
	public void setmBlobColorHsv(Scalar mBlobColorHsv) {
		this.mBlobColorHsv = mBlobColorHsv;
	}
	public Scalar getmBlobColorRgba() {
		return mBlobColorRgba;
	}
	public void setmBlobColorRgba(Scalar mBlobColorRgba) {
		this.mBlobColorRgba = mBlobColorRgba;
	}
}
