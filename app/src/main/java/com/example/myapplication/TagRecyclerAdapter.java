package com.example.myapplication;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class TagRecyclerAdapter extends RecyclerView.Adapter<TagRecyclerAdapter.ViewHolder> {

    private List<String> tags;

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tagText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tagText = itemView.findViewById(R.id.tagText);
        }
    }

    public TagRecyclerAdapter(List<String> tags) {
        this.tags = tags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tagText.setText("#" + tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }


}
