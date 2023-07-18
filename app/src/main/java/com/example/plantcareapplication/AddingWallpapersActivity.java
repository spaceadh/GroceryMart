package com.example.plantcareapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.plantcareapplication.Models.Wallpaper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddingWallpapersActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button pickImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_wallpapers);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Adding Plant...");
        mProgressDialog.setCancelable(false);

        pickImageButton = findViewById(R.id.uploadButton);
        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            uploadImageToFirebase(selectedImageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {

        EditText titleEditText = findViewById(R.id.titleEditText);
        String plantTitle = titleEditText.getText().toString();

        EditText DescriptionEditText = findViewById(R.id.DescriptionEditText);
        String description =DescriptionEditText.getText().toString();

        EditText instructionsEditText=findViewById(R.id.instructionsEditText);
        String instructions = instructionsEditText.getText().toString();

        EditText wateringScheduleEditText=findViewById(R.id.wateringScheduleEditText);
        String watering = wateringScheduleEditText.getText().toString();

        EditText MaintenanceEditText=findViewById(R.id.MaintenanceEditText);
        String maintenance=MaintenanceEditText.getText().toString();

        if (imageUri != null && !plantTitle.isEmpty()
                && !description.isEmpty()
                && !instructions.isEmpty() && !watering.isEmpty() && !maintenance.isEmpty()) {
            // Create a reference to the "Wallpapers" folder in Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Plants")
                    .child(UUID.randomUUID().toString()); // Use a random UUID as the image name

            // Upload the image file to Firebase Storage
            UploadTask uploadTask = storageRef.putFile(imageUri);

            // Show a progress dialog or update UI to indicate the upload progress
            ProgressDialog progressDialog = new ProgressDialog(AddingWallpapersActivity.this);
            progressDialog.setTitle("Uploading Plants");
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mProgressDialog.show();

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    // Update the progress dialog with the current upload progress
                    int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress(progress);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                    // Get the download URL of the uploaded image
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            String imageUrl = downloadUrl.toString();
                            saveImageUrlToFirebase(imageUrl, plantTitle,description,
                                    instructions,watering,maintenance);

                            progressDialog.dismiss();
                            mProgressDialog.dismiss();// Dismiss the progress dialog
                            Toast.makeText(AddingWallpapersActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure to get the download URL
                            Toast.makeText(AddingWallpapersActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                            progressDialog.dismiss(); // Dismiss the progress dialog
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle the failure to upload the image
                    mProgressDialog.dismiss();
                    progressDialog.dismiss(); // Dismiss the progress dialog
                    Toast.makeText(AddingWallpapersActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveImageUrlToFirebase(String imageUrl, String plantTitle, String description,
                                        String instructions, String watering, String maintenance)
    {    DatabaseReference wallpapersRef = FirebaseDatabase.getInstance().getReference()
            .child("Plants");
        mProgressDialog.show();
        // Generate a unique key for the image
        String imageKey = wallpapersRef.push().getKey();

        // Create a wallpaper object with the image URL and title
        Wallpaper wallpaper = new Wallpaper(imageKey, imageUrl, plantTitle,description,instructions,watering,maintenance);

        // Save the wallpaper object under the generated key in the "Plants" node
        wallpapersRef.child(imageKey).setValue(wallpaper)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mProgressDialog.dismiss();
                        // Wallpaper details saved successfully
                        Toast.makeText(AddingWallpapersActivity.this, "Plant details saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        // Handle the failure to save the wallpaper details
                        Toast.makeText(AddingWallpapersActivity.this, "Failed to save Plant details", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}