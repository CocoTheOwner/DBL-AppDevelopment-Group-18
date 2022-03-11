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

        Intent intent = getIntent();
        String titleText = intent.getStringExtra(MainActivity.TITLE_TEXT);

        TextView titleView = (TextView) findViewById(R.id.CreateTitle);

        titleView.setText(titleText);
    }
}