package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomePageViewModel extends ViewModel {
    private MutableLiveData<TagCollection> tags = new MutableLiveData<>(new TagCollection());
    private MutableLiveData<String> searchString = new MutableLiveData<>("");

    private final TagCollection availableTags = new TagCollection();

    private final List<Post> posts = Arrays.asList(
        new Post("What da dog doin?", Arrays.asList("ErikDeVink", "OffTopic")),
            new Post("Lost in metaforum", Arrays.asList("Location")),
            new Post("How to caclulus?", Arrays.asList("2WCB0", "Course")),
            new Post("How to analysis?", Arrays.asList("prokert", "anzats", "Course")),
            new Post("2IT90 2023 exam answers please",  Arrays.asList("2IT90", "BCS", "Course")),
            new Post("Who asked?",  Arrays.asList("OffTopic"))
    );

    public HomePageViewModel() {
        for (Post post : posts) {
            for (String tag : post.getTags().getList()) {
                this.availableTags.addTag(tag);
            }
        }

        System.out.println(this.availableTags.getList());
    }

    public void setSearchString(String string) {
        searchString.setValue(string);
    }

    public LiveData<String> getSearchString () {
        return searchString;
    }

    public void addTag(String tag) {
        tags.getValue().addTag(TagCollection.trimTag(tag));
        tags.setValue(tags.getValue());
    }

    public LiveData<TagCollection> getTags() {
        return tags;
    }

    public void removeTag(int position) {
        tags.getValue().removeTagAt(position);
        tags.setValue(tags.getValue());
    }

    public TagCollection getAvailableTags() {
        return availableTags;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
