package com.pierlesstech.journal.core.data.impl;

import javax.persistence.Id;

import com.pierlesstech.journal.core.data.DataItem;

public class Text implements DataItem {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id Long id;
	String data;

	public Text() {
		data = "";
	}

	@Override
	public void setData(Object text) {
		try {
			data = (String) text;
		}
		catch (ClassCastException exp) {
			System.out.println("Error trying to set non-text as text");
		}
	}

	@Override
	public String toString() {
		return data;
	}


	// A better definition, but still not perfect
	@Override 
	public boolean equals(Object other) {
		boolean result = false;
		if (other instanceof Text) {
			Text that = (Text) other;
			result = (data.equals(that.data));
		}
		return result;
	}

	@Override
	public boolean containsPhrase(String searchString) {
		boolean result = false;
		String temp = data.toLowerCase();
		result = temp.contains(searchString);
		return result;
	}

	public long sizeInBytes() {
		long size = 0; 
		size += data.length()*2;
		return size;
			
	}
}
