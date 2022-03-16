package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

public class OverviewFragment extends Fragment {

    private HomePageViewModel model;

    public OverviewFragment() {
        super(R.layout.fragment_overview);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

         model = new ViewModelProvider(requireActivity())
                .get(HomePageViewModel.class);

        RecyclerView categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);

        categoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireActivity().getApplicationContext()));

        categoryRecyclerView.setAdapter(
                new CategoryRecyclerAdapter(Arrays.asList("General", "Course", "Location", "Off Topic"),
                        model.getPosts()));
        categoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}