package com.jhrp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jhrp.R;

/**
 *	JHP 08/08/2013
 *	My presentation fragment.
 */

public class MyPageFragment extends Fragment {

  	public static final String TAG = MyPageFragment.class.getSimpleName();
  	
  	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
 
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.my_fragment, container, false);
    
        final String presentation=getResources().getString(R.string.presentation);
        
        TextView TV = (TextView)v.findViewById(R.id.my_tv); 
        Spannable spanString = new SpannableString(presentation);        
        spanString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), presentation.indexOf("Jo"), presentation.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        TV.setText(spanString);
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