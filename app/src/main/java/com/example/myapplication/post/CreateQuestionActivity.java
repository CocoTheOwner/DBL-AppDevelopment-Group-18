package com.example.myapplication.post;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import com.example.myapplication.R;
import com.example.myapplication.homepage.HomePageActivity;

import java.util.ArrayList;

public class CreateQuestionActivity extends AppCompatActivity {
    public static final String TITLE_TEXT = "com.example.myapplication.TITLE_TEXT";
    public static final String QUESTION_TEXT = "com.example.myapplication.QUESTION_TEXT";
    public static final String QUESTION_TIME = "com.example.myapplication.QUESTION_TIME";


    private Button submit;
    private Button exit;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        submit = findViewById(R.id.submit_question);
        exit = findViewById(R.id.cancel_button);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(CreateQuestionActivity.this, HomePageActivity.class);
                startActivity(home);
            }
        });

        submit.setOnClickListener(view -> submit());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void submit() {
        EditText QuestionTitle = (EditText) findViewById(R.id.question_title);
        EditText QuestionText = (EditText) findViewById(R.id.question_text);

        String questionTitle = QuestionTitle.getText().toString();
        String questionText = QuestionText.getText().toString();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String questionTime = dtf.format(now);


        Intent title = new Intent(CreateQuestionActivity.this, QuestionViewActivity.class);
        //REPLACE THIS WITH FIREBASE...
        title.putExtra(TITLE_TEXT, questionTitle);
        title.putExtra(QUESTION_TEXT, questionText);
        title.putExtra(QUESTION_TIME, questionTime);

        //TODO
        // new question entry {
        // add Title to database
        // add Quest body to database
        // add tags to database
        // add user ID of question poster
        // add image to base


        startActivity(title);



        //Intent text = new Intent(MainActivity.this, QuestionView.class);
        //text.putExtra(QUESTION_TEXT, questionText);
        //startActivity(text);
    }
}