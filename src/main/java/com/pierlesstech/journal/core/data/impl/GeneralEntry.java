package com.pierlesstech.journal.core.data.impl;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.jdo.annotations.Persistent;
import javax.persistence.Embedded;
import javax.persistence.Id;

import com.pierlesstech.journal.core.data.DataItem;
import com.pierlesstech.journal.core.data.Entry;

public class GeneralEntry implements Entry {


	private static final long serialVersionUID = 1L;
	@Id Long id;
	@Embedded Text dataList;
	Vector<String> imageList;
	private int imageindex;
	private boolean dirty;
	private long timestamp;

	public GeneralEntry() {
		dataList = new Text();
		imageList = new Vector<String>();

		imageindex = 0;
		dirty = false;
		timestamp = GregorianCalendar.getInstance().getTimeInMillis();
	}

	public long getTimeStamp() {
		return timestamp;
	}

	// A better definition, but still not perfect
	 
	public boolean equals(Object other) {
		boolean result = true;
		try {
			if (other instanceof GeneralEntry) {
				GeneralEntry that = (GeneralEntry) other;
//				if (dataList.size() == that.dataList.size()) {
//					for (int i = 0; i < dataList.size(); i++) {
//						result = (dataList.get(i).equals(that.dataList.get(i))) && result;
//
//					}
//				}
//				// not the smae size couldn't equal
//				else {
//					result = false;
//				}
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

	/** 
	 * @see com.pierlesstech.journal.core.data.Entry#add(com.pierlesstech.journal.core.data.DataItem)
	 */
	
	public void addText(Text newData){

		dataList = newData;
		timestamp = GregorianCalendar.getInstance().getTimeInMillis();

		
	
	}
	
	public void addImage(String image) {
		imageList.add(image);
		timestamp = GregorianCalendar.getInstance().getTimeInMillis();

	}

	
	public String toStringEntry(Calendar c) {
		String temp = printTime(c);
		temp += toString();
		return temp;
	}

	
	public String toString() {
		String temp = "";
		temp += dataList.toString() + "\n";
		for (String image : imageList) {
			temp += image + "\n";
		}
		return temp;
	}

	public static String printTime(Calendar date) {
		long day = date.get(Calendar.DAY_OF_MONTH);
		long month = date.get(Calendar.MONTH);
		long year = date.get(Calendar.YEAR);

		//		long mill = date.get(Calendar.MILLISECOND);
		//		long second = date.get(Calendar.SECOND);
		//		long minute = date.get(Calendar.MINUTE);
		//		long hour = date.get(Calendar.HOUR);

		String temp = (month+1)+"/"+ day + "/" + year + "\n";
		//temp += "  " + hour+":"+minute+":"+second + "." + mill;
		return temp;

		//		System.out.println("Current date is  "+day+"/"+(month+1)+"/"+year);
		//		System.out.println("Current time is  "+hour+" : "+minute+" : "+second + "." + mill);
	}



	
//	public void update(DataItem newData, int index) {
//		if (newData instanceof Text) {
//			Text t = (Text) newData;
//			dirty = true;
//		}
//		else if (newData instanceof ImageItem) {
//			ImageItem i = (ImageItem) newData;
//			imageList.add(index++, i);
//			dirty = true;
//		}
//	
//	}



	
	public Text get() {
		return dataList;
	}

	public String get(int i)  throws IllegalArgumentException{
		if (i >= imageList.size()) {
			throw new IllegalArgumentException();
		}
		return imageList.get(i);
		
	}


	
	public boolean containsPhrase(String searchString) {
		boolean result = false;
		result = dataList.containsPhrase(searchString);
		return result;
	}



	
	public boolean hasChanged() {
		return dirty;
	}

	 
	public void resetDirty() {
		dirty = false;
	}

	
	public boolean isEmpty() {
		return (dataList == null);
	}

	 
	public int numberOfImages() {
		return imageList.size();
	}



	@Override
	public int add(DataItem newData) throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}



	public long sizeInBytes() {
		long size = 0;
		size += dataList.sizeInBytes();
//		for (ImageItem image : imageList) {
//			size += image.sizeInBytes();
//		}
		return size;
	}
	
	public void removeImage(int index) {
		imageList.remove(index);
	}


}
