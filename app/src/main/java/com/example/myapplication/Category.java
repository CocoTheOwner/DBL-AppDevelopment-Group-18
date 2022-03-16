package com.example.myapplication;

import java.util.List;

public class Category {
    private String name;
    private List<Post> posts;

    Category (String name, List<Post> posts) {
        this.name = name;
        this.posts = posts;
    }

    public String getName() {
        return name;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
