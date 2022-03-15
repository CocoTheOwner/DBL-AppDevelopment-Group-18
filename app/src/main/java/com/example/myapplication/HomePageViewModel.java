package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomePageViewModel extends ViewModel {
    private MutableLiveData<List<String>> tags = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<String> searchString = new MutableLiveData<>("");

    private final List<String> availableTags = Arrays.asList(
            "erikdevink",
            "bcs",
            "2it90",
            "2is70",
            "prokert",
            "anzats");

    public void setSearchString(String string) {
        searchString.setValue(string);
    }

    public LiveData<String> getSearchString () {
        return searchString;
    }

    public void addTag(String tag) {
        if (! tags.getValue().contains(tag)) {
            tags.getValue().add(tag);
            tags.setValue(tags.getValue());
        }
    }

    public LiveData<List<String>> getTags() {
        return tags;
    }

    public void removeTag(int position) {
        tags.getValue().remove(position);
        tags.setValue(tags.getValue());
    }

    public List<String> getAvailableTags() {
        return availableTags;
    }
}
