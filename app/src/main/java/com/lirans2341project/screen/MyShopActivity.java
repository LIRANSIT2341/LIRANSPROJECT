package com.lirans2341project.screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.lirans2341project.Adapters.StoreItemsAdapter;
import com.lirans2341project.R;
import com.lirans2341project.model.Item;
import com.lirans2341project.services.DatabaseService;

import java.util.List;

public class MyShopActivity extends BaseActivity {

    private static final String TAG = "MyShopActivity";
    private RecyclerView itemsList;
    private StoreItemsAdapter itemsAdapter;
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;
    private FloatingActionButton fabAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_shop);

        // הוספת כפתור חזרה לאקשן בר
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("החנות שלי");  // הוספת כותרת לעמוד
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // אתחול של RecyclerView
        itemsList = findViewById(R.id.rv_items_list);
        itemsList.setLayoutManager(new GridLayoutManager(this, 2));

        // הגדרת OnItemClickListener
        StoreItemsAdapter.OnItemClickListener onItemClickListener = item -> {
            // בדיקה אם הפריט נמכר
            if (item.getStatus() == Item.PurchaseStatus.SOLD) {
                Toast.makeText(this, "לא ניתן לערוך פריט שנמכר", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // פתיחת מסך עריכת פריט
            Intent intent = new Intent(this, EditItemActivity.class);
            intent.putExtra("ITEM_ID", item.getId());
            startActivity(intent);
        };

        itemsAdapter = new StoreItemsAdapter(onItemClickListener, true);
        itemsList.setAdapter(itemsAdapter);

        // הגדרת כפתור הוספת פריט
        fabAddItem = findViewById(R.id.fab_add_item);
        fabAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddItemActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // טיפול בלחיצה על כפתור חזרה
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyItems();
    }

    private void loadMyItems() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        
        // טוען את רשימת האייטמים של המשתמש הנוכחי מה-Database
        databaseService.getItemList(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                // מסנן רק את הפריטים של המשתמש הנוכחי
                items.removeIf(item -> !item.getUserId().equals(currentUserId));
                if (items.isEmpty()) {
                    Toast.makeText(MyShopActivity.this, "אין לך פריטים בחנות", Toast.LENGTH_SHORT).show();
                }
                itemsAdapter.setItems(items);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get items list", e);
                Toast.makeText(MyShopActivity.this, "שגיאה בטעינת הפריטים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}