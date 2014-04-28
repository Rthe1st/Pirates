package com.mehow.pirates.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.mehow.pirates.Consts;

public class DefaultLevelDatabaseHelper extends DatabaseHelper{
	
	protected final String customSQL = "";//SHOULD BE OVERWRITTEN IN EXTENDS FOR EXTRA FUNK
	
	public DefaultLevelDatabaseHelper(Context tempContext, String name,
			CursorFactory factory, int version) {
		super(tempContext, name, factory, version);
	}
	protected void initialiseDatabase(SQLiteDatabase db){
		//System.out.println("ext");
		int numOfLevels = Consts.noOfMaps;
		int levelResourceId;
		TypedArray levelInfo;
		int mineLimit;
		int difficulty;
		int goldscore, silverscore, bronzescore;
		String levelName;
		ContentValues rowData = new ContentValues();
		for(int i=1;i<=numOfLevels;i++){
			levelResourceId = context.getResources().getIdentifier("level"+i+"_info", "array", "com.mehow.pirates");
		//	System.out.println("levelid: "+"level"+i+"_info");
			levelInfo = context.getResources().obtainTypedArray(levelResourceId);
			//System.out.println("i is: "+i+"level info is:"+levelInfo+" levelinfo length: "+levelInfo.length +"levelinfo0: "+levelInfo[0]+"levelinfo1: "+levelInfo[1]+"levelinfo2: "+levelInfo[2]+"levelinfo3: "+levelInfo[3]);
			mineLimit = levelInfo.getInt(0, -1);
			goldscore = levelInfo.getInt(1, -1);
			silverscore = levelInfo.getInt(2, -1);
			bronzescore = levelInfo.getInt(3, -1);
			difficulty = levelInfo.getInt(4, -1);
			levelName = levelInfo.getString(5);
			rowData.put(LEVELNAME, levelName);
			rowData.put(BESTSCORE, -1);
			rowData.put(BESTPLAYER, "");
			rowData.put(MINELIMIT, mineLimit);
			rowData.put(GOLDSCORE, goldscore);
			rowData.put(SILVERSCORE, silverscore);
			rowData.put(BRONZESCORE, bronzescore);
			rowData.put(DIFFICULTY, difficulty);
			db.insert(TABLENAME, null, rowData);
		}
	}
}
