package com.example.legislature.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.legislature.R;
import com.example.legislature.activities.AppointmentDetailsActivity;
import com.example.legislature.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProviderNotificationsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String providerId;

    private LinearLayout providerNotificationsContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provider_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        providerNotificationsContainer = view.findViewById(R.id.providerNotificationsContainer);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(requireActivity(), "Provider not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        providerId = user.getUid();
        loadNonPendingAppointments();
    }

    private void loadNonPendingAppointments() {
        providerNotificationsContainer.removeAllViews();

        db.collection("appointments")
                .whereEqualTo("providerId", providerId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Appointment appt = doc.toObject(Appointment.class);
                        appt.setId(doc.getId());

                        // ✅ Only show accepted or declined
                        if (appt.getStatus().equalsIgnoreCase("pending")) continue;

                        TextView card = new TextView(requireActivity());
                        card.setTextSize(18);
                        card.setPadding(20, 20, 20, 20);
                        card.setBackgroundResource(R.drawable.card_background); // Optional card shape

                        card.setText(
                                "Client ID: " + appt.getUserId() + "\n" +
                                        "Date: " + appt.getDate() + "\n" +
                                        "Time: " + appt.getTime() + "\n" +
                                        "Status: " + appt.getStatus().toUpperCase()
                        );

                        card.setOnClickListener(v -> {
                            Intent i = new Intent(requireActivity(), AppointmentDetailsActivity.class);
                            i.putExtra("appointmentId", appt.getId());
                            startActivity(i);
                        });

                        providerNotificationsContainer.addView(card);
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireActivity(), "Failed to load notifications.", Toast.LENGTH_SHORT).show()
                );
    }
}
