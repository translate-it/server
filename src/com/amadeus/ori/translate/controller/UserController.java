package com.amadeus.ori.translate.controller;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.amadeus.ori.translate.domain.Role;
import com.amadeus.ori.translate.domain.User;
import com.amadeus.ori.translate.json.AdminResultMessage;
import com.amadeus.ori.translate.repository.CacheManager;
import com.amadeus.ori.translate.repository.LogEntryRepository;
import com.amadeus.ori.translate.repository.UserRepository;
import com.amadeus.ori.translate.security.IdentityHolder;


/**
 * 
 * @author ingolf.tobias.rothe@amadeus.com
 */
public class UserController implements Controller {

	private static final Log LOG = LogFactory.getLog(UserController.class);

	private UserRepository userRepository;

	private CacheManager cacheManager;
	
	private IdentityHolder identityHolder;
	
	private LogEntryRepository logEntryRepository;

	/*
	 * Controller is invoked by the Spring Dispatcher. Configuration done in:
	 * /WebContent/WEB-INF/web.xml /WebContent/WEB-INF/Provisioner-servlet.xml
	 * 
	 * @see
	 * org.springframework.web.servlet.mvc.Controller#handleRequest(javax.servlet
	 * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		/*
		 * FOLLOWING ACTIONS ARE ONLY FOR ADMIN USERS
		 */
		if ((identityHolder == null)||(!identityHolder.isAdmin())) {
			throw new ControllerException(Messages.UNAUTHORIZED);
		}
		
		final String path = request.getRequestURI();

		response.setCharacterEncoding("UTF-8");

		// Get the name of the file
		String action = StringUtils.substringAfterLast(path, "/");

		LOG.debug(action);

