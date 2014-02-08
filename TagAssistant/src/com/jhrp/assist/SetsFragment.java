package com.jhrp.assist;

import java.util.ArrayList;
import java.util.List;

import com.jhrp.assist.db.SetDAO;
import com.jhrp.assist.db.SetModel;
import com.jhrp.assist.db.TagGroupDAO;
import com.jhrp.assist.object.InterBundle;
import com.jhrp.assist.ui.SwipeDismissListViewTouchListener;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.ListFragment;

/**
 * Sets Fragment displays saved sets
 * 
 * @author Jo�o Pereira
 * @e-mail joaohrpereira@gmail.com
 */

public class SetsFragment extends ListFragment {
	public static final String TAG = "SetsFragment";
	
	//Interfaces
	private OnDataPass mCallback;
	
	//List objects
	private ArrayAdapter<String> mAdapter;
	private List<SetModel> mSets;
	private List<String> items;
	
	private ViewGroup dismissableContainer;
	private Button okButton;
	private EditText newEdit;
	private TextView fragTitle;
	
	//DB objects
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
		fragTitle.setText(getResources().getString(R.string.heading_dismissable_sets));

        return v;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);

        dismissableContainer = (ViewGroup) getView().findViewById(R.id.dismissable_container);

        mSets = mSetDAO.getAllSets();
        /**
         * It might be useful to have the ids with the string in case you
         * have sets/tags with same name for some reason 
         */
        items = new ArrayList<String>();
        for (int i = 0; i < mSets.size(); i++) {
        	items.add(String.valueOf(mSets.get(i).getId()) + " " + mSets.get(i).getSetName());
        }
	    
        mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                new ArrayList<String>(items));
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
                                	mTagGroupDAO.deleteTagGroupBySetId(mSets.get(
                                			getItemDbID(mAdapter.getItem(position))).getSetTagGroupId());
                                	mSetDAO.deleteSet(mSets.get(
                                			getItemDbID(mAdapter.getItem(position))));
                             
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
        newItemButton.setText(getResources().getText(R.string.new_set));
        newItemButton.setBackground(getResources().getDrawable(R.drawable.new_item_selector));
        newItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	final Dialog dialog = new Dialog(getActivity());
		        dialog.setContentView(R.layout.new_dialog);
		        dialog.setTitle("New Set");
		        dialog.setCancelable(true);
		        //save button
		        
		        newEdit=(EditText)dialog.findViewById(R.id.newitem_edit);
		        newEdit.setHint(getResources().getString(R.string.new_set_hint));
		        
		        okButton = (Button) dialog.findViewById(R.id.ok_button);
		        okButton.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	updateList();
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

	private int getItemDbID(String in){
		int i;
		String[] s = in.split(" ");
    	for(i = 0; i < mSets.size(); i++){
    		SetModel t = mSets.get(i);
    		if(t.getId() == Integer.valueOf(s[0])){
    			break;
    		}
    	}
    	return i;
	}
	
	public interface OnDataPass {
	    public void onDataPass(InterBundle i);
	}

	private void updateList(){
		int t = mSetDAO.getLastTagGroupID();
		t++;
		SetModel insertedSet = mSetDAO.createSet(newEdit.getText().toString(), t);
        mSets = mSetDAO.getAllSets();
        /**
         * It might be useful to have the ids with the string in case you
         * have sets/tags with same name for some reason 
         */
        mAdapter.add(insertedSet.getId()+ " "+ insertedSet.getSetName());
        mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		InterBundle t = new InterBundle();
		t.setState(1);
		t.setClickedItem((int) id);
		t.setClickedItemTGId(mSets.get((int) id).getSetTagGroupId());
		t.setClickedItemName(mSets.get((int) id).getSetName());
		passData(t);
	}

	public void passData(InterBundle i) {
	    mCallback.onDataPass(i);
	}
	
	@Override
	public void onAttach(Activity a) {
	    super.onAttach(a);
	    mCallback = (OnDataPass) a;
	}
	
} 