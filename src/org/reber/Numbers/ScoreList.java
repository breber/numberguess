package org.reber.Numbers;

import java.util.ArrayList;

public class ScoreList extends ArrayList<Score> {
	private static final long serialVersionUID = 1L;

	private static int MAX_SIZE = 10;
	
	public ScoreList() {
		super(MAX_SIZE);
	}
	
	@Override
	public boolean add(Score object) {
		int canAdd = getLocationToAdd(object);
		
		if (canAdd > -1) {
			add(canAdd, object);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean canAdd(Score score) {
		return getLocationToAdd(score) > -1;
	}
	
	private int getLocationToAdd(Score score) {
		// If we are at the max size and the given score is greater than the last score in the 
		// list, we can't add it
		if (size() == MAX_SIZE && score.compareTo(get(size() - 1)) > 0) {
			return -1;
		} else {
			if (size() == 0 || score.compareTo(get(0)) < 0) {
				return 0;
			}
			for (int i = 0; i < size(); i++) {
				if (score.compareTo(get(i)) < 0) {
					return i;
				}
			}
			if (size() != MAX_SIZE) {
				return size();
			} else {
				return -1;
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Score s : this) {
			sb.append(s);
			sb.append(",");
		}
		
		return sb.toString();
	}
}
