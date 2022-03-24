package com.example.myapplication.post;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private RecyclerView QuestionListView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);
        QuestionListView = findViewById(R.id.QuestionViewRecycler);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String documentId = intent.getStringExtra("documentId");

        setupResponseButton(documentId);

        db.collection("questions").document(documentId).get().addOnCompleteListener(task -> {
            handleQuestionData(task.getResult());
            QuestionDeletionAndVote(task.getResult());
        });
    }

    private void setupResponseButton(String documentId) {
        ImageButton responseButton = findViewById(R.id.replyButton);
        EditText replyBox = findViewById(R.id.replyBox);

        if (auth.getCurrentUser() != null) {
            responseButton.setOnClickListener(v -> {
                db.collection("questions")
                        .document(documentId)
                        .collection("responses")
                        .add(new PostDatabaseRecord(auth.getCurrentUser().getUid(),
                                new ContentDatabaseRecord(new ArrayList<>(), "",
                                        replyBox.getText().toString()),
                                new Date()));
            });
        } else {
            responseButton.setVisibility(View.GONE);
            replyBox.setVisibility(View.GONE);
        }
    }

    private void QuestionDeletionAndVote(DocumentSnapshot document) {
        //Make delete and best answer buttons invisible for correct users.
        ImageButton deleteQButton = findViewById(R.id.QuestionDeleteButton);
        ImageButton QUpVoteButton = findViewById(R.id.QuestionUpVote);
        ImageButton QDownVoteButton = findViewById(R.id.QuestionDownVote);
        TextView QScoreText = findViewById(R.id.QuestionScore);

        if (auth.getCurrentUser() != null ) {
            //TODO
            // IMPLEMENT DELETION FOR IF OP AND OTHERWISE VOTING HERE
        } else {
            //If a gues delete illegal buttons and move the score down.
            deleteQButton.setVisibility(View.GONE);
            QUpVoteButton.setVisibility(View.GONE);
            QDownVoteButton.setVisibility(View.GONE);
            QScoreText.setTranslationY(50);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleQuestionData(DocumentSnapshot document) {
        QuestionDatabaseRecord record = document.toObject(QuestionDatabaseRecord.class);

        TextView titleView = findViewById(R.id.questionTitleView);
        TextView questionView = findViewById(R.id.QuestText);
        TextView timeView = findViewById(R.id.QuestTime);
        TextView userView = findViewById(R.id.QuestUser);


        titleView.setText(record.post.content.title);
        questionView.setText(record.post.content.body);

        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        timeView.setText("Posted on: " + dtf.format(record.post.creationDate));

        db.collection("users")
                .document(record.post.authorId)
                .get().addOnSuccessListener(doc -> {
                    userView.setText("By: " + doc.toObject(UserDatabaseRecord.class).userName);
        });

        setAdapter(document);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setAdapter(DocumentSnapshot document) {
        db.collection("questions")
                .document(document.getId())
                .collection("responses")
                .addSnapshotListener((responseSnapshot, e) -> {
                    handleResponses(responseSnapshot);
                });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        QuestionListView.setLayoutManager(layoutManager);
        QuestionListView.setItemAnimator(new DefaultItemAnimator());

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
            QuestionViewRecyclerAdapter adapter = new QuestionViewRecyclerAdapter(responses);
            QuestionListView.setAdapter(adapter);
        });
    }
}