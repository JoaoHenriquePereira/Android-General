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
	private long setTagGroupId; 
	
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

	public long getSetTagGroupId() {
		return setTagGroupId;
	}

	public void setSetTagGroupId(long setTagGroupId) {
		this.setTagGroupId = setTagGroupId;
	}
} 