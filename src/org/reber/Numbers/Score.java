package org.reber.Numbers;

import java.util.Scanner;

/**
 * Represents a score for the Numbers game - contains both a score and
 * a range.
 * 
 * @author brianreber
 */
public class Score {

	private int range;
	private int score;
	private String name;

	/**
	 * Creates a new score with the given range and score
	 * 
	 * @param range Range for this score
	 * @param score The score achieved
	 */
	public Score(int range, int score) {
		this(range, score, "");
	}

	/**
	 * Creates a new score with the given range, score and name
	 * 
	 * @param range Range for this score
	 * @param score The score achieved
	 * @param name The user's name
	 */
	public Score(int range, int score, String name) {
		this.range = range;
		this.score = score;
		this.name = name;
	}

	/**
	 * Gets the score for comparison
	 * 
	 * @return The score of this object
	 */
	public int getScore()
	{
		return score;
	}

	/**
	 * Sets the name for the current score
	 * 
	 * @param name Name to set the name to
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString() {
		if (!name.equals(""))
			return name + " - " + score + " (" + range + ")";

		return score + " (" + range + ")";
	}

	/**
	 * Parses a <code>String</code> and puts the values into a new <code>Score</code>
	 * object.
	 * 
	 * @param s 
	 * <code>String</code> to parse
	 * @return
	 * A new <code>Score</code> object representing the given <code>String</code>
	 */
	public static Score parseString(String s) throws ParsingException
	{
		Scanner scan = new Scanner(s);
		String _name = "";
		int _score = 0;
		int _range = 0;

		int token = 0;
		while (scan.hasNext())
		{
			switch (token)
			{
				case(0): try {
					_name = scan.next(); 
					break;
				} catch (Exception e) {
					throw new ParsingException("Error parsing name");
				}
				case(1): scan.next(); break; // The "-"
				case(2): try {
					_score = scan.nextInt(); 
					break;
				} catch (Exception e) {
					throw new ParsingException("Error parsing score");
				}
				case(3): try {
					String temp = scan.next();
					_range = Integer.parseInt(temp.substring(1,temp.length()-1));
					break;
				} catch (Exception e) {
					throw new ParsingException("Error parsing range");
				}
						
			}
			token++;
		}
		
		return new Score(_range, _score, _name);
	}

}