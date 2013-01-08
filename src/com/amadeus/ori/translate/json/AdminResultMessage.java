package com.amadeus.ori.translate.json;

public class AdminResultMessage {

	private String message;
	private String errormsg;
	private int offset;
	
	public AdminResultMessage(String errormsg) {
		this.errormsg = errormsg;	
	}
	
	public AdminResultMessage() { 
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the errormsg
	 */
	public String getErrormsg() {
		return errormsg;
	}
	/**
	 * @param errormsg the errormsg to set
	 */
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}
	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

}
