/**
 * 
 */
package com.pierlesstech.journal.core.data;

import com.pierlesstech.journal.core.data.impl.GeneralEntry;

/**
 * @author dan
 *
 */
public class EntryFactory {

	public static GeneralEntry createEntry() {
		return new GeneralEntry();
		
	}
}
