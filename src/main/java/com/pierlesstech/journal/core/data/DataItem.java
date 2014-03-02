package com.pierlesstech.journal.core.data;

import java.io.Serializable;

public interface DataItem extends Serializable {

	public void setData(Object o);
	public boolean containsPhrase(String searchString);
	

}
