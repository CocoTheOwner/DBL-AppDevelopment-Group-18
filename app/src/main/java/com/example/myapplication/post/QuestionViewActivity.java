package com.example.myapplication.post;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.User;

import java.util.ArrayList;

public class QuestionViewActivity extends AppCompatActivity {
    private ArrayList<User> usersList;
    private RecyclerView answerListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);
        answerListView = findViewById(R.id.answerView);
        usersList = new ArrayList<>();

        setUserInfo();
        setAdapter();

        Intent title = getIntent();
        String titleText = title.getStringExtra(CreateQuestionActivity.TITLE_TEXT);
        String questionText = title.getStringExtra(CreateQuestionActivity.QUESTION_TEXT);

        //Intent text = getIntent();
        //String questionText = text.getStringExtra(MainActivity.QUESTION_TEXT);



        TextView titleView = (TextView) findViewById(R.id.CreateTitle);
        TextView questionView = (TextView) findViewById(R.id.QuestText);

        titleView.setText(titleText);
        questionView.setText(questionText);
    }

    private void setAdapter() {
        AnswersRecyclerAdapter adapter = new AnswersRecyclerAdapter(usersList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        answerListView.setLayoutManager(layoutManager);
        answerListView.setItemAnimator(new DefaultItemAnimator());
        answerListView.setAdapter(adapter);
    }

    private void setUserInfo() {
        usersList.add(new User("Marnick", "Computer Science", User.getNewUserID(), User.UserType.USER, "", ""));
        usersList.add(new User("Fleur", "Computer Science", User.getNewUserID(), User.UserType.USER, "", ""));
        usersList.add(new User("Sjoerd", "Computer Science", User.getNewUserID(), User.UserType.USER, "", ""));
        usersList.add(new User("Rob", "Computer Science", User.getNewUserID(), User.UserType.USER, "", ""));
        usersList.add(new User("Robie", "Computer Science", User.getNewUserID(), User.UserType.USER, "", ""));
        usersList.add(new User("Rafael", "Computer Science", User.getNewUserID(), User.UserType.USER, "", ""));

    }
}