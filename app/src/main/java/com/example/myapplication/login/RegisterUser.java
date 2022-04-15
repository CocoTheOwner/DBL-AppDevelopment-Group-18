package com.example.myapplication.login;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.UserDatabaseRecord;
import com.example.myapplication.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterUser extends AppCompatActivity
        implements View.OnClickListener {

    /** Inputfield for the user's username. */
    private EditText editTextUsername;
    /** Inputfield for the user's password. */
    private EditText editTextPassword;
    /** Inputfield for the user's email. */
    private EditText editTextEmail;
    /** Progressbar to show the app is busy when registering. */
    private ProgressBar progressBar;
    /** Spinner for program drop-down menu. */
    private Spinner programDropdown;
    /** We use Firebase authentication. */
    private FirebaseAuth mAuth;
    /** Minimal password length of Firebase. */
    static final int MIN_PASSWORD_LENGTH = 6;

    /** Set up the UI for the register activity. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        // ---- end of default code -----

        mAuth = FirebaseAuth.getInstance();

        // Button for registering after providing credentials
        Button registerButton = findViewById(R.id.register_register);
        registerButton.setOnClickListener(this);

        TextView banner = findViewById(R.id.register_banner);
        // We allow the user to return to the login page via the banner.
        banner.setOnClickListener(this);

        // Let the user select their program to add to user information
        // --> can also be moved elsewhere ((edit) profile activity)
        programDropdown = findViewById(R.id.register_program);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.programs,
                android.R.layout.simple_spinner_item);

        // Style of the drop-down menu
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        programDropdown.setAdapter(adapter);

        progressBar = findViewById(R.id.register_progress);
        editTextUsername = findViewById(R.id.register_username);
        editTextPassword = findViewById(R.id.register_password);
        editTextEmail = findViewById(R.id.register_mail);

    }

    /** Define behavior of clickable elements. */
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            // When the banner is clicked, the user is redirected to the login
            // activity.
            case R.id.register_banner:
                startActivity(new Intent(this, LoginPage.class));
                break;
            case R.id.register_register:
                registerUser(); // Checks credentials before registering
                break;
            default: // This should never occur.
                Toast.makeText(RegisterUser.this,
                        "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * First checks if provided credentials adhere to the requirements,
     * then attempts to create a Firebase user with these credentials.
     */
    private void registerUser() {
        // using trim() to remove any trailing spaces.
        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        // Password is not trimmed, just in case spaces
        //  are used in the password.
        String password = editTextPassword.getText().toString();
        // Program - either Unspecified or TU/e program.
        String program = programDropdown.getSelectedItem().toString();

        // If the credentials conform to requirements,
        // we attempt to create an account.
        // editTexts provided to allow for unit testing of the method.
        if (checkCredentials(email, username, password,
                editTextUsername, editTextEmail, editTextPassword)) {
            createUser(email, username, password, program);
        }
    }

    /**
     * Creates a Firebase user with provided credentials.
     * Stores a UserDatabaseRecord object in the FireStore database.
     * @param email provided email address
     * @param username provided username
     * @param password provided password
     * @param program provided program (major)
     */
    private void createUser(final String email, final String username,
                            final String password, final String program) {
        // Show the user that the registration is being processed.
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        UserType type;

                        if (task.getResult().getAdditionalUserInfo() == null
                                || task.getResult().getAdditionalUserInfo()
                                        .getProfile() == null) {
                            type = UserType.USER;
                        } else {
                            type = (UserType) task.getResult()
                                    .getAdditionalUserInfo().getProfile()
                                    .getOrDefault("UserType", UserType.USER);
                        }

                        // Registration was successful.
                        if (task.isSuccessful()
                                && task.getResult() != null
                                && task.getResult().getUser() != null) {


                            // What we do here:
                            //      (1) Using getInstance() we get a reference
                            //              to the database we are using
                            //      (2) We then go to the Users "table"
                            //      (3) We locate the current user
                            //              (which does not exist yet)
                            //      (4) We set as value the newly created
                            //              UserDataBaseRecord object
                            //      (5) OnCompleteListener to signal
                            //              success/failure to the user
                            FirebaseFirestore
                                    .getInstance()
                                    .collection("users")
                                    .document(task.getResult()
                                            .getUser().getUid())
                                    .set(new UserDatabaseRecord(email, username,
                                            type, program))
                            .addOnSuccessListener(doc -> {
                                Toast.makeText(RegisterUser.this,
                                        "Successfully registered",
                                        Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);

                                // Send the user a verification email.
                                FirebaseUser user = FirebaseAuth.getInstance()
                                        .getCurrentUser();
                                user.sendEmailVerification();

                                // Redirect the user to login layout
                                startActivity(
                                        new Intent(RegisterUser.this,
                                                LoginPage.class));

                                // Failed to store User object in the database.
                            }).addOnFailureListener(e -> {
                                Toast.makeText(RegisterUser.this,
                                        "Failed to access database instance!",
                                        Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            });

                            // Registration was not successful, happens when:
                            //  e.g., an account already exists.
                        } else {
                            Toast.makeText(RegisterUser.this,
                                    "Failed to register! There already "
                                            + "exists an account for the "
                                            + "provided email address.",
                                    Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    /**
     * Checks whether the provided credentials adhere to the requirements.
     * @param email provided email address
     * @param username provided username
     * @param password provided password
     * @param editTextUsername the EditText for the username (Unit testing)
     * @param editTextEmail the EditText for the email (Unit testing)
     * @param editTextPassword the EditText for the password (Unit testing)
     * @return boolean value whether requires adhere to the requirements
     */
    public static boolean checkCredentials(final String email,
                                           final String username,
                                           final String password,
                                           final EditText editTextUsername,
                                           final EditText editTextEmail,
                                           final EditText editTextPassword) {

        // Username cannot be empty, possibly want a minimum length as well.
        if (username.isEmpty()) {
            if (editTextUsername != null) {
                editTextUsername.setError("Username is required!");
                editTextUsername.requestFocus(); // Set cursor to the textfield
            }
            return false; // Credentials do not adhere to requirements
        }

        // Provided email should be a valid email (also implies non-empty)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (editTextEmail != null) {
                editTextEmail.setError("Email is invalid!");
                editTextEmail.requestFocus(); // Set cursor to the textfield
            }
            return false; // Credentials do not adhere to requirements
        }

        // Additionally, the email should be tue domain.
        if (!(email.endsWith("tue.nl"))) {
            if (editTextEmail != null) {
                editTextEmail.setError("Please use your TU/e email");
                editTextEmail.requestFocus(); // Set cursor to the textfield
            }
            return false; // Credentials do not adhere to requirements
        }

        // Firebase requires passwords to have lengths of at least 6 characters.
        if (password.length() < MIN_PASSWORD_LENGTH) {
            if (editTextPassword != null) {
                editTextPassword.setError("Password should have length of at "
                        + "least " + MIN_PASSWORD_LENGTH);
                editTextPassword.requestFocus(); // Set cursor to the textfield
            }
            return false; // Credentials do not adhere to requirements
        }
        return true; // Credentials adhere to requirements
    }
}
