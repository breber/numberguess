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

import java.util.Random;

public class NumbersGame {

	private Random rand;
	private static int range;
	private int correctAnswer;
	private int numGuesses;	
	private boolean finished;
	
	/**
	 * Create a new Numbers game with default values.
	 */
	public NumbersGame() {
		this(1000);
	}
	
	/**
	 * Create a new Numbers game with the given range.
	 * 
	 * @param range
	 * The range of the new Numbers game
	 */
	public NumbersGame(int range) {
		rand = new Random();
		NumbersGame.range = range;
		numGuesses = 0;
		correctAnswer = rand.nextInt(range) + 1;
		finished = false;
	}
	
	/**
	 * Gets the current range of the game.
	 * 
	 * @return
	 * The current range of the game.
	 */
	public int getRange() {
		return range;
	}
	
	/**
	 * Sets the range of the game.
	 * Assumes the game will be restarted after this is called.
	 * 
	 * @param range
	 * New range for the game.
	 */
	public void setRange(int range) {
		NumbersGame.range = range;
	}
	
	/**
	 * Gets the number of guesses used.
	 * 
	 * @return
	 * Number of guesses used
	 */
	public int getNumGuesses() {
		return numGuesses;
	}
	
	/**
	 * Gets whether the current game has finished (user has guessed correctly)
	 * 
	 * @return
	 * Whether the current game has finished
	 */
	public boolean getFinished() {
		return finished;
	}
	
	/**
	 * Checks the given answer against the correct answer.
	 * 
	 * @param ans
	 * The guessed answer
	 * @return
	 * (ans) is too high!" if ans is above the correct answer<br />
	 * (ans) is too low!" if ans is below the correct answer<br />
	 * Correct, the answer was (ans)!" if ans is correct
	 * @throws OutOfBoundsException
	 * If (ans) is not in the range of the game.
	 */
	public String checkAnswer(int ans) throws OutOfBoundsException {
		if (ans > range || ans < 0) throw new OutOfBoundsException(ans + " is out of range");
		
		if (ans > correctAnswer)
    	{	 
    		numGuesses++;
    		return ans + " is too high!";
    	}
    	if (ans < correctAnswer)
    	{	
    		numGuesses++;
    		return ans + " is too low!";  	
    	}
    	if (ans == correctAnswer)
    	{	
    		numGuesses++;
    		finished = true;
    		return "Correct, the answer was "+correctAnswer+"!"; 
    	}
    	
    	return "";
	}
}
