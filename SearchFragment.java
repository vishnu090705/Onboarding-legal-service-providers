package com.example.legislature.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.legislature.R;
import com.example.legislature.activities.BookAppointmentActivity;
import com.example.legislature.models.Provider;
import com.example.legislature.models.LegalGoods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private LinearLayout searchResultsContainer;
    private FirebaseFirestore db;

    private String role = "user"; // default

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        searchEditText = view.findViewById(R.id.searchEditText);
        searchResultsContainer = view.findViewById(R.id.searchResultsContainer);

        // ✅ Get logged-in user's role
        String uid = FirebaseAuth.getInstance().getUid();
        db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists() && doc.contains("role")) {
                role = doc.getString("role");
            }

            loadResults(""); // initial load based on role
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                loadResults(s.toString().trim());
            }
        });
    }

    private void loadResults(String queryText) {

        searchResultsContainer.removeAllViews();

        if (role.equalsIgnoreCase("user")) {
            loadProviders(queryText); // User searching lawyers
        } else {
            loadLegalGoods(queryText); // Provider searching goods/services
        }
    }

    // ✅ USER VIEW → Load Lawyers
    private void loadProviders(String text) {
        Query query = db.collection("providers");

        if (!text.isEmpty()) {
            query = query.whereGreaterThanOrEqualTo("specialization", text)
                    .whereLessThan("specialization", text + "\uf8ff");
        }

        query.get().addOnSuccessListener(snap -> {
            for (var doc : snap) {
                Provider p = doc.toObject(Provider.class);
                p.setId(doc.getId());
                addProviderCard(p);
            }
        });
    }

    private void addProviderCard(Provider provider) {
        View card = LayoutInflater.from(requireActivity())
                .inflate(R.layout.item_provider_card, searchResultsContainer, false);

        TextView name = card.findViewById(R.id.cardProviderName);
        TextView spec = card.findViewById(R.id.cardProviderSpecialization);

        name.setText(provider.getName());
        spec.setText("Specialization: " + provider.getSpecialization());

        card.setOnClickListener(v -> {
            Intent i = new Intent(requireActivity(), BookAppointmentActivity.class);
            i.putExtra("providerId", provider.getId());
            startActivity(i);
        });

        searchResultsContainer.addView(card);
    }

    // ✅ PROVIDER VIEW → Load Legal Goods
    private void loadLegalGoods(String text) {
        Query query = db.collection("legalGoods");

        if (!text.isEmpty()) {
            query = query.whereGreaterThanOrEqualTo("title", text)
                    .whereLessThan("title", text + "\uf8ff");
        }

        query.get().addOnSuccessListener(snap -> {
            for (var doc : snap) {
                LegalGoods goods = doc.toObject(LegalGoods.class);
                goods.setId(doc.getId());
                addGoodsCard(goods);
            }
        });
    }

    private void addGoodsCard(LegalGoods goods) {
        View card = LayoutInflater.from(requireActivity())
                .inflate(R.layout.item_legal_goods, searchResultsContainer, false);

        TextView name = card.findViewById(R.id.goodsTitle);
        TextView price = card.findViewById(R.id.goodsPrice);

        name.setText(goods.getTitle());
        price.setText("Price: ₹" + goods.getPrice());

        searchResultsContainer.addView(card);
    }
}
