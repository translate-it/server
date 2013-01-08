package com.amadeus.ori.translate.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.amadeus.ori.translate.repository.LogEntryRepository;


/**
 * 
 * @author tobias.rothe@amadeus.com
 */
public class LogEntryController implements Controller {

	private static final long serialVersionUID = 1L;

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


		return new ModelAndView("jsonView", "logEntry", logEntryRepository.list());
	

	}

	public void setLogEntryRepository(LogEntryRepository logEntryRepository) {
		this.logEntryRepository = logEntryRepository;
	}


}
