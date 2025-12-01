package com.example.legislature.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.legislature.R;
import com.example.legislature.models.Appointment;
import com.example.legislature.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppointmentDetailsActivity extends AppCompatActivity {

    private TextView tvClientName, tvClientEmail, tvDate, tvTime, tvStatus;
    private Button btnAccept, btnDecline;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String appointmentId;
    private String currentUserId;
    private boolean isProvider = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUserId = currentUser.getUid();

        tvClientName = findViewById(R.id.tvClientName);
        tvClientEmail = findViewById(R.id.tvClientEmail);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvStatus = findViewById(R.id.tvStatus);
        btnAccept = findViewById(R.id.btnAccept);
        btnDecline = findViewById(R.id.btnDecline);

        appointmentId = getIntent().getStringExtra("appointmentId");
        if (appointmentId == null || appointmentId.isEmpty()) {
            Toast.makeText(this, "Invalid appointment ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        detectUserType();
    }

    private void detectUserType() {
        db.collection("providers").document(currentUserId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        isProvider = true;
                    }
                    loadAppointmentDetails();
                })
                .addOnFailureListener(e -> {
                    isProvider = false;
                    loadAppointmentDetails();
                });
    }

    private void loadAppointmentDetails() {
        DocumentReference docRef = db.collection("appointments").document(appointmentId);
        docRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Appointment appointment = snapshot.toObject(Appointment.class);
                if (appointment != null) {
                    tvDate.setText("Date: " + appointment.getDate());
                    tvTime.setText("Time: " + appointment.getTime());
                    tvStatus.setText("Status: " + appointment.getStatus().toUpperCase());

                    loadClientInfo(appointment.getUserId());

                    if (isProvider && appointment.getStatus().equalsIgnoreCase("pending")) {
                        btnAccept.setVisibility(View.VISIBLE);
                        btnDecline.setVisibility(View.VISIBLE);

                        btnAccept.setOnClickListener(v -> updateAppointmentStatus("accepted"));
                        btnDecline.setOnClickListener(v -> updateAppointmentStatus("declined"));
                    } else {
                        btnAccept.setVisibility(View.GONE);
                        btnDecline.setVisibility(View.GONE);
                    }
                }
            } else {
                Toast.makeText(this, "Appointment not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load appointment.", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadClientInfo(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        User user = snapshot.toObject(User.class);
                        if (user != null) {
                            tvClientName.setText("Client: " + user.getName());
                            tvClientEmail.setText("Email: " + user.getEmail());
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load client info.", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateAppointmentStatus(String newStatus) {
        db.collection("appointments").document(appointmentId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {

                    // ⭐ GIVE PROVIDER +15 CREDITS
                    awardCreditsToProvider(currentUserId, 15);

                    Toast.makeText(this, "Appointment " + newStatus + " +15 Credits", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Status: " + newStatus.toUpperCase());
                    btnAccept.setVisibility(View.GONE);
                    btnDecline.setVisibility(View.GONE);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update appointment.", Toast.LENGTH_SHORT).show()
                );
    }

    // ⭐ CREDIT FUNCTION FOR PROVIDER
    private void awardCreditsToProvider(String uid, int amount) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        long credits = doc.getLong("credits") != null ? doc.getLong("credits") : 0;

                        db.collection("users").document(uid)
                                .update("credits", credits + amount);
                    }
                });
    }
}
