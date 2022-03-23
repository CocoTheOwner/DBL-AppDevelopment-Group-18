package com.example.myapplication.homepage;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.TagCollection;
import com.example.myapplication.TagDatabaseRecord;
import com.example.myapplication.UserSettingsActivity;
import com.example.myapplication.login.LoginPage;
import com.example.myapplication.post.CreateQuestionActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.stream.Collectors;

public class HomePageActivity extends AppCompatActivity {

    private HomePageViewModel model;
    private FragmentContainerView fragmentContainer;
    private RecyclerView tags;
    private RecyclerView.Adapter tagAdapter;
    private OnBackPressedCallback backPressedCallback;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        db = FirebaseFirestore.getInstance();

        fragmentContainer = findViewById(R.id.main_page_fragment_container);
        model = new ViewModelProvider(this).get(HomePageViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.main_page_fragment_container, OverviewFragment.class, null)
                .commit();
        }

        setupSearchView();
        setupTagListView();

        backPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                returnToOverview();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();

        setupTagInputView();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupTagInputView() {

        db.collection("tags").get()
                .addOnSuccessListener(docs -> {
                   TagCollection tags = new TagCollection(docs.getDocuments()
                           .stream()
                           .map(doc -> doc.toObject(TagDatabaseRecord.class).display)
                           .collect(Collectors.toList()));

                   addTagsToAutocomplete(tags);
                });


    }

    private void addTagsToAutocomplete(TagCollection tags) {
        AutoCompleteTextView tagInputView = findViewById(R.id.tagInput);

        tagInputView.setAdapter(new ArrayAdapter<String>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                tags.getList()));
        tagInputView.setThreshold(1);

        tagInputView.setOnEditorActionListener((v, actionId, e) -> {

            if (tags.containsTag(v.getText().toString())) {
                addTag(v.getText().toString());
                v.setText("");
            } else {
                v.setError("This tag does not exist");
            }

            return true;
        });
    }

    private void setupTagListView() {
        tagAdapter = new TagRecyclerAdapter(model.getTags().getValue().getList(), position -> {
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