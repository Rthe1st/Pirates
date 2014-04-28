package com.mehow.pirates.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class CustomLevelDatabaseHelper extends DatabaseHelper{

	//THIS SQL IS CRAP, CHANGE AS SOON AS BOTHERED
	//PORBALY MAKING USE OF <1 TABLE TO STORED ARRAYS BETTER
	
	//solutions record is particularly shit
	public static final String MAPDATA = "gold_score";
	public static final String SOLUTIONS_RECORD = "silver_score";
	
	protected final String customSQL = ","+MAPDATA+" VARCHAR(200) "+SOLUTIONS_RECORD+" VARCHAR(100)";
	
	public CustomLevelDatabaseHelper(Context tempContext, String name,
			CursorFactory factory, int version) {
		super(tempContext, name, factory, version);
	}
	protected void initialiseDatabase(SQLiteDatabase db){
	}

}
