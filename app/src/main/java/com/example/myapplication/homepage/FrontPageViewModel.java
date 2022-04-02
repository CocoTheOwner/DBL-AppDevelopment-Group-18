package com.example.myapplication.homepage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.TagCollection;

public class FrontPageViewModel extends ViewModel {
    private MutableLiveData<TagCollection> tags = new MutableLiveData<>(new TagCollection());
    private MutableLiveData<String> searchString = new MutableLiveData<>("");

    private final TagCollection availableTags = new TagCollection();

    public FrontPageViewModel() {

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

}
