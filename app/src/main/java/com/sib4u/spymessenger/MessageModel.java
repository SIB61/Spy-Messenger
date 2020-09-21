package com.sib4u.spymessenger;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;

public class MessageModel extends AppCompatActivity {
    int type;
    private String message;
    private Timestamp timestamp;

    public MessageModel() {

    }

    public MessageModel(String message, Timestamp timestamp, int type) {
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", type=" + type +
                '}';
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
