package com.example.myapplication.post;

import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.myapplication.Post;
import com.example.myapplication.User;
import com.example.myapplication.VoteDatabaseRecord;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;

import java.util.function.BiConsumer;

public class Votes {
    private ImageView upVotes;
    private ImageView downVotes;
    private TextView votes;
    private CollectionReference posts;
    private String voteScoreField;

    public Votes(ImageView upVotes, ImageView downVotes, TextView votes,
                 CollectionReference posts, String voteScoreField) {
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.votes = votes;
        this.posts = posts;
        this.voteScoreField = voteScoreField;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setup(@Nullable Post post, User currentUser) {
        displayScore(post.getPostID());
        setupButtons(currentUser, post);
    }

    private void setUpVoteButtonEnabled(boolean enabled) {
        if (enabled) {
            upVotes.setAlpha(1f);
            upVotes.setClickable(true);
        } else {
            upVotes.setAlpha(0.4f);
            upVotes.setClickable(false);
        }
    }

    private void setDownVoteButtonEnabled(boolean enabled) {
        if (enabled) {
            downVotes.setAlpha(1f);
            downVotes.setClickable(true);
        } else {
            downVotes.setAlpha(0.4f);
            downVotes.setClickable(false);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchVoteData(Post post, User currentUser, BiConsumer<Boolean, Boolean> f) {
        posts
                .document(post.getPostID())
                .collection("upVotes")
                .whereEqualTo("voterId", currentUser.getUserID())
                .get()
                .addOnSuccessListener(upDocs -> {
                    posts
                            .document(post.getPostID())
                            .collection("downVotes")
                            .whereEqualTo("voterId", currentUser.getUserID())
                            .get()
                            .addOnSuccessListener(downDocs -> {
                                f.accept(upDocs.size() > 0, downDocs.size() > 0);
                            });
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupButtons(@Nullable User currentUser, Post post) {

        setUpVoteButtonEnabled(false);
        setDownVoteButtonEnabled(false);

        if (currentUser != null && !currentUser.getUserID().equals(post.getAuthor().getUserID())) {

            fetchVoteData(post, currentUser, (upVoted, downVoted) -> {
                setUpVoteButtonEnabled(!upVoted);
                setDownVoteButtonEnabled(!downVoted);
            });

            upVotes.setOnClickListener(v -> {
                fetchVoteData(post, currentUser, (upVoted, downVoted) -> {
                    if (downVoted) {
                        posts
                                .document(post.getPostID())
                                .collection("downVotes")
                                .document(currentUser.getUserID())
                                .delete();

                        posts
                                .document(post.getPostID())
                                .update(voteScoreField, FieldValue.increment(1));

                        setDownVoteButtonEnabled(true);

                    } else if (!upVoted) {
                        posts
                                .document(post.getPostID())
                                .collection("upVotes")
                                .document(currentUser.getUserID())
                                .set(new VoteDatabaseRecord(currentUser.getUserID()));

                        posts
                                .document(post.getPostID())
                                .update(voteScoreField, FieldValue.increment(1));

                        setUpVoteButtonEnabled(false);
                    }
                    displayScore(post.getPostID());
                });
            });

            downVotes.setOnClickListener(v -> {
                fetchVoteData(post, currentUser, (upVoted, downVoted) -> {
                    if (upVoted) {
                        posts
                                .document(post.getPostID())
                                .collection("upVotes")
                                .document(currentUser.getUserID())
                                .delete();

                        posts
                                .document(post.getPostID())
                                .update(voteScoreField, FieldValue.increment(-1));

                        setUpVoteButtonEnabled(true);

                    } else if (!downVoted) {
                        posts
                                .document(post.getPostID())
                                .collection("downVotes")
                                .document(currentUser.getUserID())
                                .set(new VoteDatabaseRecord(currentUser.getUserID()));

                        posts
                                .document(post.getPostID())
                                .update(voteScoreField, FieldValue.increment(-1));

                        setDownVoteButtonEnabled(false);
                    }
                    displayScore(post.getPostID());
                });
            });
        }
    }
    private void displayScore(String questionId) {
        posts
                .document(questionId)
                .get()
                .addOnSuccessListener(doc -> {
                    votes.setText(doc.get(voteScoreField) + "");
                });
    }

}
