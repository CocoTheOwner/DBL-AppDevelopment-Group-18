package com.example.myapplication.post;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.ContentDatabaseRecord;
import com.example.myapplication.PostDatabaseRecord;
import com.example.myapplication.Question;
import com.example.myapplication.QuestionDatabaseRecord;
import com.example.myapplication.R;
import com.example.myapplication.Response;
import com.example.myapplication.User;
import com.example.myapplication.UserDatabaseRecord;
import com.example.myapplication.VoteDatabaseRecord;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class QuestionViewActivity extends AppCompatActivity {
    private RecyclerView QuestionListView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialize the data from the database and start all methods to build the page.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);
        QuestionListView = findViewById(R.id.QuestionViewRecycler);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String documentId = intent.getStringExtra("documentId");

        setupResponseButton(documentId);
        fetchQuestionDate(documentId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchQuestionDate(String documentId) {
        db.collection("questions")
                .document(documentId)
                .get()
                .addOnSuccessListener(questionDoc -> {
                    QuestionDatabaseRecord record = questionDoc.toObject(QuestionDatabaseRecord.class);

                    db.collection("users")
                            .document(record.post.authorId)
                            .get()
                            .addOnSuccessListener(authDoc -> {
                                User author = User.fromDatabaseRecord(authDoc.getId(),
                                        authDoc.toObject(UserDatabaseRecord.class));

                                Question question = Question.fromDatabaseRecord(questionDoc.getId(),
                                        record, author);

                                fetchUserData(question);
                            });
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchUserData(Question question) {
        if (auth.getCurrentUser() != null) {
            db.collection("users")
                    .document(auth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(userDoc -> {
                       User user = User.fromDatabaseRecord(userDoc.getId(),
                               userDoc.toObject(UserDatabaseRecord.class));

                       handleData(user, question);
                    });
        } else {
            handleData(null, question);
        }
    }

    private void setupResponseButton(String documentId) {
        //Making the response button and implementing it's functionality.
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


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleData(@Nullable User currentUser, Question question) {
        displayQuestionData(question);
        setupQuestionButtonVisibility(currentUser, question.getAuthor(), question);
        setupResponses(question, currentUser);

        if (currentUser != null) {
            setUpVoteButtons(question, currentUser);
        }
    }

    private void setupQuestionButtonVisibility(
            @Nullable User currentUser,
            User author,
            Question question) {
        //Make delete and best answer buttons invisible for correct users.
        ImageButton deleteQButton = findViewById(R.id.QuestionDeleteButton);
        ImageButton QUpVoteButton = findViewById(R.id.QuestionUpVote);
        ImageButton QDownVoteButton = findViewById(R.id.QuestionDownVote);

        if (currentUser != null ) {
            if (!auth.getCurrentUser().getUid().equals(author.getUserID())) {
                QUpVoteButton.setVisibility(View.VISIBLE);
                QDownVoteButton.setVisibility(View.VISIBLE);
            }
            if (currentUser.getUserType() == User.UserType.MODERATOR) {
                deleteQButton.setVisibility(View.VISIBLE);
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchVoteData(Question question, User currentUser, BiConsumer<Boolean, Boolean> f) {
        db.collection("questions")
                .document(question.getPostID())
                .collection("upVotes")
                .whereEqualTo("voterId", currentUser.getUserID())
                .get()
                .addOnSuccessListener(upDocs -> {
                    db.collection("questions")
                            .document(question.getPostID())
                            .collection("downVotes")
                            .whereEqualTo("voterId", currentUser.getUserID())
                            .get()
                            .addOnSuccessListener(downDocs -> {
                               f.accept(upDocs.size() > 0, downDocs.size() > 0);
                            });
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUpVoteButtons(Question question, User currentUser) {

        ImageButton questionUpVoteButton = findViewById(R.id.QuestionUpVote);
        ImageButton questionDownVoteButton = findViewById(R.id.QuestionDownVote);

        questionUpVoteButton.setOnClickListener(v -> {
            fetchVoteData(question, currentUser, (upVoted, downVoted) -> {
                if (downVoted) {
                    db.collection("questions")
                            .document(question.getPostID())
                            .collection("downVotes")
                            .document(currentUser.getUserID())
                            .delete();
                }

                if (!upVoted) {
                    db.collection("questions")
                            .document(question.getPostID())
                            .collection("upVotes")
                            .document(currentUser.getUserID())
                            .set(new VoteDatabaseRecord(currentUser.getUserID()));


                    if (downVoted) {
                        db.collection("questions")
                                .document(question.getPostID())
                                .update("post.voteScore", FieldValue.increment(2));
                    } else {
                        db.collection("questions")
                                .document(question.getPostID())
                                .update("post.voteScore", FieldValue.increment(1));
                    }
                }
            });
        });

        questionDownVoteButton.setOnClickListener(v -> {
            fetchVoteData(question, currentUser, (upVoted, downVoted) -> {
                if (upVoted) {
                    db.collection("questions")
                            .document(question.getPostID())
                            .collection("upVotes")
                            .document(currentUser.getUserID())
                            .delete();
                }

                if (!downVoted) {
                    db.collection("questions")
                            .document(question.getPostID())
                            .collection("downVotes")
                            .document(currentUser.getUserID())
                            .set(new VoteDatabaseRecord(currentUser.getUserID()));

                    if (upVoted) {
                        db.collection("questions")
                                .document(question.getPostID())
                                .update("post.voteScore", FieldValue.increment(-2));
                    } else {
                        db.collection("questions")
                                .document(question.getPostID())
                                .update("post.voteScore", FieldValue.increment(-1));
                    }
                }
            });
        });
    }


    private void displayQuestionData(Question question) {
        //Method to display the Question data in the correct boxes.
        TextView titleView = findViewById(R.id.questionTitleView);
        TextView questionView = findViewById(R.id.QuestText);
        TextView timeView = findViewById(R.id.QuestTime);
        TextView userView = findViewById(R.id.QuestUser);


        titleView.setText(question.getContent().getTitle());
        questionView.setText(question.getContent().getBody());

        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        timeView.setText("Posted on: " + dtf.format(question.getCreationDate()));

        userView.setText("By: " + question.getAuthor().getUserName());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupResponses(Question question, @Nullable User currentUser) {
        db.collection("questions")
                .document(question.getPostID())
                .collection("responses")
                .addSnapshotListener((responseSnapshot, e) -> {
                    fetchResponseAuthors(question, responseSnapshot, currentUser);
                });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        QuestionListView.setLayoutManager(layoutManager);
        QuestionListView.setItemAnimator(new DefaultItemAnimator());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchResponseAuthors(Question question,
                                      QuerySnapshot responsesDoc,
                                      @Nullable User currentUser) {
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
            setResponseAdapter(question, responses, currentUser);
        });
    }

    private void setResponseAdapter(Question question,
                                    List<Response> responses,
                                    @Nullable User currentUser) {
        QuestionViewRecyclerAdapter adapter = new QuestionViewRecyclerAdapter(
                responses,
                currentUser,
                currentUser != null &&
                        question.getAuthor().getUserID().equals(currentUser.getUserID()));
        QuestionListView.setAdapter(adapter);
    }
}