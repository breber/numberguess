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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class HSActivity extends Activity {
	private ScoreList scores = new ScoreList();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hs);

		initScoresList();

		highScores();
	}

	/**
	 * Gets called when the menu button is pressed.
	 * param menu
	 * The menu instance that we apply a menu to
	 * 
	 * @return
	 * true so that it uses our own implementation
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.gamemenu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String menuTitle = item.getTitle().toString();

		if (menuTitle.equals("Reset High Scores")) {
			//do we really want to clear high scores?
			AlertDialog.Builder prompt = new AlertDialog.Builder(this);

			prompt.setMessage(R.string.reset_hs_prompt);

			prompt.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					initScores();
					printHighScores();
				}
			});

			prompt.setNegativeButton(R.string.cancel, null);
			prompt.show();
		}
		return true;
	}

	/**
	 * Checks to see if it is a high score and updates the UI
	 */
	private void highScores() {
		updateHighScoresPrefFile();
		printHighScores();
	}


	/**
	 * Calculates the high scores and puts them into the Preference List
	 */
	private void updateHighScoresPrefFile() {
		SharedPreferences prefStr = getSharedPreferences("Scores", MODE_WORLD_WRITEABLE);
		Editor edit = prefStr.edit();

		if (!prefStr.contains("HighScores")) {
			initScores();
		}

		edit.putString("HighScores", scores.toString());

		edit.commit();

		printHighScores();
	}

	/**
	 * Prints the high score grid.
	 */
	private void printHighScores() {
		ListView lv = (ListView) findViewById(R.id.hs_list);
		lv.setAdapter(new ScoreListAdapter(this, android.R.layout.simple_list_item_2, scores));
	}

	/**
	 * Initializes the high score preference file.
	 */
	private void initScores() {
		SharedPreferences pref = getSharedPreferences("Scores", MODE_WORLD_WRITEABLE);
		Editor edit = pref.edit();

		scores = new ScoreList();

		edit.remove("HighScores");
		edit.putString("HighScores", "");
		edit.commit();
	}

	/**
	 * Add the <code>Score</code>s to the <code>ArrayList</code> so that
	 * we can keep track of the scores that are stored in between sessions.
	 */
	private void initScoresList() {
		SharedPreferences pref = getSharedPreferences("Scores", MODE_WORLD_READABLE);

		String currentPref = pref.getString("HighScores", null);

		if (currentPref == null) {
			initScores();
			return;
		}

		String[] scoreStrings = currentPref.split(",");

		for (String current : scoreStrings) {
			try {
				if (current.trim().length() != 0) {
					scores.add(Score.parseString(current));
				}
			} catch (ParsingException e) {
				Toast.makeText(this, R.string.hs_error, Toast.LENGTH_SHORT).show();
			}
		}
	}
}