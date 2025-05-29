package com.lirans2341project.screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lirans2341project.Adapters.StoreItemsAdapter;
import com.lirans2341project.R;
import com.lirans2341project.model.Item;
import com.lirans2341project.services.DatabaseService;

import java.util.List;

public class StoreActivity extends AppCompatActivity {

    private static final String TAG = "StoreActivity";
    private RecyclerView storeItemsList;
    private StoreItemsAdapter itemsAdapter;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_store);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();

        // אתחול של RecyclerView
        storeItemsList = findViewById(R.id.rv_store_items);
        // שימוש ב-GridLayoutManager להצגת הפריטים ברשת של 2 עמודות
        storeItemsList.setLayoutManager(new GridLayoutManager(this, 2));

        // הגדרת OnItemClickListener
        StoreItemsAdapter.OnItemClickListener onItemClickListener = item -> {
            // מעבר לדף פרטי המוצר
            Intent intent = new Intent(this, ItemDetailsActivity.class);
            intent.putExtra("ITEM_ID", item.getId());
            startActivity(intent);
        };

        itemsAdapter = new StoreItemsAdapter(onItemClickListener, false);
        storeItemsList.setAdapter(itemsAdapter);

        // טעינת הפריטים
        loadItems();
    }

    private void loadItems() {
        databaseService.getItemList(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                itemsAdapter.setItems(items);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(StoreActivity.this, "שגיאה בטעינת הפריטים", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 