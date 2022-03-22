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

import android.os.health.SystemHealthManager;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OverviewFragment extends Fragment {

    private HomePageViewModel model;
    private FirebaseFirestore db;
    private RecyclerView categoryRecyclerView;


    public OverviewFragment() {
        super(R.layout.fragment_home_page_overview);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();

         model = new ViewModelProvider(requireActivity())
                .get(HomePageViewModel.class);

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);

        categoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireActivity().getApplicationContext()));


        categoryRecyclerView.setItemAnimator(new DefaultItemAnimator());

        db.collection("questions").get().addOnSuccessListener(questionDocs -> {

            List<Question> questions = new ArrayList<>();

           List<Task<DocumentSnapshot>> tasks = questionDocs.getDocuments().stream().map(doc -> {

               QuestionDatabaseRecord record = doc.toObject(QuestionDatabaseRecord.class);

               Task<DocumentSnapshot> task = db.collection("users")
                       .document(record.post.authorId).get();

               task.addOnSuccessListener(doc2 -> {
                   questions.add(Question.fromDatabaseRecord(doc.getId(),
                           record, User.fromDatabaseRecord(doc2.getId(),
                                   doc2.toObject(UserDatabaseRecord.class))));
               });

               return task;
           }).collect(Collectors.toList());

            Tasks.whenAll(tasks).addOnSuccessListener(x -> {
                updateQuestions(questions);
            });
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateQuestions(List<Question> questions) {
        categoryRecyclerView.setAdapter(
                new CategoryRecyclerAdapter(Arrays.asList(
                        new Category("General", questions.subList(0, Math.min(5, questions.size()))),
                        new Category("Course", getCategoryPosts(questions, "course")),
                        new Category("Location",getCategoryPosts(questions, "location")),
                        new Category("Off Topic", getCategoryPosts(questions, "offtopic"))
                ), questionId -> {
                    Intent intent = new Intent(requireActivity().getApplicationContext(), QuestionViewActivity.class);

                    intent.putExtra("documentId", questionId);

                    startActivity(intent);
                }));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Question> getCategoryPosts(List<Question> questions, String name) {
        return questions
                .stream()
                .filter(post -> post.getTags().containsTag(name))
                .limit(5)
                .collect(Collectors.toList());
    }
}