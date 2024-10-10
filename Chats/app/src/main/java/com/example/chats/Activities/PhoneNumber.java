package com.example.chats.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryCode = binding.ccpPhone.selectedCountryCode();
                String phoneNumber = binding.phoneBox.getText().toString().trim();
                String fullPhoneNumber = countryCode + phoneNumber;

                if (phoneNumber.isEmpty()) {
                    Toast.makeText(PhoneNumber.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed to OTP activity
                    Intent intent = new Intent(PhoneNumber.this, OTP.class);
                    intent.putExtra("phoneNumber", fullPhoneNumber);
                    startActivity(intent);
                }
            }
        });
    }
}
