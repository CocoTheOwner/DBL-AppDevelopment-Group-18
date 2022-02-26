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

    private TextView banner;
    private EditText editTextUsername, editTextPassword, editTextEmail;
    private ProgressBar progressBar;
    private Button registerButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        registerButton = findViewById(R.id.login_login);
        registerButton.setOnClickListener(this);

        banner = findViewById(R.id.register_banner);
        banner.setOnClickListener(this);

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
        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty()) {
            editTextUsername.setError("Username is required!");
            editTextUsername.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // TODO: should be tue domain.
            editTextEmail.setError("Email is invalid!");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Password should have length of at least 6!");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            User user = new User(username, email);

                            // You don't want to know how long I spent getting the database to work
                            // here, and all I needed was this stupid link.
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

                                                // TODO: verification email and redirect.
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                user.sendEmailVerification();
                                                // TODO: Redirect to login layout
                                                startActivity(new Intent(RegisterUser.this, MainActivity.class));
                                            } else {
                                                Toast.makeText(RegisterUser.this,
                                                        "Failed to register! (2)",
                                                        Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterUser.this,
                                    "Failed to register! (1)",
                                    Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}