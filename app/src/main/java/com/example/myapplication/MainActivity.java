package com.example.myapplication;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private HomePageViewModel model;
    private FragmentContainerView fragmentContainer;
    private RecyclerView tags;
    private RecyclerView.Adapter tagAdapter;
    private OnBackPressedCallback backPressedCallback;

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

        setupSearchView();
        setupTagInputView();
        setupTagListView();

        backPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                returnToOverview();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    private void setupSearchView() {
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
    }

    private void setupTagInputView() {
        AutoCompleteTextView tagInputView = findViewById(R.id.tagInput);

        tagInputView.setAdapter(new ArrayAdapter<String>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                model.getAvailableTags()));
        tagInputView.setThreshold(1);

        tagInputView.setOnEditorActionListener((v, actionId, e) -> {

            String trimmed = v.getText().toString().replaceAll("[\\s#]", "");

            if (model.getAvailableTags().contains(trimmed.toLowerCase())) {
                addTag(trimmed);
                v.setText("");
            } else {
                Toast.makeText(getApplicationContext(),
                        v.getText() + " is not a valid tag", Toast.LENGTH_LONG).show();
            }

            return true;
        });
    }

    private void setupTagListView() {
        tagAdapter = new TagRecyclerAdapter(model.getTags().getValue(), position -> {
            removeTag(position);
        });

        tags = findViewById(R.id.tags);

        tags.setItemAnimator(new DefaultItemAnimator());
        tags.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false));
        tags.setAdapter(tagAdapter);

        model.getTags().observe(this, tagList -> {
            tagAdapter.notifyDataSetChanged();
        });
    }

    private void addTag(String tag) {

        model.addTag(tag);
    }

    private void removeTag(int position) {
        model.removeTag(position);
    }



    private void search(String s) {
        model.setSearchString(s);

        if (fragmentContainer.getFragment().getClass() != SearchResultsFragment.class) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.main_page_fragment_container,
                            SearchResultsFragment.class, null)
                    .commit();

            backPressedCallback.setEnabled(true);
        }
    }

    private void returnToOverview() {
        if (fragmentContainer.getFragment().getClass() != OverviewFragment.class) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.main_page_fragment_container,
                            OverviewFragment.class, null)
                    .commit();

            backPressedCallback.setEnabled(false);
        }
    }
}