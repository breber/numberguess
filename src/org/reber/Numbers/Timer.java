package org.reber.Numbers;

/**
 * @author brianreber
 *
 */
public class Timer {

	private long startTime;
	private long endTime;
	private boolean stopped;
	
	public Timer()
	{
		stopped = true;
		startTime = -1;
		endTime = -1;
	}

	public void start() {
		startTime = System.currentTimeMillis();
		stopped = false;
	}

	public void stop() {
		endTime = System.currentTimeMillis();
		stopped = true;
	}

	public double getSeconds() {
		if (stopped && endTime != -1)
			return ((endTime - startTime) / 1000.0);
		else if (!stopped && startTime != -1)
			return ((System.currentTimeMillis() - startTime) / 1000.0);
		else
			return -1;
	}
}