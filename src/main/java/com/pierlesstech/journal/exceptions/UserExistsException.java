package com.pierlesstech.journal.exceptions;

import com.pierlesstech.journal.exceptions.UserException;

public class UserExistsException extends UserException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UserExistsException() {
		super("User already exists");
	}
	
	

}
