package com.pierlesstech.journal.web.utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;



public class MiscUtilities {

	public static String GenerateAlertWithLogout(String msg) {
		String html ="<script type=\"text/javascript\">" +
				"var r = alert(\"" + msg + "\");" +
				"window.location = \"./index.html\";" +
				"</script>" ;
		return html;
	}

	public static String GenerateAlert(String msg) {
		String html ="<script type=\"text/javascript\">" +
				"var r = alert(\"" + msg + "\");" +
				"</script>" ;
		return html;
	}
	
	public static String GenerateHeader(String colorPackage, String fontFamily) {
		int comma = fontFamily.indexOf(',');
		if (comma > 0) {
			fontFamily = fontFamily.substring(0, comma);
		}
		String html = "<!DOCTYPE html>" + 
				"<html>" +   
				"<head>" +
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"http://fonts.googleapis.com/css?family=Arizonia\">"+
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"http://fonts.googleapis.com/css?family=Arial\">"+
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"http://fonts.googleapis.com/css?family=Droid Serif\">"+
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"http://fonts.googleapis.com/css?family=Spirax\">"+
				"<link rel=\"stylesheet\" type=\"text/css\" href=\"http://fonts.googleapis.com/css?family=Tangerine\">"+
				"<title>Your Daily Journal</title>"  +
				"<link rel=\"stylesheet\"    href=\"./css/base.css\"    type=\"text/css\"/>" + 
				"<link rel=\"stylesheet\"    href=\"./css/" + colorPackage + "\"    type=\"text/css\"/>" + 


			"<script language=\"JavaScript\" type=\"text/javascript\">" +
			"var newwindow; " +
			"function popitup(url) {" +
			"newwindow=window.open('','','height=400,width=480');" +
			"if (window.focus) {newwindow.focus()} "+
			"var document = newwindow.document;" +
			"document.writeln(\"<IMG name=myImage SRC=\" +url+ \" onload=window.resizeTo(document.myImage.width+100,document.myImage.height+200)>\"); " +  
			"document.writeln(\"<form> <input type='button' value='Close Window'  onclick='window.close()'> </form>\");" +		    	



			"}" +
			"function tidy() {" +
			"if (newwindow.location && !newwindow.closed) { " +
			"   newwindow.close(); } " +
			"}" +

			"</script>" +
			"<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js\"></script><script>" +
			"$(document).ready(function(){" +
			"$(\"#uploadImage\").click(function(e) {" +
			"$(\"#popUpImage\").show();" +
			"});" +
			"$(\"#cancelButton\").click(function(e) {" +
			"    $(\"#popUpImage\").hide();" +
			"});" +
			"$(\"#uploadSettings\").click(function(e) {" +
			"$(\"#popUpSettings\").show();" +
			"});" +
			"$(\"#cancelSettings\").click(function(e) {" +
			"    $(\"#popUpSettings\").hide();" +
			"});" +
			"$(\"#uploadAccount\").click(function(e) {" +
			"$(\"#popUpAccount\").show();" +
			"});" +
			"$(\"#cancelAccount\").click(function(e) {" +
			"    $(\"#popUpAccount\").hide();" +
			"});" +
			"$(\"#logoutConfirm\").click(function(e) {" +
			"$(\"#popUpLogout\").show();" +
			"});" +
			"$(\"#cancelLogout\").click(function(e) {" +
			"$(\"#popUpLogout\").hide();" +
			"});" +
			"});" +


			"</script>" + 

			"</head>" +  
			"<body onUnload=\"tidy()\">" +  
			"<table class=\"title\"><tr><th>The <span class=\"artarea\">Art</span> of Journaling</th></tr> </table>";
		return html;
	}

	public static String GenerateFooter() {
		String html = "</center>" +        
				"</body>  " +
				"</html>";  
		return html;
	}

	public static boolean validateLogin(String parameter) {
		boolean valid = true;
		if (parameter == null) {
			valid = false;
		}
		else if (parameter.trim().length() < 6) {
			valid = false;
		}
		return valid;
	}
	
	public static boolean validate(String parameter) {
		boolean valid = true;
		if (parameter == null) {
			valid = false;
		}
		return valid;
	}
	
	

