/*
 * Copyright (C) 2011 Brian Reber
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Brian Reber.
 * THIS SOFTWARE IS PROVIDED 'AS IS' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.reber.Numbers;

import java.util.HashMap;

import org.reber.Numbers.Score.Scores;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * A Database Wrapper for Vehicles
 * 
 * @author brianreber
 */
public class HSContentProvider extends ContentProvider {

	public static final String AUTHORITY = "org.reber.Numbers.contentprovider";
	private static final UriMatcher sUriMatcher;
	private static final int SCORES = 1;
	private static HashMap<String, String> scoresProjectionMap;

	private DatabaseHelper dbHelper;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, Scores.TABLE, SCORES);

		scoresProjectionMap = new HashMap<String, String>();
		scoresProjectionMap.put(Scores._ID, Scores._ID);
		scoresProjectionMap.put(Scores.KEY_NAME, Scores.KEY_NAME);
		scoresProjectionMap.put(Scores.KEY_RANGE, Scores.KEY_RANGE);
		scoresProjectionMap.put(Scores.KEY_SCORE, Scores.KEY_SCORE);
	}

	/**
	 * A helper class for interacting with the database
	 * 
	 * @author brianreber
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		private static final int DATABASE_VERSION = 1;
		private static final String DATABASE_NAME = "scores.db";

		/**
		 * Creates a new Database with the given context
		 * 
		 * @param context
		 */
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			createScoresTable(db);
		}

		private void createScoresTable(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + Scores.TABLE + "(" +
					Scores._ID + " INTEGER PRIMARY KEY autoincrement, " +
					Scores.KEY_NAME + " TEXT, " +
					Scores.KEY_RANGE + " INTEGER, " +
					Scores.KEY_SCORE + " INTEGER" +
					");");
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + Scores.TABLE);

			onCreate(db);
		}
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case SCORES:
			count = db.delete(Scores.TABLE, where, whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null, true);

		return count;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case SCORES:
			return Scores.CONTENT_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) == UriMatcher.NO_MATCH) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		switch (sUriMatcher.match(uri)) {
		case SCORES:
			db.insert(Scores.TABLE, null, values);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case SCORES:
			qb.setTables(Scores.TABLE);
			qb.setProjectionMap(scoresProjectionMap);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		Cursor c = qb.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case SCORES:
			count = db.update(Scores.TABLE, values, where, whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
