package com.example.tatwa10;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity {

    private int doctorId;
    private int patientId;

    private String doctor;
    private String date;
    private String time;
    private String patientName;
    private int appointmentId;


    private static final String CHANNEL_ID = "payment_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        doctorId = getIntent().getIntExtra("doctorId", 0);
        patientId = getIntent().getIntExtra("patientId", 0);

        doctor = getIntent().getStringExtra("doctorName");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        patientName = getIntent().getStringExtra("patientName");
        appointmentId = getIntent().getIntExtra("appointmentId", -1);
        MaterialCardView cardWish = findViewById(R.id.cardWish);
        MaterialCardView cardOMT = findViewById(R.id.cardOMT);

        cardWish.setOnClickListener(v -> showCardDialog("Wish"));
        cardOMT.setOnClickListener(v -> showCardDialog("OMT"));
    }

    private void showCardDialog(String method) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_card_payment, null);

        EditText etCardNumber = view.findViewById(R.id.etCardNumber);
        EditText etCardName = view.findViewById(R.id.etCardName);
        EditText etExpiry = view.findViewById(R.id.etExpiry);
        EditText etCVV = view.findViewById(R.id.etCVV);

        new AlertDialog.Builder(this)
                .setTitle(method + " Payment")
                .setView(view)
                .setPositiveButton("Pay", (dialog, which) -> {

                    String card = etCardNumber.getText().toString();
                    String name = etCardName.getText().toString();
                    String expiry = etExpiry.getText().toString();
                    String cvv = etCVV.getText().toString();

                    if (card.isEmpty() || name.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    pay(method);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveAppointment(String method) {

        Toast.makeText(this, "Processing Payment...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {

            try {

                JSONObject json = new JSONObject();

                json.put("doctorId", doctorId);     // ⭐ IMPORTANT
                json.put("patientId", patientId);   // ⭐ IMPORTANT

                json.put("doctorName", doctor);       // optional
                json.put("patientName", patientName); // optional

                json.put("date", date);
                json.put("time", time);
                json.put("status", "requested");
                json.put("paymentMethod", method);

                Log.d("CREATE_APPOINTMENT", json.toString());

                ApiService.createAppointment(json.toString());

                runOnUiThread(() -> {

                    showNotification();

                    new AlertDialog.Builder(this)
                            .setTitle("Payment Successful")
                            .setMessage("Your payment is successful. Appointment requested.")
                            .setPositiveButton("OK", (dialog, which) -> finish())
                            .setCancelable(false)
                            .show();
                });

            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(() ->
                        Toast.makeText(this, "Error creating appointment", Toast.LENGTH_LONG).show()
                );
            }

        }).start();
    }

    private void showNotification() {

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Payment Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Notification for successful payments");

            manager.createNotificationChannel(channel);
        }

        // 🔥 DYNAMIC DOCTOR NAME
        String message = "Appointment with Dr " + doctor + " completed";

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Payment Successful") // ✅ TITLE
                        .setContentText(message)              // ✅ MESSAGE
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        manager.notify(1, builder.build());
    }
    private void pay(String method) {

        Toast.makeText(this, "Processing Payment...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {

            String response = ApiService.payAppointment(appointmentId);

            Log.d("PAYMENT_API", "Response = " + response);

            runOnUiThread(() -> {

                if (response != null) {

                    showNotification();

                    Intent intent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
                    intent.putExtra("doctorName", doctor);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
                }

            });

        }).start();
    }
}
