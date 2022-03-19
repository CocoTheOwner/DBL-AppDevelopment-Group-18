package com.example.myapplication.homepage;

import com.example.myapplication.Question;

import java.util.List;

public class Category {
    private String name;
    private List<Question> posts;

    Category (String name, List<Question> posts) {
        this.name = name;
        this.posts = posts;
    }

    public String getName() {
        return name;
    }

    public List<Question> getPosts() {
        return posts;
    }
}
