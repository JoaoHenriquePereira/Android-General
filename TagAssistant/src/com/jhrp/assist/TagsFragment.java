package com.jhrp.assist;

import java.util.ArrayList;
import java.util.List;

import com.jhrp.assist.db.TagGroupDAO;
import com.jhrp.assist.db.TagGroupModel;
import com.jhrp.assist.ui.SwipeDismissListViewTouchListener;
import com.jhrp.assist.ui.TagAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
	private List<TagGroupModel> mTags;
	private List<String> items;
	
	private ViewGroup dismissableContainer;
	private Button okButton;
	private EditText newEdit;
	private TextView fragTitle;
	
	//DB Objects
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

        mTags = mTagGroupDAO.getAllTagGroupsByID(getArguments().getLong("settgid"));
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
                                	/*mTagGroupDAO.deleteTagGroup(mTags.get(
                                			mAdapter.getItem(position).getId()));*/
                                			
                                	mTagGroupDAO.deleteTagGroup(mTags.get((int)mAdapter.getItemId(position)));
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