package com.jhrp.assist.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * The TagGroup DAO that navigates the sets db for tag groups
 * 
 * @author João Pereira
 * @e-mail joaohrpereira@gmail.com
 */

public class TagGroupDAO {
	// Database fields
	private SQLiteDatabase database;
	private DBManager dbHelper;
	private String[] allColumns = { DBManager.COLUMN_TAGGROUP_ID, DBManager.COLUMN_TAGGROUP_SET_ID,
			DBManager.COLUMN_TAGNAME, DBManager.COLUMN_COLOR_RGB, DBManager.COLUMN_COLOR_HSV };

	public TagGroupDAO(Context context) {
		dbHelper = new DBManager(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public TagGroupModel createTagGroup(String tag_name, long set_taggroup_id, String rgb, String hsv) {
		ContentValues values = new ContentValues();
		values.put(DBManager.COLUMN_TAGNAME, tag_name);
		values.put(DBManager.COLUMN_TAGGROUP_SET_ID, set_taggroup_id);
		values.put(DBManager.COLUMN_COLOR_RGB, rgb);
		values.put(DBManager.COLUMN_COLOR_HSV, hsv);
		long insertId = database.insert(DBManager.TABLE_TAGGROUP, null,
	        values);
		Cursor cursor = database.query(DBManager.TABLE_TAGGROUP,
	        allColumns, DBManager.COLUMN_TAGGROUP_ID + " = " + insertId, null,
	        null, null, null);
		cursor.moveToFirst();
		TagGroupModel newSet = cursorToSet(cursor);
		cursor.close();
	    return newSet;
	}

	public void deleteTagGroup(TagGroupModel set) {
		long id = set.getId();
		System.out.println("Set deleted with id: " + id);
		database.delete(DBManager.TABLE_TAGGROUP, DBManager.COLUMN_SETS_ID
	        + " = " + id, null);
	}

	public List<TagGroupModel> getAllTagGroups() {
		List<TagGroupModel> tg = new ArrayList<TagGroupModel>();

	    Cursor cursor = database.query(DBManager.TABLE_TAGGROUP,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	TagGroupModel set = cursorToSet(cursor);
	    	tg.add(set);
	    	cursor.moveToNext();
	    }
		
	    // make sure to close the cursor
		cursor.close();
		return tg;
	}
	
	public long getLastTagGroupID() {
		//Shameful hardcode
		SQLiteStatement stmt;
		String query = "SELECT MAX("+ DBManager.COLUMN_TAGGROUP_SET_ID +") FROM "
				+ DBManager.TABLE_TAGGROUP;
			stmt = database
		            .compileStatement(query);
		return stmt.simpleQueryForLong();
	}

	private TagGroupModel cursorToSet(Cursor cursor) {
		TagGroupModel set = new TagGroupModel();
		Log.e("crlh"," fds "+cursor.getLong(0));
		set.setId(cursor.getLong(0));
		set.setS_id(cursor.getLong(1));
		set.setTagName(cursor.getString(2));
		set.setRgba(cursor.getString(3));
		set.setHsv(cursor.getString(4));
		return set;
	}
}
