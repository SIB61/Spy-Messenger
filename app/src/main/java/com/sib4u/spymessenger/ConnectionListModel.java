package com.sib4u.spymessenger;

import com.google.firebase.Timestamp;

public class ConnectionListModel {
    private String ID;
    private String type;
    private Timestamp timestamp;


    public ConnectionListModel(String ID, String type, Timestamp timestamp) {
        this.ID = ID;
        this.type = type;
        this.timestamp = timestamp;
    }

    public ConnectionListModel() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
