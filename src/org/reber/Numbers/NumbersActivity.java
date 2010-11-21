package org.reber.Numbers;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class NumbersActivity extends TabActivity {

	private NumbersGame game;
	private ArrayList<Score> scores = new ArrayList<Score>();

	private int keyPressCount = 0;

	//Tab info - used for standard naming
	private static final int GAME_VIEW_TAB = 0;
	private static final int HS_VIEW_TAB = 1;
	private static final String GAME_VIEW_ID = "gameTab";
	private static final String HS_VIEW_ID = "hsTab";
	private static final String SETTINGS_VIEW_ID = "settingsTab";

	//Used for the toggling of the menu labels
	private boolean menuOption = true;

	// UI Elements
	private TabHost mTabHost;
	private Button submit;
	private EditText answer;
	private TextView hiLow;
	private TextView numGuesses;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabbed);

		//Display the logo
		final Dialog prompt = new Dialog(this);
		prompt.setTitle("Welcome to Numbers!");
		ImageView im = new ImageView(this);
		im.setImageDrawable(getResources().getDrawable(R.drawable.icon));               
		prompt.setContentView(im);      

		//The Handler allows us to send events from the two threads
		final Handler handler = new Handler();
		//Create a thread to wait for a few seconds, then we can dismiss the prompt
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						prompt.dismiss();
					}
				});
			}
		}.start();
		prompt.show();

		//Set up tabs
		createTabs();

		//Get the preference file (to get the user specified range)
		SharedPreferences pref = getSharedPreferences("GamePrefs", MODE_WORLD_READABLE);

		// New numbers game
		game = new NumbersGame(Integer.parseInt(pref.getString("Range", 1000 + "")));

		TextView label = (TextView) findViewById(R.id.label);
		label.setText("Please enter a number between 1 & " + game.getRange() + ".");

		//Set the submit button's action
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				checkAnswer();
			}
		});

		hiLow = (TextView) findViewById(R.id.result);
		numGuesses = (TextView) findViewById(R.id.numGuesses);

		//Set the properties for the EditText (where we type our answers)
		answer = (EditText) findViewById(R.id.guess);
		answer.setOnKeyListener(new OnKeyListener() {
			//We want to catch the Enter key and override the default action
			//Overriding action is to simulate pressing "Submit"
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {                             
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					submit.performClick();
					//This method gets called twice, so we keep track
					keyPressCount++;
					if (keyPressCount > 1) {
						keyPressCount = 0;
					}
					return true;

				} else if (keyCode > KeyEvent.KEYCODE_0 || keyCode < KeyEvent.KEYCODE_9 || keyCode == KeyEvent.KEYCODE_DEL) {
					return false;
				} else {
					return true;
				}
			}                       
		});
		
		Button saveSettings = (Button) findViewById(R.id.SaveSettings);
		saveSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setSettings(true);
			}
		});

		initScoresList();

		highScores();
	}
	
	/* (non-Javadoc)
	 * @see android.app.ActivityGroup#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		getSettings();
	}
	
	/* (non-Javadoc)
	 * @see android.app.ActivityGroup#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		setSettings(false);
	}
	
	private void getSettings()
	{
		CheckBox useDefaultCheckBox = (CheckBox) findViewById(R.id.UseDefaultName);
		EditText defaultNameBox = (EditText) findViewById(R.id.DefaultName);
		Spinner hubSpinner = (Spinner) findViewById(R.id.RangeSpinner);
		
		// Get the options from the limits.xml file
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.limits, android.R.layout.simple_spinner_item); 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		SharedPreferences pref = getSharedPreferences("GamePrefs", MODE_WORLD_READABLE);
		
		useDefaultCheckBox.setChecked(pref.getBoolean("useDefaultCheckBox", false));
		defaultNameBox.setText(pref.getString("defaultName", ""));
		// Get the position of the current range
		int pos = adapter.getPosition(String.valueOf(pref.getString("Range", "1000")));
		hubSpinner.setAdapter(adapter);
		// Set the current selected option
		hubSpinner.setSelection(pos);
	}
	
	private void setSettings(boolean presentToast)
	{
		CheckBox useDefaultCheckBox = (CheckBox) findViewById(R.id.UseDefaultName);
		EditText defaultNameBox = (EditText) findViewById(R.id.DefaultName);
		String defaultName = defaultNameBox.getText().toString();
		Spinner spinner = (Spinner) findViewById(R.id.RangeSpinner);
		
		SharedPreferences pref = getSharedPreferences("GamePrefs", MODE_WORLD_WRITEABLE);
		Editor edit = pref.edit();

		edit.putBoolean("useDefaultCheckBox", useDefaultCheckBox.isChecked());
		edit.putString("defaultName", defaultName);
		edit.putString("Range", Integer.parseInt((String) spinner.getSelectedItem()) + "");
		
		edit.commit();
		
		if (presentToast) {
			Toast.makeText(this, "Settings have been saved", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Set up the tabbed view
	 */
	private void createTabs()
	{
		//Set up the tab view
		mTabHost = getTabHost();

		mTabHost.addTab(mTabHost.newTabSpec(GAME_VIEW_ID).setIndicator("Game", 
				getResources().getDrawable(R.drawable.iconsmall)).setContent(R.id.gameViewTab));
		mTabHost.addTab(mTabHost.newTabSpec(HS_VIEW_ID).setIndicator("High Scores", 
				getResources().getDrawable(R.drawable.hs)).setContent(R.id.hsTableTab));
		mTabHost.addTab(mTabHost.newTabSpec(SETTINGS_VIEW_ID).setIndicator("Settings", 
				getResources().getDrawable(R.drawable.settings)).setContent(R.id.settingsTab));

		mTabHost.setOnTabChangedListener(new OnTabChangeListener()
		{
			public void onTabChanged(String tabId)
			{
				if (tabId.equals(GAME_VIEW_ID))
					menuOption = true;
				else menuOption = false;

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mTabHost.getApplicationWindowToken(), 0);
			}
		});

		//Hide the keyboard until the user presses on the text box
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mTabHost.setCurrentTab(GAME_VIEW_TAB);
	}

	/**
	 * Restarts the game.  Resets the UI and makes a new random number.
	 */
	private void restart(int range)
	{
		//Create a new numbers game
		game = new NumbersGame(range);

		//Reset the submit button (because it is changed to a restart button once the user wins)
		submit.setText("Submit");
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

		String menuTitle = item.getTitle().toString();

		if (menuTitle.equals("Start Over"))
		{
			restart(game.getRange());
		}
		if (menuTitle.equals("Range"))
		{
			setRange();
		}
		if (menuTitle.equals("Reset High Scores"))
		{
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//If we are in the high scores, we will go back to the game
			if (mTabHost.getCurrentTab() != GAME_VIEW_TAB)
			{
				goToHighScores();
				return true;
			} else if (mTabHost.getCurrentTab() == GAME_VIEW_TAB) {
				//if we are in the game, we will ask whether the user wants to quit
				AlertDialog.Builder prompt = new AlertDialog.Builder(this);

				prompt.setMessage(R.string.close);

				prompt.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						NumbersActivity.this.finish();
					}
				});

				prompt.setNegativeButton(R.string.cancel, null);
				prompt.show();
				return true;
			}
		}
		return false;
	}

	/**
	 * Switches the view to high scores, and changes the title of the menu
	 * item to correspond to the view we are on.
	 */
	private void goToHighScores()
	{
		if (!menuOption)
		{
			mTabHost.setCurrentTab(GAME_VIEW_TAB);
			restart(game.getRange());
			menuOption = true;
		}
		else if (menuOption)
		{
			mTabHost.setCurrentTab(HS_VIEW_TAB);
			menuOption = false;
		}
	}

	/**
	 * Sets the range of numbers that we are using.
	 * Default is 1000.
	 * Allows anything in the <code>Spinner</code>
	 */
	private void setRange()
	{
		Spinner hubSpinner = (Spinner) findViewById(R.id.RangeSpinner);
		game.setRange(Integer.parseInt((String) hubSpinner.getSelectedItem()));
	}

	/**
	 * Checks to see if the answer the user entered is correct
	 */
	private void checkAnswer()
	{
		//When the user presses the "Done"/"Next" button to check, it gets
		//called twice...
		if (keyPressCount >= 1)
			return;

		int response;

		try {
			response = Integer.parseInt(answer.getText().toString());
			hiLow.setText(game.checkAnswer(response)); 
		} catch (OutOfBoundsException e){
			Toast.makeText(NumbersActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
			return;
		} catch (Exception e) {
			Toast.makeText(NumbersActivity.this, R.string.bad_input, Toast.LENGTH_SHORT).show();
			return;
		} finally {
			answer.setText("");
		}

		numGuesses.setText("You have guessed " + game.getNumGuesses() + " times.");

		if (hiLow.getText().toString().contains("Correct,")) 
		{
			submit.setText("Restart");
			submit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					//Again, we have the problem of being called twice...
					if (keyPressCount >= 1)
					{
						keyPressCount = -1;
						return;
					}
					restart(game.getRange());
				}
			});
			highScores();
		}
	}

	/**
	 * Checks to see if it is a high score and updates the UI
	 */
	private void highScores()
	{
		checkHighScores();
		updateHighScoresPrefFile();
		printHighScores();
	}

	/**
	 * Checks the high scores and adds them to the <code>ArrayList</code> of
	 * <code>Score</code>s
	 */
	private void checkHighScores()
	{
		boolean added = false;
		if (!game.getFinished()) return;
		//Loop through and add the score in the correct spot
		for (int i = 0; i < scores.size(); i++)
		{
			if (game.getNumGuesses() < scores.get(i).getScore())
			{
				added = true;
				scores.add(i, new Score(game.getRange(),game.getNumGuesses()));

				CheckBox useDefaultCheckBox = (CheckBox) findViewById(R.id.UseDefaultName);
				EditText defaultNameBox = (EditText) findViewById(R.id.DefaultName);
				String defaultName = defaultNameBox.getText().toString();
				
				if (useDefaultCheckBox.isChecked() && defaultName != null && !defaultName.equals(""))
					scores.get(i).setName(defaultName);
				else
					promptForName(scores.get(i));

				if (scores.size() > 10)
					scores.remove(10);
				break;
			}
		}
		//TODO Figure out if a different data structure would be better
		// If we didn't add it, and the size of the ArrayList is smaller than 10, 
		//and the game is over, we will add it
		if (!added && scores.size() < 10 && game.getFinished())
		{
			scores.add(scores.size(), new Score(game.getRange(),game.getNumGuesses())); 
			
			CheckBox useDefaultCheckBox = (CheckBox) findViewById(R.id.UseDefaultName);
			EditText defaultNameBox = (EditText) findViewById(R.id.DefaultName);
			String defaultName = defaultNameBox.getText().toString();
			
			if (useDefaultCheckBox.isChecked() && defaultName != null && !defaultName.equals(""))
				scores.get(scores.size()-1).setName(defaultName);
			else
				promptForName(scores.get(scores.size()-1));
		}
	}

	/**
	 * Prompts the user for their name, and adds it to the given <code>Score</code> object.
	 * 
	 * @param s
	 * The <code>Score</code> the user's name will correspond to.
	 */
	private void promptForName(final Score s)
	{
		AlertDialog.Builder prompt = new AlertDialog.Builder(this);

		prompt.setTitle(R.string.high_score);
		prompt.setMessage(R.string.name_prompt);

		final EditText input = new EditText(this);
		input.setLines(1);
		input.setHint("Name");
		prompt.setView(input);
		
		prompt.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//If they don't enter anything, reprompt until they do
				if (input.getText().toString().equals(""))
					promptForName(s);
				else {
					s.setName(input.getText().toString());

					updateHighScoresPrefFile();
				}
			}
		});

		prompt.show();
	}

	/**
	 * Calculates the high scores and puts them into the Preference List 
	 */
	private void updateHighScoresPrefFile()
	{
		SharedPreferences prefStr = getSharedPreferences("Scores", MODE_WORLD_WRITEABLE);
		Editor edit = prefStr.edit();

		if (!prefStr.contains("1"))
			initScores();

		for (int i = 0; i < scores.size(); i++)
		{
			edit.putString(String.valueOf(i+1), scores.get(i).toString());
		}

		edit.commit();

		printHighScores();
	}

	/**
	 * Prints the high score grid.
	 */
	private void printHighScores()
	{
		SharedPreferences pref = getSharedPreferences("Scores", MODE_WORLD_READABLE);
		// TODO: Use a ListView
		TextView tv = (TextView) findViewById(R.id.hsText1);
		tv.setText("1. " + pref.getString("1", null));
		tv = (TextView) findViewById(R.id.hsText2);
		tv.setText("2. " + pref.getString("2", null));
		tv = (TextView) findViewById(R.id.hsText3);
		tv.setText("3. " + pref.getString("3", null));
		tv = (TextView) findViewById(R.id.hsText4);
		tv.setText("4. " + pref.getString("4", null));
		tv = (TextView) findViewById(R.id.hsText5);
		tv.setText("5. " + pref.getString("5", null));
		tv = (TextView) findViewById(R.id.hsText6);
		tv.setText("6. " + pref.getString("6", null));
		tv = (TextView) findViewById(R.id.hsText7);
		tv.setText("7. " + pref.getString("7", null));
		tv = (TextView) findViewById(R.id.hsText8);
		tv.setText("8. " + pref.getString("8", null));
		tv = (TextView) findViewById(R.id.hsText9);
		tv.setText("9. " + pref.getString("9", null));
		tv = (TextView) findViewById(R.id.hsText10);
		tv.setText("10." + pref.getString("10", null));
	}

	/**
	 * Initializes the high score preference file.
	 */
	private void initScores()
	{
		SharedPreferences pref = getSharedPreferences("Scores", MODE_WORLD_WRITEABLE);

		Editor edit = pref.edit();

		for (int i = 1; i < 11; i++)
		{
			edit.remove(String.valueOf(i));
			edit.putString(String.valueOf(i), "");
			edit.commit();
		}
	}

	/**
	 * Add the <code>Score</code>s to the <code>ArrayList</code> so that
	 * we can keep track of the scores that are stored in between sessions.
	 */
	private void initScoresList()
	{
		SharedPreferences pref = getSharedPreferences("Scores", MODE_WORLD_READABLE);
		int i = 1;
		
		String currentPref = pref.getString(String.valueOf(i), null);
		
		if (currentPref == null) {
			initScores();
		}
		while (currentPref != null && !currentPref.equals(""))
		{
			try {
				scores.add(Score.parseString(pref.getString(String.valueOf(i), "")));
			} catch (ParsingException e) {
				Toast.makeText(this, R.string.hs_error, Toast.LENGTH_SHORT).show();
			}
			i++;
			currentPref = pref.getString(String.valueOf(i), "");
			if (currentPref == null)
				currentPref = "";
		}
	}

}