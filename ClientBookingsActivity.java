package com.example.legislature.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.legislature.R;
import com.example.legislature.adapters.AppointmentAdapter;
import com.example.legislature.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ClientBookingsActivity extends AppCompatActivity {

    private ListView listViewBookings;
    private AppointmentAdapter adapter;
    private List<Appointment> bookingList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_bookings);

        listViewBookings = findViewById(R.id.listViewClientBookings);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bookingList = new ArrayList<>();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUserId = user.getUid();

        adapter = new AppointmentAdapter(this, bookingList);
        listViewBookings.setAdapter(adapter);

        // Handle click → open AppointmentDetailsActivity
        listViewBookings.setOnItemClickListener((parent, view, position, id) -> {
            Appointment appointment = bookingList.get(position);
            Intent intent = new Intent(ClientBookingsActivity.this, AppointmentDetailsActivity.class);
            intent.putExtra("appointmentId", appointment.getId());
            startActivity(intent);
        });

        loadBookings();
    }

    private void loadBookings() {
        db.collection("appointments")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    bookingList.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Appointment appointment = doc.toObject(Appointment.class);
                        appointment.setId(doc.getId());
                        bookingList.add(appointment);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load bookings.", Toast.LENGTH_SHORT).show();
                });
    }
}
