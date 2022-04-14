package com.example.myapplication.post;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import com.example.myapplication.ContentDatabaseRecord;
import com.example.myapplication.PostDatabaseRecord;
import com.example.myapplication.QuestionDatabaseRecord;
import com.example.myapplication.R;
import com.example.myapplication.TagCollection;
import com.example.myapplication.TagDatabaseRecord;
import com.example.myapplication.homepage.FrontPageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CreateQuestionActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private Button submit;
    private Button exit;

    private FirebaseAuth auth;

    // The following are needed for adding attachments to the post.
    private Button addImage;
    ActivityResultLauncher<String> pickerLauncherViaFiles;
    ActivityResultLauncher<Uri> imageViaCamera;
    //Uri Parameter of the launch method must be the destination where the picture is saved
    private Uri tempLocURI;
    private static final int CAM_REQUESTCODE = 1001;
    private StorageReference storageRef;

    private boolean imageAttached = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);


        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

        //Submit question button
        submit = findViewById(R.id.submit_question);

        //Cancel question button.
        exit = findViewById(R.id.cancel_button);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(CreateQuestionActivity.this, FrontPageActivity.class);
                startActivity(home);
            }
        });

        submit.setOnClickListener(view -> submit());

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("noCreateQuestionButton", true);

        }

        // Setup the ActivityForResult launchers we need.
        setupLaunchers();
        storageRef = FirebaseStorage.getInstance().getReference();
        // TODO: Add user choice between viaFiles or viaCamera!
        addImage = findViewById(R.id.add_image_button);
        addImage.setOnClickListener(view -> {
            handleImageOptions(view);
        });
    }


    // Show a popup menu, letting the user choose between camera and gallery.
    private void handleImageOptions(View v) {
        PopupMenu imageMenu = new PopupMenu(getApplicationContext(), v); // Anchored on the button
        imageMenu.getMenuInflater().inflate(R.menu.imagemenu, imageMenu.getMenu());
        imageMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.camera) { // Camera option clicked
                // Check the required permissions. OnRequestPermissionsResult will redirect to
                // camera activity result (callback of requestPermissions).
                String[] cameraPerms = {Manifest.permission.CAMERA};
                ActivityCompat.requestPermissions(this, cameraPerms, CAM_REQUESTCODE);
            } else if (menuItem.getItemId() == R.id.gallery) { // Gallery chosen
                pickerLauncherViaFiles.launch("image/*");
            }

            return true;
        });

        imageMenu.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void submit() {
        //Fields for entering question text and title.
        EditText questionTitle = findViewById(R.id.question_title);
        EditText questionText = findViewById(R.id.question_text);
        MultiAutoCompleteTextView tagInputView = findViewById(R.id.question_tags);

        if (validateContent(questionTitle)) {
            String questionTitleString = questionTitle.getText().toString();
            String questionTextString = questionText.getText().toString();
            List<String> tags = Arrays.asList(tagInputView
                    .getText()
                    .toString()
                    .split("\\s*,\\s*"))
                    .stream()
                    .filter(s -> !s.isEmpty())
                    .map(tag -> TagCollection.trimTag(tag))
                    .collect(Collectors.toList());


            Date now = new Date();

            // if an image is attached, we first have to upload it to the
            // database before calling createPost().
            if (imageAttached) {
                uploadPictureToStorage(imageId -> {
                    createPost(questionTitleString, questionTextString, tags, now, imageId);
                });
            } else {
                createPost(questionTitleString, questionTextString, tags, now, null);
            }
        }
    }

    private boolean validateContent(EditText questionTitle) {
        String questionTitleString = questionTitle.getText().toString();

        if (questionTitleString.replaceAll("\\s*", "").length() <= 0) {
            questionTitle.setError("Your question needs a title");

            return false;
        } else {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createPost(String title, String body, List<String> tags, Date date, @Nullable String imageId) {

        // Get the ID of the user that is currently logged in
        String userId = auth.getCurrentUser().getUid();

        QuestionDatabaseRecord newQuestionRecord = new QuestionDatabaseRecord(
                new PostDatabaseRecord(userId,
                        new ContentDatabaseRecord(
                                imageId,
                                title, body
                        ), date), tags, null);

        // Upload the question to the database
        db.collection("questions").add(newQuestionRecord).addOnSuccessListener(doc -> {
                gotoQuestionView(doc.getId());
        }).addOnFailureListener(
                error -> Toast.makeText(getApplicationContext(),
                        "Failed to create question", Toast.LENGTH_LONG).show());

        // If the user added any tags to their post that have not been used before,
        // we add these tags to the tags stored on the database. Now these tags will
        // be included in the autocomplete suggestions when searching.
        updateTags(tags);
    }

    // Redirect the user to the 'View Question' page of their newly created question
    private void gotoQuestionView(String id) {
        Intent intent = new Intent(CreateQuestionActivity.this, QuestionViewActivity.class);

        // We need to know which question to get from the database after redirecting.
        intent.putExtra("documentId", id);

        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateTags(List<String> tags) {
        // We get all tags from the database that are equal to tag,
        // If none are found, this tag is new and we add the tag to the database
        // via a call to uploadTag().
        for (String tag : tags) {
            db.collection("tags")
                    .whereEqualTo("lower", tag.toLowerCase(Locale.ROOT))
                    .get()
                    .addOnSuccessListener(docs -> {
                        if (docs.size() <= 0) {
                            uploadTag(tag);
                        }
                    }).addOnFailureListener(e -> System.out.println(e.getMessage()));
        }
    }

    // Upload a tag to the tags collection on the Firebase Firestore
    private void uploadTag(String tag) {
        db.collection("tags")
                .add(new TagDatabaseRecord(tag));
    }

    private void setupLaunchers() {
        // Start another activity with the goal of getting a result from it.
        // In this case, the other activity is a built-in activity for getting content from
        // the phone's files.
        // The input is the mime type to filter by, in our case we use: "image/*".
        pickerLauncherViaFiles = registerForActivityResult( // Via files
                // prompt the user to pick a piece of content, receiving a content:
                // Uri for that content
                new ActivityResultContracts.GetContent(),
                result -> { // Here we choose what to do with the selected image
                    if (result != null) { // If an image was selected, we can use the URI stored in 'result'

                        // ImageView used to display a preview of the selected image
                        ImageView imagePreview = findViewById(R.id.imagePreview);

                        // tempLocURI is used for the file to be be included with the post
                        tempLocURI = result;

                        // Display a preview to the user
                        imagePreview.setImageURI(tempLocURI);

                        // Notify the system that an image is attached to the post
                        imageAttached = true;
                    } else { // User does not select an image (e.g., presses the return button)
                        Toast.makeText(getApplicationContext(),
                                "No image selected",
                                Toast.LENGTH_SHORT).show();
                    }
                });


        imageViaCamera = registerForActivityResult(
                // An ActivityResultContract to take a picture saving it into the provided
                // content-Uri.
                // Returns true if the image was saved into the given Uri.
                new ActivityResultContracts.TakePicture(),
                result -> { // Here we choose what to do based on user action
                    if (result) { // The user has taken a picture

                        // ImageView used to display a preview of the selected image
                        ImageView imagePreview = findViewById(R.id.imagePreview);

                        // Display a preview to the user
                        imagePreview.setImageURI(tempLocURI);

                        // Notify the system that an image is attached to the post
                        imageAttached = true;

                    } else { // User has returned without taking a picture
                        Toast.makeText(getApplicationContext(),
                                "No picture taken",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String uploadPictureToStorage(Consumer<String> callback) {
        // Setup of a simple popup dialog that shows the user the progress of their upload
        ProgressDialog dialog = new ProgressDialog(CreateQuestionActivity.this);
        dialog.setTitle("Upload in progress");
        dialog.show();

        // We generate a random ID to use for the image's ID on the database
        String id = UUID.randomUUID().toString();

        // Store the image in the firebase Storage with a randomly generated ID
        StorageReference picRef = storageRef.child("images/" + id);
        picRef.putFile(tempLocURI)
                .addOnSuccessListener(taskSnapshot -> {
                    dialog.dismiss(); // Close the dialog
                    Toast.makeText(getApplicationContext(), // Show a 'success' notification
                            "File(s) uploaded successfully!",
                            Toast.LENGTH_SHORT).show();

                    callback.accept(id); // Run the callback method
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss(); // Close the dialog
                    Toast.makeText(getApplicationContext(), // Notify user of error encountered
                            e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(snapshot -> { // Show the user the progress of the upload
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    dialog.setMessage("Progress: " + (int) progress + "%");
                });

        return id;
    }

    private void launchCameraActivity() {

        File photoFile = null; // Initialized to null as otherwise it may not have been initialized

        try {
            photoFile = createImageFile(); // Create a temporary file in the app's pictures dir
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (photoFile != null) { // We have a temporary file location
            tempLocURI = FileProvider.getUriForFile(this, // Get URI for temporary file
                    "com.example.android.fileprovider",
                    photoFile);
            imageViaCamera.launch(tempLocURI); // Launch the activity with the URI for the location
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        // As far as I can tell we only need one temportary file
        // TODO: check if this is also the case if we want to store multiple URIs before uploading
        String imageFileName = "temporary_picture_taken";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // Designated directory for storing pictures belonging to this app.
        // We temporarily store the image on the user's phone to ensure we can get a full-quality image
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    // Callback method for ActivityCompat.requestPermissions
    // Used for further modularization of the code and to ensure the user is directly
    // redirected to the camera activity when granting permissions (instead of having to click the
    // button another time)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // length == 0 for possible cancellation
        if (grantResults.length == 0) {
            return;
        }
        // In case we also have other things requiring permissions (mic, location), using a switch
        switch (requestCode) {
            case CAM_REQUESTCODE: { // Camera permissions were requested

                // Camera Permission granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCameraActivity(); // we can launch the camera activity
                } else { // Denied
                    // user can no longer grant permissions from the app itself after denying once.
                    Toast.makeText(getApplicationContext(),
                            "Camera permissions are required to use this feature!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
