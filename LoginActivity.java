package com.example.legislature.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.legislature.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignupRedirect;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignupRedirect = findViewById(R.id.btnSignupRedirect);
        progressBar = findViewById(R.id.progressBar);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            redirectToRole(currentUser.getUid());
        }

        btnLogin.setOnClickListener(v -> loginUser());
        btnSignupRedirect.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            redirectToRole(user.getUid());
                        }
                    } else {
                        Toast.makeText(this, "Login failed. Check your credentials.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ⭐ DAILY LOGIN CREDIT SYSTEM
    private void awardDailyCredits(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    String lastDate = doc.getString("lastLoginDate");
                    long credits = doc.getLong("credits") != null ? doc.getLong("credits") : 0;

                    String today = new java.text.SimpleDateFormat("yyyy-MM-dd")
                            .format(new java.util.Date());

                    if (lastDate == null || !lastDate.equals(today)) {

                        long newCredits = credits + 10;

                        Map<String, Object> update = new HashMap<>();
                        update.put("credits", newCredits);
                        update.put("lastLoginDate", today);

                        db.collection("users").document(uid).update(update)
                                .addOnSuccessListener(a ->
                                        Toast.makeText(this, "+10 Daily Login Credits!", Toast.LENGTH_SHORT).show()
                                );
                    }
                });
    }

    private void redirectToRole(@NonNull String uid) {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    progressBar.setVisibility(View.GONE);

                    if (doc.exists() && doc.contains("role")) {

                        // ⭐ Add daily login credits here
                        awardDailyCredits(uid);

                        String role = doc.getString("role");

                        if ("provider".equalsIgnoreCase(role)) {
                            startActivity(new Intent(LoginActivity.this, ProviderMainActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivityWithNav.class));
                        }
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, "User role not found. Please sign up again.", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Error checking role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                });
    }
}
