package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    HomePageViewModel model;
    FragmentContainerView fragmentContainer;
    RecyclerView tags;
    RecyclerView.Adapter tagAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentContainer = findViewById(R.id.main_page_fragment_container);
        model = new ViewModelProvider(this).get(HomePageViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.main_page_fragment_container, OverviewFragment.class, null)
                .commit();
        }

        SearchView searchView = findViewById(R.id.postSearch);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        AutoCompleteTextView tagView = findViewById(R.id.tagInput);

        tagView.setOnEditorActionListener((v, actionId, e) -> {

            addTag(v.getText().toString());
            v.setText("");

            return true;
        });

        tagAdapter = new TagRecyclerAdapter(model.getTags().getValue());

        tags = findViewById(R.id.tags);

        tags.setItemAnimator(new DefaultItemAnimator());
        tags.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        tags.setAdapter(tagAdapter);

        model.getTags().observe(this, tagList -> {
            tagAdapter.notifyItemInserted(tagList.size() - 1);
        });
    }

    void addTag(String tag) {

        model.addTag(tag);
    }

    void search(String s) {
        model.setSearchString(s);

        if (fragmentContainer.getFragment().getClass() != SearchResultsFragment.class) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.main_page_fragment_container, SearchResultsFragment.class, null)
                    .commit();
        }
    }
}