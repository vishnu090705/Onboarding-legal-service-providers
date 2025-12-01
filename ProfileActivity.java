package com.example.legislature.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.legislature.R;
import com.example.legislature.models.Provider;
import com.example.legislature.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etSpecialization;
    private Button btnSave;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    

    private String uid;
    private boolean isProvider = false; // flag for role detection

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        uid = currentUser.getUid();

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etSpecialization = findViewById(R.id.etSpecialization);
        btnSave = findViewById(R.id.btnSave);

        detectUserType();
    }

    private void detectUserType() {
        // First check if user is provider
        db.collection("providers").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                isProvider = true;
                loadProviderProfile();
            } else {
                loadUserProfile();
            }
        }).addOnFailureListener(e -> loadUserProfile());
    }

    private void loadUserProfile() {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        User user = snapshot.toObject(User.class);
                        if (user != null) {
                            etName.setText(user.getName());
                            etEmail.setText(user.getEmail());
                            etPhone.setText(user.getPhone());
                            etSpecialization.setVisibility(EditText.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load user profile", Toast.LENGTH_SHORT).show());

        btnSave.setOnClickListener(v -> saveUserProfile());
    }

    private void loadProviderProfile() {
        db.collection("providers").document(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Provider provider = snapshot.toObject(Provider.class);
                        if (provider != null) {
                            etName.setText(provider.getName());
                            etEmail.setText(provider.getEmail());
                            etPhone.setText(provider.getPhone());
                            etSpecialization.setText(provider.getSpecialization());
                            etSpecialization.setVisibility(EditText.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load provider profile", Toast.LENGTH_SHORT).show());

        btnSave.setOnClickListener(v -> saveProviderProfile());
    }

    private void saveUserProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Name and Email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(uid, name, email, phone);
        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show());
    }

    private void saveProviderProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String specialization = etSpecialization.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Name and Email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Provider provider = new Provider(uid, name, specialization, phone, email);
        db.collection("providers").document(uid)
                .set(provider)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show());
    }
}
