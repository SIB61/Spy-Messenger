package com.sib4u.spymessenger;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class MessageModel implements Serializable {
    private String from;
    private String message;
    private Timestamp timestamp;

    public MessageModel() {

    }

    public MessageModel(String from, String message, Timestamp timestamp) {
        this.from = from;
        this.message = message;
        this.timestamp = timestamp;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
