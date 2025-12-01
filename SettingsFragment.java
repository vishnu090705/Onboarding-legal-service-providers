package com.example.legislature.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.legislature.R;
import com.example.legislature.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class SettingsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    private ImageView profileImage;
    private TextView tvName, tvEmail, tvPhone, tvRole, tvCredits;
    private LinearLayout btnLogout;

    private Uri imageUri;
    private String currentCollection = "users";

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (getActivity() != null &&
                        result.getResultCode() == getActivity().RESULT_OK &&
                        result.getData() != null) {

                    imageUri = result.getData().getData();
                    profileImage.setImageURI(imageUri);
                    uploadImageToFirebase();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        profileImage = view.findViewById(R.id.profileImage);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvRole = view.findViewById(R.id.tvRole);
        tvCredits = view.findViewById(R.id.tvCredits);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Load profile (users/providers) + listen for credits from "users"
        loadProfile();
        loadCredits();

        profileImage.setOnClickListener(v -> openImagePicker());
        btnLogout.setOnClickListener(v -> confirmLogout());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void uploadImageToFirebase() {
        if (imageUri == null || mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();
        String fileName = "profile_images/" + uid + "_" + UUID.randomUUID() + ".jpg";
        StorageReference imgRef = storageRef.child(fileName);

        imgRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveImageUrlToFirestore(uri.toString());
                    Toast.makeText(requireActivity(), "Profile picture updated!", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e ->
                        Toast.makeText(requireActivity(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        // Save into the current profile collection (users or providers)
        db.collection(currentCollection).document(uid)
                .update("imageUrl", imageUrl)
                .addOnSuccessListener(aVoid ->
                        Glide.with(requireActivity()).load(imageUrl).circleCrop().into(profileImage))
                .addOnFailureListener(e ->
                        Toast.makeText(requireActivity(), "Failed to save image URL", Toast.LENGTH_SHORT).show());
    }

    // ⭐ LOAD PROFILE DETAILS
    private void loadProfile() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        currentCollection = "users";

                        String roleFromDoc = doc.getString("role");
                        String roleLabel = roleFromDoc != null && roleFromDoc.equalsIgnoreCase("provider")
                                ? "Provider" : "User";

                        setProfileData(
                                doc.getString("name"),
                                doc.getString("email"),
                                doc.getString("phone"),
                                roleLabel,
                                doc.getString("imageUrl")
                        );
                    } else {
                        // Fallback: if no users doc, try providers collection
                        db.collection("providers").document(uid).get()
                                .addOnSuccessListener(providerDoc -> {
                                    if (providerDoc.exists()) {
                                        currentCollection = "providers";

                                        setProfileData(
                                                providerDoc.getString("name"),
                                                providerDoc.getString("email"),
                                                providerDoc.getString("phone"),
                                                "Provider",
                                                providerDoc.getString("imageUrl")
                                        );
                                    }
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireActivity(), "Failed to load profile", Toast.LENGTH_SHORT).show());
    }

    private void setProfileData(String name, String email, String phone, String role, String imageUrl) {
        tvName.setText(name != null ? name : "Unknown");
        tvEmail.setText(email != null ? email : "N/A");
        tvPhone.setText(phone != null ? phone : "N/A");
        tvRole.setText("Role: " + role);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(requireActivity()).load(imageUrl).circleCrop().into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.baseline_account_circle_24);
        }
    }

    // ⭐ LOAD CREDITS IN REAL-TIME FROM "users" COLLECTION
    private void loadCredits() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .addSnapshotListener((doc, error) -> {
                    if (error != null || doc == null || !doc.exists()) {
                        return;
                    }

                    // Use Number to support both Long and Double types
                    Number creditsNumber = (Number) doc.get("credits");
                    long credits = creditsNumber != null ? creditsNumber.longValue() : 0L;

                    tvCredits.setText("Credits: " + credits);
                    tvCredits.setVisibility(View.VISIBLE);
                });
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    mAuth.signOut();
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
