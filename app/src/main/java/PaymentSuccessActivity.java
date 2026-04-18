package com.example.tatwa10;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
        View container = findViewById(R.id.successContainer);

// 🔥 Animate (fade + scale)
        container.animate()
                .alpha(1f)
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(300)
                .withEndAction(() ->
                        container.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(200)
                )
                .start();
        TextView message = findViewById(R.id.textMessage);

        String doctor = getIntent().getStringExtra("doctorName");

        if (doctor == null) doctor = "Doctor";

        message.setText("Appointment with Dr " + doctor + " completed");

        // ⏱ auto close after 2 sec
        new Handler().postDelayed(() -> finish(), 2000);
    }
}
