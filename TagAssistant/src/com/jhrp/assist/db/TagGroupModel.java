package com.jhrp.assist.db;

/**
 * The TagGroup Model that navigates the sets db
 * 
 * @author João Pereira
 * @e-mail joaohrpereira@gmail.com
 */

public class TagGroupModel {
	
	private long id;
	private String tagName;
	private byte[] tagColor;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public byte[] getTagColor() {
		return tagColor;
	}
	public void setTagColor(byte[] tagColor) {
		this.tagColor = tagColor;
	}
}
