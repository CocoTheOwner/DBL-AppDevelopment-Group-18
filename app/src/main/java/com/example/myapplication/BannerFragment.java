package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.homepage.Category;
import com.example.myapplication.homepage.CategoryRecyclerAdapter;
import com.example.myapplication.homepage.HomePageActivity;
import com.example.myapplication.homepage.HomePageViewModel;
import com.example.myapplication.login.LoginPage;
import com.example.myapplication.post.CreateQuestionActivity;
import com.example.myapplication.post.QuestionViewActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BannerFragment extends Fragment {

    private FirebaseAuth auth;

    public BannerFragment() {
        super(R.layout.fragment_banner);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();

        setupBannerText(view);
        setupNewPostButton(view);
        setupProfileButton(view);
    }

    private void setupBannerText(View v) {
        TextView bannerText = v.findViewById(R.id.banner_text);

        bannerText.setOnClickListener(x -> {
            startActivity(new Intent(requireActivity(), HomePageActivity.class));
        });
    }

    private void setupNewPostButton(View v) {
        ImageView button = v.findViewById(R.id.newPostButton);

        if (auth.getCurrentUser() == null) {
            button.setVisibility(View.GONE);
        }

        button.setOnClickListener(x -> {
            startActivity(new Intent(requireActivity(), CreateQuestionActivity.class));
        });
    }

    private void setupProfileButton(View v) {
        v.findViewById(R.id.profileButton).setOnClickListener(x -> {
            if (auth.getCurrentUser() == null) {
                startActivity(new Intent(requireActivity(), LoginPage.class));
            } else {
                startActivity(new Intent(requireActivity(), UserSettingsActivity.class));
            }
        });
    }
}