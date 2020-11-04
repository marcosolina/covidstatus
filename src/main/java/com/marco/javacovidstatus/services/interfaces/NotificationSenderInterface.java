package com.marco.javacovidstatus.services.interfaces;

public interface NotificationSenderInterface {

    public void sendMessage(String to, String title, String message);
}
