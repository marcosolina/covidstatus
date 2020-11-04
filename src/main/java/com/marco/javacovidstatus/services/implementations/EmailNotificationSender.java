package com.marco.javacovidstatus.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.marco.javacovidstatus.services.interfaces.NotificationSenderInterface;

public class EmailNotificationSender implements NotificationSenderInterface {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendMessage(String to, String title, String message) {
        SimpleMailMessage email = new SimpleMailMessage(); 
        email.setFrom("82iknow@gmail.com");
        email.setTo(to); 
        email.setSubject(title); 
        email.setText(message);
        emailSender.send(email);
    }

}
