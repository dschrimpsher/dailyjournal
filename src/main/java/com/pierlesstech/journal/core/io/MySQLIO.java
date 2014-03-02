package com.pierlesstech.journal.core.io;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jasypt.util.password.BasicPasswordEncryptor;

import com.pierlesstech.journal.core.UserInfo;
import com.pierlesstech.journal.core.data.Entry;
import com.pierlesstech.journal.core.data.EntryFactory;
import com.pierlesstech.journal.core.data.impl.GeneralEntry;
import com.pierlesstech.journal.exceptions.UserDoesNotExistException;
import com.pierlesstech.journal.exceptions.UserException;
import com.pierlesstech.journal.exceptions.UserExistsException;

public abstract class MySQLIO implements JournalIO {

	protected UserInfo user;

	public MySQLIO() {

	}

	@Override
	public void synchEntryCollection() {
		//On pc platform there is not synching.  MySQL is the live database.
		// so donothing.
	}

	



	private void createTables() {
		//Build database and table
		try {
			Statement stmt;

			//Register the JDBC driver for MySQL.
			Class.forName("com.mysql.jdbc.Driver");

			//Define URL of database server for
			// database named mysql on the localhost
			// with the default port number 3306.
			String url =
					"jdbc:mysql://localhost:3306/" + user.getName();

			//Get a connection to the database for a
			// user named root with a blank password.
			// This user is the default administrator
			// having full privileges to do anything.
			Connection con =
					DriverManager.getConnection(
							url,"root", "ultimum0!");

			//Display URL and connection information
			//System.out.println("URL: " + url);
			//System.out.println("Connection: " + con);
			try {
				//Get a Statement object
				stmt = con.createStatement();
				String table = "CREATE TABLE Entries(Day DATE not NULL, Entry BLOB, PRIMARY KEY(Day))";
				stmt.executeUpdate(table);
				table = "CREATE TABLE User(username CHAR(64) not NULL, password CHAR(64) not NULL, fontfamily CHAR(64) not NULL, fontsize CHAR(64) not NULL, paperimage CHAR(64), background CHAR(64), PRIMARY KEY(username))";
				stmt = con.createStatement();
				stmt.executeUpdate(table);
				//				stmt = con.createStatement();
				table = "INSERT INTO user VALUES (?, ?, ?, ?, ?, ?)";
				PreparedStatement prest2 = con.prepareStatement(table);
				prest2.setString(1, user.getName());
				BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
				String password = passwordEncryptor.encryptPassword(user.getPassword());
				//				System.out.println(password + ": " + password.length());
				prest2.setString(2,password);
				prest2.setString(3, user.getFontFamily());
				prest2.setString(4, user.getFontSize());
				prest2.setString(5, user.getFontFamily());
				prest2.setString(6, user.getBackgroundImage());
				System.out.println(prest2.toString());

				prest2.executeUpdate();

				//				System.out.println("Table creation process successfully!");
			}

			catch(SQLException s){
				s.printStackTrace();
				System.out.println("Table all ready exists!");
			}
			con.close();

		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	private void createDB() throws UserException {
		//Build database and table
		try {
			Statement stmt;

			//Register the JDBC driver for MySQL.
			Class.forName("com.mysql.jdbc.Driver");

			//Define URL of database server for
			// database named mysql on the localhost
			// with the default port number 3306.
			String url =
					"jdbc:mysql://localhost:3306/mysql";

			//Get a connection to the database for a
			// user named root with a blank password.
			// This user is the default administrator
			// having full privileges to do anything.
			Connection con =
					DriverManager.getConnection(
							url,"root", "ultimum0!");

			//Display URL and connection information
			//System.out.println("URL: " + url);
			//System.out.println("Connection: " + con);

			//Get a Statement object
			stmt = con.createStatement();

			//Create the new database
			stmt.executeUpdate(
					"CREATE DATABASE " + user.getName());
			//Register a new user named auser on the
			// database named JunkDB with a password
			// drowssap enabling several different
			// privileges.
			stmt.executeUpdate(
					"GRANT SELECT,INSERT,UPDATE,DELETE," +
							"CREATE,DROP " +
							"ON " + user.getName() + ".* TO '" + user.getName() + "'@'localhost' " +
					"IDENTIFIED BY 'drowssap';");

			con.close();
		}catch( SQLException e ) {
			e.printStackTrace();
			//			throw new UserException("Warning!:  Database for " + user.getName() + " already exists");
			throw new UserExistsException();
		}//end catch
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean validateUser(UserInfo pUserInfo) throws UserDoesNotExistException {
		String username = pUserInfo.getName();
		String password = pUserInfo.getPassword();
		boolean result = false;
		try {

			//Register the JDBC driver for MySQL.
			Class.forName("com.mysql.jdbc.Driver");

			//Define URL of database server for
			// database named mysql on the localhost
			// with the default port number 3306.
			String url =
					"jdbc:mysql://localhost:3306/" + username;

			//Get a connection to the database for a
			// user named root with a blank password.
			// This user is the default administrator
			// having full privileges to do anything.
			Connection con =
					DriverManager.getConnection(
							url,"root", "ultimum0!");

			//Display URL and connection information
			//			//System.out.println("URL: " + url);
			//			//System.out.println("Connection: " + con);
			try {
				//				System.out.println(username);
				String sql 
				= "SELECT password, fontfamily, fontsize, paperimage, background FROM User WHERE username = ?";
				PreparedStatement prest = con.prepareStatement(sql);
				prest.setString(1, username);
				ResultSet rs = prest.executeQuery();

				while (rs.next()) {
					BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

					String encryptedPassword = (String) rs.getObject(1);
					encryptedPassword = encryptedPassword.trim();
					//					System.out.println(encryptedPassword + " vs " + password);
					if (passwordEncryptor.checkPassword(password, encryptedPassword)) {
						result = true;
						pUserInfo.changeFont((String) rs.getObject(2),(String) rs.getObject(3));
						pUserInfo.changeBackground((String) rs.getObject(5));

					} else {
						result = false;
					}
				}
				//				System.out.println(count);
				prest.close();
				con.close();

			}  

			catch (SQLException s){
				//System.out.println("SQL statement is not executed!");
				s.printStackTrace();
			}
		}
		catch (Exception e){
			throw new UserDoesNotExistException();
		}
		return result;
	}

	@Override
	public Entry getDate(Calendar date) {
		java.sql.Date dbDate = new java.sql.Date(date.getTimeInMillis());

		Entry entry = null;

		try {

			//Register the JDBC driver for MySQL.
			Class.forName("com.mysql.jdbc.Driver");

			//Define URL of database server for
			// database named mysql on the localhost
			// with the default port number 3306.
			String url =
					"jdbc:mysql://localhost:3306/" + user.getName();

			//Get a connection to the database for a
			// user named root with a blank password.
			// This user is the default administrator
			// having full privileges to do anything.
			Connection con =
					DriverManager.getConnection(
							url,"root", "ultimum0!");

			//Display URL and connection information
			//			//System.out.println("URL: " + url);
			//			//System.out.println("Connection: " + con);
			try {
				String sql 
				= "SELECT entry FROM entries WHERE day = ?";
				PreparedStatement prest = con.prepareStatement(sql);
				prest.setDate(1,dbDate);
				ResultSet rs = prest.executeQuery();



				while (rs.next()) {


					ByteArrayInputStream bais;

					ObjectInputStream in;

					bais = new ByteArrayInputStream(rs.getBytes(1));

					in = new ObjectInputStream(bais);

					entry = (Entry) in.readObject();


					in.close();
				}

				prest.close();
				con.close();

			}  

			catch (SQLException s){
				//System.out.println("SQL statement is not executed!");
				s.printStackTrace();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		if (entry == null) {
//			entry = new GeneralEntry();
		}
		return entry;
	}

	

	@Override
	public void close() {

		//In nothing happens for now.
	}

	@Override
	public void deleteStorage() {
		try {
			Statement stmt;

			//Register the JDBC driver for MySQL.
			Class.forName("com.mysql.jdbc.Driver");

			//Define URL of database server for
			// database named mysql on the localhost
			// with the default port number 3306.
			String url =
					"jdbc:mysql://localhost:3306/" + user.getName();

			//Get a connection to the database for a
			// user named root with a blank password.
			// This user is the default administrator
			// having full privileges to do anything.
			Connection con =
					DriverManager.getConnection(
							url,"root", "ultimum0!");


			try {
				//Get a Statement object
				stmt = con.createStatement();
				String table = "drop database " + user.getName();
				stmt.executeUpdate(table);

			}

			catch(SQLException s){
				s.printStackTrace();
			}
			con.close();

		}
		catch (SQLException s){
			s.printStackTrace();
		}
		catch (ClassNotFoundException s) {
			s.printStackTrace();

		}		
	}

	@Override
	public List<Calendar> wordSearch(List<String> searchString) {
		List<Calendar> searchResults = new Vector<Calendar>();
		//		java.sql.Date dbDate = new java.sql.Date(date.getTimeInMillis());

		try {

			//Register the JDBC driver for MySQL.
			Class.forName("com.mysql.jdbc.Driver");

			//Define URL of database server for
			// database named mysql on the localhost
			// with the default port number 3306.
			String url =
					"jdbc:mysql://localhost:3306/" + user.getName();

			//Get a connection to the database for a
			// user named root with a blank password.
			// This user is the default administrator
			// having full privileges to do anything.
			Connection con =
					DriverManager.getConnection(
							url,"root", "ultimum0!");

			//Display URL and connection information
			//			//System.out.println("URL: " + url);
			//			//System.out.println("Connection: " + con);
			try {
				String sql 
				= "SELECT day, entry FROM entries";
				PreparedStatement prest = con.prepareStatement(sql);
				ResultSet rs = prest.executeQuery();



				while (rs.next()) {
					Entry entry;

					ByteArrayInputStream bais;

					ObjectInputStream in;

					bais = new ByteArrayInputStream(rs.getBytes(2));

					in = new ObjectInputStream(bais);

					entry = (Entry) in.readObject();
					if (entry == null) {
						//						System.out.println(rs.getDate(1).toString() + " Has no entry");
						continue;
					}

					Iterator<String> strings = searchString.iterator();
					boolean allIn = true;
					while (strings.hasNext()) {		
						String next = strings.next();
//						System.out.println("Search for string +" + next + "+");
						if (!entry.containsPhrase(next)) {
//							System.out.println("Did not match");
							allIn = false;
						}
					}
					if (allIn) {
						java.sql.Date day = rs.getDate(1);
						Calendar c = GregorianCalendar.getInstance();
						c.setTimeInMillis(day.getTime());
						searchResults.add(c);
					}

					in.close();
				}

				prest.close();
				con.close();

			}  

			catch (SQLException s){
				//System.out.println("SQL statement is not executed!");
				s.printStackTrace();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return searchResults;			
	}

	@Override
	public void updateUser(UserInfo pUserInfo) throws UserException {
		String username = pUserInfo.getName();
		try {

			//Register the JDBC driver for MySQL.
			Class.forName("com.mysql.jdbc.Driver");

			//Define URL of database server for
			// database named mysql on the localhost
			// with the default port number 3306.
			String url =
					"jdbc:mysql://localhost:3306/" + username;

			//Get a connection to the database for a
			// user named root with a blank password.
			// This user is the default administrator
			// having full privileges to do anything.
			Connection con =
					DriverManager.getConnection(
							url,"root", "ultimum0!");

			//Display URL and connection information
			//			//System.out.println("URL: " + url);
			//			//System.out.println("Connection: " + con);
			try {
				String update = "UPDATE user SET  fontfamily = ?, fontsize = ?, paperimage = ?, background = ? WHERE username = ?";

				PreparedStatement prest2 = con.prepareStatement(update);
				prest2.setString(1,pUserInfo.getFontFamily());
				prest2.setString(2,pUserInfo.getFontSize());
				prest2.setString(4,pUserInfo.getBackgroundImage());
				prest2.setString(5,username);
				prest2.executeUpdate();

				prest2.close();
				con.close();
			}  

			catch (SQLException s){
				//System.out.println("SQL statement is not executed!");
				s.printStackTrace();
				throw new UserException();
			}
		}
		catch (Exception e){
			throw new UserDoesNotExistException();
		}
	}

	@Override
	public Map<String, String> getBackgrounds() {
		Map<String, String> bgs = new HashMap<String, String>();
		try {

			//Register the JDBC driver for MySQL.
			Class.forName("com.mysql.jdbc.Driver");

			//Define URL of database server for
			// database named mysql on the localhost
			// with the default port number 3306.
			String url =
					"jdbc:mysql://localhost:3306/settings";

			//Get a connection to the database for a
			// user named root with a blank password.
			// This user is the default administrator
			// having full privileges to do anything.
			Connection con =
					DriverManager.getConnection(
							url,"root", "ultimum0!");

			//Display URL and connection information
			//			//System.out.println("URL: " + url);
			//			//System.out.println("Connection: " + con);
			try {
				String sql 
				= "SELECT name, filename FROM backgrounds";
				PreparedStatement prest = con.prepareStatement(sql);
				ResultSet rs = prest.executeQuery();


				while (rs.next()) {
					String name = (String) rs.getObject(1);
					String file = (String) rs.getObject(2);
					bgs.put(name, file);
					
				}
			

				prest.close();
				con.close();

			}  

			catch (SQLException s){
				//System.out.println("SQL statement is not executed!");
				s.printStackTrace();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return bgs;		
	}

	@Override
	public Map<String, String> getPaper() {
		Map<String, String> bgs = new HashMap<String, String>();
		bgs.put("none", "none");
		try {

			//Register the JDBC driver for MySQL.
			Class.forName("com.mysql.jdbc.Driver");

			//Define URL of database server for
			// database named mysql on the localhost
			// with the default port number 3306.
			String url =
					"jdbc:mysql://localhost:3306/settings";

			//Get a connection to the database for a
			// user named root with a blank password.
			// This user is the default administrator
			// having full privileges to do anything.
			Connection con =
					DriverManager.getConnection(
							url,"root", "ultimum0!");

			//Display URL and connection information
			//			//System.out.println("URL: " + url);
			//			//System.out.println("Connection: " + con);
			try {
				String sql 
				= "SELECT name, filename FROM paper";
				PreparedStatement prest = con.prepareStatement(sql);
				ResultSet rs = prest.executeQuery();


				while (rs.next()) {
					String name = (String) rs.getObject(1);
					String file = (String) rs.getObject(2);
					bgs.put(name, file);
					
				}
			

				prest.close();
				con.close();

			}  

			catch (SQLException s){
				//System.out.println("SQL statement is not executed!");
				s.printStackTrace();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return bgs;		
	}

	@Override
	public Map<String, String> getFontFamilies() {
		Map<String, String> bgs = new HashMap<String, String>();
		bgs.put("none", "none");
		try {

			//Register the JDBC driver for MySQL.
			Class.forName("com.mysql.jdbc.Driver");

			//Define URL of database server for
			// database named mysql on the localhost
			// with the default port number 3306.
			String url =
					"jdbc:mysql://localhost:3306/settings";

			//Get a connection to the database for a
			// user named root with a blank password.
			// This user is the default administrator
			// having full privileges to do anything.
			Connection con =
					DriverManager.getConnection(
							url,"root", "ultimum0!");

			//Display URL and connection information
			//			//System.out.println("URL: " + url);
			//			//System.out.println("Connection: " + con);
			try {
				String sql 
				= "SELECT name, filename FROM fontfamily";
				PreparedStatement prest = con.prepareStatement(sql);
				ResultSet rs = prest.executeQuery();


				while (rs.next()) {
					String name = (String) rs.getObject(1);
					String file = (String) rs.getObject(2);
					bgs.put(name, file);
					
				}
			

				prest.close();
				con.close();

			}  

			catch (SQLException s){
				//System.out.println("SQL statement is not executed!");
				s.printStackTrace();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return bgs;		
	}
	
	
}
