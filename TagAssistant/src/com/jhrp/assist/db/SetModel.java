package com.jhrp.assist.db;

/**
 * The Set Model which gets the db rows
 * 
 * @author João Pereira
 * @e-mail joaohrpereira@gmail.com
 */

public class SetModel {
	private long id;
	private String setName;
	private int setTagGroupId; 
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSetName() {
		return setName;
	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	public int getSetTagGroupId() {
		return setTagGroupId;
	}

	public void setSetTagGroupId(int setTagGroupId) {
		this.setTagGroupId = setTagGroupId;
	}
} 