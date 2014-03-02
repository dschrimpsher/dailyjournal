package com.pierlesstech.journal.core.data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;



public class EntryCollection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Calendar, Entry>  entryList;
	Calendar lastModified;

	public EntryCollection() {
		entryList = new HashMap<Calendar, Entry>();	
		lastModified = null;
	}
	
	public void copyCollection(EntryCollection ec) {
		this.entryList = ec.entryList;
		this.lastModified = ec.lastModified;
	}
	
	
	

	public long getLastModified() {
		long lm = 0L;
		if (lastModified != null) {
			lm =  lastModified.getTimeInMillis();
		}
		return lm;
	}
	public Entry getEntry(Calendar c) {
		zeroOutCalendarTime(c);
		Entry e = null;
//		if (entryList.containsKey(c )) {
//			e = entryList.get(c);
//		}
//		else {
////			e = EntryFactory.createEntry();
//			entryList.put((Calendar)c.clone(), e);
//		}
//		lastModified = GregorianCalendar.getInstance();
		return e;
	}

	protected void zeroOutCalendarTime(Calendar c) {
		//		printTime(c);
		c.add(Calendar.HOUR, -c.get(Calendar.HOUR));
		c.add(Calendar.MINUTE, -c.get(Calendar.MINUTE));
		c.add(Calendar.SECOND, -c.get(Calendar.SECOND));
		c.add(Calendar.MILLISECOND, -c.get(Calendar.MILLISECOND));
		//		printTime(c);

	}

	private String printTime(Calendar date) {
		long day = date.get(Calendar.DAY_OF_MONTH);
		long month = date.get(Calendar.MONTH);
		long year = date.get(Calendar.YEAR);

		long mill = date.get(Calendar.MILLISECOND);
		long second = date.get(Calendar.SECOND);
		long minute = date.get(Calendar.MINUTE);
		long hour = date.get(Calendar.HOUR);

		String temp = (month+1)+"/"+ day + "/" + year;
		temp += "  " + hour+":"+minute+":"+second + "." + mill;
		return temp;
		
//		System.out.println("Current date is  "+day+"/"+(month+1)+"/"+year);
//		System.out.println("Current time is  "+hour+" : "+minute+" : "+second + "." + mill);
	}

	@Override
	public String toString() {
		String temp = "Entry Collection\nLast Modified:  " + printTime(lastModified) + "\n";
		for (Calendar c : entryList.keySet()) {
			temp += printTime(c);
			temp += entryList.get(c).toString();
			temp += "\n";
		}
		return temp;
	}
	
	// A better definition, but still not perfect
	@Override 
	public boolean equals(Object other) {
	    boolean result = false;
	    if (other instanceof EntryCollection) {
	    	EntryCollection that = (EntryCollection) other;
	        result = (entryList.equals(that.entryList));
	    }
	    return result;
	}
	

}
