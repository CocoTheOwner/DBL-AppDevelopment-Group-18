package com.example.myapplication.login;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import com.example.myapplication.R;
import com.example.myapplication.UserDatabaseRecord;
import com.example.myapplication.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername, editTextPassword, editTextEmail;
    private ProgressBar progressBar;
    private Spinner programDropdown;

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

        // Let the user select their program to add to user information
        // --> can also be moved elsewhere ((edit) profile activity)
        programDropdown = findViewById(R.id.register_program);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.programs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        programDropdown.setAdapter(adapter);

        progressBar = findViewById(R.id.register_progress);
        editTextUsername = findViewById(R.id.register_username);
        editTextPassword = findViewById(R.id.register_password);
        editTextEmail = findViewById(R.id.register_mail);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_banner:
                startActivity(new Intent(this, LoginPage.class));
                break;
            case R.id.login_login:
                registerUser();
                break;
        }
    }

    /**
     * First checks if provided credentials adhere to the requirements, then attempts to
     * create a Firebase user with these credentials.
     * TODO: Move this to user?
     */
    private void registerUser() {
        // using trim() to remove any trailing spaces.
        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        // Password is not trimmed, just in case spaces are used in the password.
        String password = editTextPassword.getText().toString();
        // Program - either Unspecified or TU/e program.
        String program = programDropdown.getSelectedItem().toString();

        // If the credentials conform to requirements, we attempt to create an account.
        if (checkCredentials(email, username, password, editTextUsername, editTextEmail, editTextPassword)) {
            createUser(email, username, password, program);
        }
    }

    /**
     * Creates a FIrebase user with provided credentials and stores a User object in the realtime
     * database
     * @param email provided email address
     * @param username provided username
     * @param password provided password
     * @param program provided program (major)
     * TODO: Move this to user?
     */
    private void createUser(String email, String username, String password, String program) {
        // Show the user that the registration is being processed.
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        UserType type;

                        if (task.getResult().getAdditionalUserInfo() == null ||
                                task.getResult().getAdditionalUserInfo().getProfile() == null) {
                            type = UserType.USER;
                        } else {
                            type = (UserType) task.getResult().getAdditionalUserInfo().getProfile().getOrDefault("UserType", UserType.USER);
                        }

                        // Registration was successful.
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {

                            // You don't want to know how long I spent getting the database to work
                            // here, and all I needed was this stupid link.

                            // What we do here:
                            //      (1) Using getInstance(<db link>) we get a reference to the database we are using
                            //      (2) We then go to the Users "table"
                            //      (3) We locate the current user (which does not exist yet)
                            //      (4) We set as value the newly created User object.
                            //      (5) OnCompleteListener to signal success/failure
                            FirebaseFirestore
                                    .getInstance()
                                    .collection("users")
                                    .document(task.getResult().getUser().getUid())
                                    .set(new UserDatabaseRecord(email, username, type, program))
                            .addOnSuccessListener(doc -> {
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
                                        new Intent(RegisterUser.this, LoginPage.class));

                                // Failed to store User object in the database.
                            }).addOnFailureListener(e -> {
                                Toast.makeText(RegisterUser.this,
                                        "Failed to register! Failed to access " +
                                                "database instance.",
                                        Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
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

    /**
     * Checks whether the provided credentials adhere to the requirements
     * @param email provided email address
     * @param username provided username
     * @param password provided password
     * @return boolean value whether requires adhere to the requirements
     * TODO: Move this to user?
     */
    public static boolean checkCredentials(String email, String username, String password,
                                           EditText editTextUsername, EditText editTextEmail, EditText editTextPassword) {
        // Username cannot be empty, possibly want a minimum length as well.
        if (username.isEmpty()) {
            if (editTextUsername != null) {
                editTextUsername.setError("Username is required!");
                editTextUsername.requestFocus();
            }
            return false;
        }

        // Provided email should be a valid email (also implies non-empty)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (editTextEmail != null) {
                editTextEmail.setError("Email is invalid!");
                editTextEmail.requestFocus();
            }
            return false;
        }

        // Additionally, the email should be tue domain.
        if (!(email.endsWith("@student.tue.nl") || email.endsWith("@tue.nl"))) {
            if (editTextEmail != null) {
                editTextEmail.setError("Please use your TU/e email");
                editTextEmail.requestFocus();
            }
            return false;
        }

        // Firebase requires passwords to have lengths of at least 6 characters.
        if (password.length() < 6) {
            if (editTextPassword != null) {
                editTextPassword.setError("Password should have length of at least 6!");
                editTextPassword.requestFocus();
            }
            return false;
        }
        return true;
    }
}