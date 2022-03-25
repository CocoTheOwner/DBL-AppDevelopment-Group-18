package com.example.myapplication.post;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Response;

import java.util.List;

public class QuestionViewRecyclerAdapter extends RecyclerView.Adapter<QuestionViewRecyclerAdapter.myViewHolder> {
    private List<Response> responses;

    public QuestionViewRecyclerAdapter(List<Response> responses) {
        this.responses = responses;
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        private TextView answerUserN;
        private TextView body;

        public myViewHolder(final View view){
            super(view);
            answerUserN = view.findViewById(R.id.ReplyUser);
            body = view.findViewById(R.id.ReplyText);
        }
    }

    @NonNull
    @Override
    public QuestionViewRecyclerAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View answerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_list_item, parent, false);
        return new myViewHolder(answerView);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewRecyclerAdapter.myViewHolder holder, int position) {
        //Sets the text for the questions
        Response response = responses.get(position);
        holder.answerUserN.setText("posted by: " + response.getAuthor().getUserName());
        holder.body.setText(response.getContent().getBody());
    }

    @Override
    public int getItemCount() {
        //Gets the amount of answers for the question
        return responses.size();
    }
}
