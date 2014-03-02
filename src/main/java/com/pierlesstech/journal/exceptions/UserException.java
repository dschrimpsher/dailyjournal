package com.pierlesstech.journal.exceptions;

public class UserException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserException() {
		super("Invalide User Name");
	}
	
	public UserException(String msg) {
		super(msg);
	}
}
