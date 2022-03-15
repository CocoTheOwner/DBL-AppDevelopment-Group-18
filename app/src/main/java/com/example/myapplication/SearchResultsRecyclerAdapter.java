package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class SearchResultsRecyclerAdapter extends RecyclerView.Adapter<SearchResultsRecyclerAdapter.ViewHolder> {

    private List<String> posts;

    public SearchResultsRecyclerAdapter(List<String> posts) {
        this.posts = posts;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(final View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
        }
    }

    @NonNull
    @Override
    public SearchResultsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsRecyclerAdapter.ViewHolder holder, int position) {
        holder.textView.setText(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
