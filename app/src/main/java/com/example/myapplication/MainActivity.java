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
                String questionTitle = QuestionTitle.getText().toString();

                Intent intent = new Intent(MainActivity.this, QuestionView.class);
                intent.putExtra(TITLE_TEXT, questionTitle);
                startActivity(intent);
            }
        });

    }
}