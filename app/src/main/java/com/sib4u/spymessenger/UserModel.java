package com.sib4u.spymessenger;

import java.io.Serializable;

public class UserModel implements Serializable {

    String userId, profilePic, name, status, education, job, location, lastSeen, publicKey;
    boolean visibility;

    public UserModel(String userId, String profilePic, String name, String status, String education, String job, String location, boolean visibility, String lastSeen, String publicKey) {
        this.userId = userId;
        this.profilePic = profilePic;
        this.name = name;
        this.status = status;
        this.education = education;
        this.job = job;
        this.location = location;
        this.visibility = visibility;
        this.lastSeen = lastSeen;
        this.publicKey = publicKey;
    }

    public UserModel() {
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "profilePic='" + profilePic + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", education='" + education + '\'' +
                ", job='" + job + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
