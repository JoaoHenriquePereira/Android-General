package com.jhrp.assist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * DBManager object to create and maintain DB
 * 
 * @author João Pereira
 * @e-mail joaohrpereira@gmail.com
 */

public class DBManager extends SQLiteOpenHelper {

	//Table Sets
	public static final String TABLE_SETS = "sets";
	public static final String COLUMN_SETS_ID = "_id";
	public static final String COLUMN_SETNAME = "name";
	public static final String COLUMN_SETTAGGROUPID = "tag_group_id";

	//Table TagGroup
	public static final String TABLE_TAGGROUP = "tag_group";
	public static final String COLUMN_TAGGROUP_ID = "_id";
	public static final String COLUMN_TAGNAME = "name";
	public static final String COLUMN_COLOR = "tag_color";
	
	private static final String DATABASE_NAME = "sets.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE_SETS = "create table "
			+ TABLE_SETS + "(" + COLUMN_SETS_ID
			+ " integer primary key autoincrement, " + COLUMN_SETNAME
			+ " text not null, " + COLUMN_SETTAGGROUPID
			+ " integer not null);" 
			+ " create table " + TABLE_TAGGROUP + "(" 
            + COLUMN_TAGGROUP_ID + " integer,"
            + COLUMN_TAGNAME + " text not null, "
            + COLUMN_COLOR + " blob);";

	private static final String DATABASE_CREATE_TAGGROUP = " create table " 
			+ TABLE_TAGGROUP + "(" 
            + COLUMN_TAGGROUP_ID + " integer,"
            + COLUMN_TAGNAME + " text not null, "
            + COLUMN_COLOR + " blob);";
	
	public DBManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}//http://answers.opencv.org/question/8873/best-way-to-store-a-mat-object-in-android/
  //http://www.vogella.com/tutorials/AndroidSQLite/article.html#databasetutorial
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_SETS);
		database.execSQL(DATABASE_CREATE_TAGGROUP);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DBManager.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGGROUP);
		onCreate(db);
	}
} 