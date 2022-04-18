package com.example.myapplication.databaseRecords;

import java.util.Locale;

public class TagDatabaseRecord {
    public String lower;
    public String display;

    public TagDatabaseRecord() {}

    public TagDatabaseRecord(String name) {
        this.lower = name.toLowerCase(Locale.ROOT);
        this.display = name;
    }
}
