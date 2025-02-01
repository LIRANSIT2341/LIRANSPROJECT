package com.lirans2341project.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.lirans2341project.R;
import com.lirans2341project.model.Item;
import com.lirans2341project.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;


/// Adapter for the items recycler view
/// @see RecyclerView
/// @see Item
/// @see R.layout#item_item
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        public void onItemClick(Item item);
    }

    private final List<Item> itemList;
    private final OnItemClickListener onItemClickListener;

    public ItemsAdapter(OnItemClickListener onItemClickListener) {
        itemList = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }

    public void addItems(@NonNull List<Item> items) {
        for (Item item : items) {
            addItem(item);
        }
    }

    public void addItem(@Nullable Item item) {
        if (item == null) return;
        itemList.add(item);
        notifyItemInserted(itemList.size() - 1);
    }

    public void setItems(@NonNull List<Item> items){
        this.itemList.clear();
        this.itemList.addAll(items);
        this.notifyDataSetChanged();
    }

    public List<Item> getItems() {
        return this.itemList;
    }

    /// create a view holder for the adapter
    /// @param parent the parent view group
    /// @param viewType the type of the view
    /// @return the view holder
    /// @see ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /// inflate the item_selected_item layout
        /// @see R.layout.item_selected_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item, parent, false);
        return new ViewHolder(view);
    }

    /// bind the view holder with the data
    /// @param holder the view holder
    /// @param position the position of the item in the list
    /// @see ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        if (item == null) return;

        holder.itemNameTextView.setText(item.getName());
        holder.itemImageView.setImageBitmap(ImageUtil.convertFrom64base(item.getPic()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(item);
            }
        });
    }

    /// get the number of items in the list
    /// @return the number of items in the list
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /// View holder for the items adapter
    /// @see RecyclerView.ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView itemNameTextView;
        public final ImageView itemImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.item_name_text_view);
           itemImageView = itemView.findViewById(R.id.item_image_view);
        }
    }
}
