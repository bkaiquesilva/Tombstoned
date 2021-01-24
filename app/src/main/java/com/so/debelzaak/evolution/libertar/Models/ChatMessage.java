package com.so.debelzaak.evolution.libertar.Models;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String profileUrl;
    private String ejin;
    private String eout;
    private String messageTime;
    private String uidee;

    public ChatMessage() {

    }

    public ChatMessage(String messageText, String messageUser, String ejin, String profileUrl, String eout, String uidee, String messageTime) {
        this.messageText = messageText;
        this.profileUrl = profileUrl;
        this.messageUser = messageUser;
        this.ejin = ejin;
        this.eout = eout;
        this.uidee = uidee;
        // Initialize to current time
        this.messageTime = messageTime;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getEjin() {
        return ejin;
    }

    public void setEjin(String ejin) {
        this.ejin = ejin;
    }

    public String getEout() {
        return eout;
    }

    public void setEout(String eout) {
        this.eout = eout;
    }

    public String getUidee() {
        return uidee;
    }

    public void setUidee(String uidee) {
        this.uidee = uidee;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }
}
