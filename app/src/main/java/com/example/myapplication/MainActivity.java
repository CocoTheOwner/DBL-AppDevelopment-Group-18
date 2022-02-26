package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.*;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, resetPassword;
    private EditText inputEmail, inputPassword;
    private Button loginButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        register = findViewById(R.id.login_register);
        register.setOnClickListener(this);

        inputEmail = findViewById(R.id.login_mail);
        inputPassword = findViewById(R.id.login_password);

        loginButton = findViewById(R.id.login_login);
        loginButton.setOnClickListener(this);

        progressBar = findViewById(R.id.login_progress);

        resetPassword = findViewById(R.id.login_forgot);
        resetPassword.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_register:
                startActivity(new Intent(this, RegisterUser.class));
                break;
            case R.id.login_login:
                userLogin();
                break;
            case R.id.login_forgot:
                startActivity(new Intent(this, ResetPassword.class));
                break;
        }

    }

    private void userLogin() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString();

        if (email.isEmpty()) {
            inputEmail.setError("Missing email!");
            inputEmail.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Invalid email!");
            inputEmail.requestFocus();
            return;
        } else if (password.isEmpty()) {
            inputPassword.setError("Missing password!");
            inputPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Ensure account is verified
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user.isEmailVerified()) {
                                // redirect to landing page
                                startActivity(new Intent(MainActivity.this, LandingPage.class));
                            } else {
                                // TODO: Allow user to re-send verification email.
                                Toast.makeText(MainActivity.this,
                                        "Email has not yet been verified",
                                        Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Failed to login, check your credentials or try again later.",
                                    Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}