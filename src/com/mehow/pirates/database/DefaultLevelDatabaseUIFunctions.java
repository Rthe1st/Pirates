package com.mehow.pirates.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DefaultLevelDatabaseUIFunctions extends DatabaseUIFunctions{
	
	private static final String DEFAULT_LEVEL_DB = "defaultLevels";
	private static final int DEFAULT_LEVEL_DB_VERSION = 2;
	
	public DefaultLevelDatabaseUIFunctions(Context dbContext,
			CursorFactory dbCurFact) {
		super(dbContext, dbCurFact);
		databaseHelper = new DefaultLevelDatabaseHelper(dbContext, DEFAULT_LEVEL_DB, dbCurFact, DEFAULT_LEVEL_DB_VERSION);
	}

}
