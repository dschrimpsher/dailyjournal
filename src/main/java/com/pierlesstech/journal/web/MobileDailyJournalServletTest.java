package com.pierlesstech.journal.web;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class MobileDailyJournalServletTest {
	String jsonExpected = "{\"name\":\"aaaaaa\",\"password\":\"aaaaaa\",\"isGoogleAccount\":false,\"sizeLimit\":10485760,\"colorPackage\":\"Nottingham.css\",\"fontFamily\":\"Arial, Helvetica, sans-serif\",\"fontSize\":\"16px\",\"entries\":{\"2012-12-23\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356634730361},\"2012-12-27\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356634705414},\"2012-12-26\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356634726966},\"2012-12-25\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356634728106},\"2012-12-24\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356634729198}}}";
	String jsonNew = "{\"name\":\"aaaaaa\",\"password\":\"aaaaaa\",\"isGoogleAccount\":false,\"sizeLimit\":10485760,\"colorPackage\":\"Nottingham.css\",\"fontFamily\":\"Tangerine, cursive\",\"fontSize\":\"44px\",\"entries\":{\"2012-12-19\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656796},\"2012-12-17\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656790},\"2012-12-18\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656802},\"2012-12-15\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656793},\"2012-12-16\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656785},\"2012-12-13\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656785},\"2012-12-14\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656806},\"2012-12-23\":{\"dataList\":{\"data\":\"Testing 1 2 3 4 Its a new day.\"},\"imageList\":[\"http://i2.cdn.turner.com/cnn/dam/assets/121222054356-mr-magoo-s-christmas-carol-story-top.jpg\"],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656789},\"2012-12-22\":{\"dataList\":{\"data\":\"Testing 1 2 3\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656796},\"2012-12-21\":{\"dataList\":{\"data\":\"dsfgdfgsdf\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656790},\"2012-12-20\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656783},\"2012-12-5\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656794},\"2012-12-3\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656797},\"2012-12-8\":{\"dataList\":{\"data\":\"This is today.\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656784},\"2012-12-9\":{\"dataList\":{\"data\":\"This is tomorrow.\\r\\nAdd image\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656800},\"2012-12-6\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656748},\"2012-12-7\":{\"dataList\":{\"data\":\"I wish I had a spaceship of my very own.\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656789},\"2012-12-27\":{\"dataList\":{\"data\":\"asdfasdfasdfasd\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656760},\"2012-12-10\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656808},\"2012-12-26\":{\"dataList\":{\"data\":\"\"},\"imageList\":[\"https://lh4.googleusercontent.com/-xhhsROGqpfo/UMTqytU1U_I/AAAAAAAAAjY/kFknZxso_Ao/w252-h447-k/IMAG0066.jpg\",\"https://lh5.googleusercontent.com/-uEf2lrWcrmg/ULKo-nDwzPI/AAAAAAAAAiM/-1SgohExRfg/w447-h252-k/IMAG0064.jpg\"],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656788},\"2012-12-25\":{\"dataList\":{\"data\":\"\"},\"imageList\":[\"http://www.topwallpapersdesktop.com/wp-content/uploads/2012/11/christmas-pictures.jpg\"],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656798},\"2012-12-12\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656803},\"2012-12-11\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656784},\"2012-12-24\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656798},\"2012-12-28\":{\"dataList\":{\"data\":\"\"},\"imageList\":[],\"imageindex\":0,\"dirty\":false,\"timestamp\":1356632656784}}}";
	@Test
	public void testDoPostHttpServletRequestHttpServletResponse() {
		try {
			String parameters = "username=aaaaaa&password=aaaaaa&json=" + jsonExpected;
			URL url = new URL("http://localhost:8890/mobiledailyjournal");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false); 
			connection.setRequestMethod("POST"); 
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
			connection.setUseCaches (false);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
			wr.writeBytes(parameters);
			wr.flush();

			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String json = "";
			while ((line = reader.readLine()) != null) {
				json += line;
			}
			assertTrue(json.equals("{\"status\":\"success\"}"));

			wr.close();
			reader.close();     
			connection.disconnect();

		} 
		catch (MalformedURLException e) { 
			// new URL() failed
			// ...
		} 
		catch (IOException e) {   
			// openConnection() failed
			// ...
		}
	}



	@Test
	public void testDoGetHttpServletRequestHttpServletResponse() {
		try {
			URL url = new URL("http://localhost:8890/mobiledailyjournal?username=aaaaaa&password=aaaaaa");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
			connection.setDoOutput(true); 
			connection.setInstanceFollowRedirects(false); 
			connection.setRequestMethod("GET");

			connection.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			String json ="";
			while ((line = rd.readLine()) != null) {
				json += line;
			}
			assertTrue(json.equals(jsonExpected));

		} 
		catch (MalformedURLException e) { 
			fail(e.getMessage());
		} 
		catch (IOException e) {   
			fail(e.getMessage());
		}	
	}

	@Test
	public void testUpdate() throws IOException {

		String parameters = "username=aaaaaa&password=aaaaaa&json=" + jsonNew;
		URL url = new URL("http://localhost:8890/mobiledailyjournal");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false); 
		connection.setRequestMethod("POST"); 
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
		connection.setUseCaches (false);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
		wr.writeBytes(parameters);
		wr.flush();

		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String json = "";
		while ((line = reader.readLine()) != null) {
			json += line;
		}
		assertTrue(json.equals("{\"status\":\"success\"}"));

		wr.close();
		reader.close();     
		connection.disconnect();

		//Check it took hold.

		url = new URL("http://localhost:8890/mobiledailyjournal?username=aaaaaa&password=aaaaaa");
		connection = (HttpURLConnection) url.openConnection();           
		connection.setDoOutput(true); 
		connection.setInstanceFollowRedirects(false); 
		connection.setRequestMethod("GET");

		connection.connect();
		BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));			
		json ="";
		line = "";
		while ((line = rd.readLine()) != null) {
			json += line;
		}
		System.out.println(json);
		System.out.println(jsonNew);
		assertTrue(json.equals(jsonNew));
		rd.close();
		
		parameters = "username=aaaaaa&password=aaaaaa&json=" + jsonExpected;
		url = new URL("http://localhost:8890/mobiledailyjournal");
		connection = (HttpURLConnection) url.openConnection();           
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false); 
		connection.setRequestMethod("POST"); 
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
		connection.setUseCaches (false);

		wr = new DataOutputStream(connection.getOutputStream ());
		wr.writeBytes(parameters);
		wr.flush();

		 reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		 json = "";
		while ((line = reader.readLine()) != null) {
			json += line;
		}
		assertTrue(json.equals("{\"status\":\"success\"}"));
		wr.close();
		reader.close();
	}
}










