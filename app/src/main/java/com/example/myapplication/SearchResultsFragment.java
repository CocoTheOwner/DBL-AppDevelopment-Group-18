package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SearchResultsFragment extends Fragment {

    private List<Post> sortedPosts;
    private RecyclerView results;
    private RecyclerView.Adapter resultsAdapter;

    public SearchResultsFragment() {
        super(R.layout.fragment_search_results);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        HomePageViewModel model = new ViewModelProvider(requireActivity())
                .get(HomePageViewModel.class);

        sortedPosts = new ArrayList<>(model.getPosts());

        model.getSearchString().observe(getViewLifecycleOwner(), s -> {
            updateSearchOrder(s);
        });

        results = view.findViewById(R.id.search_results);

        resultsAdapter = new SearchResultsRecyclerAdapter(sortedPosts);

        results.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        results.setAdapter(resultsAdapter);
        results.setItemAnimator(new DefaultItemAnimator());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateSearchOrder(String query) {
        Collections.sort(sortedPosts,
                (a, b) -> b.getSearchQueryScore(query) - a.getSearchQueryScore(query));

        resultsAdapter.notifyDataSetChanged();

        System.out.println(sortedPosts.stream().map( p -> p.getTitle()).collect(Collectors.toList()));
    }
}