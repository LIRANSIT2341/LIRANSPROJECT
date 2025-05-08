package com.lirans2341project.screen;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lirans2341project.R;
import com.lirans2341project.Adapters.ItemsAdapter;
import com.lirans2341project.model.Cart;
import com.lirans2341project.model.Item;
import com.lirans2341project.services.DatabaseService;

public class CartDetailActivity extends AppCompatActivity {

    private DatabaseService databaseService;
    private TextView tvCartTitle;
    private TextView tvCartDescription;
    private RecyclerView rvCartItems;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();

        tvCartTitle = findViewById(R.id.tv_cart_title);
        tvCartDescription = findViewById(R.id.tv_cart_description);
        rvCartItems = findViewById(R.id.rv_cart_items);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.cart_detail_progress_bar);

        String cart_id = getIntent().getStringExtra("cart_id");
        if (cart_id == null) {
            Toast.makeText(this, "Cart ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        rvCartItems.setVisibility(View.GONE);
        databaseService.getCart(cart_id, new DatabaseService.DatabaseCallback<Cart>() {
            @Override
            public void onCompleted(Cart cart) {
                progressBar.setVisibility(View.GONE);
                rvCartItems.setVisibility(View.VISIBLE);
                if (cart == null) {
                    Toast.makeText(CartDetailActivity.this, "Cart not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                setCartView(cart);
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void setCartView(Cart cart) {
        tvCartTitle.setText(cart.getTitle());
        tvCartDescription.setText("Total items: " + cart.getItems().size());
       ItemsAdapter foodsAdapter = new ItemsAdapter(new ItemsAdapter.OnItemClickListener() {
           @Override
           public void onItemClick(Item item) {

           }
       });
        foodsAdapter.addItems(cart.getItems());
        rvCartItems.setAdapter(foodsAdapter);
    }
}