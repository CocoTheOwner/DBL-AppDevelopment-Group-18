package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomePageViewModel extends ViewModel {
    private MutableLiveData<List<String>> tags = new MutableLiveData<>(new ArrayList());
    private MutableLiveData<String> searchString = new MutableLiveData<>("");

    void setSearchString(String string) {
        searchString.setValue(string);
    }
    LiveData<String> getSearchString () {
        return searchString;
    }
}
