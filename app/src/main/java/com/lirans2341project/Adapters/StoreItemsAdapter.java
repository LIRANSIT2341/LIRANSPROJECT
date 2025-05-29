package com.lirans2341project.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lirans2341project.R;
import com.lirans2341project.model.Item;
import com.lirans2341project.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class StoreItemsAdapter extends RecyclerView.Adapter<StoreItemsAdapter.StoreItemViewHolder> {

    private List<Item> items;
    private final OnItemClickListener listener;
    private final boolean isOwner;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public StoreItemsAdapter(OnItemClickListener listener, boolean isOwner) {
        this.items = new ArrayList<>();
        this.listener = listener;
        this.isOwner = isOwner;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoreItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_item_card, parent, false);
        return new StoreItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class StoreItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameTextView;
        private final TextView priceTextView;
        private final TextView statusTextView;

        public StoreItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.store_item_image);
            nameTextView = itemView.findViewById(R.id.store_item_name);
            priceTextView = itemView.findViewById(R.id.store_item_price);
            statusTextView = itemView.findViewById(R.id.store_item_status);

            // הגדרת לחיצה על הכרטיס
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Item item = items.get(position);
                    listener.onItemClick(item);
                }
            });
        }

        public void bind(Item item) {
            // טעינת תמונת המוצר
            if (item.getPic() != null && !item.getPic().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(ImageUtil.convertFrom64base(item.getPic()))
                        .placeholder(R.drawable.placeholder_image)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.placeholder_image);
            }

            // הצגת פרטי המוצר
            nameTextView.setText(item.getName());
            priceTextView.setText(String.format("₪%.2f", item.getPrice()));
            
            // הצגת סטטוס המוצר
            if (item.getStatus() == Item.PurchaseStatus.SOLD) {
                statusTextView.setText("נמכר");
                itemView.setAlpha(0.7f); // מעמעם מוצרים שנמכרו
            } else {
                statusTextView.setText("זמין");
                itemView.setAlpha(1.0f);
            }
        }
    }
} 