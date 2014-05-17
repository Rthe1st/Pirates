package com.mehow.pirates.database;

import android.database.sqlite.SQLiteDatabase;

public interface DataAccess {

	public interface Callbacks{
		public SQLiteDatabase getDatabase();
	}
	
	public void create(SQLiteDatabase database);
	public void drop(SQLiteDatabase database);
//	public ContentValues[] queryAll(String[] columns);
//	public void insert(ContentValues columnData);
	
}
