package com.amadeus.ori.translate.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ControllerException extends Exception {

	private static final Log LOG = LogFactory.getLog(ControllerException.class);
	
	public ControllerException(String message) {
		super(message);
		LOG.error(message);
	}

	private static final long serialVersionUID = 1L;

}
