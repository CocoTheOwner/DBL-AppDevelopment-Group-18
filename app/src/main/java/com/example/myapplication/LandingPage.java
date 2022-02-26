package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.google.firebase.auth.*;
import com.google.firebase.database.*;

public class LandingPage extends AppCompatActivity implements View.OnClickListener {

    private TextView logout, displayUser;

    // To get data from Firebase
    private DatabaseReference userDataReference;
    private FirebaseUser user;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);

        displayUser = findViewById(R.id.display_username);
        user = FirebaseAuth.getInstance().getCurrentUser();

        // TODO: make this link a project constant.
        userDataReference = FirebaseDatabase.getInstance("https://test-a19ba-default-rtdb.europe-west1.firebasedatabase.app/").getReference("/Users");
        userID = user.getUid();

        // This listener will be triggered once with the value of the data at the location.
        userDataReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userProfile = dataSnapshot.getValue(User.class);

                if (userProfile != null) {
                    String username = userProfile.username;
                    displayUser.setText("Currently signed in as " + username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LandingPage.this,
                        "Something went wrong!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(LandingPage.this, MainActivity.class));
        }
    }
}