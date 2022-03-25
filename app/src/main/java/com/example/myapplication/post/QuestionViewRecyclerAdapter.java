package com.example.myapplication.post;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Response;
import com.example.myapplication.User;

import java.util.List;

import kotlin.reflect.KVisibility;

public class QuestionViewRecyclerAdapter extends RecyclerView.Adapter<QuestionViewRecyclerAdapter.MyViewHolder> {
    private List<Response> responses;
    private User currentUser;

    public QuestionViewRecyclerAdapter(List<Response> responses, User currentUser) {
        this.responses = responses;
        this.currentUser = currentUser;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView answerUserN;
        private TextView body;
        private ImageView delete;
        private ImageButton upVote;
        private ImageButton downVote;

        public MyViewHolder(final View view){
            super(view);
            answerUserN = view.findViewById(R.id.ReplyUser);
            body = view.findViewById(R.id.ReplyText);
            delete = view.findViewById(R.id.ReplyDelete);
            upVote = view.findViewById(R.id.ReplyUpVote);
            downVote = view.findViewById(R.id.ReplyDownVote);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View answerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_list_item, parent, false);
        return new MyViewHolder(answerView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Sets the text for the questions
        Response response = responses.get(position);
        holder.answerUserN.setText("posted by: " + response.getAuthor().getUserName());
        holder.body.setText(response.getContent().getBody());

        if (currentUser.getUserType() == User.UserType.MODERATOR) {
            holder.delete.setVisibility(View.VISIBLE);
        }

        if (!currentUser.getUserID().equals(response.getAuthor().getUserID())) {
            holder.upVote.setVisibility(View.VISIBLE);
            holder.downVote.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        //Gets the amount of answers for the question
        return responses.size();
    }
}
