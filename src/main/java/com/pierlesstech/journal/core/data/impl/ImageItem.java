package com.pierlesstech.journal.core.data.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Id;

import com.pierlesstech.journal.core.data.DataItem;

@PersistenceCapable
public class ImageItem implements DataItem, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id Long id;
//	transient private BufferedImage data;
	private String filename;

	public ImageItem() { 
//		data = null;
		filename = "";
	}	

//	public BufferedImage getImage() {
//		loadImage();
//		return  data;
//	}
	
	public String getData() {
		return filename;
	}
	
	public String getHtmlPage() {
		String result = "./";
		result += filename.substring(filename.lastIndexOf('/') + 1, filename.lastIndexOf('.'));
		result += ".html";
		return result;
	}
//	private void loadImage() {
//		try {
//			data = ImageIO.read(new File(filename));
//		} 
//		catch (IOException e) {
//			System.out.println("Error Loading Image");
//			throw new IllegalArgumentException();
//		}
//
//	}

	@Override
	public void setData(Object o) throws IllegalArgumentException {
		try {
			filename = (String) o;
		} 
	
		catch (ClassCastException e) {
			System.out.println("Error Loading Image");
			throw new IllegalArgumentException();

		}
	}

	@Override
	public String toString() {
		return "<<"+ filename + ">>";
	}

	@Override
	public boolean containsPhrase(String searchString) {
		//Currently do not search images.
		return false;
	}

	private void writeObject(ObjectOutputStream stream) throws IOException
	{
		stream.writeObject(filename);
	
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
	{

		// read the image filename and recreate the image
		filename = (String)stream.readObject();
		setData(filename);

	}

	public long sizeInBytes() {
		// TODO Auto-generated method stub
		return (filename.length()*2);
	}

}
