package com.jhrp.assist.utils;

import org.opencv.core.Scalar;

/**
 * Helper class to convert Scalar to strings and vice-versa
 * 
 * @author João Pereira
 * @e-mail joaohrpereira@gmail.com
 */


public class Utils {

	public static String convertScalarToString(Scalar i){
		StringBuilder sb = new StringBuilder();
		sb.append(i.val[0]);
		sb.append(",");
		sb.append(i.val[1]);
		sb.append(",");
		sb.append(i.val[2]);
		sb.append(",");
		sb.append(i.val[3]);
		return sb.toString();
	}
	
	public static Scalar convertStringToScalar(String i){
		String[] t = i.split(",");
		Scalar r = new Scalar(Double.valueOf(t[0]),Double.valueOf(t[1]),Double.valueOf(t[2]),Double.valueOf(t[3]));
		return r;
	}
}
