package com.example.myapplication;

import java.util.Date;

public class PostDatabaseRecord {
    public String authorId;

    public ContentDatabaseRecord content;

    public Date creationDate;

    public PostDatabaseRecord() {}

    public PostDatabaseRecord(String authorId, ContentDatabaseRecord content, Date creationDate) {
        this.authorId = authorId;
        this.content = content;
        this.creationDate =  creationDate;
    }
}
