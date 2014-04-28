package com.mehow.pirates.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.mehow.pirates.Consts.Achievements;

public class DatabaseUIFunctions {
	protected DatabaseHelper databaseHelper;
	protected SQLiteDatabase db ;
	
	public DatabaseUIFunctions(Context dbContext, CursorFactory dbCurFact){
		//DATABASEHELPER SHOULD BE CREATED HERE IN EXTENSION
		//	databaseHelper = new DatabaseHelper(dbContext, DEFAULT_LEVEL_DB, dbCurFact, DEFAULT_LEVEL_DB_VERSION);
	}
	public void closeDb(){
		
		databaseHelper.close();
	}
	//CURSOR SHOULD PRBALY NOT BE RETURNED, AS NEEDS TO BE CLOSED OUTSIDE OF FUNCTION
	public Cursor getMenuLevelInfo(int levelNum){
		db = databaseHelper.getWritableDatabase();
        String[] columns = new String[]{DatabaseHelper.BESTPLAYER, DatabaseHelper.BESTSCORE,
        		DatabaseHelper.DIFFICULTY, DatabaseHelper.LEVELNAME, DatabaseHelper.MINELIMIT,
        		DatabaseHelper.GOLDSCORE, DatabaseHelper.SILVERSCORE, DatabaseHelper.BRONZESCORE};
        String where = DatabaseHelper.LEVELID+"="+levelNum;
        return db.query(DatabaseHelper.TABLENAME, columns, where, null, null, null, null);
	}
	public int getMineLimit(int levelNum){
		int mineCount;
        System.out.println("request level num for mine limit: "+levelNum);
		db = databaseHelper.getWritableDatabase();
		String[] columns = new String[]{DatabaseHelper.MINELIMIT};
		String where = DatabaseHelper.LEVELID+"="+levelNum;
		Cursor cursor = db.query(DatabaseHelper.TABLENAME, columns, where, null, null, null, null);
		cursor.moveToFirst();
		mineCount = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MINELIMIT));
		cursor.close();
		return mineCount;
	}
	public int getLevelBestScore(int levelNum){
		db = databaseHelper.getWritableDatabase();
		String[] columns = new String[]{DatabaseHelper.BESTSCORE};
		String where = DatabaseHelper.LEVELID+"="+levelNum;
		Cursor cursor = db.query(DatabaseHelper.TABLENAME, columns, where, null, null, null, null);
		cursor.moveToFirst();
		int bestScore =  cursor.getInt(cursor.getColumnIndex(DatabaseHelper.BESTSCORE));
		cursor.close();
		return bestScore;
	}
	public void newBestScore(int levelNum, int newScore, String playerName){
		db = databaseHelper.getWritableDatabase();
		ContentValues updateValues = new ContentValues();
		updateValues.put(DatabaseHelper.BESTSCORE, newScore);
		updateValues.put(DatabaseHelper.BESTPLAYER, playerName);
		String where = DatabaseHelper.LEVELID+"="+levelNum;
		db.update(DatabaseHelper.TABLENAME, updateValues, where, null);	
	}
	public Achievements getLevelAchievment(int levelNum){
		db = databaseHelper.getWritableDatabase();
		String[] columns = new String[]{DatabaseHelper.BESTSCORE, DatabaseHelper.GOLDSCORE, DatabaseHelper.SILVERSCORE, DatabaseHelper.BRONZESCORE};
		String where = DatabaseHelper.LEVELID+"="+levelNum;
		Cursor cursor = db.query(DatabaseHelper.TABLENAME, columns, where, null, null, null, null);
		cursor.moveToFirst();
		int goldScore = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.GOLDSCORE));
		int silverScore = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.SILVERSCORE));
		int bronzeScore = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.BRONZESCORE));
		int bestScore = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.BESTSCORE));
		cursor.close();
		//System.out.println("goldscore: "+goldScore);
	//	System.out.println("bestScore: "+bestScore);
		if(bestScore == -1){
			return Achievements.NOT_COMP;
		}
		//backwards because proberly most levels wont have gold
		if(bestScore < bronzeScore){
			return Achievements.NONE;
		}else if(bestScore < silverScore){
			return Achievements.BRONZE;
		}else if(bestScore < goldScore){
			return Achievements.SILVER;
		}else{
			return Achievements.GOLD;
		}
	}
	public void clearScores(){
		db = databaseHelper.getWritableDatabase();
		ContentValues updateValues = new ContentValues();
		updateValues.put(DatabaseHelper.BESTSCORE, -1);
		updateValues.put(DatabaseHelper.BESTPLAYER, "");
		db.update(DatabaseHelper.TABLENAME, updateValues, null, null);
	}
}