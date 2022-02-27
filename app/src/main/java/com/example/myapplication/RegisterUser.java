package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername, editTextPassword, editTextEmail;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        // ---- end of default code -----

        mAuth = FirebaseAuth.getInstance();

        Button registerButton = findViewById(R.id.login_login);
        registerButton.setOnClickListener(this);

        TextView banner = findViewById(R.id.register_banner);
        banner.setOnClickListener(this); // Allows the user to return via the banner.

        progressBar = findViewById(R.id.register_progress);
        editTextUsername = findViewById(R.id.register_username);
        editTextPassword = findViewById(R.id.register_password);
        editTextEmail = findViewById(R.id.register_mail);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.login_login:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        // using trim() to remove any trailing spaces.
        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        // Password is not trimmed, just in case spaces are used in the password.
        String password = editTextPassword.getText().toString();

        // Username cannot be empty, possibly want a minimum length as well.
        if (username.isEmpty()) {
            editTextUsername.setError("Username is required!");
            editTextUsername.requestFocus();
            return;
        }

        // Provided email should be a valid email (also implies non-empty)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Email is invalid!");
            editTextEmail.requestFocus();
            return;
        }

        // Additionally, the email should be tue domain.
        // TODO: We could possibly create our own regex to combine the two checks into one.
        if (!(email.contains("@student.tue.nl") || email.contains("@tue.nl"))) {
            editTextEmail.setError("Please use your TU/e email");
            editTextEmail.requestFocus();
            return;
        }

        // Firebase requires passwords to have lengths of at least 6 characters.
        if (password.length() < 6) {
            editTextPassword.setError("Password should have length of at least 6!");
            editTextPassword.requestFocus();
            return;
        }

        // Show the user that the registration is being processed.
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Registration was successful.
                        if (task.isSuccessful()) {
                            User user = new User(username, email);

                            // You don't want to know how long I spent getting the database to work
                            // here, and all I needed was this stupid link.

                            // What we do here:
                            //      (1) Using getInstance(<db link>) we get a reference to the database we are using
                            //      (2) We then go to the Users "table"
                            //      (3) We locate the current user (which does not exist yet)
                            //      (4) We set as value the newly created User object.
                            //      (5) OnCompleteListener to signal success/failure
                            FirebaseDatabase
                                    .getInstance("https://test-a19ba-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .getReference("/Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterUser.this,
                                                        "Successfully registered",
                                                        Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);

                                                // Send the user a verification email.
                                                FirebaseUser user = FirebaseAuth.getInstance()
                                                        .getCurrentUser();
                                                user.sendEmailVerification();

                                                // Redirect to login layout
                                                startActivity(
                                                        new Intent(RegisterUser.this, MainActivity.class));

                                                // Failed to store User object in the database.
                                            } else {
                                                Toast.makeText(RegisterUser.this,
                                                        "Failed to register! Failed to access " +
                                                                "database instance.",
                                                        Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                            // Registration was not successful, happens when:
                            //  (1) An account already exists.
                        } else {
                            Toast.makeText(RegisterUser.this,
                                    "Failed to register! There already exists an account " +
                                            "for the provided email address.",
                                    Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}