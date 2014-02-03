package com.jhrp.assist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jhrp.assist.SetsFragment.OnDataPass;
import com.jhrp.assist.db.SetDAO;
import com.jhrp.assist.db.SetModel;
import com.jhrp.assist.db.TagGroupDAO;
import com.jhrp.assist.db.TagGroupModel;
import com.jhrp.assist.object.InterBundle;
import com.jhrp.assist.ui.SwipeDismissListViewTouchListener;
import com.jhrp.assist.ui.TagAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.ListFragment;

/**
 * Tag fragment displays a set's tags
 * 
 * @author João Pereira
 * @e-mail joaohrpereira@gmail.com
 */

public class TagsFragment extends ListFragment {
	public static final String TAG = "TagsFragment";
	
	private OnDataPass mCallback;
	
	//List objects
	private TagAdapter mAdapter;
	private List<String> mList; 
	private List<TagGroupModel> mTags;
	private List<String> items;
	
	private ViewGroup dismissableContainer;
	private Button okButton;
	private EditText newEdit;
	private TextView fragTitle;
	
	//DB Objects
	private final SetDAO mSetDAO = ManageTagsActivity.getmSetDAO();
    private final TagGroupDAO mTagGroupDAO = ManageTagsActivity.getmTagGroupDAO();

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
		    	
		final View v = inflater.inflate(R.layout.listfragment_view, container, false);
		
		fragTitle = (TextView) v.findViewById(R.id.frag_title);
		fragTitle.setText(getArguments().getString("setname")+" | Tags");
		
        return v;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);

	    dismissableContainer = (ViewGroup) getView().findViewById(R.id.dismissable_container);

        mTags = mTagGroupDAO.getAllTagGroups();

        /**
         * It might be useful to have the ids with the string in case you
         * have sets/tags with same name for some reason 
         */
        items = new ArrayList<String>();
        for (int i = 0; i < mTags.size(); i++) {
        	items.add(mTags.get(i).getTagName());
        }
	    
        mAdapter = new TagAdapter(getActivity(),
                R.layout.tag_listitem_view,
                mTags);
        setListAdapter(mAdapter);
        
        ListView listView = getListView();
        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                /*	mTagGroupDAO.deleteTagGroup(mTags.get(
                                			getItemDbID(mAdapter.getItem(position))));*/
                                	mAdapter.remove(mAdapter.getItem(position));
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());
        
        final Button newItemButton = new Button(getActivity());
        newItemButton.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newItemButton.setText(getResources().getText(R.string.new_tag));
        newItemButton.setBackground(getResources().getDrawable(R.drawable.new_item_selector));
        newItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	final Dialog dialog = new Dialog(getActivity());
		        dialog.setContentView(R.layout.new_dialog);
		        dialog.setTitle("New Tag");
		        dialog.setCancelable(true);
		        //save button
		        
		        newEdit=(EditText)dialog.findViewById(R.id.newitem_edit);
		        newEdit.setHint(getResources().getString(R.string.new_tag_hint));
		        
		        okButton = (Button) dialog.findViewById(R.id.ok_button);
		        okButton.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	passData(newEdit.getText().toString());
		            	dialog.dismiss();
		            }
		        });
		        
		        try{
		        	dialog.show();
		        }
		        catch (Exception IGNORED){}
			}
        });
        
        dismissableContainer.addView(newItemButton);
        
        if(items.size() != 0){ //We have sets
        	/*for (int i = 0; i < mSets.size(); i++) {
                final Button dismissableButton = new Button(this);
                dismissableButton.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                dismissableButton.setText("Button " + (i + 1));
                dismissableButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ManageTagsActivity.this,
                                "Clicked " + ((Button) view).getText(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                // Create a generic swipe-to-dismiss touch listener.
                dismissableButton.setOnTouchListener(new SwipeDismissTouchListener(
                        dismissableButton,
                        null,
                        new SwipeDismissTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(Object token) {
                                return true;
                            }

                            @Override
                            public void onDismiss(View view, Object token) {
                                dismissableContainer.removeView(dismissableButton);
                            }
                        }));
                dismissableContainer.addView(dismissableButton);
            }
        	final Button newItemButton = new Button(this);
            newItemButton.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            newItemButton.setText(getResources().getText(R.string.new_set));
            newItemButton.setBackground(getResources().getDrawable(R.drawable.new_item_selector));
            newItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ManageTagsActivity.this,
                            "Clicked " + ((Button) view).getText(),
                            Toast.LENGTH_SHORT).show();
                }
            });
            
            dismissableContainer.addView(newItemButton);*/
        }
        
	}

	private int getItemDbID(String in){
		int i;
		String[] s = in.split(" ");
    	for(i = 0; i < mTags.size(); i++){
    		TagGroupModel t = mTags.get(i);
    		if(t.getId() == Integer.valueOf(s[0])){
    			break;
    		}
    	}
    	return i;
	}
	
	public interface OnDataPass {
	    public void onDataPass(String i);
	}

	@Override
	public void onResume(){
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		//TODO make it open tag group
	
	}

	public void passData(String i) {
	    mCallback.onDataPass(i);
	}
	
	@Override
	public void onAttach(Activity a) {
	    super.onAttach(a);
	    mCallback = (OnDataPass) a;
	}
	
} 