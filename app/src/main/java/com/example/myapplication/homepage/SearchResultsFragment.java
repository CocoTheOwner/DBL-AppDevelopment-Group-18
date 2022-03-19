package com.example.myapplication.homepage;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.example.myapplication.Question;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchResultsFragment extends Fragment {

    private List<Question> sortedQuestions;
    private RecyclerView results;
    private SearchResultsRecyclerAdapter resultsAdapter;
    private HomePageViewModel model;
    private String query = "";

    public SearchResultsFragment() {
        super(R.layout.fragment_home_page_search_results);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        model = new ViewModelProvider(requireActivity())
                .get(HomePageViewModel.class);

        sortedQuestions = new ArrayList<>(model.getQuestions());

        model.getSearchString().observe(getViewLifecycleOwner(), s -> {
            this.query = s;
            updateSearchOrder();
        });

        model.getTags().observe(getViewLifecycleOwner(), tags -> {
            updateSearchOrder();
        });

        results = view.findViewById(R.id.search_results);

        resultsAdapter = new SearchResultsRecyclerAdapter(sortedQuestions);

        results.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        results.setAdapter(resultsAdapter);
        results.setItemAnimator(new DefaultItemAnimator());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateSearchOrder() {

        resultsAdapter.setQuestions(model
                .getQuestions()
                .stream()
                .sorted((a, b) ->
                        b.getContent().getSearchQueryScore(query)
                                - a.getContent().getSearchQueryScore(query))
                .filter(p -> model
                        .getTags()
                        .getValue()
                        .getList()
                        .stream()
                        .allMatch(tag -> p.getTags().containsTag(tag)))
                .collect(Collectors.toList()));

        resultsAdapter.notifyDataSetChanged();
    }
}