package com.osu.unitrade.model;

public class User {

    public String nickname, emailAddress;

    public User() {
    }

    public User(String nickname, String emailAddress) {
        this.nickname = nickname;
        this.emailAddress = emailAddress;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
