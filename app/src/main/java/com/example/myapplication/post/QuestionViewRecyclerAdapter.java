package com.example.myapplication.post;

import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.PostDatabaseRecord;
import com.example.myapplication.Question;
import com.example.myapplication.R;
import com.example.myapplication.Response;
import com.example.myapplication.User;
import com.example.myapplication.VoteDatabaseRecord;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class QuestionViewRecyclerAdapter extends RecyclerView.Adapter<QuestionViewRecyclerAdapter.MyViewHolder> {
    private List<Response> responses;
    private User currentUser;
    private Question question;
    private FirebaseFirestore db;
    private Consumer<String> acceptButtonAction;
    private BiConsumer<View, String> deleteButtonAction;

    public QuestionViewRecyclerAdapter(List<Response> responses, User currentUser,
                                       Question question, Consumer<String> acceptButtonAction,
                                       BiConsumer<View, String> deleteButtonAction) {
        this.responses = responses;
        this.currentUser = currentUser;
        this.question = question;
        this.db = FirebaseFirestore.getInstance();
        this.acceptButtonAction = acceptButtonAction;
        this.deleteButtonAction = deleteButtonAction;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView answerUserN;
        private TextView body;
        private ImageView delete;
        private ImageButton upVote;
        private ImageButton downVote;
        private ImageView accept;
        private TextView voteScore;
        private TextView bestAnswer;
        private TextView deletedText;

        public MyViewHolder(final View view){
            super(view);
            answerUserN = view.findViewById(R.id.ReplyUser);
            body = view.findViewById(R.id.ReplyText);
            delete = view.findViewById(R.id.ReplyDelete);
            upVote = view.findViewById(R.id.ReplyUpVote);
            downVote = view.findViewById(R.id.ReplyDownVote);
            accept = view.findViewById(R.id.ReplyAccept);
            voteScore = view.findViewById(R.id.ReplyScore);
            bestAnswer = view.findViewById(R.id.BestAnswer);
            deletedText = view.findViewById(R.id.ResponseDeletedText);

        }

        public void displayScore(DocumentReference responseDoc) {
            responseDoc
                    .get()
                    .addOnSuccessListener(doc -> {
                        voteScore.setText("" + doc.toObject(PostDatabaseRecord.class).voteScore);
                    });
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void setUpButtons(DocumentReference responseDoc) {
            upVote.setOnClickListener(v -> {
                fetchVoteData(responseDoc, (upVoted, downVoted) -> {
                    if (downVoted) {
                        responseDoc.collection("downVotes")
                                .document(currentUser.getUserID())
                                .delete();
                    }

                    if (!upVoted) {
                        responseDoc.collection("upVotes")
                                .document(currentUser.getUserID())
                                .set(new VoteDatabaseRecord(currentUser.getUserID()));

                        if (downVoted) {
                            responseDoc.update("voteScore", FieldValue.increment(2));
                        } else {
                            responseDoc.update("voteScore", FieldValue.increment(1));
                        }
                    }
                });
            });

            downVote.setOnClickListener(v -> {
                fetchVoteData(responseDoc, (upVoted, downVoted) -> {
                    if (upVoted) {
                        responseDoc.collection("upVotes")
                                .document(currentUser.getUserID())
                                .delete();
                    }

                    if (!downVoted) {
                        responseDoc.collection("downVotes")
                                .document(currentUser.getUserID())
                                .set(new VoteDatabaseRecord(currentUser.getUserID()));

                        if (upVoted) {
                            responseDoc.update("voteScore", FieldValue.increment(-2));
                        } else {
                            responseDoc.update("voteScore", FieldValue.increment(-1));
                        }
                    }
                });
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void fetchVoteData(DocumentReference responseDoc, BiConsumer<Boolean, Boolean> f) {
            responseDoc.collection("upVotes")
                    .whereEqualTo("voterId", currentUser.getUserID())
                    .get()
                    .addOnSuccessListener(upDocs -> {
                        responseDoc.collection("downVotes")
                                .whereEqualTo("voterId", currentUser.getUserID())
                                .get()
                                .addOnSuccessListener(downDocs -> {
                                    f.accept(upDocs.size() > 0, downDocs.size() > 0);
                                });
                    });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View answerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_list_item, parent, false);
        return new MyViewHolder(answerView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Sets the text for the questions
        Response response = responses.get(position);
        holder.answerUserN.setText(TextUtils.concat("Response by: ",
                response.getAuthorAndDateText()));
        holder.body.setText(response.getContent().getBody());

        if (currentUser != null) {
            if (currentUser.getUserType() == User.UserType.MODERATOR) {
                holder.delete.setVisibility(View.VISIBLE);
            }

            if (!currentUser.getUserID().equals(response.getAuthor().getUserID())) {
                holder.upVote.setVisibility(View.VISIBLE);
                holder.downVote.setVisibility(View.VISIBLE);
            }
        }
        DocumentReference responseDoc = db.collection("questions")
                .document(question.getPostID())
                .collection("responses")
                .document(response.getPostID());

        holder.displayScore(responseDoc);

        if (currentUser != null) {
            if (question.getAuthor().getUserID().equals(currentUser.getUserID())) {
                holder.accept.setVisibility(View.VISIBLE);
            }
            holder.setUpButtons(responseDoc);
        }

        if (response.getPostID().equals(question.getBestAnswerId())) {
            holder.bestAnswer.setVisibility(View.VISIBLE);
        }

        holder.accept.setOnClickListener(v -> {
            acceptButtonAction.accept(response.getPostID());
        });

        holder.delete.setOnClickListener(v -> {
            deleteButtonAction.accept(holder.deletedText, response.getPostID());
        });
    }

    @Override
    public int getItemCount() {
        //Gets the amount of answers for the question
        return responses.size();
    }
}
