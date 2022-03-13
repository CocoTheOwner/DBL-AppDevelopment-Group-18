package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class QuestionView extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);

        Intent title = getIntent();
        String titleText = title.getStringExtra(MainActivity.TITLE_TEXT);

        //Intent text = getIntent();
        //String questionText = text.getStringExtra(MainActivity.QUESTION_TEXT);



        TextView titleView = (TextView) findViewById(R.id.CreateTitle);

        titleView.setText(titleText);
    }
}