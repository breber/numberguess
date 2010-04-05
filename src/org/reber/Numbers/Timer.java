package org.reber.Numbers;

/**
 * A simple stopwatch-like timer.
 * 
 * @author brianreber
 */
public class Timer {

	private long startTime;
	private long endTime;
	private boolean stopped;
	
	/**
	 * Create a new Timer instance that is stopped.
	 */
	public Timer()
	{
		stopped = true;
		startTime = -1;
		endTime = -1;
	}

	/**
	 * Start timing.
	 */
	public void start() {
		startTime = System.currentTimeMillis();
		stopped = false;
	}

	/**
	 * Stop timing.
	 */
	public void stop() {
		endTime = System.currentTimeMillis();
		stopped = true;
	}

	/**
	 * Get amount of seconds ellapsed.
	 * 
	 * @return
	 * Amount of seconds ellapsed from starting time.<br />
	 * (-1) if the timer hasn't been started 
	 */
	public double getSeconds() {
		if (stopped && endTime != -1)
			return ((endTime - startTime) / 1000.0);
		else if (!stopped && startTime != -1)
			return ((System.currentTimeMillis() - startTime) / 1000.0);
		else
			return -1;
	}
}