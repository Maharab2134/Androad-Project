package com.example.chats.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.chats.Models.User;
import com.example.chats.R;
import com.example.chats.databinding.ActivityOthersProfileShowBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class OthersProfileShow extends AppCompatActivity {

    ActivityOthersProfileShowBinding binding;
    String receiverPhoneNumber, receiverName, about, receiverUid;
    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOthersProfileShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        receiverName = getIntent().getStringExtra("name");
        receiverUid = getIntent().getStringExtra("uid");
        String profile = getIntent().getStringExtra("profile");
        receiverPhoneNumber = getIntent().getStringExtra("phoneNumber");

        binding.name.setText(receiverName);
        binding.receiverPhoneNumber.setText(receiverPhoneNumber);

        binding.audioCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (receiverPhoneNumber != null && !receiverPhoneNumber.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + receiverPhoneNumber));
                    if (checkSelfPermission(android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(intent);
                    } else {
                        requestPermissions(new String[]{android.Manifest.permission.CALL_PHONE}, 1);
                    }
                } else {
                }
            }
        });

        if (profile != null && !profile.isEmpty()) {
            Glide.with(OthersProfileShow.this)
                    .load(profile)
                    .placeholder(R.drawable.avatar)
                    .into(binding.profileImageView);
        }

        fetchUserProfile();
    }


    private void fetchUserProfile() {
        if (receiverUid != null) {
            FirebaseDatabase.getInstance().getReference().child("users").child(receiverUid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                // Set the "About" text
                                if (user.getAbout() != null) {
                                    binding.aboutText.setText(user.getAbout());
                                } else {
                                    binding.aboutText.setText("About add ..");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Handle possible errors
                        }
                    });
        }


        if (receiverName != null && !receiverName.isEmpty()) {
            getSupportActionBar().setTitle("");
        } else {
            getSupportActionBar().setTitle("");
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OthersProfileShow.this, Chat.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.others_profile, menu);
        return true;
    }

}
