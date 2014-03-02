package com.pierlesstech.journal.core.io;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.pierlesstech.journal.core.UserInfo;
import com.pierlesstech.journal.core.data.Entry;
import com.pierlesstech.journal.exceptions.SizeLimitException;
import com.pierlesstech.journal.exceptions.UserDoesNotExistException;
import com.pierlesstech.journal.exceptions.UserException;

public interface JournalIO extends Serializable {

	public void synchEntryCollection();
	public void createStorage(UserInfo user) throws UserException;
	public Entry getDate(Calendar date);
	public void commitEntry(UserInfo pUser) throws SizeLimitException;
	public void close();
	public void deleteStorage();
	public List<Calendar> wordSearch(List<String> searchString);
	boolean validateUser(UserInfo pUserInfo) throws UserDoesNotExistException;
	public void updateUser(UserInfo pUser) throws UserException;
	public Map<String, String> getBackgrounds();
	Map<String, String> getPaper();
	public Map<String, String> getFontFamilies();
	
}
