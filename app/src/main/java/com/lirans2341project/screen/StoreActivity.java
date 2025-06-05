package com.lirans2341project.screen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.lirans2341project.Adapters.StoreItemsAdapter;
import com.lirans2341project.R;
import com.lirans2341project.model.Item;
import com.lirans2341project.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends BaseActivity {

    private static final String TAG = "StoreActivity";
    private RecyclerView storeItemsList;
    private StoreItemsAdapter itemsAdapter;
    private DatabaseService databaseService;
    private List<Item> allItems; // רשימת כל הפריטים לפני הסינון

    // רכיבי הסינון
    private AutoCompleteTextView itemTypeFilter;
    private TextInputEditText minPriceFilter;
    private TextInputEditText maxPriceFilter;
    private MaterialButton applyFilterButton;
    private MaterialButton clearFilterButton;

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
        allItems = new ArrayList<>();

        initializeViews();
        setupFilters();
        loadItems();
    }

    private void initializeViews() {
        // אתחול RecyclerView
        storeItemsList = findViewById(R.id.rv_store_items);
        storeItemsList.setLayoutManager(new GridLayoutManager(this, 2));

        // הגדרת OnItemClickListener
        StoreItemsAdapter.OnItemClickListener onItemClickListener = item -> {
            Intent intent = new Intent(this, ItemDetailsActivity.class);
            intent.putExtra("ITEM_ID", item.getId());
            startActivity(intent);
        };

        itemsAdapter = new StoreItemsAdapter(onItemClickListener, false);
        storeItemsList.setAdapter(itemsAdapter);

        // אתחול רכיבי הסינון
        itemTypeFilter = findViewById(R.id.item_type_filter);
        minPriceFilter = findViewById(R.id.min_price_filter);
        maxPriceFilter = findViewById(R.id.max_price_filter);
        applyFilterButton = findViewById(R.id.apply_filter_button);
        clearFilterButton = findViewById(R.id.clear_filter_button);
    }

    private void setupFilters() {
        // הגדרת מתאם לסוגי פריטים
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.item_types, android.R.layout.simple_dropdown_item_1line);
        itemTypeFilter.setAdapter(typeAdapter);

        // הגדרת כפתור החלת הסינון
        applyFilterButton.setOnClickListener(v -> applyFilters());

        // הגדרת כפתור ניקוי הסינון
        clearFilterButton.setOnClickListener(v -> clearFilters());
    }

    private void loadItems() {
        databaseService.getItemList(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                allItems = items;
                itemsAdapter.setItems(items);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(StoreActivity.this, "שגיאה בטעינת הפריטים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFilters() {
        // ניקוי שדות הסינון
        itemTypeFilter.setText("");
        minPriceFilter.setText("");
        maxPriceFilter.setText("");

        // הצגת כל הפריטים מחדש
        itemsAdapter.setItems(allItems);
        Toast.makeText(this, "הסינון נוקה", Toast.LENGTH_SHORT).show();
    }

    private void applyFilters() {
        String selectedType = itemTypeFilter.getText().toString();
        String minPriceStr = minPriceFilter.getText().toString();
        String maxPriceStr = maxPriceFilter.getText().toString();

        // יצירת רשימה מסוננת
        List<Item> filteredItems = new ArrayList<>(allItems);

        // סינון לפי סוג פריט
        if (!TextUtils.isEmpty(selectedType)) {
            filteredItems.removeIf(item -> !item.getType().equals(selectedType));
        }

        // סינון לפי טווח מחירים
        try {
            if (!TextUtils.isEmpty(minPriceStr)) {
                double minPrice = Double.parseDouble(minPriceStr);
                filteredItems.removeIf(item -> item.getPrice() < minPrice);
            }
            if (!TextUtils.isEmpty(maxPriceStr)) {
                double maxPrice = Double.parseDouble(maxPriceStr);
                filteredItems.removeIf(item -> item.getPrice() > maxPrice);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "נא להזין מחיר תקין", Toast.LENGTH_SHORT).show();
            return;
        }

        // עדכון הרשימה המוצגת
        itemsAdapter.setItems(filteredItems);

        if (filteredItems.isEmpty()) {
            Toast.makeText(this, "לא נמצאו פריטים התואמים לסינון", Toast.LENGTH_SHORT).show();
        }
    }
} 