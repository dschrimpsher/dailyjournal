package com.pierlesstech.journal.db;

/**File Jdbc11.java
Copyright 2004, R.G.Baldwin
Rev 09/18/04

The purpose of this program is to log onto
the master database named mysql as the default
administrator named root whose password is blank
in order to perform the following updates:

1. Create a new database named JunkDB.
2. Create a new user named auser with a password
 of drowssap with broad access to the
 database named JunkDB.

These two operations produce no visible output
when successful. However, they produce error
messages in the output when unsuccessful.

This program is the reverse of the program named
Jdbc12, which deletes the database JunkDB and
removes the user named auser.

The MySQL server must be running on localhost
before this program is started.

It is necessary to manually start the MySQL
database server running on localhost. See the
documentation sections 2.2.1.5,Starting the
Server for the First Time and 2.2.1.4 Selecting a
Windows Server. This is accomplished by executing
the following command at the command prompt:

C:\mysql\bin\mysqld-opt --console

Similarly, it is necessary to manually stop the
MySQL database server. See the documentation
Section 2.2.1.6 Starting MySQL from the Windows
Command Line. This is accomplished by executing
the following command at the command prompt:

C:\mysql\bin\mysqladmin -u root shutdown

To install the JDBC interface classes, I copied
the jar file named
mysql-connector-java-3.0.15-ga-bin.jar into the
jre\lib\ext folder of my Java installation. I
did this to avoid having to make changes to the
classpath.

I am currently running SDK v1.4.2. When I
upgrade to a newer version of the SDK, it will be
necessary for me to copy the JDBC jar file into
the jre\lib\ext folder for the new version of the
SDK.

This program produces the following output as
a result of a successful run:

Copyright 2004, R.G.Baldwin
URL: jdbc:mysql://localhost:3306/mysql
Connection: com.mysql.jdbc.Connection@1430b5c

Tested using SDK 1.4.2 under WinXP, MySQL
version 4.0.21-win, and JDBC connector
version mysql-connector-java-3.0.15-ga.
************************************************/
import java.sql.*;

public class Jdbc11 {
 public static void main(String args[]){
 System.out.println(
 "Copyright 2004, R.G.Baldwin");
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
 System.out.println("URL: " + url);
 System.out.println("Connection: " + con);

 //Get a Statement object
 stmt = con.createStatement();

 //Create the new database
 stmt.executeUpdate(
 "CREATE DATABASE JunkDB");
 //Register a new user named auser on the
 // database named JunkDB with a password
 // drowssap enabling several different
 // privileges.
 stmt.executeUpdate(
 "GRANT SELECT,INSERT,UPDATE,DELETE," +
 "CREATE,DROP " +
 "ON JunkDB.* TO 'auser'@'localhost' " +
 "IDENTIFIED BY 'drowssap';");
 con.close();
 }catch( Exception e ) {
 e.printStackTrace();
 }//end catch
 }//end main
}//end class Jdbc11
