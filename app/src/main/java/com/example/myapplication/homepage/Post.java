package com.example.myapplication.homepage;

import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class Post {

    private TagCollection tags;

    private String title;

    public Post(String title, List<String> tags) {
        this.title = title;
        this.tags = new TagCollection(tags);
    }

    public TagCollection getTags() {
        return tags;
    }

    public String getTitle() {
        return title;
    }

    public int getSearchQueryScore(String query) {
        return FuzzySearch.weightedRatio(query, title);
    }
}
