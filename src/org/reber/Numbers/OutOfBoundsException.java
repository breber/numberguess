package org.reber.Numbers;

/**
 * A specific version of an exception that can be thrown if the number
 * guess was out of bounds.  Makes the exception handling easier because
 * we can tell what exactly the exception is about.
 * 
 * @author brianreber
 */
@SuppressWarnings("serial")
public class OutOfBoundsException extends Exception {

	public OutOfBoundsException() {
		super();
	}
	
	public OutOfBoundsException(String s) {
		super(s);
	}
}
