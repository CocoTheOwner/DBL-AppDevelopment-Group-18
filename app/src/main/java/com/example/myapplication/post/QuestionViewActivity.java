package com.example.myapplication.post;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Content;
import com.example.myapplication.ContentDatabaseRecord;
import com.example.myapplication.PostDatabaseRecord;
import com.example.myapplication.Question;
import com.example.myapplication.QuestionDatabaseRecord;
import com.example.myapplication.R;
import com.example.myapplication.Response;
import com.example.myapplication.User;
import com.example.myapplication.UserDatabaseRecord;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class QuestionViewActivity extends AppCompatActivity {
    private RecyclerView QuestionListView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private StorageReference storageRef;
    private List<Response> responses = new ArrayList<>();
    private QuestionViewRecyclerAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialize the data from the database and start all methods to build the page.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);
        QuestionListView = findViewById(R.id.QuestionViewRecycler);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        String documentId = intent.getStringExtra("documentId");

        fetchQuestionAndUserData(documentId, question -> {
            fetchUserData(user -> {
                handleData(user, question);
            });
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchQuestionAndUserData(String documentId, Consumer<Question> callback) {
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

                                callback.accept(question);
                            });
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchUserData(Consumer<User> callback) {
        if (auth.getCurrentUser() != null) {
            db.collection("users")
                    .document(auth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(userDoc -> {
                       User user = User.fromDatabaseRecord(userDoc.getId(),
                               userDoc.toObject(UserDatabaseRecord.class));

                        callback.accept(user);
                    });
        } else {
            callback.accept(null);
        }
    }

    private void setupResponseButton(Question question, User currentUser) {
        //Making the response button and implementing it's functionality.
        ImageButton responseButton = findViewById(R.id.replyButton);
        EditText replyBox = findViewById(R.id.replyBox);

        if (auth.getCurrentUser() != null) {
            responseButton.setOnClickListener(v -> {
                if (validateResponse(replyBox)) {
                    String text = replyBox.getText().toString();
                    replyBox.setText("");

                    PostDatabaseRecord record = new PostDatabaseRecord(auth.getCurrentUser().getUid(),
                            new ContentDatabaseRecord(null, "", text),
                            new Date());

                    db.collection("questions")
                            .document(question.getPostID())
                            .collection("responses")
                            .add(record)
                            .addOnSuccessListener(doc -> {
                                Response response = Response.fromDatabaseRecord(doc.getId(),
                                        record, currentUser);

                                responses.add(response);
                                adapter.notifyItemInserted(responses.size() - 1);
                            });
                }
            });
        } else {
            responseButton.setVisibility(View.GONE);
            replyBox.setVisibility(View.GONE);
        }
    }

    private boolean validateResponse(EditText replyBox) {
        if (replyBox.getText().toString().replaceAll("\\s*", "").length() <= 0) {
            replyBox.setError("You cannot submit an empty response");

            return false;
        } else {
            return true;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleData(@Nullable User currentUser, Question question) {
        displayQuestionData(question);
        setupQuestionButtonVisibility(currentUser);
        setupDeleteButton(question);
        setupResponses(question, currentUser);
        displayAttachment(question);
        setupResponseButton(question, currentUser);

        new Votes(
                findViewById(R.id.QuestionUpVote),
                findViewById(R.id.QuestionDownVote),
                findViewById(R.id.QuestionScore),
                db.collection("questions"),
                "post.voteScore"
        ).setup(question, currentUser);
    }

    private void setupDeleteButton(Question question) {
        ImageButton deleteButton = findViewById(R.id.QuestionDeleteButton);

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this).setMessage(R.string.reafirm_delete_post)
                    .setPositiveButton(R.string.yes, (dialog, id) -> {
                        db.collection("questions")
                                .document(question.getPostID())
                                .delete();

                        findViewById(R.id.questionDeletedText).setVisibility(View.VISIBLE);
                    })
                    .setNegativeButton(R.string.no, (dialog, id ) -> {})
                    .create()
                    .show();
        });
    }

    private void displayAttachment(Question question) {
        String attachment = question.getContent().getAttachment();
        ImageView attachmentView = findViewById(R.id.QuestionImage);

        if (attachment != null) {

            try {
                File localFile = File.createTempFile(attachment, "jpg");

                storageRef.child("images/" + attachment).getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                    attachmentView.setImageBitmap(bitmap);
                }).addOnFailureListener(e -> {e.printStackTrace();});

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            attachmentView.setVisibility(View.GONE);
        }
    }

    private void setupQuestionButtonVisibility(@Nullable User currentUser) {
        //Make delete and best answer buttons invisible for correct users.
        ImageButton deleteQButton = findViewById(R.id.QuestionDeleteButton);

        if (currentUser != null ) {
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




    private void displayQuestionData(Question question) {
        //Method to display the Question data in the correct boxes.
        TextView titleView = findViewById(R.id.questionTitleView);
        TextView questionView = findViewById(R.id.QuestText);
        TextView userView = findViewById(R.id.QuestUser);


        titleView.setText(question.getContent().getTitle());
        questionView.setText(question.getContent().getBody());

        userView.setText(TextUtils.concat("By: ", question.getAuthorAndDateText()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupResponses(Question question, @Nullable User currentUser) {
        db.collection("questions")
                .document(question.getPostID())
                .collection("responses")
                .get()
                .addOnSuccessListener(responseSnapshot -> {

                    TextView replyCountView = findViewById(R.id.ReplyCount);

                    replyCountView.setText(responseSnapshot.size() +
                            (responseSnapshot.size() == 1 ? " Response" : " Responses"));

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
        List<Task<DocumentSnapshot>> userQueries = responsesDoc
                .getDocuments()
                .stream()
                .map(responseDoc -> {
                    PostDatabaseRecord record = responseDoc.toObject(PostDatabaseRecord.class);

                    responses = new ArrayList<>();

                    Task<DocumentSnapshot> userQuery = db.collection("users")
                            .document(record.authorId)
                            .get();


                    userQuery.addOnSuccessListener(userDoc -> {
                        User author = User.fromDatabaseRecord(userDoc.getId(),
                                userDoc.toObject(UserDatabaseRecord.class));

                        responses.add(Response.fromDatabaseRecord(
                                responseDoc.getId(), record, author));
                    });

                    return userQuery;
                }).collect(Collectors.toList());

        Tasks.whenAllSuccess(userQueries).addOnSuccessListener(x -> {

            if (responses.size() > 1) {
                for (int i = 0; i < responses.size(); i++) {
                    if (responses.get(i).getPostID().equals(question.getBestAnswerId())) {
                        Response bestResp = responses.get(i);
                        responses.set(i, responses.get(0));
                        responses.set(0, bestResp);
                        break;
                    }
                }

                responses.subList((question.getBestAnswerId() == null ? 0 : 1),
                        responses.size())
                        .sort((a, b) -> b.getVoteScore() - a.getVoteScore());
            }

            setResponseAdapter(question, responses, currentUser);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setResponseAdapter(Question question,
                                    List<Response> responses,
                                    @Nullable User currentUser) {
        adapter = new QuestionViewRecyclerAdapter(
                responses,
                currentUser,
                question,
                (responseId, position) -> {
                    setBestAnswer(question, responseId, position);
                },
                (deletedText, responseId) -> {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.reafirm_delete_post)
                            .setPositiveButton(R.string.yes, (d, i) -> {
                                deletedText.setVisibility(View.VISIBLE);
                                db.collection("questions")
                                        .document(question.getPostID())
                                        .collection("responses")
                                        .document(responseId)
                                        .delete();
                            })
                            .setNegativeButton(R.string.no, (d, i) -> {})
                            .create()
                            .show();
                });
        QuestionListView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setBestAnswer(Question question, String responseId, int responsePosition) {

        question.setBestAnswerId(responseId);
        adapter.setQuestion(question);
        adapter.notifyItemChanged(0);
        adapter.notifyItemChanged(responsePosition);

        db.collection("questions")
                .document(question.getPostID())
                .update("bestAnswer", responseId);
    }
}