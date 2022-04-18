package com.example.myapplication.databaseRecords;

import java.io.File;
import java.util.List;

public class ContentDatabaseRecord {
    public String attachment;

    public String title;

    public String body;

    public ContentDatabaseRecord() {}

    public  ContentDatabaseRecord(String attachment, String title, String body) {
        this.attachment = attachment;
        this.title = title;
        this.body = body;
    }
}
