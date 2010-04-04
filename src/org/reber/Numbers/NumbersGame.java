package org.reber.Numbers;

import java.util.Random;

public class NumbersGame {

	private Random rand;
	private static int range;
	private int correctAnswer;
	private int numGuesses;	
	private boolean finished;
	
	public NumbersGame()
	{
		rand = new Random();
		range = 1000;
		numGuesses = 0;
		correctAnswer = rand.nextInt(range) + 1;
		finished = false;
	}
	
	public NumbersGame(int range)
	{
		rand = new Random();
		NumbersGame.range = range;
		numGuesses = 0;
		correctAnswer = rand.nextInt(range) + 1;
		finished = false;
	}
	
	public int getRange()
	{
		return range;
	}
	
	public void setRange(int range)
	{
		NumbersGame.range = range;
	}
	
	public int getNumGuesses()
	{
		return numGuesses;
	}
	
	public boolean getFinished()
	{
		return finished;
	}
	
	public String checkAnswer(int ans) throws OutOfBoundsException
	{
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
