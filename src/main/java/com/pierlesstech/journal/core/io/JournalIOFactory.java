package com.pierlesstech.journal.core.io;


public class JournalIOFactory {

	public static JournalIO getInstance() {
		return (new NoSQLIO());
	}
	
}
