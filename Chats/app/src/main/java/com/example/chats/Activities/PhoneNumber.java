package com.example.chats.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chats.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;

public class PhoneNumber extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            Intent intent = new Intent(PhoneNumber.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryCode = binding.ccpPhone.selectedCountryCode();
                String phoneNumber = binding.phoneBox.getText().toString().trim();
                String fullPhoneNumber = countryCode + phoneNumber;

                Intent intent = new Intent(PhoneNumber.this, OTP.class);
                intent.putExtra("phoneNumber", fullPhoneNumber);
                startActivity(intent);
            }
        });
    }
}
