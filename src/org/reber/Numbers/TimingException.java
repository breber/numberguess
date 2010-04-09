package org.reber.Numbers;

/**
 * A specific version of an exception that can be thrown if the we have
 * problems with our <code>Timer</code>
 * 
 * @author brianreber
 */
@SuppressWarnings("serial")
public class TimingException extends Exception {

	public TimingException()
	{
		super();
	}
	
	public TimingException(String s)
	{
		super(s);
	}
}
