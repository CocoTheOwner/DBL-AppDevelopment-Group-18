package com.example.myapplication.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private TextView banner;
    private EditText userMail;
    private Button resetButton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        userMail = findViewById(R.id.reset_email);
        resetButton = findViewById(R.id.reset_reset);
        auth = FirebaseAuth.getInstance();
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        banner = findViewById(R.id.reset_banner);
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), LoginPage.class));
            }
        });


    }

    private void resetPassword() {
        String email = userMail.getText().toString().trim();

        // TODO: decompose
        if (email.isEmpty()) {
            userMail.setError("Email is required!");
            userMail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userMail.setError("Email is invalid!");
            userMail.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // TODO: ensure password fits requirements?
                    Toast.makeText(ResetPassword.this,
                            "Check your email to reset your password",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ResetPassword.this,
                            "No account found for this email!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}