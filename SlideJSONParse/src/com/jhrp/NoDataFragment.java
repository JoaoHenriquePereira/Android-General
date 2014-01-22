package com.jhrp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jhrp.R;

/**
 *	JHP 08/08/2013
 *	When API server is unreachable this fragment warns the user about it.
 */

public class NoDataFragment extends Fragment {

  	public static final String TAG = NoDataFragment.class.getSimpleName();
  	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
 
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.nodata_fragment, container, false);
        return v;
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}