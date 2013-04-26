package com.amadeus.ori.translate.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.amadeus.ori.translate.domain.LogEntry;
import com.amadeus.ori.translate.repository.LogEntryRepository;


/**
 * 
 * @author tobias.rothe@amadeus.com
 */
public class LogEntryController implements Controller {

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

		
		List<LogEntry> logs = logEntryRepository.list();
		Collections.reverse(logs);
		return new ModelAndView("jsonView", "logEntry", logs );
	}

	public void setLogEntryRepository(LogEntryRepository logEntryRepository) {
		this.logEntryRepository = logEntryRepository;
	}


}
