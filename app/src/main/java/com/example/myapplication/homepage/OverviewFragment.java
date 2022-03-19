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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OverviewFragment extends Fragment {

    private HomePageViewModel model;

    public OverviewFragment() {
        super(R.layout.fragment_home_page_overview);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

         model = new ViewModelProvider(requireActivity())
                .get(HomePageViewModel.class);

        RecyclerView categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);

        categoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireActivity().getApplicationContext()));

        categoryRecyclerView.setAdapter(
                new CategoryRecyclerAdapter(Arrays.asList(
                        new Category("General", model.getQuestions().subList(0, 5)),
                        new Category("Course", getCategoryPosts("course")),
                        new Category("Location",getCategoryPosts("location")),
                        new Category("Off Topic", getCategoryPosts("offtopic"))
                )));
        categoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Question> getCategoryPosts(String name) {
        return model.getQuestions()
                .stream()
                .filter(post -> post.getTags().containsTag(name))
                .limit(5)
                .collect(Collectors.toList());
    }
}