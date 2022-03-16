package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

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
        usersList.add(new User("Marnick"));
        usersList.add(new User("Fleur"));
        usersList.add(new User("Sjoerd"));
        usersList.add(new User("Rob"));
        usersList.add(new User("Robie"));
        usersList.add(new User("Rafael"));

    }
}