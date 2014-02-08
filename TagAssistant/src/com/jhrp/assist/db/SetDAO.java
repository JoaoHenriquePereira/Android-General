package com.jhrp.assist.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * The Set DAO that navigates the sets db for sets
 * 
 * @author João Pereira
 * @e-mail joaohrpereira@gmail.com
 */

public class SetDAO {
	// Database fields
	private SQLiteDatabase database;
	private DBManager dbHelper;
	private String[] allColumns = { DBManager.COLUMN_SETS_ID,
			DBManager.COLUMN_SETNAME, DBManager.COLUMN_SETTAGGROUPID };

	public SetDAO(Context context) {
		dbHelper = new DBManager(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public SetModel createSet(String set_name, long set_taggroup_id) {
		ContentValues values = new ContentValues();
		values.put(DBManager.COLUMN_SETNAME, set_name);
		values.put(DBManager.COLUMN_SETTAGGROUPID, set_taggroup_id);
		long insertId = database.insert(DBManager.TABLE_SETS, null,
	        values);
		Cursor cursor = database.query(DBManager.TABLE_SETS,
	        allColumns, DBManager.COLUMN_SETS_ID + " = " + insertId, null,
	        null, null, null);
		cursor.moveToFirst();
		SetModel newSet = cursorToSet(cursor);
		cursor.close();
	    return newSet;
	}

	public void deleteSet(SetModel set) {
		long id = set.getId();
		System.out.println("Set deleted with id: " + id);
		database.delete(DBManager.TABLE_SETS, DBManager.COLUMN_SETS_ID
	        + " = " + id, null);
	}

	public int getLastTagGroupID() {
		//Shameful hardcode
		SQLiteStatement stmt;
		String query = "SELECT MAX("+ DBManager.COLUMN_SETTAGGROUPID +") FROM "
				+ DBManager.TABLE_SETS;
			stmt = database
		            .compileStatement(query);
		return (int)stmt.simpleQueryForLong();
	}
	
	public List<SetModel> getAllSets() {
		List<SetModel> sets = new ArrayList<SetModel>();

	    Cursor cursor = database.query(DBManager.TABLE_SETS,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	SetModel set = cursorToSet(cursor);
	    	sets.add(set);
	    	cursor.moveToNext();
	    }
		
	    // make sure to close the cursor
		cursor.close();
		return sets;
	}
	
	public List<SetModel> getAllSetsWithTags() {
		List<SetModel> sets = new ArrayList<SetModel>();

		final String q = "SELECT * FROM "+ DBManager.TABLE_SETS +" a " +
				"INNER JOIN "+DBManager.TABLE_TAGGROUP+" b " +
				"ON a."+DBManager.COLUMN_SETTAGGROUPID+"=b."+DBManager.COLUMN_TAGGROUP_SET_ID +
				" GROUP BY "+DBManager.COLUMN_SETNAME;

		Cursor cursor = database.rawQuery(q, null);
		
		cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	SetModel set = cursorToSet(cursor);
	    	sets.add(set);
	    	cursor.moveToNext();
	    }
		
	    // make sure to close the cursor
		cursor.close();
		
		return sets;
	}

	private SetModel cursorToSet(Cursor cursor) {
		SetModel set = new SetModel();
		set.setId(cursor.getLong(0));
		set.setSetName(cursor.getString(1));
		set.setSetTagGroupId(cursor.getInt(2));
		return set;
	}
}
