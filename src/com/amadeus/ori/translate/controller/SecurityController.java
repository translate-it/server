package com.amadeus.ori.translate.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


import com.amadeus.ori.translate.domain.Project;
import com.amadeus.ori.translate.domain.Role;
import com.amadeus.ori.translate.domain.User;
import com.amadeus.ori.translate.repository.LogEntryRepository;
import com.amadeus.ori.translate.repository.UserRepository;
import com.amadeus.ori.translate.security.IdentityHolder;
import com.amadeus.ori.translate.security.UserDisabledException;
import com.google.appengine.api.users.UserServiceFactory;
import com.sun.xml.internal.ws.message.stream.StreamHeader12;


/**
 * Controller for user authentication and to retrieve the
 * identity of a previously authenticated user.
 * 
 * @author ingolf.tobias.rothe@amadeus.com
 */
public class SecurityController implements Controller {
	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(SecurityController.class);

	private UserRepository userRepository;	

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

		final String path = request.getRequestURI();

		response.setCharacterEncoding("UTF-8");

		// Get the name of the file
		String action = StringUtils.substringAfterLast(path, "/");

		LOG.debug(action);			

		if (action.equals("getIdentity")) {

			return getIdentity();
			
		} else if (action.equals("logout")) {

				return logout(response);
		
		} else if (action.equals("loginGoogle")) {

			String redirect = request.getHeader("Referer");
			if (redirect == null) {
				redirect = "/";
			}
			response.sendRedirect(UserServiceFactory.getUserService().createLoginURL(redirect));

		} else if (action.equals("logoutGoogle")) {

			//google account has been successful signed out!
			return new ModelAndView("jsonView", "logout", true);
			
		} else if (action.equals("authenticate")) {
			
			final String username = request.getParameter("username");
			final String password = request.getParameter("password");
			
			
			if (StringUtils.isEmpty(username)||StringUtils.isEmpty(password)) {
				
				throw new ControllerException(Messages.ERROR_INVALID_REQUEST);

			}

			//PasswordEncoder enc = new ShaPasswordEncoder(256);		
			
			return authenticate(username, password);
			
		} else if (action.equals("updateProfile")) {
			
			final String password = request.getParameter("password");
			final String firstName = request.getParameter("firstName");
			final String lastName = request.getParameter("lastName");
			final String email = request.getParameter("email");
				
			return updateProfile(password, firstName, lastName, email) ;								


		} else {

			throw new ControllerException(Messages.ERROR_INVALID_REQUEST);
		}
		
		return null;

	}


	/**
	 * A user can update certain fields of its own profile.
	 * 
	 * @param username
	 * @param password
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @return
	 * @throws ControllerException
	 */
	private ModelAndView updateProfile(String password,
			String firstName, String lastName, String email)  throws ControllerException{
		
		if ((identityHolder == null)||(!identityHolder.hasIdentity())) {
			throw new ControllerException("User not autenticated.");
		}
		
		User identity = userRepository.findByKey(identityHolder.getIdentity().getUsername());
		
		//check if user exists
		if (identity == null) {
			throw new ControllerException("User not found.");
		} 
		
		if (StringUtils.isNotEmpty(password)) {
			identity.setPassword(password);						
		}

		identity.setFirstName(firstName);
		identity.setLastName(lastName);
		identity.setEmail(email);

		logEntryRepository.addEntry("PROFILE UPDATE: " + identity.getUsername());
		userRepository.save(identity);
		
		return new ModelAndView("jsonView", "identity", identity);
	}


	/**
	 * destroys the identity in the session context
	 * @param response 
	 * @param request 
	 * 
	 * @return
	 * @throws IOException 
	 */
	private ModelAndView logout(HttpServletResponse response) throws IOException {
		
		if (!identityHolder.hasIdentity()) {
			//no one logged in
			return new ModelAndView("jsonView", "logout", false);
		}
		
		User identity = identityHolder.getIdentity();
		boolean redirect = identity.isGoogleAccount();
	
		logEntryRepository.addEntry("LOGOUT: " + identity.getUsername());
		
		identityHolder.destroyIdentity();
		
		if (redirect) {
			response.sendRedirect(UserServiceFactory.getUserService().createLogoutURL("/adengine/security/logoutGoogle"));
		}		
		
		return new ModelAndView("jsonView", "logout", true);
	}


	/**
	 * Returns the Identity if a user is already authenticated (e.g. in case of reload)
	 * or if a Google account context exists.
	 * 
	 * @return
	 * @throws UserDisabledException 
	 */
	private ModelAndView getIdentity() throws UserDisabledException {
		
		//check if session is started and if the user is already authenticated
		
		if ((identityHolder != null)&&(identityHolder.hasIdentity())) {
			return new ModelAndView("jsonView", "identity", identityHolder.getIdentity());
		}
		
		com.google.appengine.api.users.User googleUser = UserServiceFactory.getUserService().getCurrentUser();
		if (googleUser != null) {
			String userId = googleUser.getEmail();
			
			
			//check if a valid Google apps account and set the identity if true
			User u = userRepository.findByKey(userId.toLowerCase());
			if(u != null){ 
				logEntryRepository.addEntry("LOGIN (google sso): " + userId );
				identityHolder.setIdentity(u);
				return new ModelAndView("jsonView", "identity", u);
			} else if(UserServiceFactory.getUserService().isUserAdmin()){
				//this is a domain admin that needs to be added to the database
				logEntryRepository.addEntry("NEW USER for Account Admin: " + userId );
				User newUser = new User(userId, null ,true);
					 newUser.setRole(Role.ROLE_ADMIN);
					 newUser.setEnabled(true);
					 userRepository.save(newUser);

					logEntryRepository.addEntry("LOGIN (google sso): " + userId );
					identityHolder.setIdentity(u);
					return new ModelAndView("jsonView", "identity", u);					 
			} else {
				LOG.error("FAILED LOGIN (google sso): " + userId + " not found.");
				logEntryRepository.addEntry("FAILED LOGIN (google sso): " + userId + " not found.");
			}
			
		} else {
			LOG.debug("no google account present");
		}
		

		return new ModelAndView("jsonView", "identity", null);
		

	}


	/**
	 *  
	 * Local user authentication. Not for google accounts!
	 * 
	 * @param username the username as identidier
	 * @param password a SHA256 hashed password
	 * @return
	 * @throws Exception
	 */
	private ModelAndView authenticate(String username, String password) throws Exception {

		User user = userRepository.findByKey(username);

		//check if user exists
		if ((user != null) && (user.isGoogleAccount())) {
			
			throw new ControllerException("Google account, please press the button on the left.");
			
		}
		
		if ((user == null)||(!user.comparePassword(password))) {
		
			logEntryRepository.addEntry("FAILED LOGIN: " + username, true);
			throw new ControllerException("Username or password do not match!");
		
		}
		
		if (!user.isEnabled()) {
			throw new ControllerException("User has been diabled, please contact the admin.");
		}
		

		
		identityHolder.setIdentity(user);

		logEntryRepository.addEntry("LOGIN: " + user.getUsername());

		
		
		return new ModelAndView("jsonView", "identity", user);
	}

	
	
	

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	
	public void setIdentityHolder(IdentityHolder identityHolder) {
		this.identityHolder = identityHolder;
	}


	public void setLogEntryRepository(LogEntryRepository logEntryRepository) {
		this.logEntryRepository = logEntryRepository;
	}

}