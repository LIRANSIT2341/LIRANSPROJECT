package com.lirans2341project.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lirans2341project.R;

import java.util.List;

public class ItemAdapter  <P> extends ArrayAdapter<Item> {

    Context context;
    List<Item> objects;

    public  ItemAdapter(Context context, int resource, int textViewResourceId, List<Item> objects) {
        super(context, resource, textViewResourceId, objects);

        this.context=context;
        this.objects=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.activity_item_row, parent, false);

        TextView tvname = (TextView)view.findViewById(R.id.tvItemName);

        TextView tvprice = (TextView)view.findViewById(R.id.tvItemprice);
        ImageView iv = view.findViewById(R.id.ivItempic);


        Item temp = objects.get(position);
        tvname.setText("שם:"+temp.getName());

        tvprice.setText("מחיר:"+temp.getPrice());
        HandleImage.DownLoadImage(iv,context,temp.getId());
        return view;
    }
}
