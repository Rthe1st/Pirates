package com.mehow.pirates.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper implements DataAccess.Callbacks{
	
	private static DatabaseHelper instance = null;
	
	Context context;
	private static String DATABASE_NAME = "game_database";
	private static int DATABASE_VERSION = 20;
	
	public LevelsTable levelsTable;
	
	private SQLiteDatabase database;
	
	private DatabaseHelper (Context tempContext){
		super(tempContext, DATABASE_NAME, null, DATABASE_VERSION);
		context = tempContext;
		levelsTable = new LevelsTable(this);
	}
	
	public static DatabaseHelper getInstance(Context context){
		if(instance == null){
			instance = new DatabaseHelper(context.getApplicationContext());
		}
		return instance;
	}
	//called when no database already exists
	@Override
	public void onCreate(SQLiteDatabase db){
		//db.execSQL(LevelsTable.tableCreationSQL());
		levelsTable.create(db);
		//createTable(LevelsTable.CREATE_TABLE);
		//initialiseDatabase(db);
		//loadFromResourceXML();
		levelsTable.loadPresetXMLLevels(db, context);
	}
	
	//called when db on disk is not current version
	//(i assume when app upgraded?)
	//currently wipes old data, change for release, but useful for testing
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		levelsTable.drop(db);
		onCreate(db);
	}
	
	//bit of a hack to solve issue of closing database between activities
	//better solved by having this managed be separte singleton class?
	public void clearDatabase(){
		database = null;
	}
	
	@Override
	public SQLiteDatabase getDatabase() {
		//add code to return reable database if writable cant be accessed?
		if(database == null){
			database = this.getWritableDatabase();
		}
		return database;
	}
	
}
