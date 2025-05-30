package com.lirans2341project.screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.lirans2341project.Adapters.ItemsAdapter;
import com.lirans2341project.R;
import com.lirans2341project.model.Item;
import com.lirans2341project.services.DatabaseService;

import java.util.List;

public class MyShopActivity extends AppCompatActivity {

    private static final String TAG = "MyShopActivity";
    private RecyclerView itemsList;
    private ItemsAdapter itemsAdapter;
    private DatabaseService databaseService;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_shop);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // אתחול של RecyclerView
        itemsList = findViewById(R.id.rv_items_list);
        itemsList.setLayoutManager(new LinearLayoutManager(this));

        // הגדרת OnItemClickListener
        ItemsAdapter.OnItemClickListener onItemClickListener = item -> {
            // טיפול בלחיצה על פריט
            Log.d(TAG, "Item clicked: " + item);
            Intent intent = new Intent(this, ItemDetailsActivity.class);
            intent.putExtra("ITEM_ID", item.getId());
            startActivity(intent);
        };

        itemsAdapter = new ItemsAdapter(onItemClickListener);
        itemsList.setAdapter(itemsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        String currentUserId = mAuth.getCurrentUser().getUid();
        
        // טוען את רשימת האייטמים של המשתמש הנוכחי מה-Database
        databaseService.getItemList(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                items.removeIf(item -> !item.getUserId().equals(currentUserId));
                itemsAdapter.setItems(items);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get items list", e);
            }
        });
    }
}