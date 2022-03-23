package com.example.myapplication.homepage;

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

import android.view.View;

import com.example.myapplication.Question;
import com.example.myapplication.QuestionDatabaseRecord;
import com.example.myapplication.R;
import com.example.myapplication.User;
import com.example.myapplication.UserDatabaseRecord;
import com.example.myapplication.post.QuestionViewActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchResultsFragment extends Fragment {

    private List<Question> sortedQuestions;
    private RecyclerView results;
    private SearchResultsRecyclerAdapter resultsAdapter;
    private HomePageViewModel model;
    private String query = "";
    private FirebaseFirestore db;

    public SearchResultsFragment() {
        super(R.layout.fragment_home_page_search_results);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        model = new ViewModelProvider(requireActivity())
                .get(HomePageViewModel.class);

        db = FirebaseFirestore.getInstance();

        sortedQuestions = new ArrayList<>(model.getQuestions());

        model.getSearchString().observe(getViewLifecycleOwner(), s -> {
            this.query = s;
            updateSearchResults();
        });

        model.getTags().observe(getViewLifecycleOwner(), tags -> {
            updateSearchResults();
        });

        results = view.findViewById(R.id.search_results);

        resultsAdapter = new SearchResultsRecyclerAdapter(sortedQuestions, id -> {
            Intent intent = new Intent(requireActivity().getApplicationContext(),
                    QuestionViewActivity.class);

            intent.putExtra("documentId", id);

            startActivity(intent);
        });

        results.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        results.setAdapter(resultsAdapter);
        results.setItemAnimator(new DefaultItemAnimator());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateSearchResults() {

        db.collection("questions")
                .get()
                .addOnSuccessListener(docs -> {
                    List<Question> questions = new ArrayList<>();

                    List<Task<DocumentSnapshot>> tasks = docs
                            .getDocuments()
                            .stream()
                            .map(doc -> {
                                QuestionDatabaseRecord record =
                                        doc.toObject(QuestionDatabaseRecord.class);

                                Task<DocumentSnapshot> task = db.collection("users")
                                        .document(record.post.authorId)
                                        .get();

                                task.addOnSuccessListener(doc2 -> {
                                    User user = User.fromDatabaseRecord(record.post.authorId,
                                            doc2.toObject(UserDatabaseRecord.class));

                                    questions
                                            .add(Question.fromDatabaseRecord(doc.getId(),
                                                    record,
                                                    user));
                                });

                                return task;
                            }).collect(Collectors.toList());

                    Tasks.whenAll(tasks).addOnSuccessListener(x -> {
                       displaySearchResults(questions);
                    });
                });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void displaySearchResults(List<Question> questions) {
        resultsAdapter.setQuestions(questions
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