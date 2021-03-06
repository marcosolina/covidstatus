package com.marco.javacovidstatus.services.interfaces;

/**
 * This interface defines what type of notification you can perform
 * 
 * @author Marco
 *
 */
public interface NotificationSenderInterface {

	/**
	 * It sends the email message
	 * 
	 * @param to
	 * @param title
	 * @param message
	 */
	public void sendEmailMessage(String to, String title, String message);
}
