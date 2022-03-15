package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SearchResultsFragment extends Fragment {

    public SearchResultsFragment() {
        super(R.layout.fragment_search_results);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        HomePageViewModel model = new ViewModelProvider(requireActivity())
                .get(HomePageViewModel.class);

        TextView text = view.findViewById(R.id.search_results_text_string);

        model.getSearchString().observe(getViewLifecycleOwner(), s -> {
//            System.out.println("Search string: " + s);
            text.setText(s);
        });
    }
}