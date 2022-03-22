package com.example.myapplication.post;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.health.SystemHealthManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import com.example.myapplication.ContentDatabaseRecord;
import com.example.myapplication.PostDatabaseRecord;
import com.example.myapplication.QuestionDatabaseRecord;
import com.example.myapplication.R;
import com.example.myapplication.homepage.HomePageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateQuestionActivity extends AppCompatActivity {
    public static final String TITLE_TEXT = "com.example.myapplication.TITLE_TEXT";
    public static final String QUESTION_TEXT = "com.example.myapplication.QUESTION_TEXT";
    public static final String QUESTION_TIME = "com.example.myapplication.QUESTION_TIME";

    private FirebaseFirestore db;
    private Button submit;
    private Button exit;
    private FirebaseAuth auth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);



        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

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

        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        Date now = new Date();
        String questionTime = dtf.format(now);


        Intent title = new Intent(CreateQuestionActivity.this, QuestionViewActivity.class);
        //REPLACE THIS WITH FIREBASE...
        title.putExtra(TITLE_TEXT, questionTitle);
        title.putExtra(QUESTION_TEXT, questionText);
        title.putExtra(QUESTION_TIME, questionTime);

        createPost(questionTitle, questionText, now);



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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createPost(String title, String body, Date date) {

        String userId = auth.getCurrentUser().getUid();

        QuestionDatabaseRecord newQuestionRecord = new QuestionDatabaseRecord(
                new PostDatabaseRecord(userId,
                        new ContentDatabaseRecord(
                                new ArrayList<>(),
                                title, body
                        ), date), new ArrayList<>());
    }
}