package com.example.legislature.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.legislature.R;
import com.example.legislature.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class BookAppointmentActivity extends AppCompatActivity {

    private EditText etDate, etTime;
    private Button btnBook;
    private String providerId;
    private String userId;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        providerId = getIntent().getStringExtra("providerId");

        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        btnBook = findViewById(R.id.btnBookAppointment);

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
        btnBook.setOnClickListener(v -> bookAppointment());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    month = month + 1;
                    etDate.setText(day + "/" + month + "/" + year);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePicker = new TimePickerDialog(this,
                (view, hour, minute) -> {
                    String selectedTime = String.format("%02d:%02d", hour, minute);
                    etTime.setText(selectedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);

        timePicker.show();
    }

    private void bookAppointment() {
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        Appointment appt = new Appointment(
                userId,
                providerId,
                date,
                time,
                "pending"
        );

        db.collection("appointments")
                .add(appt)
                .addOnSuccessListener(doc -> {

                    // ⭐ GIVE USER +15 CREDITS
                    awardCreditsToUser(userId, 15);

                    Toast.makeText(this, "Appointment Request Sent! +15 Credits", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to book appointment", Toast.LENGTH_SHORT).show()
                );
    }

    // ⭐ CREDIT FUNCTION FOR USER
    private void awardCreditsToUser(String uid, int amount) {
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
