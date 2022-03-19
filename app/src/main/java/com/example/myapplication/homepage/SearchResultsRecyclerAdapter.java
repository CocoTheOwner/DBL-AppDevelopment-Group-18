package com.example.myapplication.homepage;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Question;
import com.example.myapplication.R;

import java.util.List;

public class SearchResultsRecyclerAdapter extends RecyclerView.Adapter<SearchResultsRecyclerAdapter.ViewHolder> {

    private List<Question> questions;

    public SearchResultsRecyclerAdapter(List<Question> questions) {
        this.questions = questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(final View view) {
            super(view);
            textView = view.findViewById(R.id.content);
        }
    }

    @NonNull
    @Override
    public SearchResultsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_post_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsRecyclerAdapter.ViewHolder holder, int position) {
        holder.textView.setText(
                Html.fromHtml(
                        "<u>"+ questions.get(position).getContent().getTitle()+"</u>"
                + " (+69) By "
                                // TODO: Figure out how to make this color string not hardcoded
                                // This will probably be done using spannable in the future
                + "<font color=#87ceeb>Joe Shmo</font>"));
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
