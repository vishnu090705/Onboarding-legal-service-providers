package com.example.legislature.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.legislature.R;
import com.example.legislature.models.Provider;

import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {

    private List<Provider> providerList;
    private OnProviderClickListener listener;

    public interface OnProviderClickListener {
        void onProviderClick(Provider provider);
    }

    public ProviderAdapter(List<Provider> providerList, OnProviderClickListener listener) {
        this.providerList = providerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_provider_card, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        Provider provider = providerList.get(position);

        holder.name.setText(provider.getName());
        holder.specialization.setText("Specialization: " + provider.getSpecialization());

        holder.itemView.setOnClickListener(v -> listener.onProviderClick(provider));
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public static class ProviderViewHolder extends RecyclerView.ViewHolder {
        TextView name, specialization;

        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cardProviderName);
            specialization = itemView.findViewById(R.id.cardProviderSpecialization);
        }
    }
}
