package com.marco.javacovidstatus.services.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;

/**
 * My implementation for the {@link NotificationSenderInterface}
 * 
 * @author Marco
 *
 */
public class EmailNotificationSender implements NotificationSenderInterface {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationSender.class);

    @Value("${covidstatus.notification.enabled}")
    private boolean notificationEnabled;
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendEmailMessage(String to, String title, String message) {
        if (!notificationEnabled) {
            logger.info("Notification not enabled");
            return;
        }
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(emailFrom);
        email.setTo(to);
        email.setSubject(title);
        email.setText(message);
        emailSender.send(email);
        logger.debug("Notification sent");
    }

}
