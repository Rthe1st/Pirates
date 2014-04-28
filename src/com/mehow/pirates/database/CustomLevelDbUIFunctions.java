package com.mehow.pirates.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class CustomLevelDbUIFunctions extends DatabaseUIFunctions{
	
	private static final String CUSTOM_LEVEL_DB = "customLevels";
	private static final int CUSTOM_LEVEL_DB_VERSION = 1;
	private static final String delimiter = ";";
	
	public CustomLevelDbUIFunctions(Context dbContext, CursorFactory dbCurFact){
		super(dbContext, dbCurFact);
		databaseHelper = new CustomLevelDatabaseHelper(dbContext, CUSTOM_LEVEL_DB, dbCurFact, CUSTOM_LEVEL_DB_VERSION);
	}
	public String[] getMapData(int levelNum){
		db = databaseHelper.getWritableDatabase();
		String[] columns = new String[]{CustomLevelDatabaseHelper.MAPDATA};
		String where = CustomLevelDatabaseHelper.LEVELID+"="+levelNum;
		Cursor cursor = db.query(CustomLevelDatabaseHelper.TABLENAME, columns, where, null, null, null, null);
		cursor.moveToFirst();
		String[] result = new String[10];
		result = cursor.getString(cursor.getColumnIndex(CustomLevelDatabaseHelper.MAPDATA)).split(delimiter);
		return result;
	}
	public void updateMapData(String[] data, int levelNum){
		db = databaseHelper.getWritableDatabase();
		String where = CustomLevelDatabaseHelper.LEVELID+" = "+levelNum;
		ContentValues contentVal = new ContentValues();
		contentVal.put(CustomLevelDatabaseHelper.MAPDATA, joinMapData(data));
		db.update(CustomLevelDatabaseHelper.TABLENAME, contentVal, where, null);
	}
	public void updateMapInfo(int levelNum, int mineLimit, int goldScore, int silverScore, 
			int bronzeScore, int difficulty, String name){
		db = databaseHelper.getWritableDatabase();
		String where = CustomLevelDatabaseHelper.LEVELID+" = "+levelNum;
		ContentValues contentVal = new ContentValues();
		contentVal.put(CustomLevelDatabaseHelper.GOLDSCORE, goldScore);
		contentVal.put(CustomLevelDatabaseHelper.SILVERSCORE, silverScore);
		contentVal.put(CustomLevelDatabaseHelper.BRONZESCORE, bronzeScore);
		contentVal.put(CustomLevelDatabaseHelper.DIFFICULTY, difficulty);
		contentVal.put(CustomLevelDatabaseHelper.LEVELNAME, name);
		db.update(CustomLevelDatabaseHelper.TABLENAME, contentVal, where, null);
	}
	//tutorials cannot be added to custom maps
	public void createMap(String[] data, int mineLimit, int goldScore, int silverScore, 
			int bronzeScore, int difficulty, String name){
		db = databaseHelper.getWritableDatabase();
		ContentValues contentVal = new ContentValues();
		contentVal.put(CustomLevelDatabaseHelper.MAPDATA, joinMapData(data));
		contentVal.put(CustomLevelDatabaseHelper.GOLDSCORE, goldScore);
		contentVal.put(CustomLevelDatabaseHelper.SILVERSCORE, silverScore);
		contentVal.put(CustomLevelDatabaseHelper.BRONZESCORE, bronzeScore);
		contentVal.put(CustomLevelDatabaseHelper.DIFFICULTY, difficulty);
		contentVal.put(CustomLevelDatabaseHelper.LEVELNAME, name);
		db.insert(CustomLevelDatabaseHelper.TABLENAME, null, contentVal);
	}
	private String joinMapData(String[] data){
		String joinedArray = "";
		for(int i=0;i<10;i++){
			joinedArray += data[i];
			if(i != 9){
				joinedArray += delimiter;
			}
		}
		return joinedArray;
	}
}
