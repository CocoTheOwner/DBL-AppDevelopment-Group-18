package com.example.myapplication.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    /** Banner at the top of the screen. */
    private TextView banner;
    /** Input field for the user mail. */
    private EditText userMail;
    /** Button to send a password reset mail. */
    private Button resetButton;
    /** We use Firebase authentication. */
    private FirebaseAuth auth;

    /** Set up the UI. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // References to the EditText and Button.
        userMail = findViewById(R.id.reset_email);
        resetButton = findViewById(R.id.reset_reset);
        // Reference to the Firebase authentication system.
        auth = FirebaseAuth.getInstance();
        // Add OnCLickListener to the reset password button.
        resetButton.setOnClickListener(view -> resetPassword());

        // The banner redirects the user to the login activity.
        banner = findViewById(R.id.reset_banner);
        banner.setOnClickListener(view ->
                startActivity(new Intent(view.getContext(), LoginPage.class)));


    }

    /** Check if the provided email indeed is a valid email before
     * sending a password reset email if there exists a user with this
     * email address. */
    private void resetPassword() {
        String email = userMail.getText().toString().trim();

        if (email.isEmpty()) { // Email should not be empty
            userMail.setError("Email is required!");
            userMail.requestFocus(); // Move cursor to this EditText
            return; // As the email is invalid, we do not attempt a reset
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userMail.setError("Email is invalid!");
            userMail.requestFocus(); // Move cursor to this EditText
            return; // As the email is invalid, we do not attempt a reset
        }

        // The provided email is valid, so we attempt to send a reset email.
        // Firebase checks whether or not a user exists for this email address.
        // If so, a password reset email is sent.
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) { // There exists an account
                Toast.makeText(ResetPassword.this,
                        "Check your email to reset your password",
                        Toast.LENGTH_LONG).show();
            } else { // No account found
                Toast.makeText(ResetPassword.this,
                        "No account found for this email!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