		if (action.equals("listUsers")) {

			return new ModelAndView("jsonView", "user", userRepository.list());

		}else if (action.equals("createUser")) {
			
			final String username = request.getParameter("username");
			final String password = request.getParameter("password");		
			
			final String isGoogleAccount = request.getParameter("isGoogleAccount");

			if (StringUtils.isEmpty(username) ) {
				
				throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
				
			}
			
			
			if (isGoogleAccount != null) {
				
				return createUser(username, password, Boolean.parseBoolean(isGoogleAccount));
				
			} else {
				
				return createUser(username, password, false);
			}
			
		} else if (action.equals("updateUser")) {
			
			final String username = request.getParameter("username");
			final String password = request.getParameter("password");
			final String firstName = request.getParameter("firstName");
			final String lastName = request.getParameter("lastName");
			final String email = request.getParameter("email");
			final String languages = request.getParameter("languages");
			final String bundles = request.getParameter("bundles");
			final String isEnabled = request.getParameter("isEnabled");			
			final String isAdmin = request.getParameter("isAdmin");
			final String projects = request.getParameter("projects");
			
			if (StringUtils.isEmpty(username) ) {
				
				throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
				
			} else {
				
				return updateUser(username, password, firstName, lastName, email, languages, projects, bundles, isEnabled, isAdmin) ;								
			}

		} else if (action.equals("deleteUser")) {
			
				final String username = request.getParameter("username");
			
			
				if (StringUtils.isEmpty(username) ) {
					
					throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
					
				} else {
					
					return deleteUser(username);								
				}			

		} else if (action.equals("sendMail")) {
			
			final String subject = request.getParameter("subject");
			final String message = request.getParameter("message");
			final String receipients = request.getParameter("receipients");
		
		
			if (StringUtils.isEmpty(subject) || StringUtils.isEmpty(message) ||  StringUtils.isEmpty(receipients) ) {
				
				throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
				
			} else {
				
				return sendMail(subject, message, receipients.split(","));								
			}			
			
		} else if (action.equals("clearCache")) {
			return clearCache();

		} else {

			throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
		}

	}


	private ModelAndView createUser(String username, String password, boolean isGoogleAccount) throws Exception {

		User user = userRepository.findByKey(username);
		
		//check if user exists
		if (user != null) {
			throw new ControllerException("A user with that name already exists!");
		}
		
		User newUser = new User(username, password, isGoogleAccount);
			 newUser.setEmail(username); //user name is an email address
			 newUser.setEnabled(true);
		
		User res = userRepository.save(newUser);

		return new ModelAndView("jsonView", "user", res);
	}

	
	private ModelAndView updateUser(String username, String password, String firstName, String lastName,
			String email, String languages, String projects, String bundles, String isEnabled, String isAdmin) throws ControllerException{
		
		 
		
		User user = userRepository.findByKey(username);
		
		//check if user exists
		if (user == null) {
			throw new ControllerException("User not found");
		} 
		
		if (StringUtils.isNotEmpty(password)) {
			user.setPassword(password);						
		}

		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		user.setLanguages(languages);
		user.setProjects(projects);
		user.setBundles(bundles);		
		user.setEnabled(Boolean.valueOf(isEnabled));
		
		if (Boolean.parseBoolean(isAdmin)) {
			user.setRole(Role.ROLE_ADMIN);
		} else {
			user.setRole(Role.ROLE_USER);
		}
		
		userRepository.save(user);
		
		return new ModelAndView("jsonView", "user", user);
		
	}

	private ModelAndView deleteUser(String username) throws ControllerException{

		userRepository.deleteByKey(username);
		
		AdminResultMessage res = new AdminResultMessage();
			res.setMessage("User deleted.");

		return new ModelAndView("jsonView", "adminResult", res);
		
	}

	// Send email
	private ModelAndView sendMail(String subject, String message, String[] receipients) {
		
		AdminResultMessage res = new AdminResultMessage();
		try {
		//	LOG.info("Got a contact request from: " + req.getParameter("email") + ", " + req.getParameter("name"));
			
			
			Session session = Session.getDefaultInstance(new Properties(), null);
		 	Message msg = new MimeMessage(session);
		 			msg.setFrom(new InternetAddress("lab.amadeus@gmail.com", "TranslateIT Administrator"));
		 			msg.setSubject(subject);
			
			for (int i = 0; i < receipients.length; i++) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(receipients[i]));				
			}            
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress("innovation@amadeus.com", "Innovation"));

            //put the contents as HTML/Mime Body
            MimeBodyPart 	htmlPart = new MimeBodyPart();
							htmlPart.setContent("<span style=\"font-family:sans-serif;color:#1F497D\">" + 
												message +
												"</span><span style=\"font-family:sans-serif;color:gray;font-size:10px\">" +
												"<br>--<br>" +
												"Send by <a href=\"http://translate-it.appspot.com\" style=\"color:gray\">TranslateIT!</a></span>"
												, "text/html");
							
			Multipart 	mp = new MimeMultipart();            			 
		 				mp.addBodyPart(htmlPart);            
            msg.setContent(mp);
            
			Transport.send(msg);

			logEntryRepository.addEntry("Message was sent to users: " + subject);
			
			res.setMessage(Messages.MAIL_SENT_SUCCESS);
			
		} catch (Exception e) {
			LOG.error("Error while sending email", e);
			res.setMessage(Messages.MAIL_SENT_ERROR);
		}
		
		return new ModelAndView("jsonView", "adminResult", res);
	}

	

	private ModelAndView clearCache() {

		cacheManager.clearAll();

		AdminResultMessage res = new AdminResultMessage();
		res.setMessage(Messages.CACHE_CLEARED);

		return new ModelAndView("jsonView", "adminResult", res);
	}

	
	

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}


	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void setIdentityHolder(IdentityHolder identityHolder) {
		this.identityHolder = identityHolder;
	}


	public void setLogEntryRepository(LogEntryRepository logEntryRepository) {
		this.logEntryRepository = logEntryRepository;
	}
}
