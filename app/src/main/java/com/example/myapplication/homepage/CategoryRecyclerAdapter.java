package com.example.myapplication.homepage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {

    private List<Category> categories;
    private SearchResultsRecyclerAdapter.ItemClickListener listener;

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private RecyclerView postRecycler;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.categoryTitle);
            postRecycler = itemView.findViewById(R.id.categoryPostsRecycler);
        }
    }

    public CategoryRecyclerAdapter(List<Category> categories,
                                   SearchResultsRecyclerAdapter.ItemClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_category_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclerView postRecycler = holder.postRecycler;

        postRecycler.setLayoutManager(new LinearLayoutManager(postRecycler.getContext()));
        postRecycler.setAdapter(
                new SearchResultsRecyclerAdapter(this.categories.get(position).getPosts(), listener));
        postRecycler.setItemAnimator(new DefaultItemAnimator());
        holder.title.setText(this.categories.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return this.categories.size();
    }

}
