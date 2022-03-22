package com.example.myapplication.homepage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.Content;
import com.example.myapplication.Question;
import com.example.myapplication.TagCollection;
import com.example.myapplication.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HomePageViewModel extends ViewModel {
    private MutableLiveData<TagCollection> tags = new MutableLiveData<>(new TagCollection());
    private MutableLiveData<String> searchString = new MutableLiveData<>("");

    private final TagCollection availableTags = new TagCollection();
    private final List<User> users = new ArrayList<>();

    private final List<Question> questions;

    public HomePageViewModel() {

        users.add(new User("Marnick", "Computer Science", User.getNewUserID(),
                User.UserType.USER, ""));
        users.add(new User("Fleur", "Computer Science",
                User.getNewUserID(), User.UserType.USER, ""));
        users.add(new User("Sjoerd", "Computer Science",
                User.getNewUserID(), User.UserType.USER, ""));
        users.add(new User("Rob", "Applied Mathematics",
                User.getNewUserID(), User.UserType.USER, ""));
        users.add(new User("Robie", "Computer Science",
                User.getNewUserID(), User.UserType.USER, ""));
        users.add(new User("Rafael", "Computer Science",
                User.getNewUserID(), User.UserType.USER, ""));

        questions = Arrays.asList(
                new Question("0",
                        users.get(0),
                        new Content(new ArrayList<>(), "Lost in metaforum", ""),
                        new Date(),
                        Arrays.asList("Location")),
                new Question("1",
                        users.get(1),
                        new Content(new ArrayList<>(), "What da dog doin?", ""),
                        new Date(),
                        Arrays.asList("ErikDeVink", "OffTopic")),
                new Question("2",
                        users.get(2),
                        new Content(new ArrayList<>(), "How to caclulus?", ""),
                        new Date(),
                        Arrays.asList("2WCB0", "Course")),
                new Question("3",
                        users.get(3),
                        new Content(new ArrayList<>(), "How to analysis?", ""),
                        new Date(),
                        Arrays.asList("prokert", "anzats", "Course")),
                new Question("4",
                        users.get(4),
                        new Content(new ArrayList<>(), "2IT90 2023 exam answers please", ""),
                        new Date(),
                        Arrays.asList("2IT90", "BCS", "Course")),
                new Question("5",
                        users.get(5),
                        new Content(new ArrayList<>(), "Who asked?", ""),
                        new Date(),
                        Arrays.asList("OffTopic"))
        );


        for (Question post : questions) {
            for (String tag : post.getTags().getList()) {
                this.availableTags.addTag(tag);
            }
        }
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

    public List<Question> getQuestions() {
        return questions;
    }
}
