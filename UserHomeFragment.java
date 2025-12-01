package com.example.legislature.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.legislature.R;
import com.example.legislature.models.LegalGoods;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UserHomeFragment extends Fragment {

    private LinearLayout goodsContainer;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        goodsContainer = view.findViewById(R.id.goodsContainer);
        db = FirebaseFirestore.getInstance();

        loadLegalGoods();
    }

    // ✅ Load a few legal goods / information snippets
    private void loadLegalGoods() {
        db.collection("legalGoods")
                .limit(5)
                .get()
                .addOnSuccessListener(snap -> {
                    goodsContainer.removeAllViews();

                    if (snap.isEmpty()) {
                        TextView emptyMsg = new TextView(requireActivity());
                        emptyMsg.setText("No legal resources available yet.");
                        emptyMsg.setTextSize(16);
                        goodsContainer.addView(emptyMsg);
                        return;
                    }

                    for (QueryDocumentSnapshot doc : snap) {
                        LegalGoods item = doc.toObject(LegalGoods.class);
                        addGoodsCard(item);
                    }
                });
    }

    private void addGoodsCard(LegalGoods goods) {
        View card = LayoutInflater.from(requireActivity())
                .inflate(R.layout.item_legal_goods, goodsContainer, false);

        TextView title = card.findViewById(R.id.goodsTitle);
        TextView description = card.findViewById(R.id.goodsCategory);
        TextView price = card.findViewById(R.id.goodsPrice);

        title.setText(goods.getTitle());
        description.setText(goods.getDescription());
        price.setText(goods.getPrice() == 0 ? "Free" : "Price: ₹" + goods.getPrice());

        goodsContainer.addView(card);
    }
}
