package com.sib4u.spymessenger;

import com.google.firebase.Timestamp;

import java.io.Serializable;


public class ChatListModel implements Serializable {
    private String from;
    private String userId;
    private String lastMessage;
    private Timestamp timestamp;

    public ChatListModel() {
    }

    public ChatListModel(String from, String userId, String lastMessage, Timestamp timestamp) {
        this.from = from;
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
