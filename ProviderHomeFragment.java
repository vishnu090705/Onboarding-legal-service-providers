package com.example.legislature.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.legislature.R;
import com.example.legislature.activities.AppointmentDetailsActivity;
import com.example.legislature.adapters.AppointmentAdapter;
import com.example.legislature.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProviderHomeFragment extends Fragment {

    private ListView listView;
    private AppointmentAdapter adapter;
    private List<Appointment> homeAppointmentsList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentProviderId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provider_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listViewProviderHome);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        homeAppointmentsList = new ArrayList<>();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(requireActivity(), "Provider not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentProviderId = user.getUid();

        // ✅ Simple adapter without click interface
        adapter = new AppointmentAdapter(requireActivity(), homeAppointmentsList);
        listView.setAdapter(adapter);

        // Handle item clicks to view appointment details
        listView.setOnItemClickListener((parent, v, position, id) -> {
            Appointment appt = homeAppointmentsList.get(position);
            Intent intent = new Intent(requireActivity(), AppointmentDetailsActivity.class);
            intent.putExtra("appointmentId", appt.getId());
            startActivity(intent);
        });

        // Load appointments
        loadProviderAppointments();
    }

    private void loadProviderAppointments() {
        db.collection("appointments")
                .whereEqualTo("providerId", currentProviderId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    homeAppointmentsList.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Appointment appt = doc.toObject(Appointment.class);
                        appt.setId(doc.getId());

                        // ✅ Show only pending appointments
                        if (appt.getStatus() != null && appt.getStatus().equalsIgnoreCase("pending")) {
                            homeAppointmentsList.add(appt);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireActivity(), "Failed to load appointments.", Toast.LENGTH_SHORT).show()
                );
    }
}
