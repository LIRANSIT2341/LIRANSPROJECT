package com.lirans2341project.screen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lirans2341project.R;
import com.lirans2341project.Adapters.CartItemsAdapter;
import com.lirans2341project.model.Item;
import com.lirans2341project.services.DatabaseService;
import com.lirans2341project.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartItemsList;
    private CartItemsAdapter cartItemsAdapter;
    private TextView totalPriceText;
    private Button checkoutButton;
    private DatabaseService databaseService;
    private List<Item> cartItems;
    private double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // הגדרת כפתור חזרה ב-ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("עגלת קניות");
        }

        // אתחול הרכיבים
        initializeViews();

        // טיפול בפעולת הוספה לסל
        handleAddToCartAction();

        // טעינת פריטי הסל
        loadCartItems();
    }

    private void initializeViews() {
        cartItemsList = findViewById(R.id.cart_items_list);
        totalPriceText = findViewById(R.id.total_price);
        checkoutButton = findViewById(R.id.checkout_button);
        databaseService = DatabaseService.getInstance();
        cartItems = new ArrayList<>();

        // הגדרת ה-RecyclerView
        cartItemsList.setLayoutManager(new LinearLayoutManager(this));
        cartItemsAdapter = new CartItemsAdapter(this, cartItems, new CartItemsAdapter.CartItemListener() {
            @Override
            public void decrease(Item item) {
                removeFromCart(item);
            }
        });
        cartItemsList.setAdapter(cartItemsAdapter);

        // הגדרת כפתור התשלום
        checkoutButton.setOnClickListener(v -> proceedToCheckout());
    }

    private void handleAddToCartAction() {
        String action = getIntent().getStringExtra("ACTION");
        if ("ADD_TO_CART".equals(action)) {
            String itemId = getIntent().getStringExtra("ITEM_ID");
            if (itemId != null) {
                databaseService.getItem(itemId, new DatabaseService.DatabaseCallback<Item>() {
                    @Override
                    public void onCompleted(Item item) {
                        addToCart(item);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(CartActivity.this, "שגיאה בהוספת פריט לסל", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void loadCartItems() {
        cartItems.clear();
        cartItems.addAll(SharedPreferencesUtil.getCart(this).getItems());
        updateTotalPrice();
        cartItemsAdapter.notifyDataSetChanged();
    }

    private void addToCart(Item item) {
        if (!cartItems.contains(item)) {
            cartItems.add(item);
            SharedPreferencesUtil.addItemToCart(this, item);
            updateTotalPrice();
            cartItemsAdapter.notifyDataSetChanged();
            Toast.makeText(this, "הפריט נוסף לסל", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "הפריט כבר קיים בסל", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeFromCart(Item item) {
        cartItems.remove(item);
        SharedPreferencesUtil.removeItemFromCart(this, item);
        updateTotalPrice();
        cartItemsAdapter.notifyDataSetChanged();
        Toast.makeText(this, "הפריט הוסר מהסל", Toast.LENGTH_SHORT).show();
    }

    private void updateTotalPrice() {
        totalPrice = 0.0;
        for (Item cartItem : cartItems) {
            totalPrice += cartItem.getPrice();
        }
        totalPriceText.setText(String.format("₪%.2f", totalPrice));
        checkoutButton.setEnabled(totalPrice > 0);
    }

    private void proceedToCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "הסל ריק", Toast.LENGTH_SHORT).show();
            return;
        }

        // מעבר למסך התשלום
        Intent intent = new Intent(this, PurchaseActivity.class);
        startActivity(intent);
    }
} 