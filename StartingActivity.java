package com.example.tatwa10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.card.MaterialCardView;

public class StartingActivity extends AppCompatActivity {

    private MaterialCardView buttonDoctor;
    private MaterialCardView buttonPatient;

    public static final String SHARED_PREFERENCES = "shared_prefs";
    public static final String KEY_DOCTOR_NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        // Initialize buttons
        buttonDoctor = findViewById(R.id.button_start_doctor);
        buttonPatient = findViewById(R.id.button_start_patient);

        // Doctor button click
        buttonDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(StartingActivity.this, DoctorVerificationActivity.class);
            startActivity(intent);
        });

        // Patient button click
        buttonPatient.setOnClickListener(v -> {
            Intent intent = new Intent(StartingActivity.this, PhoneVerificationActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if doctor already logged in (LOCAL storage)
        SharedPreferences sharedPreferences =
                getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

        String name = sharedPreferences.getString(KEY_DOCTOR_NAME, null);

        if (name != null) {
            Intent intent = new Intent(this, DoctorMainActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
        }
    }
}