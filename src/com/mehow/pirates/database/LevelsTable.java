package com.mehow.pirates.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mehow.pirates.Consts;
import com.mehow.pirates.Consts.Achievements;
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
	
	public static final String CREATE_TABLE = "create table "+TABLENAME+"("
			+ID+" integer primary key autoincrement, "+NAME+" VARCHAR(20), "
			+BESTSCORE+" integer, "+BESTPLAYER+" VARCHAR(20), "+MINELIMIT+" integer, "
			+GOLDSCORE+" integer, "+SILVERSCORE+" integer, "+BRONZESCORE+" integer, "
			+DIFFICULTY+" integer, "+MAP+" TEXT )";
	
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
		int numOfLevels = Consts.noOfMaps;
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
			levelInfoXML.recycle();
			//-----------------
			//load actual map data
			int levelDataResourceId = context.getResources().getIdentifier("level"+i, "array", "com.mehow.pirates");
			String[] levelData = context.getResources().getStringArray(levelDataResourceId);
			String levelDataFormatted = "";
			for(String row : levelData){
				levelDataFormatted += row+"|";
			}
			//remove final |
			levelDataFormatted = levelDataFormatted.substring(0, levelDataFormatted.length()-2);
			rowData.put(MAP, levelDataFormatted);
			Log.i("LevelsTable", "mapdata "+rowData.getAsString(MAP));
			database.insert(TABLENAME, null, rowData);
			rowData.clear();
		}
	}
	@Override
	public void drop(SQLiteDatabase database){
		database.execSQL("DROP TABLE IF EXISTS "+TABLENAME);
	}

	public LevelInfo getLevelInfo(int levelNum){
		Log.i("LevelsTable", "getLevelInfo for "+levelNum);
		SQLiteDatabase database = callbacks.getDatabase();
        String[] columns = new String[]{
        		BESTPLAYER
        		, BESTSCORE
        		, DIFFICULTY
        		, NAME
        		, MINELIMIT
        		, GOLDSCORE
        		, SILVERSCORE
        		, BRONZESCORE
        		, MAP 
        		};
        String where = ID+"="+levelNum;
        Cursor cursor = database.query(TABLENAME, columns, where, null, null, null, null);
        cursor.moveToFirst();
        LevelInfo levelInfo = new LevelInfo(
        		  cursor.getString(cursor.getColumnIndexOrThrow(NAME))
        		, cursor.getInt(cursor.getColumnIndexOrThrow(DIFFICULTY))
        		, cursor.getInt(cursor.getColumnIndexOrThrow(MINELIMIT))
        		, cursor.getInt(cursor.getColumnIndexOrThrow(BESTSCORE))
        		, cursor.getString(cursor.getColumnIndexOrThrow(BESTPLAYER))
        		, cursor.getInt(cursor.getColumnIndexOrThrow(GOLDSCORE))
        		, cursor.getInt(cursor.getColumnIndexOrThrow(SILVERSCORE))
        		, cursor.getInt(cursor.getColumnIndexOrThrow(BRONZESCORE))
        		, cursor.getString(cursor.getColumnIndexOrThrow(MAP))
        		); 
        cursor.close();
        return levelInfo;
	}
	public void clearScores(){
		SQLiteDatabase database = callbacks.getDatabase();
		ContentValues updateValues = new ContentValues();
		updateValues.put(BESTSCORE, -1);
		updateValues.put(BESTPLAYER, "");
		database.update(TABLENAME, updateValues, null, null);
	}
	public Achievements getLevelAchievment(int levelNum){
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
		//System.out.println("goldscore: "+goldScore);
	//	System.out.println("bestScore: "+bestScore);
		if(bestScore == -1){
			return Achievements.NOT_COMP;
		}
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
	public void newBestScore(int levelNum, int newScore, String playerName){
		SQLiteDatabase database = callbacks.getDatabase();
		ContentValues updateValues = new ContentValues();
		updateValues.put(BESTSCORE, newScore);
		updateValues.put(BESTPLAYER, playerName);
		String where = ID+"="+levelNum;
		database.update(TABLENAME, updateValues, where, null);	
	}
	
	
	/*public String[] getMapData(int levelNum){
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
	int mapSize = 10;
	for(int i=0;i<mapSize;i++){
		joinedArray += data[i];
		if(i != mapSize-1){
			joinedArray += delimiter;
		}
	}
	return joinedArray;
}*/
}
