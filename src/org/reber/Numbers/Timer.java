package org.reber.Numbers;

/**
 * A simple stop watch-like timer.
 * 
 * @author brianreber
 */
public class Timer {

	private long startTime;
	private long endTime;
	private long elapsedTime;
	private boolean stopped;

	/**
	 * Create a new Timer instance that is stopped.
	 */
	public Timer()
	{
		stopped = true;
		startTime = -1;
		endTime = -1;
		elapsedTime = 0;
	}

	/**
	 * Start timing - clear the old time and start over.
	 */
	public void start() {
		elapsedTime = 0;
		startTime = System.currentTimeMillis();
		stopped = false;
	}

	/**
	 * Start timing.
	 * 
	 * @param continueTiming
	 * If we want to continue timing from where we last stopped, pass true
	 */
	public void start(boolean continueTiming) {
		if (continueTiming)
		{
			startTime = System.currentTimeMillis();
			stopped = false;
		} else {
			start();
		}
	}

	/**
	 * Stop timing. We add the end time - start time to 
	 * elapsed time in case we want to continue timing.
	 */
	public void stop() {
		endTime = System.currentTimeMillis();
		elapsedTime += ((endTime - startTime) / 1000.0);
		stopped = true;
	}

	/**
	 * Get amount of seconds elapsed.
	 * 
	 * @return
	 * Amount of seconds elapsed from starting time.
	 * @throws TimingException 
	 * If the timer hasn't been started
	 */
	public double getSeconds() throws TimingException {
		if (stopped)
		{
			if (elapsedTime == 0)
				throw new TimingException("The timer hasn't ever been started");
			else return elapsedTime;
		} else {
			if (startTime == -1 || endTime == -1)
				throw new TimingException("The timer hasn't ever been started");
			else
				return elapsedTime + ((System.currentTimeMillis() - startTime) / 1000.0);
		}
	}
}