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
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumbersActivity extends Activity {

	private NumbersGame game;

	private Button submit;
	private EditText answer;
	private TextView hiLow;
	private TextView numGuesses;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Set the submit button's action
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				checkAnswer();
			}
		});

		hiLow = (TextView) findViewById(R.id.result);
		numGuesses = (TextView) findViewById(R.id.numGuesses);
		answer = (EditText) findViewById(R.id.guess);

		restart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (100 == requestCode) {
			restart();
		}
	}

	/**
	 * Restarts the game.  Resets the UI and makes a new random number.
	 */
	private void restart() {
		// Create a new numbers game
		// Get the preference file (to get the user specified range)
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		game = new NumbersGame(Integer.parseInt(pref.getString(getResources().getString(R.string.limit), "1000")));

		TextView label = (TextView) findViewById(R.id.label);
		label.setText("Please enter a number between 1 & " + game.getRange() + ".");

		//Reset the submit button (because it is changed to a restart button once the user wins)
		submit.setText(getResources().getString(R.string.submit));
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				checkAnswer();
			}
		});

		numGuesses.setText("");
		hiLow.setText("");
		answer.setText("");
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
		switch (item.getItemId()) {
		case R.id.restart: restart(); break;
		case R.id.settings: startActivityForResult(new Intent(this, SettingsActivity.class), 100); break;
		case R.id.highScores: startActivity(new Intent(this, HSActivity.class)); break;
		}

		return true;
	}

	/**
	 * Checks to see if the answer the user entered is correct
	 */
	private void checkAnswer() {
		int response;

		try {
			response = Integer.parseInt(answer.getText().toString());
			hiLow.setText(game.checkAnswer(response));
		} catch (IllegalArgumentException e){
			Toast.makeText(NumbersActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
			return;
		} catch (Exception e) {
			Toast.makeText(NumbersActivity.this, R.string.bad_input, Toast.LENGTH_SHORT).show();
			return;
		} finally {
			answer.setText("");
		}

		numGuesses.setText("You have guessed " + game.getNumGuesses() + " times.");

		if (game.getFinished()) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			ContentValues values = new ContentValues();
			values.put(Scores.KEY_NAME, pref.getString(getResources().getString(R.string.name), "Anonymous"));
			values.put(Scores.KEY_RANGE, game.getRange());
			values.put(Scores.KEY_SCORE, game.getNumGuesses());
			getContentResolver().insert(Scores.CONTENT_URI, values);

			submit.setText(getResources().getString(R.string.restart));
			submit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					restart();
				}
			});
		}
	}

}