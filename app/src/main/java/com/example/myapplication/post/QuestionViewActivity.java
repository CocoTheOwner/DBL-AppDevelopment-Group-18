package com.example.myapplication.post;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.ContentDatabaseRecord;
import com.example.myapplication.PostDatabaseRecord;
import com.example.myapplication.QuestionDatabaseRecord;
import com.example.myapplication.R;
import com.example.myapplication.Response;
import com.example.myapplication.UserDatabaseRecord;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(userID)
                .get().addOnSuccessListener(doc -> {
                    userView.setText("By: " + doc.toObject(UserDatabaseRecord.class).userName);
        });

        db.collection("questions")
                .document(document.getId())
                .collection("respones").add(new PostDatabaseRecord(userID,
                    new ContentDatabaseRecord(new ArrayList<>(), "Manke", "Stanke"), new Date()));

        db.collection("questions")
                .document(document.getId())
                .collection("respones").add(new PostDatabaseRecord(userID,
                new ContentDatabaseRecord(new ArrayList<>(), "Manke2", "Stanke2Electricboogaloo"), new Date()));

        setAdapter(document);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setAdapter(DocumentSnapshot document) {

        db.collection("questions")
                .document(document.getId())
                .collection("respones").get().addOnSuccessListener(responsesDoc -> {
                    handleResponses(responsesDoc);


        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        answerListView.setLayoutManager(layoutManager);
        answerListView.setItemAnimator(new DefaultItemAnimator());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleResponses(QuerySnapshot responsesDoc) {
        List<Response> responses = new ArrayList<>();

        List<Task<DocumentSnapshot>> userQueries = responsesDoc
                .getDocuments()
                .stream()
                .map(responseDoc -> {
                    PostDatabaseRecord record = responseDoc.toObject(PostDatabaseRecord.class);


                    Task<DocumentSnapshot> userQuery = db.collection("users")
                            .document(record.authorId)
                            .get();



                    userQuery.addOnSuccessListener(userDoc ->
                            responses
                                    .add(Response.fromDatabaseRecord(responseDoc.getId(),
                                            record,
                                            userDoc.toObject(UserDatabaseRecord.class))));

                    return userQuery;
                }).collect(Collectors.toList());

        Tasks.whenAllSuccess(userQueries).addOnSuccessListener(x -> {
            AnswersRecyclerAdapter adapter = new AnswersRecyclerAdapter(responses);
            answerListView.setAdapter(adapter);
        });
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