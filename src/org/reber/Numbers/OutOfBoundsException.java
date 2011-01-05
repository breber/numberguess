/*
 * Copyright (C) 2011 Brian Reber
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
