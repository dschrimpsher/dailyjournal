package com.pierlesstech.journal.core.io;


import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.pierlesstech.journal.core.UserInfo;
import com.pierlesstech.journal.core.data.Entry;
import com.pierlesstech.journal.exceptions.SizeLimitException;
import com.pierlesstech.journal.exceptions.UserDoesNotExistException;
import com.pierlesstech.journal.exceptions.UserException;
import com.pierlesstech.journal.exceptions.UserExistsException;

public class NoSQLIO implements JournalIO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	static {
		ObjectifyService.register(UserInfo.class);
	}



	@Override
	public void synchEntryCollection() {

	}



	@Override
	public void createStorage(UserInfo user) throws UserException {
		Objectify ofy = ObjectifyService.begin();

		try {

			UserInfo userExists = ofy.get(UserInfo.class, user.getName());
			throw new UserExistsException();
		}
		catch (NotFoundException exp){
			//good to go!
			ofy.put(user);

		}

	}

	@Override
	public Entry getDate(Calendar date) {
		return null;
	}

	@Override
	public void commitEntry(UserInfo user) throws SizeLimitException {
		if (user.withinSizeLimit()) {
		Objectify ofy = ObjectifyService.begin();
		ofy.put(user);
		}
		else {
			throw new SizeLimitException();
		}


	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteStorage() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Calendar> wordSearch(List<String> searchString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateUser(UserInfo pUserInfo)
			throws UserDoesNotExistException {
		try {
			boolean result = false;
			Objectify ofy = ObjectifyService.begin();
			UserInfo userExists = ofy.get(UserInfo.class, pUserInfo.getName());
			if (pUserInfo.getPassword().equals(userExists.getPassword())) {
				result = true;
				pUserInfo.changeBackground(userExists.getBackgroundImage());
				pUserInfo.changeFont(userExists.getFontFamily(), userExists.getFontSize());
				pUserInfo.entries = userExists.entries;
			}
			else {
				System.out.println("Password " + pUserInfo.getPassword() + " does not match " + userExists.getPassword());
			}
			return result;
		}
		catch (NotFoundException exp) {
			exp.printStackTrace();
			throw new UserDoesNotExistException();
		}
	}

	@Override
	public void updateUser(UserInfo pUser) throws UserException {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, String> getBackgrounds() {
		Map<String, String>defaultMap = new TreeMap<String, String>();
		defaultMap.put("Antique", "Antique.css");
		defaultMap.put("Nottingham", "Nottingham.css");
		defaultMap.put("Sassy", "Sassy.css");

		return defaultMap;
	}

	@Override
	public Map<String, String> getPaper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getFontFamilies() {
		Map<String, String>defaultMap = new TreeMap<String, String>();
		defaultMap.put("Arial", "Arial, Helvetica, sans-serif");
		defaultMap.put("Droid Serif", "'Droid Serif', serif");
		defaultMap.put("Spirax", "Spirax, serif");
		defaultMap.put("Tangerine", "Tangerine, cursive");

		return defaultMap;

	}

}
