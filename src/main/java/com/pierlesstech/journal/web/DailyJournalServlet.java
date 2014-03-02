package com.pierlesstech.journal.web;

import java.io.*; 
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.*;
import javax.servlet.http.*;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.pierlesstech.journal.core.JournalManager;
import com.pierlesstech.journal.core.UserInfo;
import com.pierlesstech.journal.core.data.impl.GeneralEntry;
import com.pierlesstech.journal.core.data.impl.Text;
import com.pierlesstech.journal.exceptions.SizeLimitException;
import com.pierlesstech.journal.exceptions.UserException;
import com.pierlesstech.journal.exceptions.UserExistsException;
import com.pierlesstech.journal.web.utilities.MiscUtilities;
import com.pierlesstech.journal.web.utilities.RequestException;

import java.util.List;

@SuppressWarnings("serial")
public class DailyJournalServlet extends HttpServlet {

	public static long index = 0;
	public static long staticIndex = 0;

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)throws ServletException, IOException {
		//		uploadImage(request, response);
		throw new ServletException("Form tried to Post!");

	}	

	@Override
	public void doGet(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {
		HttpSession session = request.getSession();
		System.out.println("Request: " + request.getQueryString());
		synchronized(session) {
			System.out.println("Inside");
			//Store of whatwe have berfore we move.
			try {
				JournalActions ja = whichSubmitType(request);
				switch (ja) {
				case INIT :	intialSetup(response);  System.out.println("Executing INIT");break;
				case LICENSE:  loadLicense(request, response); System.out.println("Executing LICENSE");break;
				case REGISTER : createUser(request, response);  System.out.println("Executing REGISTER");break;
				case GOOGLELOGIN : createGoogleUser(request, response);  System.out.println("Executing GOOGLE ACCOUNT");break;
				case LOGIN : loadUser(request, response);  System.out.println("Executing LOGIN");break;
				case SAVE :	storeEntry(request, response);saveEntry(request, response); System.out.println("Executing SAVE"); break;
				case NEXTDAY: storeEntry(request, response);incrementDay(request, response);  System.out.println("Executing NEXTDAY");break;
				case PREVDAY: storeEntry(request, response);decrementDay(request, response);  System.out.println("Executing PREVDAY");break;
				case LOGOUT: storeEntry(request, response);logout(request, response);  System.out.println("Executing LOGOUT");break;
				case SEARCH: storeEntry(request, response);search(request, response);  System.out.println("Executing SEARCH");break;
				case DATE: storeEntry(request, response);gotoDate(request, response);  System.out.println("Executing DATE");break;
				case SETTING: storeEntry(request, response);gotoSetting(request, response);  System.out.println("Executing SETTING");break;
				case CHANGESETTING: storeEntry(request, response); changeSetting(request, response);  System.out.println("Executing CHANGESETTING");break;
				case CHANGEACCOUNT: storeEntry(request, response); changeAccount(request, response);  System.out.println("Executing CHANGEACCOUNT");break;
				case ADDIMAGE: storeEntry(request, response); addImage(request, response);  System.out.println("Executing ADDIMAGE");break;
				case UPLOADIMAGE: storeEntry(request, response); uploadImage(request, response);  System.out.println("Executing UPLOADIMAGE");break;
				case DELETEIMAGE:  deleteImage(request, response); storeEntry(request, response); System.out.println("Executing DELETEIMAGE");break;
				case DEBUG: debug(request, response); System.out.println("Executing Debug");break;
				case CONTACT: contact(request, response); System.out.println("Executing Contact");break;
				default: intialSetup(response);  System.out.println("Executing DEFAULT");break;
				}
				System.out.println("Done");
			}
			catch (RequestException e) {
				//This means a query didn't make sense so ignore it. 
				reprintDay(request, response);
			}
			catch (UserException e) {
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				String html = MiscUtilities.GenerateAlertWithLogout("This is a password protected Daily Journal.  Please login to use.  If you are new, please register.");
				out.println(html);
				intialSetup(response);	
			}
		}
		System.out.println("Unlocked.");
	}



	private void changeAccount(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			JournalManager jm = getJournalManager(request);
			String pw1 = request.getParameter("newPassword");
			String pw2 = request.getParameter("newPassword2");

			Calendar c = jm.getCurrentDay();
			GeneralEntry e = jm.getEntry(c);
			
			
			if (!MiscUtilities.validateLogin(pw1)) {
				String html = MiscUtilities.GenerateAlert("Passwords not valid.  Please use a password with at least 6 characters. Please try again.");
				out.println(html);
				out.println(generateJournal(jm, c, e, request));
			}
			
			else if (!pw1.equals(pw2)) {

				String html = MiscUtilities.GenerateAlert("Passwords did not match.  Password has not been changed!  Please try again.");
				out.println(html);
				out.println(generateJournal(jm, c, e, request));
				
			}
			else {
				UserInfo user = jm.getCurrentUser();
				user.changePassword(pw1);
				jm.updateUser();

				
				HttpSession session = request.getSession();
				synchronized(session) {
					session.setAttribute("jm", jm);
				}
				out.println(generateJournal(jm, c, e, request));
				
			}

		}
		catch (UserException e) {
			String html = MiscUtilities.GenerateAlertWithLogout("This is a password protected Daily Journal.  Please login to use.  If you are new, please register.");
			out.println(html);
		}

	}

	private void loadLicense(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String password2 = request.getParameter("password2");



		String html = "<html><head>";
		html += "<script type=\"text/javascript\">";
		html += "function enableSubmit() {";
		html += "var c = document.getElementById('ckbox').checked;";
		html += "if (c==true) {";
		html += "document.getElementById('okay').disabled=\"\";";
		html += "}";
		html += "else {";
		html += "document.getElementById('okay').disabled=\"disabled\";";
		html += "}";
		html += "}";
		html += "</script>";	
		html += "</head>";
		html += "<body>";

		html += "<h1>Terms of Service</h1>";
		html +=	"<P>Thanks for using our products and services (\"Services\"). The Services are provided by Pierless Technologies. (\"PT\")";
		html += "<P>By using our Services, you are agreeing to these terms. Please read them carefully.";
		html += "<P>PT reserves the right to change format, information, rules, and any other part of Daily Journal at any time and for any reseason.";
		html += "<P>PT also reserves the right to erase any account if activity that is illegal or in violation of these TOS.";
		html += "<P>The user will receive a limited amount of free storage on PT&#39s servers.  If storage limits are exceeded the user may be charged an undisclosed fee.";

		html += "<P>We are constantly changing and improving our Services. We may add or remove functionalities or features, and we may suspend or stop a Service altogether.  You can stop using our Services at any time, although we&#39ll be sorry to see you go. PT may also stop providing Services to you, or add or create new limits to our Services at any time.  We believe that you own your data and preserving your access to such data is important. If we discontinue a Service, where reasonably possible, we will give you reasonable advance notice and a chance to get information out of that Service.";
		html += "<p>We provide our Services using a commercially reasonable level of skill and care and we hope that you will enjoy using them. But there are certain things that we don&#39t promise about our Services.";
		html += "<P>OTHER THAN AS EXPRESSLY SET OUT IN THESE TERMS OR ADDITIONAL TERMS, NEITHER PT NOR ITS SUPPLIERS OR DISTRIBUTORS MAKE ANY SPECIFIC PROMISES ABOUT THE SERVICES. FOR EXAMPLE, WE DON&#39T MAKE ANY COMMITMENTS ABOUT THE CONTENT WITHIN THE SERVICES, THE SPECIFIC FUNCTION OF THE SERVICES, OR THEIR RELIABILITY, AVAILABILITY, OR ABILITY TO MEET YOUR NEEDS. WE PROVIDE THE SERVICES \"AS IS\".  SOME JURISDICTIONS PROVIDE FOR CERTAIN WARRANTIES, LIKE THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, WE EXCLUDE ALL WARRANTIES.";
		html += "<P>WHEN PERMITTED BY LAW, PT, AND PT&#39s SUPPLIERS AND DISTRIBUTORS, WILL NOT BE RESPONSIBLE FOR LOST PROFITS, REVENUES, OR DATA, FINANCIAL LOSSES OR INDIRECT, SPECIAL, CONSEQUENTIAL, EXEMPLARY, OR PUNITIVE DAMAGES.  TO THE EXTENT PERMITTED BY LAW, THE TOTAL LIABILITY OF PT, AND ITS SUPPLIERS AND DISTRIBUTORS, FOR ANY CLAIM UNDER THESE TERMS, INCLUDING FOR ANY IMPLIED WARRANTIES, IS LIMITED TO THE AMOUNT YOU PAID US TO USE THE SERVICES (OR, IF WE CHOOSE, TO SUPPLYING YOU THE SERVICES AGAIN).  IN ALL CASES, PT, AND ITS SUPPLIERS AND DISTRIBUTORS, WILL NOT BE LIABLE FOR ANY LOSS OR DAMAGE THAT IS NOT REASONABLY FORESEEABLE.";


		html += "<form method=\"GET\" action=\"./dailyjournal\">";
		html += "<input type=\"checkbox\" id=\"ckbox\" name=\"confirm\" onClick=\"enableSubmit()\">I Agree<br>";
		html += "<input   type=\"hidden\" name=\"username\" value=\"" + username + "\"></input>";
		html += "<input     type=\"hidden\" name=\"password\" value=\"" + password + "\" ></input>";
		html += "<input  type=\"hidden\" type=\"password\" name=\"password2\" value=\"" +password2 + "\"></input>";
		html += "<button type=\"submit\" disabled=\"disabled\" id=\"okay\">Okay</button>";
		html += "<button type=\"submit\" name=\"cancelNewUser\">Cancel</button>";
		html += "</form>";

		html += "</body></html>";

		out.print(html);

	}

	private void contact(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		final String username = "dan.schrimpsher@pierlesstech.com";
		final String password = "ultimum0!";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			String email = request.getParameter("email");
			String subject = request.getParameter("subject");
			String body = request.getParameter("textarea");
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("dan.schrimpsher@pierlesstech.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("dan.schrimpsher@pierlesstech.com"));
			message.setSubject("DailyJournal Comment/Question Ref:"+ staticIndex++);
			message.setText(email +"\n" + subject + "\n" + body);

			Transport.send(message);

			System.out.println("Done");

		} 
		catch (MessagingException e) {
			System.out.println("send failed, exception: " + e);
			MiscUtilities.GenerateAlertWithLogout("Email Send Failed");		
		}

		intialSetup(response);
	}

	private void debug(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {

			JournalManager jm = getJournalManager(request);
			String html = MiscUtilities.GenerateHeader("Nottingham.css", "Arial");
			html += "<pre>" + jm.getCurrentUser().toString() + "</pre>";
			html += MiscUtilities.GenerateFooter();
			out.println(html);
		}
		catch (UserException e) {
			String html = MiscUtilities.GenerateAlertWithLogout(" do not meet the requirements.<br>Username and passwords must be 6-15 alphanumeric or special characters.");
			out.println(html);
			e.printStackTrace();
			intialSetup(response);
		}
	}

	private void intialSetup(HttpServletResponse response) throws IOException {
		response.sendRedirect("./index.html");		

		//		response.setContentType("text/html");
		//		PrintWriter out = response.getWriter();
		//
		//		String html = MiscUtilities.GenerateHeader("Nottingham.css", "Arial") +
		//				"<center><h2>Login</h2><FORM ACTION=\"dailyjournal\"><INPUT TYPE=\"Submit\" value=\"googleAccount\" name=\"googleAccount\"></FORM></center>" +  
		//
		//				"<center><h2>Login</h2><FORM ACTION=\"dailyjournal\" >  Username: <input type=\"text\" name=\"username\" size=\"15\" />" +
		//				"<br />Password: <input type=\"password\" name=\"password\" size=\"15\" />" +
		//				"<br /><INPUT TYPE=\"Submit\" value=\"Login\" name=\"login\"></FORM></center>" +   
		//
		//
		//
		//			"<center><h2>New User Registration</h2><FORM ACTION=\"dailyjournal\" >  Username: <input type=\"text\" name=\"username\" size=\"15\" />" +
		//			"<br />Password: <input type=\"password\" name=\"password\" size=\"15\" />" +
		//			"<br /><INPUT TYPE=\"SUBMIT\" value=\"Register\" name=\"register\"></FORM>" +   			
		//			MiscUtilities.GenerateFooter();
		//		out.println(html);


	}

	private void createUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String password2 = request.getParameter("password2");
		if (!MiscUtilities.validateLogin(username) || !MiscUtilities.validateLogin(password) || !MiscUtilities.validateLogin(password2)) {
			System.out.println("Invalid Username/Password");
			String html = MiscUtilities.GenerateAlertWithLogout("Username/Password do not meet the requirements.  Username and passwords must be 6-15 alphanumeric or special characters.");
			out.println(html);
		}
		else if (!password.equals(password2)) {
			System.out.println("Passwords did not match!");
			String html = MiscUtilities.GenerateAlertWithLogout("Passwords did not match, please try again.");
			out.println(html);
		}
		else {

			try {

				JournalManager jm = new JournalManager();
				jm.createUser(username, password);
				loadUser(request, response);
			}
			catch (UserExistsException e) {
				//				e.printStackTrace();
				System.out.println(e.getLocalizedMessage());
				String html = MiscUtilities.GenerateAlertWithLogout("Username: " + username + " already exists, please choose another username.  We recommend you use your email address.");
				out.println(html);
			}

			catch (UserException e) {
				System.out.println("User Exception" + e.getLocalizedMessage());
				String html = MiscUtilities.GenerateAlertWithLogout("Username/Password do not meet the requirements.<br>Username and passwords must be 6-15 alphanumeric or special characters.");
				out.println(html);
			}	
		}
	}

	private void createGoogleUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user == null) {
			response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
		}





		try {
			JournalManager jm = new JournalManager();
			jm.createGoogleUser(user);
			loadUser(request, response);
		}
		catch (UserExistsException e) {
			e.printStackTrace();
			String html = MiscUtilities.GenerateHeader("none", "Arial") + "<p>Username: " + user.getNickname() + " already exists, please choose another username.  We recommend you use your email address.";
			html += "<a href=\"dailyjournal\">Try Again</a></body></html>";
			html += MiscUtilities.GenerateFooter();
			out.println(html);
		}

		catch (UserException e) {
			out.println(e.toString());
		}	

	}

	private void loadUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		if (!MiscUtilities.validateLogin(username) || !MiscUtilities.validateLogin(password)) {
			String html = MiscUtilities.GenerateAlertWithLogout(username  + ":" + password + " do not meet the requirements.  Username and passwords must be 6-15 alphanumeric or special characters.");
			out.println(html);
		}
		else {
			try {
				JournalManager jm = new JournalManager();
				if (jm.login(username, password)) {
					Calendar c = GregorianCalendar.getInstance();
					GeneralEntry e = jm.getEntry(c);
					Text di = e.get();


					HttpSession session = request.getSession();
					synchronized(session) {
						session.setAttribute("jm", jm);
					}
					String html = generateJournal(jm, c, e, request);
					out.println(html);
				}
				else {


					String html = MiscUtilities.GenerateAlertWithLogout("That username and password combination is invalid.  If you do not yet have an account, please register as a new user.");
					out.println(html);
				}
			} 
			catch (UserException e) {
				String html = MiscUtilities.GenerateAlertWithLogout(username  + ":" + password + " do not meet the requirements.<br>Username and passwords must be 6-15 alphanumeric or special characters.");
				out.println(html);
				e.printStackTrace();
			}
		}
	}

	private void storeEntry(HttpServletRequest request, HttpServletResponse response) throws UserException, IOException, RequestException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		synchronized (response) {
			try {

				JournalManager jm = getJournalManager(request);
				String text = request.getParameter("textarea");
				if (text == null) {
					return;
				}
				String date = request.getParameter("dateOfSubmit");
				date = date.trim();
				//			text = MiscUtilities.removeDate(text);
				text = text.trim();
				//			System.out.println(text);
				Calendar c = jm.getCurrentDay();
				String currentDate = MiscUtilities.printTime(c).trim();
				System.out.println(date + " vs " + currentDate);
				if (date.equals(currentDate)) {
					jm.saveEntry(text);
					HttpSession session = request.getSession();
					synchronized(session) {
						session.setAttribute("jm", jm);
					}
					UserInfo user= jm.getCurrentUser();
					System.out.println("=================================================================");
					System.out.println(user.toString());
					System.out.println("=================================================================");

				}
				else {
					String html = MiscUtilities.GenerateAlertWithLogout("Incorrect Date in the Text.  Not saving." +  currentDate + " vs " + date);
					out.println(html);
					throw new RequestException();
				}
			}
			catch (SizeLimitException e) {
				String html = MiscUtilities.GenerateAlertWithLogout("You have excected the size limitations.  You will be able to purchase additional storage soon.");
				out.println(html);
				e.printStackTrace();
				reprintDay(request, response);
			}
		}

	}

	private void saveEntry(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			JournalManager jm = getJournalManager(request);
			Calendar c = jm.getCurrentDay();
			GeneralEntry e = jm.getEntry(c);
			out.println(generateJournal(jm, c, e, request));
		}
		catch (UserException e) {
			String html = MiscUtilities.GenerateAlertWithLogout("This is a password protected Daily Journal.  Please login to use.  If you are new, please register.");
			out.println(html);
			intialSetup(response);	
		}

	}

	private void incrementDay(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			JournalManager jm = getJournalManager(request);
			Calendar c = jm.getCurrentDay();
			c.add(Calendar.DAY_OF_MONTH, 1);
			jm.setCurrentDay(c);
			HttpSession session = request.getSession();
			synchronized(session) {
				session.setAttribute("jm", jm);
			}
			GeneralEntry e = jm.getEntry(c);
			out.println(generateJournal(jm, c, e, request));
		}
		catch (UserException e) {
			String html = MiscUtilities.GenerateAlertWithLogout("This is a password protected Daily Journal.  Please login to use.  If you are new, please register.");
			out.println(html);
			intialSetup(response);	
		}

	}

	private void decrementDay(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			JournalManager jm = getJournalManager(request);
			Calendar c = jm.getCurrentDay();
			c.add(Calendar.DAY_OF_MONTH, -1);
			jm.setCurrentDay(c);
			HttpSession session = request.getSession();
			synchronized(session) {
				session.setAttribute("jm", jm);
			}
			GeneralEntry e = jm.getEntry(c);
			out.println(generateJournal(jm, c, e, request));
		}
		catch (UserException e) {
			String html = MiscUtilities.GenerateAlertWithLogout("This is a password protected Daily Journal.  Please login to use.  If you are new, please register.");
			out.println(html);
			intialSetup(response);	
		}

	}

	private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		synchronized(session) {
			session.removeAttribute("JournalManager");
			session.removeAttribute("Username");
			session.invalidate();
		}
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String html = MiscUtilities.GenerateAlertWithLogout("Please shut down the browser window to prevent unauthorized access to your data.");
		out.println(html);
		//		intialSetup(response);
	}

	private void uploadImage(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String urlImage = request.getParameter("urlImage");
		try {

			JournalManager jm = getJournalManager(request);
			if (!urlImage.equals("")) {
				jm.addImage(urlImage);
			}

			Calendar c = jm.getCurrentDay();
			GeneralEntry e = jm.getEntry(c);
			HttpSession session = request.getSession();
			synchronized(session) {
				session.setAttribute("jm", jm);
			}
			out.println(generateJournal(jm, c, e, request));


			//		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			//		// Create a factory for disk-based file items
			//		FileItemFactory factory = new DiskFileItemFactory();
			//
			//		// Create a new file upload handler
			//		ServletFileUpload upload = new ServletFileUpload(factory);
			//
			//		// Parse the request
			//		List<FileItem> items;
			//		try {
			//
			//			JournalManager jm = getJournalManager(request);
			//			Calendar c = jm.getCurrentDay();
			//			GeneralEntry e = jm.getEntry(c);
			//
			//
			//
			//
			//			items = upload.parseRequest(request);
			//			for (FileItem ft : items) {
			//				if (ft.isFormField()) {
			//					System.out.println("Form Field "+ ft.getFieldName() + ": " + ft.getString());
			//					if (ft.getSize() >= 1) {
			//						ImageItem it = new ImageItem();
			//						it.setData(ft.getString());
			//						e.addImage(it);
			//					}
			//				}
			//				else if (ft.getSize() >= 1) {					
			//					File file = new File("C:/Program Files (x86)/Apache Software Foundation/Apache2.2/htdocs/img/" + ft.getName());
			//					System.out.println(ft.getFieldName() + ":" + file.getAbsolutePath());
			//					ft.write(file);
			//					ImageItem it = new ImageItem();
			//					it.setData("http://192.168.1.6/img/" + ft.getName());
			//					e.addImage(it);
			//				}
			//			}

		}
		catch (UserException e) {
			String html = MiscUtilities.GenerateAlertWithLogout("This is a password protected Daily Journal.  Please login to use.  If you are new, please register.");
			out.println(html);
		}





	}

	private void addImage(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			JournalManager jm = getJournalManager(request);
			UserInfo user = jm.getCurrentUser();
			String colorPackage = user.getBackgroundImage();
			String fontfamily = user.getFontFamily();
			String fontsize = user.getFontSize();
			String html = MiscUtilities.GenerateHeader(colorPackage, fontfamily);

			html += "<form action=\"dailyjournal\">" +
					"<p>Please specify the web address of an image:<br>" + 
					"<input type=\"text\" name=\"urlImage\" ></p>" + 
					"<div><input class=\"stone\" type=\"submit\" value=\"uploadImage\" name=\"uploadImage\">" +
					"</div></form>";

			html += MiscUtilities.GenerateFooter();
			out.println(html);
		}
		catch (UserException e) {
			String html = MiscUtilities.GenerateAlertWithLogout("This is a password protected Daily Journal.  Please login to use.  If you are new, please register.");
			out.println(html);
			intialSetup(response);	
		}

	}

	private void changeSetting(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			JournalManager jm = getJournalManager(request);
			String background = request.getParameter("colorPackage");
			String ff = request.getParameter("fontfamily");
			String fs = request.getParameter("fontsize");

			UserInfo user = jm.getCurrentUser();
			user.changeBackground(background);
			user.changeFont(ff, fs);
			jm.updateUser();




			Calendar c = jm.getCurrentDay();
			GeneralEntry e = jm.getEntry(c);
			HttpSession session = request.getSession();
			synchronized(session) {
				session.setAttribute("jm", jm);
			}
			out.println(generateJournal(jm, c, e, request));


		}
		catch (UserException e) {
			String html = MiscUtilities.GenerateAlertWithLogout("This is a password protected Daily Journal.  Please login to use.  If you are new, please register.");
			out.println(html);
		}

	}

	private void gotoSetting(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			JournalManager jm = getJournalManager(request);
			UserInfo user = jm.getCurrentUser();
			String colorPackage = user.getBackgroundImage();
			String fontfamily = user.getFontFamily();
			String fontsize = user.getFontSize();
			String html = MiscUtilities.GenerateHeader(colorPackage, fontfamily);
			html += "<div id=\"textarea\">";
			html += "<form  ACTION=\"dailyjournal\">" ;
			//Background
			html += "<select name=\"colorPackage\" style=\"font-family: " + fontfamily + ";font-size: " + fontsize + ";\">";
			Map<String, String> colorPackages = jm.getBackgrounds();
			String currentBackground = colorPackage;
			for (String background : colorPackages.keySet()) {
				String filename = colorPackages.get(background);
				if (currentBackground.equals(filename)) {
					html += "<option selected value=\"" + filename + "\">" + background + "</option>";

				}
				else {				
					html += "<option value=\"" + filename + "\">" + background + "</option>";
				}
			}
			html += "</select><BR>";


			//Font Family
			String currentFontFamily = fontfamily;
			html += "<select name=\"fontfamily\" style=\"font-family: " + fontfamily + ";font-size: " + fontsize + ";\">";
			Map<String, String> fontfamilys = jm.getFontFamilies();
			for (String font : fontfamilys.keySet()) {
				String filename = fontfamilys.get(font);
				if (currentFontFamily.equals(filename)) {
					html += "<option class=\"" + font + "option\" selected value=\"" + filename + "\">" + font + "</option>";

				}
				else {				
					html += "<option  class=\"" + font + "option\" value=\"" + filename + "\">" + font + "</option>";
				}
			}
			html += "</select><BR>";

			//Font Size
			String currentFont = fontsize;
			html += "<select name=\"fontsize\" style=\"font-family: " + fontfamily + ";font-size: " + fontsize + ";\">";

			List<String> fontsizes = jm.getFontSizes();
			for (String fs : fontsizes) {
				if (currentFont.equals(fs)) {
					html += "<option selected value=\"" + fs + "\">" + fs + "</option>";					
				}
				else {
					html += "<option value=\"" + fs + "\">" + fs + "</option>";
				}			}
			html += "</select><BR>";
			html += "<input class=\"stone\" type=\"SUBMIT\" value =\"Change Settings\" name=\"changesettings\">";
			html += "</form></div>";
			html += MiscUtilities.GenerateFooter();
			out.println(html);
		}
		catch (UserException e) {
			String html = MiscUtilities.GenerateAlertWithLogout("This is a password protected Daily Journal.  Please login to use.  If you are new, please register.");
			out.println(html);
		}
	}

	private void gotoDate(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String textDate = request.getParameter("date");


		try {
			Calendar c = MiscUtilities.convertHtml5Date(textDate);

			JournalManager jm = getJournalManager(request);
			GeneralEntry e = jm.getEntry(c);
			jm.setCurrentDay(c);
			HttpSession session = request.getSession();
			synchronized(session) {
				session.setAttribute("jm", jm);
			}
			out.println(generateJournal(jm, c, e, request));
		}
		catch (UserException e) {
			String html = MiscUtilities.GenerateAlertWithLogout("This is a password protected Daily Journal.  Please login to use.  If you are new, please register.");
			out.println(html);
		}
		catch (IllegalArgumentException e) {
			reprintDay(request, response);
			String html = MiscUtilities.GenerateAlert(textDate + " is in an incorrect format.  PLease use YYYY-MM-DD or MM/DD/YYYY");
			out.println(html);
		}
	}


	private void search(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			JournalManager jm = getJournalManager(request);
			UserInfo user = jm.getCurrentUser();
			String colorPackage = user.getBackgroundImage();
			String fontfamily = user.getFontFamily();
			String fontsize = user.getFontSize();

			String searchString = request.getParameter("searchString");
			List<String> searchList = MiscUtilities.getWordList(searchString);
			List<Calendar> dates = jm.wordSearch(searchList);
			String html = MiscUtilities.GenerateHeader(colorPackage, fontfamily);
			html += "<div id=\"textarea\">";
			if (dates.isEmpty()) {
				html +=  "<p style=\"font-family: " + fontfamily + ";font-size: " + fontsize + 
						";\"><a href=dailyjournal?date=" + request.getParameter("dateOfSubmit2") + ">" + searchString +" not found.</a></p>";
			}
			else {
				for (Calendar c : dates) {
					html += "<p style=\"font-family: " + fontfamily + ";font-size: " + fontsize + 
							";\"><a href=dailyjournal?date=" + MiscUtilities.printSimpleTime(c) + ">"  + MiscUtilities.printTime(c) + "</a></p>";
				}
			}
			html += "</div>";
			out.println(html);
		}
		catch (UserException e) {
			String html = MiscUtilities.GenerateAlertWithLogout("This is a password protected Daily Journal.  Please login to use.  If you are new, please register.");
			out.println(html);
			intialSetup(response);	
		}
	}

	private void deleteImage(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			JournalManager jm = getJournalManager(request);
			UserInfo user = jm.getCurrentUser();
			String index = request.getParameter("deleteImage");
			jm.removeImage(Integer.parseInt(index));
			HttpSession session = request.getSession();
			synchronized(session) {
				session.setAttribute("jm", jm);
			}
			reprintDay(request, response);
		}
		catch (UserException e) {
			String html = MiscUtilities.GenerateAlertWithLogout("This is a password protected Daily Journal.  Please login to use.  If you are new, please register.");
			out.println(html);
			intialSetup(response);	
		}
	}

	private JournalActions whichSubmitType(HttpServletRequest request) {
		//		System.out.println(request.getParameterMap().toString());
		JournalActions result  = JournalActions.INIT; 
		//Check if they hit a button
		String type = request.getParameter("register");
		if (MiscUtilities.validate(type)) {
			result = JournalActions.LICENSE;
			return result;
		}

		type = request.getParameter("confirm");
		if (MiscUtilities.validate(type)) {
			result = JournalActions.REGISTER;
			return result;

		}

		type = request.getParameter("login");
		if (MiscUtilities.validate(type)) {
			result = JournalActions.LOGIN;
			return result;

		}
		type = request.getParameter("search");
		if (MiscUtilities.validate(type))  {
			result = JournalActions.SEARCH;
			return result;

		}

		type = request.getParameter("nextDay");
		if (MiscUtilities.validate(type)) {
			result = JournalActions.NEXTDAY;
			return result;

		}
		type = request.getParameter("prevDay");
		if (MiscUtilities.validate(type)) {
			result = JournalActions.PREVDAY;
			return result;

		}
		type = request.getParameter("logout");
		if (MiscUtilities.validate(type)) {
			result = JournalActions.LOGOUT;
			return result;

		}
		type = request.getParameter("save");
		if (MiscUtilities.validate(type)) {
			result = JournalActions.SAVE;
			return result;

		}

		//		type = request.getParameter("setting");
		//		if (MiscUtilities.validate(type)) {
		//			result = JournalActions.SETTING;
		//			return result;
		//
		//		}
		type = request.getParameter("changesettings");
		if (MiscUtilities.validate(type)) {
			result = JournalActions.CHANGESETTING;
			return result;

		}

		type = request.getParameter("changeAccount");
		if (MiscUtilities.validate(type)) {
			result = JournalActions.CHANGEACCOUNT;
			return result;

		}		

		//		type = request.getParameter("addImage");
		//		if (MiscUtilities.validate(type)) {
		//			result = JournalActions.ADDIMAGE;
		//			return result;
		//
		//		}
		type = request.getParameter("uploadImage");
		if (MiscUtilities.validate(type)) {
			result = JournalActions.UPLOADIMAGE;
			return result;

		}
		//		type = request.getParameter("googleAccount");
		//		if (MiscUtilities.validate(type)) {
		//			result = JournalActions.GOOGLELOGIN;
		//			return result;
		//
		//		}
		type = request.getParameter("deleteImage");
		if (MiscUtilities.validate(type)) {
			result = JournalActions.DELETEIMAGE;
			return result;

		}
		type = request.getParameter("debug");
		if (MiscUtilities.validate(type)) {
			result = JournalActions.DEBUG;
			return result;

		}

		type = request.getParameter("contact");
		if (MiscUtilities.validate(type)) {
			result = JournalActions.CONTACT;
			return result;

		}


		//submit date does play nice if the user hits enter rather than the buttons.  
		type = request.getParameter("submitDate");
		if (MiscUtilities.validate(type))  {
			String date = request.getParameter("date");
			if (MiscUtilities.validate(date) && !date.trim().equals("")) {			
				result = JournalActions.DATE;
				return result;
			}

		}

		//No button was pressed so we have to infer what was meant. 
		//1.  Check goto date
		//2.  check search string
		//3.  urlImage
		//4. Package
		//5.  Save Text


		type = request.getParameter("date");
		if (MiscUtilities.validate(type) && !type.trim().equals("")) {
			result = JournalActions.DATE;
			return result;

		}
		type = request.getParameter("searchString");
		if (MiscUtilities.validate(type) && !type.trim().equals("")) {
			result = JournalActions.SEARCH;
			return result;

		}
		type = request.getParameter("urlImage");
		if (MiscUtilities.validate(type) && !type.trim().equals("")) {
			result = JournalActions.UPLOADIMAGE;
			return result;

		}		
		type = request.getParameter("colorPackage");
		if (MiscUtilities.validate(type) && !type.trim().equals("")) {
			result = JournalActions.CHANGESETTING;
			return result;

		}	
		//if all else fails just save the text and moveon.
		type = request.getParameter("textarea");
		if (MiscUtilities.validate(type) && !type.trim().equals("")) {
			result = JournalActions.SAVE;
			return result;

		}			
		return result;
	}




	private void reprintDay(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			JournalManager jm = getJournalManager(request);
			Calendar c = jm.getCurrentDay();
			GeneralEntry e = jm.getEntry(c);
			out.println(generateJournal(jm, c, e, request));
		}
		catch (UserException e) {
			String html = MiscUtilities.GenerateAlertWithLogout("This is a password protected Daily Journal.  Please login to use.  If you are new, please register.");
			out.println(html);
			intialSetup(response);	
		}

	}

	private JournalManager getJournalManager(HttpServletRequest request) throws UserException {
		HttpSession session = request.getSession();
		JournalManager jm;
		synchronized(session) {
			jm = (JournalManager) session.getAttribute("jm"); 
		}
		if ( (jm == null) || (jm == null) )  { 
			throw new UserException();
		}
		else {
			return jm;
		}
	}



	private String generateJournal(JournalManager jm, Calendar c, GeneralEntry e, HttpServletRequest request) throws UserException {
		//		System.out.println(e.toString());
		String text = "";
		if (!e.isEmpty()) {
			text = e.get().toString();
		}
		UserInfo user = jm.getCurrentUser();
		String colorPackage = user.getBackgroundImage();
		String fontfamily = user.getFontFamily(); 
		String fontsize = user.getFontSize();

		String html = MiscUtilities.GenerateHeader(colorPackage, fontfamily);





		//		//Ads
		//		html += buildLeftAd();
		//		html += buildRightAd();


		html += "<div class=\"container\">";

		html +=		"<FORM ACTION=\"dailyjournal\">" +


				"<table><tr> " +
				"<th><div id=\"spacer1\"><br></div></th>" +

				"<th><input type=\"date\" name=\"date\" onkeyup=\"f()\" title=\"Enter the date as YYYY-MM-DD\"></th>" + 
				"<th><INPUT class=\"stone\" TYPE=\"SUBMIT\" value=\"GoTo\" name=\"submitDate\"/></th>" +
				"<th><div id=\"spacer2\"><br></div></th>" +

				"<th><input type=\"text\" name=\"searchString\"></th>" +
				"<th><INPUT class=\"stone\" TYPE=\"SUBMIT\" value=\"Search\" name=\"search\"></th>" +

				"</tr> </table>" +

				"<table class=\"ex1\">";

		//Build Top Row of stuff
		html += "<tr>";
		html += buildDateSpacer();
		html += buildDate(c);

		//Build buttons
		html += buildHiddenDate(c);
		html += buildSaveButton(jm);
		html += buildLogoutButton(jm);
		html += buildAccountButton(jm, colorPackage, fontfamily, fontsize);	


		html += buildAddImageButton(jm);
		html += buildSettingsButton(jm, colorPackage, fontfamily, fontsize);	
		html += buildHiddenDate2(c);
		html += "</tr></table>";



		html += buildMainWritingArea(fontfamily, fontsize, text, e);




		html += "</FORM>";
		html += "</div>";


		html += buildBottomAd();

		html += tracking();

		html += MiscUtilities.GenerateFooter();			     			     		

		return html;
	}




	private String buildAccountButton(JournalManager jm, String colorPackage,
			String fontfamily, String fontsize) {
		String html = "\n";

		html += "<span id=\"uploadAccount\"><button class =\"stone\" TYPE=\"button\">Account</button></span>";
		html += "<center>";		
		html +="<div id=\"popUpAccount\">";
		html +="<table class=\"popupTable\"><tr>";


		String password = jm.getCurrentUser().getPassword();
		html += "<tr class=\"field\"><td>New Password</td><td><input type=\"text\" class=\"infoSelect\" name=\"newPassword\" placeholder=\"New Password\">";
		html += "<BR></td></tr>";		
		html += "<tr class=\"field\"><td>Verify New Password</td><td><input type=\"text\" class=\"infoSelect\" name=\"newPassword2\" placeholder=\"Retype Password\">";
		html += "<BR></td></tr>";	
		html += "<tr class=\"singleButton\"><td colspan=\"2\"><a href=\"./license.html\" target=\"_blank\"><button class=\"stone\" id=\"wide\" type=\"button\">View License</button></a></td></tr>";
		html += "<tr class=\"singleButton\"><td colspan=\"2\"><a href=\"./tutorial.html\" target=\"_blank\"><button class=\"stone\" id=\"wide\" type=\"button\">View Tutorial</button></a></td></tr>";

		html += "<tr><td><input class=\"stone\" type=\"SUBMIT\" value =\"OK\" name=\"changeAccount\"></td>";
		html += "<td id=\"cancelAccount\"><button class=\"stone\" type=\"button\">Cancel</button></td></tr></table>";
		html += "</div>";				
		html += "</center>";
		html += "\n";
		return html;
	}

	private String tracking() {
		String html ="\n";
		html += "<script type=\"text/javascript\">\n";
		html += "var _gaq = _gaq || [];\n";
		html += "_gaq.push(['_setAccount', 'UA-37713910-1']);\n";
		html += "_gaq.push(['_trackPageview']);\n";

		html += "(function() {\n";
		html += "var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n";
		html += "ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n";
		html += "var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n";
		html += "})();\n";

		html += "</script>\n";
		return html;
	}

	public String buildMainWritingArea(String fontfamily, String fontsize, String text, GeneralEntry e) {
		String html = "\n"; 
		//Main writing area
		html +=		"<table class=\"ex1\"><tr>";

		html +=	"<tr><td><INPUT class =\"prev\" TYPE=\"SUBMIT\" value=\"\" name=\"prevDay\">" +
				"<td><textarea id=\"textarea\" name=\"textarea\"  wrap=\"virtual\" style=\"font-family: " + fontfamily + ";font-size: " + fontsize + 
				";\">" + 

				 text + "</textarea></td>";
		html += buildImages(e);

		html +=	"<td><INPUT class =\"next\" TYPE=\"SUBMIT\"  value=\"\" name=\"nextDay\"></td>";
		html += "<td>" + buildRightAd() + "</td>";
		html += "</tr></table>";
		return html;
	}

	private String buildRightAd() {
		String html = "\n"; 
		html += "<div class=\"rightAds\">";
		html += "<script type=\"text/javascript\"><!--\n" + 
				"google_ad_client = \"ca-pub-2907621812701143\";\n" +
				"/* SideAds */\n" +
				"google_ad_slot = \"6806732818\";\n" +
				"google_ad_width = 160;\n" +
				"google_ad_height = 600;\n"+
				"//-->\n" +
				"</script>\n" +
				"<script type=\"text/javascript\"\n" +
				"src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\">\n" +	
				"</script>\n";
		html += "</div>";

		return html;
	}


	private String buildBottomAd() {
		String html = "\n"; 
		html += "<div class=\"bottomAds\">";
		html += "<script type=\"text/javascript\"><!--\n";
		html += "google_ad_client = \"ca-pub-2907621812701143\";\n";
		html += "/* TopBanner */\n";
		html += "google_ad_slot = \"6586406813\";\n";
		html += "google_ad_width = 728;\n";
		html += "google_ad_height = 90;\n";
		html += "//-->\n"; 
		html += "</script>\n";
		html += "<script type=\"text/javascript\"\n";
		html += "src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\">\n";
		html += "</script>\n";

		html += "</div>";		
		return html;
	}

	private String buildLeftAd() {
		String html = "\n"; 
		html += "<div class=\"leftAds\">Place Advertisements here.</div>"; 
		return html;
	}

	private String buildImages(GeneralEntry e) {
		String html = "\n"; 
		html +="<td><div id=\"divarea\" contentEditable=\"false\" >";

		for (int i = 0; i < e.numberOfImages(); i++) {
			System.out.println(e.get(i));
			html += "<a href=\"javascript:popitup('" + e.get(i) + "')\">";
			html += "<img src=\"" + e.get(i) + "\" class=\"thumbnail\"></a>";
			html += "<button class=\"stone\" TYPE=\"SUBMIT\" value=\"" + i + "\" name=\"deleteImage\">Remove</button>";

		}
		html +=		"</div></td>";
		return html;
	}

	private String buildHiddenDate2(Calendar c) {
		String html = "\n";
		html += "<INPUT class =\"stone\" TYPE=\"hidden\" value=\"" + MiscUtilities.printSimpleTime(c) + "\" name=\"dateOfSubmit2\"> ";
		return html;
	}

	private String buildHiddenDate(Calendar c) {
		String html = "\n";
		html += "<td><INPUT class =\"stone\" TYPE=\"hidden\" value=\"" + MiscUtilities.printTime(c) + "\" name=\"dateOfSubmit\">";		
		return html;
	}

	private String buildDateSpacer() {
		String html = "\n";
		html += "<tr><td><div id=\"shortdivarea\"><td>";
		return html;
	}

	private String buildDate(Calendar c) {
		String html = "\n";
		html += "<td id=\"dateTd\"><div id=\"datearea\">" + MiscUtilities.printTime(c) + "</div></td>";
		return html;
	}


	private String buildSaveButton(JournalManager jm) {
		String html = "\n";
		html += "<INPUT class =\"stone\" TYPE=\"SUBMIT\" value=\"Save\" name=\"save\">";
		html += "\n";
		return html;
	}

	private String buildLogoutButton(JournalManager jm) {
		String html = "\n";
		html += "<span id=\"logoutConfirm\"><button class =\"stone\" TYPE=\"button\">Logout</button></span>";
		html +="<div id=\"popUpLogout\">";
		html += "<center>";				
		html +=	"<p class=\"info\">Are you sure you want to leave your Daily Journal?<br>";
		html +="<table><tr>";

		html += "<td><input class=\"stone\" type=\"submit\" value=\"Leave\" name=\"logout\"></td>";
		html +=		"<td id=\"cancelLogout\"><button class=\"stone\" type=\"button\">Cancel</button></td>";
		html +=		"</tr></table>";
		html += "</center>";
		html +="</div>";	
		html += "\n";
		return html;
	}

	private String buildAddImageButton(JournalManager jm) {
		String html = "\n";
		html += "<span id=\"uploadImage\"><button class =\"stone\" TYPE=\"button\">Add Image</button></span>";
		html +="<div id=\"popUpImage\">";
		//		html += "<form action=\"dailyjournal\">";
		html += "<p class=\"info\">Please specify the web address of an image:<br>";
		html += "<input size=\"40\" type=\"text\" name=\"urlImage\" ></p>"; 
		html += "<p class=\"commingSoon\">Upload you own photos comming soon</p>";
		html += "<center>";		
		html +="<table><tr>";

		html += "<td><input class=\"stone\" type=\"submit\" value=\"OK\" name=\"uploadImage\"></td>";
		html +=		"<td id=\"cancelButton\"><button class=\"stone\" type=\"button\">Cancel</button></td>";
		html +=		"</tr></table>";
		html += "</center>";
		html +="</div>";	
		html += "\n";
		return html;
	}

	private String buildSettingsButton(JournalManager jm, String colorPackage, String fontfamily, String fontsize) {
		String html = "\n";

		html += "<span id=\"uploadSettings\"><button class =\"stone\" TYPE=\"button\">Settings</button></span>";
		html += "<center>";		
		html +="<div id=\"popUpSettings\">";
		//		html += "<form  ACTION=\"dailyjournal\">" ;
		html +="<table class=\"popUpTable\"><tr tr class=\"field\">";

		//Background
		html += "<td>Package</td><td><select class=\"infoSelect\" name=\"colorPackage\">";
		Map<String, String> colorPackages = jm.getBackgrounds();
		String currentBackground = colorPackage;
		for (String background : colorPackages.keySet()) {
			String filename = colorPackages.get(background);
			if (currentBackground.equals(filename)) {
				html += "<option selected value=\"" + filename + "\">" + background + "</option>";

			}
			else {				
				html += "<option value=\"" + filename + "\">" + background + "</option>";
			}
		}
		html += "</select></td></tr>";


		//Font Family
		String currentFontFamily = fontfamily;
		html += "<tr tr class=\"field\"><td>Font</td><td><select class=\"infoSelect\"  name=\"fontfamily\" >";
		Map<String, String> fontfamilys = jm.getFontFamilies();
		for (String font : fontfamilys.keySet()) {
			String filename = fontfamilys.get(font);
			if (currentFontFamily.equals(filename)) {
				html += "<option selected value=\"" + filename + "\">" + font + "</option>";

			}
			else {				
				html += "<option  value=\"" + filename + "\">" + font + "</option>";
			}
		}
		html += "</select><BR></td></tr>";

		//Font Size
		String currentFont = fontsize;
		html += "<tr tr class=\"field\"><td>Font Size</td><td><select class=\"infoSelect\"  name=\"fontsize\" >";

		List<String> fontsizes = jm.getFontSizes();
		for (String fs : fontsizes) {
			if (currentFont.equals(fs)) {
				html += "<option selected value=\"" + fs + "\">" + fs + "</option>";					
			}
			else {
				html += "<option value=\"" + fs + "\">" + fs + "</option>";
			}			}
		html += "</select><BR></td></tr>";


		html += "<tr><td><input class=\"stone\" type=\"SUBMIT\" value =\"OK\" name=\"changesettings\"></td>";
		html += "<td id=\"cancelSettings\"><button class=\"stone\" type=\"button\">Cancel</button></td></tr></table>";
		html += "</div>";				
		html += "</center>";
		html += "\n";
		return html;
	}

}


