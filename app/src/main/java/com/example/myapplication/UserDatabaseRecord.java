package com.example.myapplication;

public class UserDatabaseRecord {

    public String email;
    public String userName;
    public User.UserType userType;
    public String program;

    public UserDatabaseRecord() {}

    public UserDatabaseRecord(String email, String userName, User.UserType userType, String program) {
        this.email = email;
        this.userName = userName;
        this.userType = userType;
        this.program = program;
    }
}
