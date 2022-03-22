package com.example.myapplication.post;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ContentDatabaseRecord;
import com.example.myapplication.PostDatabaseRecord;
import com.example.myapplication.QuestionDatabaseRecord;
import com.example.myapplication.R;
import com.example.myapplication.User;
import com.example.myapplication.UserSettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionViewActivity extends AppCompatActivity {
    private RecyclerView answerListView;
    private FirebaseFirestore db;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);
        answerListView = findViewById(R.id.answerView);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String documentId = intent.getStringExtra("documentId");

        db.collection("questions").document(documentId).get().addOnCompleteListener(task -> {
            handleQuestionData(task.getResult());
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleQuestionData(DocumentSnapshot document) {
        QuestionDatabaseRecord record = document.toObject(QuestionDatabaseRecord.class);

        TextView titleView = findViewById(R.id.CreateTitle);
        TextView questionView = findViewById(R.id.QuestText);
        TextView timeView = findViewById(R.id.QuestTime);
        TextView userView = findViewById(R.id.QuestUser);


        titleView.setText(record.post.content.title);
        questionView.setText(record.post.content.body);

        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");

        timeView.setText(dtf.format(record.post.creationDate));


        DatabaseReference userDataReference = FirebaseDatabase.getInstance("https://test-a19ba-default-rtdb.europe-west1.firebasedatabase.app/").getReference("/Users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();

        userDataReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userProfile = dataSnapshot.getValue(User.class);

                if (userProfile != null) {
                    String username = userProfile.getUserName();
                    userView.setText("By: " + username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),
                        "Something went wrong!",
                        Toast.LENGTH_LONG).show();
            }
        });
//
//        db.collection("questions")
//                .document(document.getId())
//                .collection("respones").add(new PostDatabaseRecord(userID,
//                    new ContentDatabaseRecord(new ArrayList<>(), "Manke", "Stanke"), new Date()));

        setAdapter(document);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setAdapter(DocumentSnapshot document) {
//
//        db.collection("questions")
//                .document(document.getId())
//                .collection("respones").get().addOnCompleteListener(task -> {
//                    for (QueryDocumentSnapshot doc : task.getResult()) {
//                        System.out.println(doc.toObject(PostDatabaseRecord.class).content.title);
//                        System.out.println(doc.toObject(PostDatabaseRecord.class).content.body);
//                        System.out.println(doc.toObject(PostDatabaseRecord.class).authorId);
//                    }
//
//                    List<PostDatabaseRecord> respones = task.getResult()
//                            .getDocuments()
//                            .stream()
//                            .map(doc -> doc.toObject(PostDatabaseRecord.class))
//                            .collect(Collectors.toList());
//
//            AnswersRecyclerAdapter adapter = new AnswersRecyclerAdapter(respones);
//
//            answerListView.setAdapter(adapter);
//        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        answerListView.setLayoutManager(layoutManager);
        answerListView.setItemAnimator(new DefaultItemAnimator());
    }

//    private void setUserInfo() {
//        usersList.add(new User("Marnick", "Computer Science", User.getNewUserID(), User.UserType.USER, "", ""));
//        usersList.add(new User("Fleur", "Computer Science", User.getNewUserID(), User.UserType.USER, "", ""));
//        usersList.add(new User("Sjoerd", "Computer Science", User.getNewUserID(), User.UserType.USER, "", ""));
//        usersList.add(new User("Rob", "Computer Science", User.getNewUserID(), User.UserType.USER, "", ""));
//        usersList.add(new User("Robie", "Computer Science", User.getNewUserID(), User.UserType.USER, "", ""));
//        usersList.add(new User("Rafael", "Computer Science", User.getNewUserID(), User.UserType.USER, "", ""));
//
//    }
}