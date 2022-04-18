package com.example.myapplication.databaseRecords;

import com.example.myapplication.UserType;

public class UserDatabaseRecord {

    public String email;
    public String userName;
    public UserType userType;
    public String program;

    public UserDatabaseRecord() {}

    public UserDatabaseRecord(String email, String userName, UserType userType, String program) {
        this.email = email;
        this.userName = userName;
        this.userType = userType;
        this.program = program;
    }
}
