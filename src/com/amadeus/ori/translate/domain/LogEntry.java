package com.amadeus.ori.translate.domain;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * Special events are logged using the LogEntry.
 * They can be displayed in the backend UI.
 * This is not to replace the GAE logging facilities.
 *
 */
@Entity
public class LogEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long key;

	@Index
	private Date date;

	private String message;
	
	private boolean important;

    public LogEntry() {
    	// default constructor for Objectify
	}
	
	public LogEntry(String message, boolean important) {
		super();
		this.date = new Date();
		this.message = message;
		this.important = important;
	}
	
	

	public LogEntry(String message) {
		super();
		this.date = new Date();
		this.important = false;
		this.message = message;
	}



	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isImportant() {
		return important;
	}

	public void setImportant(boolean important) {
		this.important = important;
	}

	public long getKey() {
		return key;
	}

	public Date getDate() {
		return date;
	}
}
