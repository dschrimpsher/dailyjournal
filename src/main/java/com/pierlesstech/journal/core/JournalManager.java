/**
 * 
 */
package com.pierlesstech.journal.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import java.util.List;


import com.google.appengine.api.users.User;
import com.pierlesstech.journal.core.UserInfo;
import com.pierlesstech.journal.core.data.DataItem;
import com.pierlesstech.journal.core.data.impl.GeneralEntry;
import com.pierlesstech.journal.core.data.impl.ImageItem;
import com.pierlesstech.journal.core.data.impl.Text;
import com.pierlesstech.journal.core.io.JournalIO;
import com.pierlesstech.journal.core.io.JournalIOFactory;
import com.pierlesstech.journal.exceptions.SizeLimitException;
import com.pierlesstech.journal.exceptions.UserDoesNotExistException;
import com.pierlesstech.journal.exceptions.UserException;

/**
 * JournalManager is the access point for all core journal functions.  This includes HMI independent 
 * functions such as creating users, validating accounts, saving entries, loading entries, and deleteing user accounts. 
 * 
 * @author dan
 *
 */
public class JournalManager implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private GeneralEntry currentEntry;
	private Calendar currentDay;
	private UserInfo user;
	private JournalIO io;
	
	/**
	 * Constructor, creates a new Journal Manager. 
	 */
	public JournalManager() {
		currentEntry = null;
		currentDay = GregorianCalendar.getInstance();
		io = JournalIOFactory.getInstance();

	}
	
	public void synchUser(UserInfo pUser) {
		user = pUser;
	}
	
	/**
	 * returns the current day 
	 * @return
	 */
	public Calendar getCurrentDay() {
		return currentDay;
	}
	
	public void setCurrentDay(Calendar c) {
		currentDay = c;
		
	}
	
	/**
	 * Validates that the user, pUser, has an account in the system.
	 * @param pUser account info (username and password) for the user.
	 * @return true if a valid user, false otherwise
	 */
	public boolean login(String username, String password) {
		boolean result = false;
		try {
			user = new UserInfo(username, password);
			if (io.validateUser(user)) {
					result = true;
			}
		} catch (UserDoesNotExistException e) {
			result = false;
		}
		catch (UserException exp) {
			result = false;
		}
		return result;
	}
	
	/**
	 * Creates all necessary account information for a new user.
	 * 
	 * @param pUser user account information
	 * @return true if user was created
	 * @throws UserException if user already exists or account information does not fit requirements.
	 */
	public boolean createUser(String username, String password) throws UserException {
		user = new UserInfo(username, password);
		io.createStorage(user);
		return true;		
	}
	

	
	
	/**
	 * Syncs local database with cloud.  
	 * 
	 * Not used on Web Journals.
	 */
	public void syncJournal() {
		
//		io.setUser(user);
		//Make sure the local copy is synch with the server
		io.synchEntryCollection();
		
	}

	/**
	 * Returns the current GeneralEntry stored for the given data.
	 * @param pDate date to look up
	 * @return GeneralEntry stored in date.  If no GeneralEntry is stored a blank one is created.
	 * @throws IllegalArgumentException if Date is invalide. 
	 */
	public GeneralEntry getEntry(Calendar pDate) throws IllegalArgumentException{
		if (pDate == null) {
			throw new IllegalArgumentException();
		}
		currentDay = pDate;
		currentEntry = user.getEntry(pDate);


		return currentEntry;
	}
	
	/**
	 * Removes account and storage for current user. 
	 */
	public void deleteUser() {
		io.deleteStorage();
	}
	
	/**
	 * Stores the current GeneralEntry to storage for the current date.
	 */
	public void saveEntry(String text) throws SizeLimitException {
		GeneralEntry entry = user.getEntry(currentDay);
		Text di;
		if (entry.isEmpty()) {
			di = new Text();
			entry.addText(di);
		}
		else {
			di = entry.get();
		}
		di.setData(text);
		io.commitEntry(user);
		currentEntry.resetDirty();
	}
	

	/**
	 * Searches all entries for the current account.  Can be a word or a phrase.  
	 * Returns a list of dates with entries containing the search phrase.
	 * 
	 * @param searchString a search string.  Can be a word or phrase
	 * @return List of dates that have matching entries. 
	 */
	public List<Calendar> wordSearch(List<String> searchString) {
		List<Calendar> results = user.wordSearch(searchString);
		return results;
		
	}
	
	/**
	 * Update the user info stored on the database.  This includes paper, background, and font.
	 * 
	 */
	public void updateUser() throws UserException {
		io.commitEntry(user);
	}

	/**
	 * return current users information.
	 */
	public UserInfo getCurrentUser() {
		return user;
	}
	
	/**
	 * @return
	 */
	public Map<String, String>  getBackgrounds() {
		
//		List<String> bgs = new ArrayList<String>();
//		bgs.add("none");
//		bgs.add("sand.jpg");
		Map<String, String> bgs = io.getBackgrounds();
		
		
		return bgs;
		
		
		
	}
	

	
	public  Map<String, String> getFontFamilies() {

		Map<String, String> bgs = io.getFontFamilies();

		return bgs;
	}
	
	public List<String> getFontSizes() {
		List<String> bgs = new ArrayList<String>();
		for  (int i = 8; i < 60; i += 2) {
			bgs.add(i + "px");
		}
		return bgs;
	}

	public void addImage(String urlImage) throws SizeLimitException{
		GeneralEntry e = getEntry(currentDay);
		
//		ImageItem it = new ImageItem();
//		it.setData(urlImage);
		e.addImage(urlImage);		
		io.commitEntry(user);
	}

	public boolean createGoogleUser(User user2) throws UserException {
		user = new UserInfo(user2);
		io.createStorage(user);
		return true;	
	}

	public void removeImage(int index) {
		user.removeImage(currentDay, index);
	}


}
