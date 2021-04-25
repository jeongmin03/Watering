package com.example.watering;

public class User {
    private String personId;
    private String personPasswd;

    public User() {}

    public User(String personId, String personPasswd) {
        this.personId = personId;
        this.personPasswd = personPasswd;
    }

    public String getPersonId(){
        return personId;
    }
    public void setPersonId() { this.personId = personId; }
    public String getPersonPasswd(){
        return personPasswd;
    }
    public void setPersonPasswd() { this.personPasswd = personPasswd; }

}
