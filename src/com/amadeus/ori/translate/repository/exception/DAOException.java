package com.amadeus.ori.translate.repository.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DAOException extends RuntimeException
{
	private static final long serialVersionUID = -8852593974738250673L;
	private static final Log LOG = LogFactory.getLog(DAOException.class);

	public DAOException(String message)
	{
		super(message);
		LOG.error(message);
	}

	public DAOException(Throwable cause)
	{		
		super(cause);
		LOG.error(cause);
	}

	public DAOException(String message, Throwable cause)
	{
		super(message, cause);
		LOG.error(cause);
	}

}
