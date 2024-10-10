package com.example.chats.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class MyProfileShow extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView profileImageView, cameraIcon, editNameIcon, editAboutIcon;
    private EditText nameTextInputEditText, aboutTextInputEditText;
    private TextView phoneTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_show);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setElevation(8f);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        profileImageView = findViewById(R.id.profileImageView);
        nameTextInputEditText = findViewById(R.id.nameTextInputEditText);
        aboutTextInputEditText = findViewById(R.id.aboutTextInputEditText);
        phoneTextView = findViewById(R.id.phoneTextInputEditText);

        editNameIcon = findViewById(R.id.editNameIcon);
        editAboutIcon = findViewById(R.id.editAboutIcon);
        cameraIcon = findViewById(R.id.cameraIcon);



        editNameIcon.setOnClickListener(v -> toggleEditing(nameTextInputEditText, "name"));
        editAboutIcon.setOnClickListener(v -> toggleEditing(aboutTextInputEditText, "about"));

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileShow.this, ProfileImageShow.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Pair<View, String> pair = new Pair<>(profileImageView, "profileImage");
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MyProfileShow.this, pair);
                startActivity(intent, optionsCompat.toBundle());
            }
        });



        cameraIcon.setOnClickListener(v -> showImagePickerDialog());

        fetchUserProfile();

    }


    private void toggleEditing(EditText editText, String field) {
        boolean isEditing = false;
        if (editText.isEnabled()) {
            // Save changes
            saveUserProfile(field, editText.getText().toString());
            editText.setEnabled(false);
        } else {
            // Enable editing
            editText.setEnabled(true);
            editText.requestFocus();
            isEditing = true;
        }
    }

    private void saveUserProfile(String field, String value) {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        userRef.child(field).setValue(value);
    }

    private void fetchUserProfile() {
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            nameTextInputEditText.setText(user.getName());
                            aboutTextInputEditText.setText(user.getAbout());
                            phoneTextView.setText(user.getPhoneNumber());

                            if (!user.getProfileImage().equals("No Image")) {
                                if (!MyProfileShow.this.isFinishing() && !MyProfileShow.this.isDestroyed()) {
                                    Glide.with(MyProfileShow.this)
                                            .load(user.getProfileImage())
                                            .into(profileImageView);
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }

    private void showImagePickerDialog() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 75);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 75) {
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            } else if (requestCode == 100) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                uploadBitmapToFirebase(bitmap);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        String uid = FirebaseAuth.getInstance().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_images").child(uid + ".jpg");

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    updateProfileImageUri(uri.toString());
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(MyProfileShow.this, "Image upload failed.", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadBitmapToFirebase(Bitmap bitmap) {
        String uid = FirebaseAuth.getInstance().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_images").child(uid + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            updateProfileImageUri(uri.toString());
        })).addOnFailureListener(e -> {
            Toast.makeText(MyProfileShow.this, "Image upload failed.", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateProfileImageUri(String uri) {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        userRef.child("profileImage").setValue(uri);
        Glide.with(MyProfileShow.this).load(uri).into(profileImageView);
        Toast.makeText(MyProfileShow.this, "Profile image updated.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyProfileShow.this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
