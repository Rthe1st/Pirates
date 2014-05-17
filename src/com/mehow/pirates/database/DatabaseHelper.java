package com.mehow.pirates.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper implements DataAccess.Callbacks{
	
	private static DatabaseHelper instance = null;
	
	Context context;
	private static String DATABASE_NAME = "game_database";
	private static int DATABASE_VERSION = 4;
	
	public LevelsTable levelsTable;
	
	public enum InitialisationMethod {
		RESOURCE_XML
	}
	
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
	
	//SHOULD NORMALY BE OVERWRITTEN
	//abstract void initialiseDatabase(SQLiteDatabase db);
	/*
	protected void initialiseDatabase(SQLiteDatabase db){
		//System.out.println("ext");
		int numOfLevels = Consts.noOfMaps;
		ContentValues rowData = new ContentValues();
		for(int i=1;i<=numOfLevels;i++){
			int levelInfoResourceId = context.getResources().getIdentifier("level"+i+"_info", "array", "com.mehow.pirates");
		//	System.out.println("levelid: "+"level"+i+"_info");
			TypedArray levelInfo = context.getResources().obtainTypedArray(levelInfoResourceId);
			//System.out.println("i is: "+i+"level info is:"+levelInfo+" levelinfo length: "+levelInfo.length +"levelinfo0: "+levelInfo[0]+"levelinfo1: "+levelInfo[1]+"levelinfo2: "+levelInfo[2]+"levelinfo3: "+levelInfo[3]);
			int mineLimit = levelInfo.getInt(0, -1);
			int goldscore = levelInfo.getInt(1, -1);
			int silverscore = levelInfo.getInt(2, -1);
			int bronzescore = levelInfo.getInt(3, -1);
			int difficulty = levelInfo.getInt(4, -1);
			String levelName = levelInfo.getString(5);
			rowData.put(LEVELNAME, levelName);
			rowData.put(BESTSCORE, -1);
			rowData.put(BESTPLAYER, "");
			rowData.put(MINELIMIT, mineLimit);
			rowData.put(GOLDSCORE, goldscore);
			rowData.put(SILVERSCORE, silverscore);
			rowData.put(BRONZESCORE, bronzescore);
			rowData.put(DIFFICULTY, difficulty);
			
			
			int levelMapDataResourceId = context.getResources().getIdentifier("level"+i, "array", "com.mehow.pirates");;
			TypedArray levelMapData = context.getResources().obtainTypedArray(levelMapDataResourceId);
			
			db.insert(TABLENAME, null, rowData);
		}
	}*/
	
	//called when db on disk is not current version
	//(i assume when app upgraded?)
	//currently wipes old data, change for release, but useful for testing
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		//dropTable(LevelsTable.TABLENAME);
		levelsTable.drop(db);
		onCreate(db);
	}
	
	/*public void createTable(String creationSQL){
		SQLiteDatabase database = this.getWritableDatabase();
		database.execSQL(creationSQL);
	}
	public void dropTable(String tableName){
		SQLiteDatabase database = this.getWritableDatabase();
		database.execSQL("DROP "+tableName);
	}*/

	
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
