package org.scada_lts.dao.error;

import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;

public class ParseErrorResult {
	
	private boolean notErr = true;
	private String messageOrgin;
	private String message;
		
	public boolean getNotErr() {
		return notErr;
	}
	/**
	 * @param notErr the notErr to set
	 */
	public void setNotErr(boolean notErr) {
		this.notErr = notErr;
	}
	
	/**
	 * @param messageOrgin the messageOrgin to set
	 */
	public void setMessageOrgin(String messageOrgin) {
		this.messageOrgin = messageOrgin;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		if (notErr) {
			//return msgSource.getMessage(message,null, Locale.getDefault());
			return message;
		} else {
			return messageOrgin;
		}
	}
	
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
