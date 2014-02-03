package com.jhrp.assist.object;

/**
 * Object passed between the listview fragments and their manager activity
 * 
 * @author João Pereira
 * @e-mail joaohrpereira@gmail.com
 */

public class InterBundle {
	
	private int state;
	private int clickedItem;
	private String clickedItemName;
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getClickedItem() {
		return clickedItem;
	}
	public void setClickedItem(int clickedItem) {
		this.clickedItem = clickedItem;
	}
	public String getClickedItemName() {
		return clickedItemName;
	}
	public void setClickedItemName(String clickedItemName) {
		this.clickedItemName = clickedItemName;
	}
}
