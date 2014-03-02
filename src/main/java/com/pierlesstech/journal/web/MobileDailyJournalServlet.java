package com.pierlesstech.journal.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.pierlesstech.journal.core.JournalManager;
import com.pierlesstech.journal.core.UserInfo;
import com.pierlesstech.journal.core.data.impl.GeneralEntry;
import com.pierlesstech.journal.core.data.impl.Text;
import com.pierlesstech.journal.exceptions.UserException;
import com.pierlesstech.journal.exceptions.UserExistsException;
import com.pierlesstech.journal.web.utilities.MiscUtilities;

@SuppressWarnings("serial")
public class MobileDailyJournalServlet extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		uploadJson(request, response);
	}

	@Override
	public void doGet(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {
		String date = request.getPathInfo().substring(1);
		if (request.getParameter("register") != null) {
			createUser(request, response);
		}
		else {
			getUser(request, response, date);
		}
	}
	
		
	private void getUser(HttpServletRequest request, HttpServletResponse response, String date) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		Writer writer = null;
		try {
			writer = response.getWriter();
			UserInfo user = loadUser(request, response);
			String json;
			if (user == null) {
				json = "{\"error\":\"bad username or password\"}";
			}
			else {
				Calendar c = MiscUtilities.convertHtml5Date(date);
				json = new Gson().toJson(user.getEntry(c));
			}
			writer.write(json);

		} finally {
			writer.close();
		}
	}


	private void createUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		Writer writer = null;		

		try {
			writer = response.getWriter();

			String json;
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			if (!MiscUtilities.validateLogin(username) || !MiscUtilities.validateLogin(password)) {
				json = "{\"error\":\"invalid username or password\"}";
			}
			else {

				try {
					JournalManager jm = new JournalManager();
					jm.createUser(username, password);
					UserInfo user = loadUser(request, response);
					json = new Gson().toJson(user);
				}
				catch (UserExistsException e) {
					json = "{\"error\":\"existing username or password\"}";

				}

				catch (UserException e) {
					json = "{\"error\":\"" + e.getMessage() + "\"}";

				}	
			}
			writer.write(json);

		}
		finally {
			writer.close();
		}	
	}


	private void uploadJson(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Writer writer = response.getWriter();
		String output = "";
		try {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			JournalManager jm = new JournalManager();
			if (jm.login(username, password)) {
				String json = request.getParameter("json");
				//				output += json;
				Gson gson = new Gson();
				UserInfo pUser = gson.fromJson(json, UserInfo.class);
				jm.synchUser(pUser);
				jm.updateUser();
				output += "{\"status\":\"success\"}";

			}
			else {
				output += "{\"status\":\"error\"}";
			}

		}
		catch (UserException e) {
			output += "{\"status\":\"exception\"}";

		}
		finally {
			writer.write(output);
			writer.close();
		}
	}
	private UserInfo loadUser(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		JournalManager jm = new JournalManager();
		if (jm.login(username, password)) {

			return jm.getCurrentUser();

		}
		else {
			return null;
		}
	}


}
