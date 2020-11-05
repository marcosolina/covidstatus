package com.marco.javacovidstatus.services.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;

public class EmailNotificationSender implements NotificationSenderInterface {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationSender.class);
    
    @Value("${covidstatus.notification.enabled}")
    private boolean notificationEnabled;
    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendMessage(String to, String title, String message) {
        if (!notificationEnabled) {
            logger.info("Notification not enabled");
            return;
        }
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("82iknow@gmail.com");
        email.setTo(to);
        email.setSubject(title);
        email.setText(message);
        emailSender.send(email);
    }

}