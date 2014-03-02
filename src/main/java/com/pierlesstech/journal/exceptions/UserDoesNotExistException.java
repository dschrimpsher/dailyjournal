package com.pierlesstech.journal.exceptions;

import com.pierlesstech.journal.exceptions.UserException;

public class UserDoesNotExistException extends UserException {

/**
 * 
 */
private static final long serialVersionUID = 1L;

public  UserDoesNotExistException() {
	super("User Does not exists");
}

}