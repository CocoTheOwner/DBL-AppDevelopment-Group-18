package com.example.myapplication;

import java.io.File;
import java.util.List;

public class ContentDatabaseRecord {
    public List<File> attachments;

    public String title;

    public String body;

    public ContentDatabaseRecord() {}

    public  ContentDatabaseRecord(List<File> attachments, String title, String body) {
        this.attachments = attachments;
        this.title = title;
        this.body = body;
    }
}
