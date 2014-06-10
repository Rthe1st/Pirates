package com.mehow.pirates.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mehow.pirates.LevelInfo;

public class LevelsTable implements DataAccess{
	public static final String TABLENAME = "level_data";
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String BESTSCORE = "best_score";
	public static final String BESTPLAYER = "best_player";
	public static final String MINELIMIT = "mine_limit";
	public static final String GOLDSCORE = "gold_score";
	public static final String SILVERSCORE = "silver_score";
	public static final String BRONZESCORE = "bronze_score";
	public static final String DIFFICULTY = "difficulty";
	public static final String MAP = "mapdata";
	public static final String TYPE ="type";
	
	public static  enum Achievement {
	    GOLD, SILVER, BRONZE, NONE, NOT_COMP
	}
	
	public static Achievement calculateAchievement(int bestScore, int bronzeScore, int silverScore, int goldScore){
		if(bestScore == -1){
			return Achievement.NOT_COMP;
		}
		if(bestScore < bronzeScore){
			return Achievement.NONE;
		}else if(bestScore < silverScore){
			return Achievement.BRONZE;
		}else if(bestScore < goldScore){
			return Achievement.SILVER;
		}else{
			return Achievement.GOLD;
		}
	}
	
	public static enum LevelTypes {
		PRE_MADE
		, CUSTOM
	}
	
	//todo make columns nullable
	public static final String CREATE_TABLE = "create table "+TABLENAME+"("
			+ID+" integer primary key, "+NAME+" VARCHAR(20), "
			+BESTSCORE+" integer, "+BESTPLAYER+" VARCHAR(20), "+MINELIMIT+" integer, "
			+GOLDSCORE+" integer, "+SILVERSCORE+" integer, "+BRONZESCORE+" integer, "
			+DIFFICULTY+" integer, "+MAP+" TEXT, "+TYPE+" VARCHAR(20)"+")";
	
	private DataAccess.Callbacks callbacks;
	
	LevelsTable(DataAccess.Callbacks tCallbacks){
		callbacks = tCallbacks;
	}
	
	@Override
	public void create(SQLiteDatabase database){
		database.execSQL(CREATE_TABLE);
	}
	//this imports from xml.
	//When customLevels has been set up, it may be better to loaded from some compressed binary format or such
	protected void loadPresetXMLLevels(SQLiteDatabase database, Context context){
		//System.out.println("ext");
		int numOfLevels = 5;//number of levels in xml file
		ContentValues rowData = new ContentValues();
		for(int i=1;i<=numOfLevels;i++){
			//load "meta data"
			Log.i("LevelsTable", "instering xml level");
			int levelInfoResourceId = context.getResources().getIdentifier("level"+i+"_info", "array", "com.mehow.pirates");
			TypedArray levelInfoXML = context.getResources().obtainTypedArray(levelInfoResourceId);
			rowData.put(NAME, levelInfoXML.getString(5));
			Log.i("LevelsTable", "levelName "+rowData.getAsString(NAME));
			rowData.put(BESTSCORE, -1);
			Log.i("LevelsTable", "bestscore "+rowData.getAsString(BESTSCORE));
			rowData.put(BESTPLAYER, "");
			Log.i("LevelsTable", "bestplayer "+rowData.getAsString(BESTPLAYER));
			rowData.put(MINELIMIT, levelInfoXML.getInt(0, -1));
			Log.i("LevelsTable", "minelimit "+rowData.getAsString(MINELIMIT));
			rowData.put(GOLDSCORE, levelInfoXML.getInt(1, -1));
			Log.i("LevelsTable", "goldscore "+rowData.getAsString(GOLDSCORE));
			rowData.put(SILVERSCORE, levelInfoXML.getInt(2, -1));
			Log.i("LevelsTable", "silverscore "+rowData.getAsString(SILVERSCORE));
			rowData.put(BRONZESCORE, levelInfoXML.getInt(3, -1));
			Log.i("LevelsTable", "bronzescore "+rowData.getAsString(BRONZESCORE));
			rowData.put(DIFFICULTY, levelInfoXML.getInt(4, -1));
			Log.i("LevelsTable", "difficulty "+rowData.getAsString(DIFFICULTY));
			rowData.put(MAP, levelInfoXML.getString(8));
			Log.i("LevelsTable", "mapdata "+rowData.getAsString(MAP));
			levelInfoXML.recycle();
			//-----------------
			//load actual map data
			/*int levelDataResourceId = context.getResources().getIdentifier("level"+i, "array", "com.mehow.pirates");
			String[] levelData = context.getResources().getStringArray(levelDataResourceId);
			String levelDataFormatted = "";
			for(String row : levelData){
				levelDataFormatted += row+"|";
			}
			//remove final |
			levelDataFormatted = levelDataFormatted.substring(0, levelDataFormatted.length()-2);
			rowData.put(MAP, levelDataFormatted);
			Log.i("LevelsTable", "mapdata "+rowData.getAsString(MAP));
			*/
			rowData.put(TYPE, LevelTypes.PRE_MADE.toString());
			database.insert(TABLENAME, null, rowData);
			rowData.clear();
		}
	}
	@Override
	public void drop(SQLiteDatabase database){
		database.execSQL("DROP TABLE IF EXISTS "+TABLENAME);
	}

