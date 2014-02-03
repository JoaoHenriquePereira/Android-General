package com.jhrp.assist.db;

/**
 * The TagGroup Model that navigates the sets db
 * 
 * @author João Pereira
 * @e-mail joaohrpereira@gmail.com
 */

public class TagGroupModel {
	
	private long id;
	private long s_id;
	private String tagName;
	private String rgba;
	private String hsv;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getS_id() {
		return s_id;
	}
	public void setS_id(long s_id) {
		this.s_id = s_id;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getRgba() {
		return rgba;
	}
	public void setRgba(String rgb) {
		this.rgba = rgb;
	}
	public String getHsv() {
		return hsv;
	}
	public void setHsv(String hsv) {
		this.hsv = hsv;
	}
	
}
