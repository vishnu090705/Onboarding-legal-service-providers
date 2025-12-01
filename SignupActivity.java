package com.example.legislature.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.legislature.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etPhone, etSpecialization;
    private RadioGroup roleGroup;
    private Button btnSignup, btnLoginRedirect;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etSpecialization = findViewById(R.id.etSpecialization);
        roleGroup = findViewById(R.id.roleGroup);
        btnSignup = findViewById(R.id.btnSignup);
        btnLoginRedirect = findViewById(R.id.btnLoginRedirect);
        progressBar = findViewById(R.id.progressBar);

        etSpecialization.setVisibility(View.GONE);

        roleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioProvider) {
                etSpecialization.setVisibility(View.VISIBLE);
            } else {
                etSpecialization.setVisibility(View.GONE);
            }
        });

        btnSignup.setOnClickListener(v -> registerUser());
        btnLoginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String specialization = etSpecialization.getText().toString().trim();

        int selectedRoleId = roleGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = (selectedRoleId == R.id.radioProvider) ? "provider" : "user";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (role.equals("provider") && TextUtils.isEmpty(specialization)) {
            Toast.makeText(this, "Please enter your specialization", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSignup.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnSignup.setEnabled(true);

                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {

                        String uid = mAuth.getCurrentUser().getUid();

                        // ⭐ SHARED FIELDS (user + provider)
                        Map<String, Object> data = new HashMap<>();
                        data.put("uid", uid);
                        data.put("name", name);
                        data.put("email", email);
                        data.put("phone", phone);
                        data.put("role", role);
                        data.put("credits", 50);          // ⭐ Add 50 credits
                        data.put("lastLoginDate", "");     // ⭐ Empty until first login

                        if (role.equals("provider")) {
                            data.put("specialization", specialization);

                            db.collection("providers").document(uid).set(data);
                        }

                        // Save to USERS (main collection)
                        db.collection("users").document(uid)
                                .set(data)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, role + " account created! +50 Credits", Toast.LENGTH_SHORT).show();

                                    if (role.equals("provider")) {
                                        startActivity(new Intent(this, ProviderMainActivity.class));
                                    } else {
                                        startActivity(new Intent(this, MainActivityWithNav.class));
                                    }
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Signup failed: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
