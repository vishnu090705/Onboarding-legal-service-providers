package com.example.legislature.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.legislature.R;
import com.example.legislature.models.LegalGoods;

import java.util.List;
import java.util.Locale;

public class LegalGoodsAdapter extends RecyclerView.Adapter<LegalGoodsAdapter.GoodsViewHolder> {

    private Context context;
    private List<LegalGoods> goodsList;

    public LegalGoodsAdapter(Context context, List<LegalGoods> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
    }

    @NonNull
    @Override
    public GoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_legal_goods, parent, false);
        return new GoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsViewHolder holder, int position) {
        LegalGoods goods = goodsList.get(position);

        holder.tvTitle.setText(goods.getTitle());
        holder.tvCategory.setText("Category: " + goods.getCategory());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "Price: ₹%.2f", goods.getPrice()));

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "Selected: " + goods.getTitle(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

    public static class GoodsViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvPrice;

        public GoodsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.goodsTitle);
            tvCategory = itemView.findViewById(R.id.goodsCategory);
            tvPrice = itemView.findViewById(R.id.goodsPrice);
        }
    }
}
