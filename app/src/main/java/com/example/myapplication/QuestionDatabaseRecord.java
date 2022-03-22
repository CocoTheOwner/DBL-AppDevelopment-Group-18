package com.example.myapplication;

import java.util.List;

public class QuestionDatabaseRecord {

    public PostDatabaseRecord post;

    public List<String> tags;

    public QuestionDatabaseRecord() {}

    public QuestionDatabaseRecord(PostDatabaseRecord post, List<String> tags) {
            this.post = post;
            this.tags = tags;
    }
}
