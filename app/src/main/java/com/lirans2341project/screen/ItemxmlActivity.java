package com.lirans2341project.screen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.lirans2341project.R;
import com.lirans2341project.model.Item;

public class ItemxmlActivity extends AppCompatActivity {

    private TextView tvItemName, tvItemType, tvItemSize, tvItemColor, tvItemFabric, tvItemPrice, tvItemStatus;
    private Button btnAddToCart;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemxml);

        // קבלת הנתונים שהעברנו ב-Intent
        item = (Item) getIntent().getSerializableExtra("ITEM");

        // אתחול רכיבי ה-UI
        tvItemName = findViewById(R.id.tvItemName);
        tvItemType = findViewById(R.id.tvItemType);
        tvItemSize = findViewById(R.id.tvItemSize);
        tvItemColor = findViewById(R.id.tvItemColor);
        tvItemFabric = findViewById(R.id.tvItemFabric);
        tvItemPrice = findViewById(R.id.tvItemPrice);
//        tvItemStatus = findViewById(R.id.tvItemStatus);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // הצגת המידע על המוצר
        tvItemName.setText(item.getName());
        tvItemType.setText(item.getType());
        tvItemSize.setText(item.getSize());
        tvItemColor.setText(item.getColor());
        tvItemFabric.setText(item.getFabric());
        tvItemPrice.setText("מחיר: " + item.getPrice());

        // כפתור הוספה לסל
        btnAddToCart.setOnClickListener(v -> {
            // כאן תוכל להוסיף את המוצר לסל
        });
    }
}