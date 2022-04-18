package com.example.myapplication.databaseRecords;

import java.util.List;

public class QuestionDatabaseRecord {

    public PostDatabaseRecord post;

    public List<String> tags;

    public String bestAnswer;

    public QuestionDatabaseRecord() {}

    public QuestionDatabaseRecord(PostDatabaseRecord post, List<String> tags, String bestAnswer) {
            this.post = post;
            this.tags = tags;
            this.bestAnswer = bestAnswer;
    }
}
