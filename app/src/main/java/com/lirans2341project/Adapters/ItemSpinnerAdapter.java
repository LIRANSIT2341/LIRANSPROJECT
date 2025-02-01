package com.lirans2341project.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lirans2341project.R;
import com.lirans2341project.model.Item;
import com.lirans2341project.utils.ImageUtil;

import java.util.List;

/// Adapter for the item spinner
/// @see ArrayAdapter
/// @see Item
/// @see R.layout#item_item
public class ItemSpinnerAdapter extends ArrayAdapter<Item> {

    /// inflater for the layout
    /// @see LayoutInflater
    private final LayoutInflater inflater;
    /// resource for the layout
    /// @see android.R.layout#simple_spinner_item
    private final int resource;

    /// constructor
    /// @param context the context
    /// @param resource the resource
    /// @param objects the list of objects
    public ItemSpinnerAdapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    /// get the view for the spinner
    /// @param position the position of the item in the list
    /// @param convertView the view to convert
    /// @param parent the parent view group
    /// @return the view for the spinner
    /// @see View
    /// @see ViewGroup
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
        }
        Item item = getItem(position);
        if (item == null) {
            return convertView;
        }

        /// Set the text for the TextView
        TextView textView = convertView.findViewById(android.R.id.text1);

        textView.setText(item.getName());

        return convertView;
    }

    /// get the view for the dropdown
    /// @param position the position of the item in the list
    /// @param convertView the view to convert
    /// @param parent the parent view group
    /// @return the view for the dropdown
    /// @see View
    /// @see ViewGroup
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        /// inflate the item_selected_item layout
        if (convertView == null) {
            /// @see R.layout#item_item
            convertView = inflater.inflate(R.layout.item_item, parent, false);
        }
        /// get the item at the position
        Item item = getItem(position);

        /// return the view if the food is null
        if (item == null) return convertView;

        /// Set the text for the TextView
        TextView textView = convertView.findViewById(R.id.item_name_text_view);
        textView.setText(item.getName());

        /// Set the image for the ImageView
        ImageView imageView = convertView.findViewById(R.id.item_image_view);
        /// convert the image from base64 to bitmap
        /// @see ImageUtil#convertFrom64base(String)
        //imageView.setImageBitmap(ImageUtil.convertFrom64base(item.getImageBase64()));


        return convertView;
    }
}
