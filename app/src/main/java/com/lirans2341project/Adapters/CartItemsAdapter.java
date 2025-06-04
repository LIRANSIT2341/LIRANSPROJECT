package com.lirans2341project.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lirans2341project.R;
import com.lirans2341project.model.Item;
import com.lirans2341project.utils.ImageUtil;

import java.util.List;

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.CartItemViewHolder> {

    private final Context context;
    private final List<Item> items;
    private final CartItemListener cartItemListener;

    public interface CartItemListener {
        public void decrease(Item item);
    }


    public CartItemsAdapter(Context context, List<Item> items, CartItemListener cartItemListener) {
        this.context = context;
        this.items = items;
        this.cartItemListener = cartItemListener;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_card, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView itemImage;
        private final TextView itemName;
        private final TextView itemPrice;
        private final ImageButton decreaseButton;
        private final TextView totalPrice;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.cart_item_image);
            itemName = itemView.findViewById(R.id.cart_item_name);
            itemPrice = itemView.findViewById(R.id.cart_item_price);
            decreaseButton = itemView.findViewById(R.id.decrease_button);
            totalPrice = itemView.findViewById(R.id.total_price);
        }

        public void bind(Item item) {
            // טעינת תמונת המוצר
            if (item.getPic() != null && !item.getPic().isEmpty()) {
                Glide.with(context)
                        .load(ImageUtil.convertFrom64base(item.getPic()))
                        .placeholder(R.drawable.placeholder_image)
                        .into(itemImage);
            } else {
                itemImage.setImageResource(R.drawable.placeholder_image);
            }

            // הצגת פרטי המוצר
            itemName.setText(item.getName());
            itemPrice.setText(String.format("₪%.2f", item.getPrice()));
            totalPrice.setText(String.format("₪%.2f", item.getPrice()));

            decreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartItemListener.decrease(item);
                }
            });
        }
    }
} 