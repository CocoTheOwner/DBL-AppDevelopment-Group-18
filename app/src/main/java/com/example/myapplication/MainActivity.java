package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button courseButton = findViewById(R.id.course);

        courseButton.setOnClickListener((e) -> {
            startActivity(new Intent(this, PostListActivity.class));
        });
    }
}