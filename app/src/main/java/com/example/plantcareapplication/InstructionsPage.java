package com.example.plantcareapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class InstructionsPage extends AppCompatActivity {

    private ImageView wallpaperImageView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView priceTextView;
    private TextView wateringTextView,MaintenanceTextView;
    private Button moreInfoButton;
    private DatabaseReference wallpaperRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions_page);

        wallpaperImageView = findViewById(R.id.wallpaperImageView);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        priceTextView=findViewById(R.id.priceTextView);

        moreInfoButton = findViewById(R.id.moreInfoButton);

        wallpaperRef = FirebaseDatabase.getInstance().getReference().child("Groceries");

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("WallpaperId")) {
            String wallpaperId = intent.getStringExtra("WallpaperId");
            loadWallpaperDetails(wallpaperId);
        } else {
            Toast.makeText(this, "No wallpaper ID found", Toast.LENGTH_SHORT).show();
        }

        moreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBrowserForMoreInformation();
            }
        });
    }

    private void loadWallpaperDetails(String wallpaperId) {
        Query query = wallpaperRef.child(wallpaperId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    String title = dataSnapshot.child("imageTitle").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String price =dataSnapshot.child("price").getValue(String.class);
                    Glide.with(InstructionsPage.this)
                            .load(imageUrl)
                            .into(wallpaperImageView);

                    titleTextView.setText(title);
                    descriptionTextView.setText("Price Description is : "+description);
                    priceTextView.setText("Price is :"+price);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(InstructionsPage.this, "Failed to load wallpaper details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openBrowserForMoreInformation() {
        String url = "https://www.paypal.com/myaccount/"; // Replace with your desired URL
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}