package com.jhrp.ui;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.jhrp.ui.SeekArc;
import com.jhrp.ui.SeekArc.OnSeekArcChangeListener;
import com.jhrp.ui.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


public class CalendarFragment extends Fragment {

  	public static final String TAG="FeedFragment";

  	private SeekArc mSeekArc;
  	private TextView mSeekArcProgress;
  	private SeekArc mSeekArc2;
  	private TextView mSeekArcProgress2;
  	private SeekBar mMaxProgress;
  	
  	private FrameLayout circleContainer;
  	private RelativeLayout seekArcContainer2;
  	private ImageView flag;
  	
  	private SeekBar mRotation;

	
	private TextView calendarTag;
	private TextView monthTag;
	//Inner button
	private TextView top_tv;
	private TextView bot_tv;
	
	//Period and fertile globals
	private int mProgressFertileStart;
	private int mProgressFertileEnd;
	private int mProgressPeriodStart;
	private int mProgressPeriodEnd;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
		    	
		    	final View v = inflater.inflate(R.layout.calendar_fragment, container, false);

			mSeekArc = (SeekArc) v.findViewById(R.id.seekArc);
			mSeekArcProgress = (TextView) v.findViewById(R.id.seekArcProgress);
			mSeekArc2 = (SeekArc) v.findViewById(R.id.seekArc2);
			mSeekArcProgress2 = (TextView) v.findViewById(R.id.seekArcProgress2);
			top_tv = (TextView) v.findViewById(R.id.top_tv);
			bot_tv = (TextView) v.findViewById(R.id.bot_tv);
			
			//Hardcoded
			mSeekArcProgress2.setText("0");
			
			calendarTag = (TextView) v.findViewById(R.id.numbertag);
			monthTag = (TextView) v.findViewById(R.id.monthtag);
			
			circleContainer = (FrameLayout) v.findViewById(R.id.circleContainer);
			
			flag = (ImageView) v.findViewById(R.id.flag);
			
			Calendar calendar = Calendar.getInstance();
			final int day = calendar.get(Calendar.DAY_OF_MONTH); 
			
			SimpleDateFormat month_date = new SimpleDateFormat("MMM");
			String month_name = month_date.format(calendar.getTime());
			
			calendarTag.setText(Integer.toString(day));
			monthTag.setText(month_name);
			
			mSeekArc.setProgress(day);
			mSeekArcProgress.setText("Today");

			//Setup example period and fertile times
			mProgressFertileStart = 13;
			mProgressFertileEnd = 20;
			mProgressPeriodStart = 2;
			mProgressPeriodEnd = 6;
			
			mSeekArc.setProgressFertileStart(mProgressFertileStart);
			mSeekArc.setProgressFertileEnd(mProgressFertileEnd);
			mSeekArc.setProgressPeriodStart(mProgressPeriodStart);
			mSeekArc.setProgressPeriodEnd(mProgressPeriodEnd);
			mSeekArc.updateFertilePeriodProgress();

			mSeekArc2.setProgressFertileStart(mProgressFertileStart);
			mSeekArc2.setProgressFertileEnd(mProgressFertileEnd);
			mSeekArc2.setProgressPeriodStart(mProgressPeriodStart);
			mSeekArc2.setProgressPeriodEnd(mProgressPeriodEnd);
			mSeekArc2.updateFertilePeriodProgress();
			
			//Update central button
			updateCentralButton(day);
			//mSeekArc.setMarkProgress(day);

			mSeekArc.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekArc seekArc) {	
				}		
				@Override
				public void onStartTrackingTouch(SeekArc seekArc) {
				}
				
				@Override
				public void onProgressChanged(SeekArc seekArc, int progress,
						boolean fromUser) {
					
					progress++;
					if(progress==day)
						mSeekArcProgress.setText("Today");
					else
						mSeekArcProgress.setText(String.valueOf(progress));
					
					updateCentralButton(progress);
				}
			});
			
			circleContainer.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			
			flag.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			
			return v;
		
	}
	
	private void updateCentralButton(int day){
		if(inFertile(day)){
			circleContainer.setBackground(getResources().getDrawable(R.drawable.ic_round_button_fertile));
			
			int diff=mProgressFertileEnd-day;
			top_tv.setText("End of FP\n"+diff+" days");
			bot_tv.setText("Next FP\n15 Feb");
		}
		else if(inPeriod(day)){
			circleContainer.setBackground(getResources().getDrawable(R.drawable.ic_round_button_period));
			
			int diff=mProgressPeriodEnd-day;
			top_tv.setText("End of PMS\n"+diff+" days");
			bot_tv.setText("Next PMS\n1 Feb");
		}
		//Free clicking
		else{
			circleContainer.setBackground(getResources().getDrawable(R.drawable.ic_round_button));
			
			if(day < mProgressFertileStart && day > mProgressPeriodEnd ){
				int diff = mProgressFertileStart-day;
				top_tv.setText("FP in\n"+diff+" days");
				bot_tv.setText("Next FP\n15 Feb");
			}
			else{//Need to write other cases but cba at this moment
				top_tv.setText("etc in \n10 days");
				bot_tv.setText("Stuff\n happens");
			}
		}

	}
	
	private boolean inFertile(int day){
		return day >= mProgressFertileStart && day <= mProgressFertileEnd;
	
	}
	
	private boolean inPeriod(int day){
		return day >= mProgressPeriodStart && day <= mProgressPeriodEnd;
	}
	
}