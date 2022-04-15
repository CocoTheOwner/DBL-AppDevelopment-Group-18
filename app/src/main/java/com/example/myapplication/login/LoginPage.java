package com.example.myapplication.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.R;
import com.example.myapplication.homepage.FrontPageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity
        implements View.OnClickListener {
    /** Input fields for user email. */
    private EditText inputEmail;
    /** Input fields for user password. */
    private EditText inputPassword;
    /** Shows that the app is logging the user in. */
    private ProgressBar progressBar;
    /** We use Firebase authentication. */
    private FirebaseAuth mAuth;

    /** Set up of the login page.
     * If the user is already logged in we immediately redirect to the
     * homepage. Assigns OnClickListeners where needed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.myapplication.R.layout.activity_login_page);

        // Authentication instance
        mAuth = FirebaseAuth.getInstance();

//        if (mAuth.getCurrentUser() != null
//                && mAuth.getCurrentUser().isEmailVerified()) {
//            startActivity(
//                    new Intent(this,
//                    FrontPageActivity.class));
//        }

        // Register button (clickable text)
        TextView register = findViewById(
                com.example.myapplication.R.id.login_register);
        register.setOnClickListener(this);

        // Credential input fields (email address & password)
        inputEmail = findViewById(
                com.example.myapplication.R.id.login_mail);
        inputPassword = findViewById(
                com.example.myapplication.R.id.login_password);

        // Login button
        Button loginButton = findViewById(
                com.example.myapplication.R.id.login_login);
        loginButton.setOnClickListener(this);

        // Progress bar
        progressBar = findViewById(
                com.example.myapplication.R.id.login_progress);

        // Password reset button (as clickable text)
        TextView resetPassword = findViewById(
                com.example.myapplication.R.id.login_forgot);
        resetPassword.setOnClickListener(this);

        // "Continue as guest" button (as clickable text)
        TextView contGuest = findViewById(
                com.example.myapplication.R.id.login_guest);
        contGuest.setOnClickListener(this);
    }

    /** Handle the behavior of different clickable UI elements.
     * Behavior can be redirecting the user to a different activity,
     * or logging in the user, provided valid credentials
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case com.example.myapplication.R.id.login_register:
                startActivity(
                        new Intent(
                                this, RegisterUser.class));
                break;
            case com.example.myapplication.R.id.login_login:
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString();

                // If correct credentials are given, we attempt a login.
                if (confirmCredentials(email, password)) {
                    userLogin(email, password);
                }
                break;
            case com.example.myapplication.R.id.login_forgot:
                startActivity(new Intent(
                        this, ResetPassword.class));
                break;
            case R.id.login_guest:
                /* When using this, FirebaseAuth.getInstance().getCurrentUser()
                 will return null. This does require some additional checks
                 for each activity, as it risks nullpointer exceptions
                 whenever using data of the current user. */
                mAuth.signOut();
                startActivity(new Intent(
                        this, FrontPageActivity.class));
            default:
                Toast.makeText(LoginPage.this,
                        "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Attempt a login using {@param email} and {@param password} provided.
     * @param email provided email address
     * @param password provided password
     */
    private void userLogin(final String email, final String password) {
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Ensure account is verified
                            FirebaseUser user = FirebaseAuth.getInstance()
                                    .getCurrentUser();
                            if (user.isEmailVerified()) {
                                // redirect to landing page
                                startActivity(new Intent(
                                        LoginPage.this,
                                        FrontPageActivity.class));
                            } else {
                                Toast.makeText(LoginPage.this,
                                        "Email has not yet been verified",
                                        Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(LoginPage.this,
                                    "Failed to login, check your "
                                            + "credentials or try again later.",
                                    Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * Check if all credentials are given and conform to requirements.
     * @param email provided email address
     * @param password provided password
     * @return boolean value whether credentials are okay.
     */
    private boolean confirmCredentials(final String email,
                                       final String password) {
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
