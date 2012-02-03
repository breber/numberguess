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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Represents a score for the Numbers game - contains both a score and
 * a range.
 * 
 * @author brianreber
 */
public class Score implements Comparable<Score>{

	public static final class Scores implements BaseColumns {
		private Scores() {}

		public static final String KEY_RANGE	= "range";
		public static final String KEY_SCORE	= "score";
		public static final String KEY_NAME 	= "name";

		public static final Uri CONTENT_URI = Uri.parse("content://" + HSContentProvider.AUTHORITY + "/scores");
		public static final String TABLE = "scores";
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.reber.score";
		public static final String DEFAULT_SORT = KEY_SCORE + " desc";
	}

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
	public int getScore() {
		return score;
	}

	/**
	 * Gets the range for comparison
	 * 
	 * @return The range of this object
	 */
	public int getRange() {
		return range;
	}

	/**
	 * Gets the name of the person for this Score object
	 * 
	 * @return The name of this object
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name for the current score
	 * 
	 * @param name Name to set the name to
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		if (!name.equals("")) {
			return name + " - " + score + " (" + range + ")";
		}

		return score + " (" + range + ")";
	}

	@Override
	public int compareTo(Score another) {
		return ((Integer) score).compareTo(another.score);
	}
}
