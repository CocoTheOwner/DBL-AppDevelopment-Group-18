package com.example.myapplication.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.databaseRecords.UserDatabaseRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        findViewById(R.id.signOutButton).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginPage.class));
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // TODO: Handle failure
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.getUid())
                .get().addOnSuccessListener(doc -> {
            ((TextView) findViewById(R.id.userName))
                    .setText("Username: " + doc.toObject(UserDatabaseRecord.class).userName);
        });
    }
}