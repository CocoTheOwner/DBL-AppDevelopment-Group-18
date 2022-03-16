package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AnswersRecyclerAdapter extends RecyclerView.Adapter<AnswersRecyclerAdapter.myViewHolder> {
    private ArrayList<User> userList;

    public AnswersRecyclerAdapter(ArrayList<User> userList) {
        this.userList = userList;
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        private TextView answerUserN;

        public myViewHolder(final View view){
            super(view);
                    answerUserN = view.findViewById(R.id.answerUser);
        }
    }

    @NonNull
    @Override
    public AnswersRecyclerAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View answerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_list_item, parent, false);
        return new myViewHolder(answerView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswersRecyclerAdapter.myViewHolder holder, int position) {
        //Sets the text for the questions
        String name = userList.get(position).getUsername();
        holder.answerUserN.setText("posted by: " + name);
    }

    @Override
    public int getItemCount() {
        //Gets the amount of answers for the question
        return userList.size();
    }
}