	public LevelInfo[] getLevelInfos(LevelTypes type){
		String where = TYPE+"='"+type+"'";
		return innerGetLevelInfos(where);
	}
	
	public LevelInfo getLevelInfo(long levelNum){
		Log.i("LevelsTable", "getLevelInfo for "+levelNum);
		String where = ID+"="+levelNum;
		LevelInfo[] levelInfos = innerGetLevelInfos(where);
        return levelInfos[0];
	}
	
	private LevelInfo[] innerGetLevelInfos(String where){
		SQLiteDatabase database = callbacks.getDatabase();
        String[] columns = new String[]{
        		ID
        		, BESTPLAYER
        		, BESTSCORE
        		, DIFFICULTY
        		, NAME
        		, MINELIMIT
        		, GOLDSCORE
        		, SILVERSCORE
        		, BRONZESCORE
        		, MAP
        		, TYPE
        		};
        Cursor cursor = database.query(TABLENAME, columns, where, null, null, null, null);
        cursor.moveToFirst();
        LevelInfo[] levelInfos = new LevelInfo[cursor.getCount()];
        while(!cursor.isAfterLast()){
        	levelInfos[cursor.getPosition()] = new LevelInfo(
        		cursor.getInt(cursor.getColumnIndexOrThrow(ID))
          		, cursor.getString(cursor.getColumnIndexOrThrow(NAME))
          		, cursor.getInt(cursor.getColumnIndexOrThrow(DIFFICULTY))
          		, cursor.getInt(cursor.getColumnIndexOrThrow(MINELIMIT))
          		, cursor.getInt(cursor.getColumnIndexOrThrow(BESTSCORE))
          		, cursor.getString(cursor.getColumnIndexOrThrow(BESTPLAYER))
          		, cursor.getInt(cursor.getColumnIndexOrThrow(GOLDSCORE))
          		, cursor.getInt(cursor.getColumnIndexOrThrow(SILVERSCORE))
          		, cursor.getInt(cursor.getColumnIndexOrThrow(BRONZESCORE))
          		, cursor.getString(cursor.getColumnIndexOrThrow(MAP))
          		, LevelTypes.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(TYPE)))
          		);
        	cursor.moveToNext();
        }
        cursor.close();
        return levelInfos;
	}
	
	public int countLevels(LevelTypes type){
		SQLiteDatabase database = callbacks.getDatabase();
		String sql = "SELECT COUNT(*) FROM "+TABLENAME+" WHERE "+TABLENAME+"."+TYPE+" = ?";
		String[] selectionArgs = {type.toString()};
		Cursor cursor = database.rawQuery(sql, selectionArgs);
		cursor.moveToFirst();
		int levelCount = cursor.getInt(0);
		cursor.close();
		return levelCount;
	}
	
	public void clearScores(){
		SQLiteDatabase database = callbacks.getDatabase();
		ContentValues updateValues = new ContentValues();
		updateValues.put(BESTSCORE, -1);
		updateValues.put(BESTPLAYER, "");
		database.update(TABLENAME, updateValues, null, null);
	}
	
	public Achievement getLevelAchievment(long levelNum){
		SQLiteDatabase database = callbacks.getDatabase();
		String[] columns = new String[]{
				BESTSCORE
				, GOLDSCORE
				, SILVERSCORE
				, BRONZESCORE
				};
		String where = ID+"="+levelNum;
		Cursor cursor = database.query(TABLENAME, columns, where, null, null, null, null);
		cursor.moveToFirst();
		int goldScore = cursor.getInt(cursor.getColumnIndexOrThrow(GOLDSCORE));
		int silverScore = cursor.getInt(cursor.getColumnIndexOrThrow(SILVERSCORE));
		int bronzeScore = cursor.getInt(cursor.getColumnIndexOrThrow(BRONZESCORE));
		int bestScore = cursor.getInt(cursor.getColumnIndexOrThrow(BESTSCORE));
		cursor.close();
		if(bestScore == -1){
			return Achievement.NOT_COMP;
		}
		if(bestScore < bronzeScore){
			return Achievement.NONE;
		}else if(bestScore < silverScore){
			return Achievement.BRONZE;
		}else if(bestScore < goldScore){
			return Achievement.SILVER;
		}else{
			return Achievement.GOLD;
		}
	}
	public void newBestScore(long levelNum, int newScore, String playerName){
		SQLiteDatabase database = callbacks.getDatabase();
		ContentValues updateValues = new ContentValues();
		updateValues.put(BESTSCORE, newScore);
		updateValues.put(BESTPLAYER, playerName);
		String where = ID+"="+levelNum;
		database.update(TABLENAME, updateValues, where, null);	
	}
	
	public LevelInfo createCustomLevel(){
		SQLiteDatabase database = callbacks.getDatabase();
		ContentValues rowData = new ContentValues();
		String defaultName = "customLevel";
		rowData.put(NAME, defaultName);
		Log.i("LevelsTable", "levelName "+rowData.getAsString(NAME));
		rowData.put(BESTSCORE, -1);
		Log.i("LevelsTable", "bestscore "+rowData.getAsString(BESTSCORE));
		rowData.put(BESTPLAYER, "");
		Log.i("LevelsTable", "bestplayer "+rowData.getAsString(BESTPLAYER));
		int defaultMineLimit = 5;
		rowData.put(MINELIMIT, defaultMineLimit);
		Log.i("LevelsTable", "minelimit "+rowData.getAsString(MINELIMIT));
		int defaultGoldScore = 70;
		rowData.put(GOLDSCORE, defaultGoldScore);
		Log.i("LevelsTable", "goldscore "+rowData.getAsString(GOLDSCORE));
		int defaultSilverScore = 60;
		rowData.put(SILVERSCORE, defaultSilverScore);
		Log.i("LevelsTable", "silverscore "+rowData.getAsString(SILVERSCORE));
		int defaultBronzeScore = 50;
		rowData.put(BRONZESCORE, defaultBronzeScore);
		Log.i("LevelsTable", "bronzescore "+rowData.getAsString(BRONZESCORE));
		int defaultDifficulty = 1;
		rowData.put(DIFFICULTY, defaultDifficulty);
		Log.i("LevelsTable", "difficulty "+rowData.getAsString(DIFFICULTY));
		//load actual map data
		String levelDataFormatted = "";
		for(int i=0;i<10;i++){
			levelDataFormatted += "0,0,0,0,0,0,0,0,0,0"+"|";
		}
		//remove final |
		levelDataFormatted = levelDataFormatted.substring(0, levelDataFormatted.length()-2);
		rowData.put(MAP, levelDataFormatted);
		Log.i("LevelsTable", "mapdata "+rowData.getAsString(MAP));
		rowData.put(TYPE, LevelTypes.CUSTOM.toString());
		long id = database.insert(TABLENAME, null, rowData);
		//return the id of the inserted level
		return new LevelInfo(id, defaultName, defaultDifficulty, defaultMineLimit, -1, "", defaultGoldScore, defaultSilverScore, defaultBronzeScore, levelDataFormatted, LevelTypes.CUSTOM);
	}
	public void updateLevel(LevelInfo levelInfo){
		SQLiteDatabase database = callbacks.getDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(BRONZESCORE, levelInfo.bronzeScore);
		contentValues.put(SILVERSCORE, levelInfo.silverScore);
		contentValues.put(GOLDSCORE, levelInfo.goldScore);
		contentValues.put(MINELIMIT, levelInfo.mineLimit);
		contentValues.put(NAME, levelInfo.name);
		contentValues.put(MAP, levelInfo.mapData);
		String where = ID+"=?";
		String[] whereParams =  new String[]{
			String.valueOf(levelInfo.id)
		};
		database.update(TABLENAME, contentValues, where, whereParams);
	}
	
	public void deleteLevel(long levelId){
		SQLiteDatabase database = callbacks.getDatabase();
		String where = ID+"=?";
		String[] whereArgs = {String.valueOf(levelId)};
		database.delete(TABLENAME, where, whereArgs);
	}
}
