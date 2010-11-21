package org.reber.Numbers;

/**
 * A specific version of an exception that can be thrown if the we have
 * problems parsing our <code>String</code>
 * 
 * @author brianreber
 */
@SuppressWarnings("serial")
public class ParsingException extends Exception {

	public ParsingException() {
		super();
	}
	
	public ParsingException(String s) {
		super(s);
	}
}