	public static String printTime(Calendar date) {
		long day = date.get(Calendar.DAY_OF_MONTH);
		long month = date.get(Calendar.MONTH);
		long year = date.get(Calendar.YEAR);

		//		long mill = date.get(Calendar.MILLISECOND);
		//		long second = date.get(Calendar.SECOND);
		//		long minute = date.get(Calendar.MINUTE);
		//		long hour = date.get(Calendar.HOUR);

		String temp = "";
		switch ((int) month) {
		case 0 : temp = "January "; break;
		case 	1 : temp = "Feburary "; break;
		case	2 : temp = "March "; break;
		case	3 : temp = "April "; break;
		case	4 : temp = "May "; break;
		case	5 : temp = "June "; break;
		case	6 : temp = "July "; break;
		case	7 : temp = "August "; break;
		case	8 : temp = "September "; break;
		case	9 : temp = "October "; break;
		case	10 : temp = "November "; break;
		case	11: temp = "December "; break;

		}

		temp += day + ", " + year + "\n";
		//temp += "  " + hour+":"+minute+":"+second + "." + mill;
		return temp;

		//		System.out.println("Current date is  "+day+"/"+(month+1)+"/"+year);
		//		System.out.println("Current time is  "+hour+" : "+minute+" : "+second + "." + mill);
	}

	public static Calendar convertDate(String textDate) {
		String[] dateParts =textDate.split("/");

		Calendar c = GregorianCalendar.getInstance();
		c.set(Calendar.MONTH, Integer.parseInt(dateParts[0]));
		c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[1]));
		c.set(Calendar.YEAR, Integer.parseInt(dateParts[2]));

		return c;
	}

	public static Calendar convertHtml5Date(String textDate) throws IllegalArgumentException{
		Calendar c = null;

		//See what format th date is, yyyymmdd or mmddyyyy
		try {
			Integer.parseInt(textDate.substring(0, 4));
			c = convertYYYYMMDD(textDate);

		}
		catch (NumberFormatException e) {
			c = conertMMDDYYYY(textDate);
		}			



		return c;


	}

	private static Calendar conertMMDDYYYY(String textDate) throws IllegalArgumentException {
		String[] dateParts;

		if (textDate.contains("-")) {
			dateParts =textDate.split("-");
		}
		else if (textDate.contains("/")) {
			dateParts =textDate.split("/");	
		}
		else {
			throw new IllegalArgumentException();
		}

		Calendar c = GregorianCalendar.getInstance();
		c.set(Calendar.MONTH, Integer.parseInt(dateParts[0])-1);
		c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[1]));
		c.set(Calendar.YEAR, Integer.parseInt(dateParts[2]));

		return c;
	}

	private static Calendar convertYYYYMMDD(String textDate) throws IllegalArgumentException {
		String[] dateParts;

		if (textDate.contains("-")) {
			dateParts =textDate.split("-");
		}
		else if (textDate.contains("/")) {
			dateParts =textDate.split("/");	
		}
		else {
			throw new IllegalArgumentException();
		}

		Calendar c = GregorianCalendar.getInstance();
		c.set(Calendar.MONTH, Integer.parseInt(dateParts[1])-1);
		c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));
		c.set(Calendar.YEAR, Integer.parseInt(dateParts[0]));

		return c;
	}

	public static String printSimpleTime(Calendar date) {
		long day = date.get(Calendar.DAY_OF_MONTH);
		long month = date.get(Calendar.MONTH) + 1;
		long year = date.get(Calendar.YEAR);
		//		String result = month + "/" + day + "/" + year;
		String result = year + "-" + month + "-" + day;
		return result;
	}

	public static String removeDate(String text) {
		String result;
		int endOfDate = text.indexOf('\n');
		//		System.out.println(text + "\n" + endOfDate);

		result = text.substring(endOfDate+1);
		result.trim();
		return result;

	}

	public static List<String> getWordList(String searchString) {
		List<String> result = new ArrayList<String>();
		String[] parts = searchString.split(" ");
		for (int i = 0; i < parts.length; i++) {
			result.add(parts[i].trim());
		}

		return result;
	}

	public static String getDate(String text) {
		String result;
		int endOfDate = text.indexOf('\n');
		//		System.out.println(text + "\n" + endOfDate);

		result = text.substring(0, endOfDate);
		result.trim();
		return result;
	}
}
