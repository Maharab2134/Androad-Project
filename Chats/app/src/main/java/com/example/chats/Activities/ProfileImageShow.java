package com.example.chats.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import com.bumptech.glide.Glide;
import com.example.chats.Models.User;
import com.example.chats.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileImageShow extends AppCompatActivity {

    private ImageView profileImageView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image_show);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        profileImageView = findViewById(R.id.profileImageView);

        fetchUserProfile();
    }

    private void fetchUserProfile() {
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            if (!user.getProfileImage().equals("No Image")) {
                                Glide.with(ProfileImageShow.this)
                                        .load(user.getProfileImage())
                                        .into(profileImageView);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Handle possible errors.
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileImageShow.this, MyProfileShow.class);
        Pair<View, String> pair = new Pair<>(profileImageView, "profileImage");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileImageShow.this, pair);
        startActivity(intent, optionsCompat.toBundle());
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ProfileImageShow.this, MyProfileShow.class);
            Pair<View, String> pair = new Pair<>(profileImageView, "profileImage");
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileImageShow.this, pair);
            startActivity(intent, optionsCompat.toBundle());
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_edit) {

            return true;
        } else if (item.getItemId() == R.id.action_share) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_profile, menu);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getIcon() != null) {
                item.getIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
            }
        }
        return true;
    }
}
