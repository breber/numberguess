package org.reber.Numbers;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * A game where the user guesses a number between zero and 1000 (or
 * another number that is pre-specified). We will then tell them whether
 * their guess is too high, or too low.
 * 
 * @author brianreber
 */
public class Numbers extends Activity {

	private NumbersGame game;

	private ArrayList<Score> scores = new ArrayList<Score>();

	private int keyPressCount = 0;

	//We keep this MenuItem so that we can change its label
	//when the user presses the back button
	private MenuItem hScore;

	//For the progress bar on the logo screen
	private ProgressBar progress;
	private Timer time = new Timer();

	//The Handler allows us to send events from the two threads
	private final Handler handler = new Handler();

	//This is what is "posted" to the handler - the run() method gets
	//run when the progress bar finishes
	private final Runnable run = new Runnable() {
		public void run() {
			flipView();
			time.stop();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		progress = (ProgressBar) findViewById(R.id.loading);

		// New numbers game
		game = new NumbersGame();

		// Set the range spinner value to the current range
		Spinner hubSpinner = (Spinner) findViewById(R.id.numSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.limits, android.R.layout.simple_spinner_item); 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		int position = adapter.getPosition(String.valueOf(game.getRange()));
		hubSpinner.setAdapter(adapter);
		hubSpinner.setSelection(position);
		hubSpinner.setVisibility(View.INVISIBLE);

		//Start timing
		time.start();
		//Create a new thread to update the progress bar - need
		//another thread otherwise the UI wouldn't show up until
		//this has finished running.
		new Thread() {
			@Override
			public void run() {
				//While the ProgressBar isn't finished, we will update
				//it with the current amount of seconds given by our Timer
				while (progress.getProgress() < progress.getMax()) 
				{
					//Try to update
					try {
						// Update the progress bar
						progress.setProgress((int) time.getSeconds());
					} catch (TimingException e) {
						//If we run into problems, we will automatically
						//forward them to the game
						handler.post(run);
					}
				}
				//When the ProgressBar is finished, we come here and post
				//to the Handler our Runnable object
				handler.post(run);
			}
		}.start();

		//Set the action to be completed when we click the Submit button
		final Button submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				checkAnswer();
			}
		});

		//Set the properties for the EditText (where we type our answers)
		EditText number = (EditText) findViewById(R.id.guess);
		number.setOnKeyListener(new OnKeyListener() {
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
		initScoresList();

		highScores();
	}

	/**
	 * Flips the view from the logo screen to the game view
	 */
	private void flipView()
	{
		ViewFlipper change = (ViewFlipper) findViewById(R.id.viewFlipper);
		View view = (View) findViewById(R.id.logoView);

		Animation animate = new AlphaAnimation(0,1);
		Animation animate1 = new AlphaAnimation(1,0);

		animate.setDuration(1000);
		animate1.setDuration(1000);

		change.setInAnimation(animate);
		change.setOutAnimation(animate1);
		change.removeViewInLayout(view);

		change.setOnClickListener(null);
	}

	/**
	 * Restarts the game.  Resets the UI and makes a new random number.
	 */
	private void restart(int range)
	{
		game = new NumbersGame(range);

		TextView numGuess = (TextView) findViewById(R.id.numGuesses);
		EditText answer = (EditText) findViewById(R.id.guess);
		TextView correction = (TextView) findViewById(R.id.result);

		numGuess.setText("");
		correction.setText("");
		answer.setText("");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ViewFlipper change = (ViewFlipper) findViewById(R.id.viewFlipper);
			//If we are in the high scores, we will go back to the game
			if (change.getCurrentView().getId() == R.id.hsTable)
			{
				if (hScore != null)
					goToHighScores(hScore);
				return true;

			} else if (change.getCurrentView().getId() == R.id.gameView) {
				//if we are in the game, we will ask whether the user wants to quit
				AlertDialog.Builder prompt = new AlertDialog.Builder(this);

				prompt.setMessage(R.string.close);

				prompt.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Numbers.this.finish();
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
	 * Gets called when the menu button is pressed.
	 * param menu
	 * The menu instance that we apply a menu to
	 * return
	 * true so that it uses our own implementation
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ViewFlipper change = (ViewFlipper) findViewById(R.id.viewFlipper);

		// If there are only 2 views in the ViewFlipper, we will 
		// show the menu.
		if (change.getChildCount() == 2)
		{
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.gamemenu, menu);

			return true;
		}

		// There are 3 views, meaning that we still have the logo displayed.
		// We don't want to show the menu if the logo screen is displayed
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		String menuTitle = item.getTitle().toString();

		if (menuTitle.equals("Start Over"))
		{
			restart(game.getRange());
		}
		if (menuTitle.equals("High Scores") || menuTitle.equals("Back to Game"))
		{
			//Save the MenuItem so we can switch back the label if we press the back button
			hScore = item;
			goToHighScores(item);	
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

	/**
	 * Switches the view to high scores, and changes the title of the menu
	 * item to correspond to the view we are on.
	 * 
	 * @param item
	 * The menu item that we clicked on. Used to switch the label.
	 */
	private void goToHighScores(MenuItem item)
	{
		ViewFlipper change = (ViewFlipper) findViewById(R.id.viewFlipper);

		if (item.getTitle().toString().equals("High Scores"))
		{
			change.showNext();
			item.setTitle("Back to Game");
		}
		else if (item.getTitle().toString().equals("Back to Game"))
		{
			change.showNext();
			restart(game.getRange());
			item.setTitle("High Scores");
		}
	}

	/**
	 * Sets the range of numbers that we are using.
	 * Default is 1000.
	 * Allows anything in the <code>Spinner</code>
	 */
	private void setRange()
	{
		// Get the spinner
		Spinner hubSpinner = (Spinner) findViewById(R.id.numSpinner);
		// Get the options from the limits.xml file
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.limits, android.R.layout.simple_spinner_item); 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Get the position of the current range
		int pos = adapter.getPosition(String.valueOf(game.getRange()));
		hubSpinner.setAdapter(adapter);
		// Set the current selected option
		hubSpinner.setSelection(pos);
		hubSpinner.setVisibility(View.VISIBLE);

		// Disable the other fields
		Button submit = (Button) findViewById(R.id.submit);
		EditText number = (EditText) findViewById(R.id.guess);
		submit.setEnabled(false);
		number.setEnabled(false);

		Button submit1 = (Button) findViewById(R.id.spinnerButton);
		submit1.setVisibility(View.VISIBLE);

		submit1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Spinner hubSpinner = (Spinner) findViewById(R.id.numSpinner);
				String str = (String)hubSpinner.getSelectedItem();
				game.setRange(Integer.parseInt(str));

				TextView label = (TextView) findViewById(R.id.label);
				label.setText("Please enter a number between 1 & "+game.getRange()+".");
				hubSpinner.setVisibility(View.INVISIBLE);
				Button submit1 = (Button) findViewById(R.id.spinnerButton);
				submit1.setVisibility(View.INVISIBLE);

				// Reenable the other UI elements
				Button submit = (Button) findViewById(R.id.submit);
				EditText number = (EditText) findViewById(R.id.guess);
				number.setEnabled(true);
				submit.setEnabled(true);

				restart(game.getRange());
			}
		});
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

		EditText answer = (EditText) findViewById(R.id.guess);
		TextView correction = (TextView) findViewById(R.id.result);
		TextView numGuess = (TextView) findViewById(R.id.numGuesses);
		Editable userResponse;
		int response;

		try {
			userResponse = answer.getText();
			response = Integer.parseInt(userResponse.toString());
			correction.setText(game.checkAnswer(response)); 

		} catch (OutOfBoundsException e){
			Toast.makeText(Numbers.this, e.getMessage(), Toast.LENGTH_SHORT).show();
			return;
		} catch (Exception e) {
			Toast.makeText(Numbers.this, R.string.bad_input, Toast.LENGTH_SHORT).show();
			return;
		} finally {
			answer.setText("");
		}

		numGuess.setText("You have guessed " + game.getNumGuesses() + " times.");

		if (correction.getText().toString().contains("Correct,")) 
		{
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

				promptForName(scores.get(i));

				if (scores.size() > 10)
					scores.remove(10);
				break;
			}
		}

		// If we didn't add it, and the size of the ArrayList is smaller than 10, 
		//and the game is over, we will add it
		if (!added && scores.size() < 10 && game.getFinished())
		{
			scores.add(scores.size(), new Score(game.getRange(),game.getNumGuesses()));			
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
		if (pref.getString(String.valueOf(i), null) == null) {
			initScores();
		}
		while (!pref.getString(String.valueOf(i), null).equals(""))
		{
			try {
				scores.add(Score.parseString(pref.getString(String.valueOf(i), null)));
			} catch (ParsingException e) {
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle(R.string.error);
				alert.setMessage(R.string.hs_error);
				alert.setPositiveButton(R.string.reset_hs, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						initScores();
					}				
				});
				alert.show();
			}
			i++;
		}
	}
}