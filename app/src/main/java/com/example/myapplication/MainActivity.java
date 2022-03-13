package com.example.myapplication;

import static android.content.Intent.EXTRA_TEXT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String TITLE_TEXT = "com.example.myapplication.TITLE_TEXT";
    public static final String QUESTION_TEXT = "com.example.myapplication.QUESTION_TEXT";


    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submit=findViewById(R.id.submit_question);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText QuestionTitle = (EditText) findViewById(R.id.question_title);
                EditText QuestionText = (EditText) findViewById(R.id.question_text);

                String questionTitle = QuestionTitle.getText().toString();
                String questionText = QuestionText.getText().toString();


                Intent title = new Intent(MainActivity.this, QuestionView.class);
                title.putExtra(TITLE_TEXT, questionTitle);
                startActivity(title);

                //Intent text = new Intent(MainActivity.this, QuestionView.class);
                //text.putExtra(QUESTION_TEXT, questionText);
                //startActivity(text);
            }
        });

    }
}