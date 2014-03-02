/**
 * 
 */
package com.pierlesstech.journal.core.data;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author dan
 *
 */
public interface Entry extends Serializable{
	public int add(DataItem newData) throws IllegalArgumentException;
//	public void update(DataItem newData, int index);
	public String toStringEntry(Calendar c); 
	public String get(int index) throws IllegalArgumentException;
	public boolean containsPhrase(String searchString);
	public boolean hasChanged();
	public void resetDirty();
	public boolean isEmpty();
	public int numberOfImages();

}
