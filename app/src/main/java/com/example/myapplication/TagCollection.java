package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TagCollection {
    private List<String> tags;

    public TagCollection() {
        tags = new ArrayList<>();
    }

    public TagCollection(List<String> tags) {
        this.tags = tags;
    }

    public void addTag(String tag) {
        String trimmed = trimTag(tag);

        if (!this.containsTrimmedTag(trimmed)) {
            this.tags.add(trimmed);
        }
    }

    private boolean containsTrimmedTag(String tag) {

        String lower = tag.toLowerCase(Locale.ROOT);

        for (String i : tags) {
            if (i.toLowerCase(Locale.ROOT).equals(lower)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsTag(String tag) {
        return this.containsTrimmedTag(trimTag(tag));
    }

    public static String trimTag(String tag) {
        return tag.replaceAll("[#\\s]", "");
    }

    public List<String> getList() {
        return this.tags;
    }

    public void removeTagAt (int position) {
        this.tags.remove(position);
    }
}
