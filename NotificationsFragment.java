package com.example.legislature.fragments;

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
import com.example.legislature.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class NotificationsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    private LinearLayout notificationsContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationsContainer = view.findViewById(R.id.notificationsContainer);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(requireActivity(), "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        userId = user.getUid();
        detectUserType();
    }

    private void detectUserType() {
        // Check if user is a Provider
        db.collection("providers").document(userId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        // ✅ Provider account
                        loadProviderNotifications();
                    } else {
                        // ✅ Normal User account
                        loadUserNotifications();
                    }
                });
    }

    // ✅ USER: Show ALL of their appointment status updates
    private void loadUserNotifications() {
        notificationsContainer.removeAllViews();

        db.collection("appointments")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(snap -> {
                    for (QueryDocumentSnapshot d : snap) {
                        Appointment a = d.toObject(Appointment.class);

                        TextView row = new TextView(requireActivity());
                        row.setTextSize(16);
                        row.setPadding(10, 18, 10, 18);

                        row.setText(a.getDate() + " • " + a.getTime() + " • " + a.getStatus().toUpperCase());

                        notificationsContainer.addView(row);
                    }
                });
    }

    // ✅ PROVIDER: Show only accepted/declined (not pending)
    private void loadProviderNotifications() {
        notificationsContainer.removeAllViews();

        db.collection("appointments")
                .whereEqualTo("providerId", userId)
                .get()
                .addOnSuccessListener(snap -> {
                    for (QueryDocumentSnapshot d : snap) {
                        Appointment a = d.toObject(Appointment.class);

                        if (a.getStatus().equalsIgnoreCase("pending")) continue;

                        TextView row = new TextView(requireActivity());
                        row.setTextSize(16);
                        row.setPadding(10, 18, 10, 18);

                        row.setText(a.getDate() + " • " + a.getTime() + " • " + a.getStatus().toUpperCase());

                        notificationsContainer.addView(row);
                    }
                });
    }
}
