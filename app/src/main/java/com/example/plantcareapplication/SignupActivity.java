package com.example.plantcareapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.plantcareapplication.Models.User;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity"; // Tag for logging
    private EditText editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonSignup;
    private FirebaseAuth mAuth;
    private Handler handler;
    private ProgressDialog mProgressDialog;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference buyerReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the activity to always use night mode
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Signing Up...");
        mProgressDialog.setCancelable(false);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

//        firebaseDatabase.setPersistenceEnabled(true);
        firebaseDatabase = FirebaseDatabase.
                getInstance("https://plantcare-d3414-default-rtdb.firebaseio.com/");
        firebaseDatabase.getReference().keepSynced(true);

        // Set the buyer reference
        buyerReference = firebaseDatabase.getReference("MartUsers").child("Clients");

        editTextUsername = findViewById(R.id.username_input);
        editTextEmail = findViewById(R.id.emailinput);
        editTextPassword = findViewById(R.id.password_input);
        editTextConfirmPassword = findViewById(R.id.password_inputconfirm);
        buttonSignup = findViewById(R.id.sign_up_button);
        progressBar = findViewById(R.id.progress_bar);

        buttonSignup.setOnClickListener(view -> {
            String username = editTextUsername.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            signup(username, email, password, confirmPassword);
        });
        ImageView passwordVisibilityToggle = findViewById(R.id.password_visibility_toggle);
        passwordVisibilityToggle.setOnClickListener(v -> {
            // Toggle password visibility
            int inputType = editTextPassword.getInputType();
            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordVisibilityToggle.setImageResource(R.drawable.eyeon);
            } else {
                editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordVisibilityToggle.setImageResource(R.drawable.eyeoff);
            }
            // Move the cursor to the end of the password field
            editTextPassword.setSelection(editTextPassword.getText().length());
        });
    }

    private void signup(String username, String email, String password, String confirmPassword) {
        mProgressDialog.show();
        // Trim leading/trailing spaces from entered credentials
        String trimmedUsername = username.trim();
        String trimmedEmail = email.trim();
        String trimmedPassword = password.trim();
        String trimmedConfirmPassword = confirmPassword.trim();


        buyerReference.orderByChild("username").equalTo(trimmedUsername).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    Toast.makeText(SignupActivity.this,"Username already exists. Please choose a different username", Toast.LENGTH_SHORT);
                } else {
                    if (!trimmedPassword.equals(trimmedConfirmPassword)) {
                        Toast.makeText(SignupActivity.this,"Passwords do not match", Toast.LENGTH_SHORT);
                        return;
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    // Inside your signup process:
                    try {
                        progressBar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword)
                                .addOnCompleteListener(this, task1 -> {
                                    progressBar.setVisibility(View.GONE);

                                    if (task1.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String userId = user.getUid();

                                        User newUser = new User(userId, trimmedUsername, trimmedEmail, "clients");

                                        addToDatabase(newUser, buyerReference);

                                        mProgressDialog.dismiss();
                                        Toast.makeText(SignupActivity.this,"Signup successful. Welcome to GroceriesMart", Toast.LENGTH_SHORT);
                                        Toast.makeText(SignupActivity.this,"Kindly login again to complete the registration process", Toast.LENGTH_SHORT);

                                        // Delay for 2 seconds before navigating to the next page
                                        handler = new Handler();
                                        handler.postDelayed(() -> {
                                            Intent intent = new Intent(SignupActivity.this, GuestActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }, 2000); // Delay duration in milliseconds
                                    } else {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(SignupActivity.this,"Account with similar account present.", Toast.LENGTH_SHORT);
                                        Toast.makeText(SignupActivity.this,"Please try to login with existing credentials.", Toast.LENGTH_SHORT);
                                        Toast.makeText(SignupActivity.this,"Or use a different Email \uD83D\uDE42 ", Toast.LENGTH_SHORT);
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                mProgressDialog.dismiss();
                Toast.makeText(SignupActivity.this,"An error occurred. Please try again.", Toast.LENGTH_SHORT);
            }
        });
    }

    private void addToDatabase(User user, DatabaseReference databaseReference) {
        String userId = user.getUserId();

        // Create a Map to store the user data
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", user.getUserId());
        userMap.put("username", user.getUsername());
        userMap.put("email", user.getEmail());
        //userMap.put("phoneNumber", user.getPhone());


        // Save the user data to the database
        synchronized (databaseReference) {
            databaseReference.child(userId).setValue(userMap, (databaseError, reference) -> {
                if (databaseError == null) {
                    Toast.makeText(SignupActivity.this,"Data Added to the database successfully", Toast.LENGTH_SHORT);
                    //Toast.makeText(SignupActivity.this, "Data added to the database successfully.", Toast.LENGTH_SHORT).show();
                    //Log.d(TAG, "Data added to the database successfully."); // Log message to console
                } else {
                    Toast.makeText(SignupActivity.this,"Failed to add data to the database", Toast.LENGTH_SHORT);
                    //Toast.makeText(SignupActivity.this, "Failed to add data to the database.", Toast.LENGTH_SHORT).show();
                    //Log.d(TAG, "Failed to add data to the database: " + databaseError.getMessage()); // Log message to console
                }
            });
        }
    }
}