package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import java.util.List;

public class SearchResultsFragment extends Fragment {

    public SearchResultsFragment() {
        super(R.layout.fragment_search_results);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        HomePageViewModel model = new ViewModelProvider(requireActivity())
                .get(HomePageViewModel.class);

        model.getSearchString().observe(getViewLifecycleOwner(), s -> {
//            System.out.println("Search string: " + s);
        });

        RecyclerView results = view.findViewById(R.id.search_results);

        List<String> posts = new ArrayList<>();

        posts.add("one");
        posts.add("two");
        posts.add("three");
        posts.add("four");
        posts.add("five");
        posts.add("six");

        results.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        results.setAdapter(new SearchResultsRecyclerAdapter(posts));
        results.setItemAnimator(new DefaultItemAnimator());
    }
}