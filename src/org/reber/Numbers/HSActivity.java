/*
 * Copyright (C) 2012 Brian Reber
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

import org.reber.Numbers.Score.Scores;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class HSActivity extends Activity {
	/**
	 * The Adapter that will hold our Scores in the listview
	 */
	private SimpleCursorAdapter adapter;

	/**
	 * The listview that will be displayed
	 */
	private ListView listView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hs);

		listView = (ListView) findViewById(R.id.hs_list);

		Cursor cursor = getContentResolver().query(Scores.CONTENT_URI, null, null,	null, Scores.DEFAULT_SORT);

		adapter = new SimpleCursorAdapter(this, R.layout.rows, cursor,
				new String[] { Scores.KEY_NAME, Scores.KEY_RANGE, Scores.KEY_SCORE },
				new int[] {	R.id.userName, R.id.userRange, R.id.userScore });
		listView.setAdapter(adapter);
	}

	/**
	 * Gets called when the menu button is pressed.
	 * @param menu
	 * The menu instance that we apply a menu to
	 * 
	 * @return
	 * true so that it uses our own implementation
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.hsmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings: startActivityForResult(new Intent(this, SettingsActivity.class), 100); break;
		}

		return true;
	}

}