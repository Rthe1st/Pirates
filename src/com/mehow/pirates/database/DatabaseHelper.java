package com.mehow.pirates.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

abstract class DatabaseHelper extends SQLiteOpenHelper{
	
	public static final String TABLENAME = "defaultLevelsData";
	public static final String LEVELID = "level_id";
	public static final String LEVELNAME = "level_name";
	public static final String BESTSCORE = "best_score";
	public static final String BESTPLAYER = "best_player";
	public static final String MINELIMIT = "mine_limit";
	public static final String GOLDSCORE = "gold_score";
	public static final String SILVERSCORE = "silver_score";
	public static final String BRONZESCORE = "bronze_score";
	public static final String DIFFICULTY = "difficulty";
	
	Context context;
	
	//Sql database creation string
	//done like this so extension classes alwasy make these columns
	private static final String DATABASE_CREATE_START = "create table "+TABLENAME+"("
			+LEVELID+" integer primary key autoincrement, "+LEVELNAME+" VARCHAR(20), "
			+BESTSCORE+" integer, "+BESTPLAYER+" VARCHAR(20), "+MINELIMIT+" integer, "
			+GOLDSCORE+" integer, "+SILVERSCORE+" integer, "+BRONZESCORE+" integer, "
			+DIFFICULTY+" integer";
	private final String customSQL = "";//SHOULD BE OVERWRITTEN IN EXTENDS FOR EXTRA FUNK
	protected static final String DATABASE_CREATE_END = ");";
	public DatabaseHelper (Context tempContext, String name, CursorFactory factory, int version){
		super(tempContext, name, factory, version);
		context = tempContext;
	}
	
	//called when no database already exists
	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL(DATABASE_CREATE_START+customSQL+DATABASE_CREATE_END);
		initialiseDatabase(db);
	}
	//SHOULD NORMALY BE OVERWRITTEN
	abstract void initialiseDatabase(SQLiteDatabase db);
	//called when db on disk is not current version
	//(i assume when app upgraded?)
	//currently wipes old data, change for release, but useful for testing
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE "+TABLENAME);
		onCreate(db);
	}
}
