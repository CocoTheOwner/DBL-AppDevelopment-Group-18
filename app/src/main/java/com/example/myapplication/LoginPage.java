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


public class LoginPage extends AppCompatActivity implements View.OnClickListener {

    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Register button (clickable text)
        TextView register = findViewById(R.id.login_register);
        register.setOnClickListener(this);

        // Credential input fields (email address & password)
        inputEmail = findViewById(R.id.login_mail);
        inputPassword = findViewById(R.id.login_password);

        // Login button
        Button loginButton = findViewById(R.id.login_login);
        loginButton.setOnClickListener(this);

        // Progress bar
        progressBar = findViewById(R.id.login_progress);

        // Password reset button (as clickable text)
        TextView resetPassword = findViewById(R.id.login_forgot);
        resetPassword.setOnClickListener(this);

        // "Continue as guest" button (as clickable text)
        TextView contGuest = findViewById(R.id.login_guest);
        contGuest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_register:
                startActivity(new Intent(this, RegisterUser.class));
                break;
            case R.id.login_login:
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString();

                // If correct credentials are given, we attempt a login.
                if (confirmCredentials(email, password)) {
                    userLogin(email, password);
                }
                break;
            case R.id.login_forgot:
                startActivity(new Intent(this, ResetPassword.class));
                break;
            case R.id.login_guest:
                /* When using this, FirebaseAuth.getInstance().getCurrentUser() will return null.
                 This does require some additional checks for each activity, as it risks
                 nullpointer exceptions whenever using data of the current user. */
                startActivity(new Intent(this, LandingPage.class));
        }

    }

    /**
     * Attempt a login using {@param email} and {@param password} provided.
     * @param email provided email address
     * @param password provided password
     */
    private void userLogin(String email, String password) {
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
                                startActivity(new Intent(LoginPage.this, LandingPage.class));
                            } else {
                                // TODO: Allow user to re-send verification email.
                                Toast.makeText(LoginPage.this,
                                        "Email has not yet been verified",
                                        Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(LoginPage.this,
                                    "Failed to login, check your credentials or try again later.",
                                    Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Check if all credentials are given and conform to requireements
     * @param email provided email address
     * @param password provided password
     * @return boolean value whether credentials are okay.
     */
    private boolean confirmCredentials (String email, String password) {
        if (email.isEmpty()) {
            inputEmail.setError("Missing email!");
            inputEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Invalid email!");
            inputEmail.requestFocus();
            return false;
        } else if (password.isEmpty()) {
            inputPassword.setError("Missing password!");
            inputPassword.requestFocus();
            return false;
        }
        return true;
    }
}