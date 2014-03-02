package com.pierlesstech.journal.core;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.persistence.Embedded;
import javax.persistence.Id;


import com.google.appengine.api.users.User;
import com.pierlesstech.journal.core.UserInfo;
import com.pierlesstech.journal.core.data.EntryFactory;
import com.pierlesstech.journal.core.data.impl.GeneralEntry;
import com.pierlesstech.journal.exceptions.UserException;
import com.pierlesstech.journal.web.utilities.MiscUtilities;

public class UserInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Key info
	@Id String name;
	String password;
	boolean isGoogleAccount;
	User googleAccount;
	long sizeLimit;

	//Settings
	String colorPackage;
	String fontFamily;
	String fontSize;

	//Entrie store
	@Embedded public Map<String, GeneralEntry> entries;

	public UserInfo() {
		entries = new HashMap<String, GeneralEntry>();
	}

	@Override
	public String toString() {
		String result = "";
		result += name + "\n";
		result += fontFamily + "\n";
		result += fontSize + "\n";
		result += colorPackage + "\n";
		for (String key : entries.keySet()) {
			GeneralEntry entry = entries.get(key);
			result += key + ":" + entry.toString();
		}
		return result;
	}


	public UserInfo(User pUser) {
		name = pUser.getEmail();
		password = "";
		googleAccount = pUser;
		isGoogleAccount = true;
		colorPackage = "";
		sizeLimit = 10*1024*1024; //10MB

		//default fonts
		fontFamily = "Arial, Helvetica, sans-serif";
		fontSize = "16px";
		colorPackage = "Nottingham.css";
		entries = new HashMap<String, GeneralEntry>();

	}

	public UserInfo(String pName, String pPassword) throws UserException {
		if (validateName(pName)  && validateName(pPassword)) {
			name = pName;
			isGoogleAccount = false;
			colorPackage = "";
			//			BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
			//			password = passwordEncryptor.encryptPassword(pPassword);
			password = pPassword;
			sizeLimit = 10*1024*1024; //10MB
			//default fonts
			fontFamily = "Arial, Helvetica, sans-serif";
			fontSize = "16px";
			colorPackage = "Nottingham.css";
			entries = new HashMap<String, GeneralEntry>();

		}
		else {
			throw new UserException();
		}
	}

	public UserInfo(String pName, String pPassword, String pFontFamily, String pFontSize) throws UserException {
		if (validateName(pName)  && validateName(password)) {
			name = pName;
			isGoogleAccount = false;
			colorPackage = "";
			//			BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
			//			password = passwordEncryptor.encryptPassword(pPassword);
			password = pPassword;
			//default fonts
			fontFamily = pFontFamily;
			fontSize = pFontSize;
			colorPackage = "sand.jpg";
			entries = new HashMap<String, GeneralEntry>();

		}
		else {
			throw new UserException();
		}
	}


	public void changeBackground(String pBackgroundImage) {
		colorPackage = pBackgroundImage;
	}

	public void changeFont(String pFontFamily, String pFontSize) {
		fontFamily = pFontFamily;
		fontSize = pFontSize;
	}

	/**
	 * Username must be at least 6 characters and contain letters, numbers, and special characters. 
	 * No spaces, /, or ".
	 * @param pName
	 * @return
	 */
	private boolean validateName(String pName) {
		boolean result = true;
		System.out.println("Verifying " + pName);
		if (pName == null) {
			return false;
		}
		if (pName.length() < 6)
			result = false;
		if (pName.contains(" "))
				result = false;
		if (pName.contains("/"))
			result = false;				
		if (pName.contains("\\"))
			result = false;
		if (pName.contains("\""))
				result = false;
		return result;
	}

	public String getName() {
		return name;
	}

	public String getPassword () {
		return password;
	}




	public String getBackgroundImage() {
		return colorPackage;

	}

	public String getFontFamily() {
		return fontFamily;
	}

	public String getFontSize() {
		return fontSize;
	}



	// A better definition, but still not perfect
	@Override 
	public boolean equals(Object other) {
		boolean result = true;
		try {
			if (other instanceof UserInfo) {
				UserInfo that = (UserInfo) other;
				if (!name.equals(that.name)) {
					result = false;
				}
				else if (!password.equals(that.password)) {
					result = false;
				}

				else if (!colorPackage.equals(that.colorPackage)) {
					result = false;
				}	
				else if (!fontFamily.equals(that.fontFamily)) {
					result = false;
				}	
				else if (!fontSize.equals(that.fontSize)) {
					result = false;
				}	
				else {
					result = true;
				}
			}
			else {
				result = false;
			}
		}
		catch (ArrayIndexOutOfBoundsException exp) {
			result = false;
		}
		return result;
	}

	public GeneralEntry getEntry(Calendar c) {
		GeneralEntry e;
		String date = MiscUtilities.printSimpleTime(c);
		e = entries.get(date);
		if (e == null) {
			e = EntryFactory.createEntry();
			entries.put(date, e);
		}
		return e;
	}

	public List<Calendar> wordSearch(List<String> searchString) {
		List<Calendar> searchResults = new ArrayList<Calendar>();
		for (String date : entries.keySet()) {
			GeneralEntry entry = entries.get(date);
			Iterator<String> strings = searchString.iterator();
			boolean allIn = true;
			while (strings.hasNext()) {		
				String next = strings.next();
				next = next.toLowerCase();
				//			System.out.println("Search for string +" + next + "+");
				if (!entry.containsPhrase(next)) {
					//				System.out.println("Did not match");
					allIn = false;
				}
			}
			if (allIn) {
				Calendar c = MiscUtilities.convertHtml5Date(date);
				searchResults.add(c);
			}
		}
		return searchResults;
	}


	public boolean withinSizeLimit() {
		if (getStorageSize() > sizeLimit) {
			return false;
		}
		else {
			return true;
		}
	}

	private long getStorageSize() {
		long size = 0;
		for (GeneralEntry entry : entries.values()) {
			size += entry.sizeInBytes();
		}


		return size;

	}
	
	public void removeImage(Calendar c, int index) {
		String date = MiscUtilities.printSimpleTime(c);
		entries.get(date).removeImage(index);
	}

	public void changePassword(String pw1) {
		password = pw1;
		
	}
}
