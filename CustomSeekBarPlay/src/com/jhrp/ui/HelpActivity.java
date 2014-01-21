package com.jhrp.ui;

import com.jhrp.ui.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class HelpActivity extends Activity{

	private static ImageView backViewButton;
  	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_fragment);
        

        backViewButton = (ImageView) findViewById(R.id.backViewButton);
        backViewButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent= new Intent(HelpActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            	finish(); 
            }
        });


    }
    
 
}
