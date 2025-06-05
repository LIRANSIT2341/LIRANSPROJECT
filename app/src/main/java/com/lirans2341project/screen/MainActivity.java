package com.lirans2341project.screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lirans2341project.R;
import com.lirans2341project.model.User;
import com.lirans2341project.utils.SharedPreferencesUtil;

/// Main activity for the app
/// This activity is the main activity that is shown when the user is signed in
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Button btnToAdmin, btnGoToMyShop, btnStore, btnMainCart;

    /// the current user instance
    /// NOTE:
    /// THIS IS THE INSTANCE WHEN THE USER LOGS IN
    /// THIS IS NOT THE REALTIME USER INSTANCE
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        /// set the layout for the activity
        setContentView(R.layout.activity_main3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// Check if user is signed in or not and redirect to LandingActivity if not signed in

        /// get the user data from shared preferences
        user = SharedPreferencesUtil.getUser(MainActivity.this);
        Log.d(TAG, "User: " + user);

        /// get the views
        btnStore = findViewById(R.id.btn_main_store);
        btnToAdmin = findViewById(R.id.btn_main_to_admin);
        btnGoToMyShop = findViewById(R.id.btnGoToMyShop);
        btnMainCart = findViewById(R.id.btn_main_cart);

        /// set the click listeners
        btnStore.setOnClickListener(this);
        btnToAdmin.setOnClickListener(this);
        btnGoToMyShop.setOnClickListener(this);
        btnMainCart.setOnClickListener(this);

        // Show admin card if user is admin
        View adminCard = findViewById(R.id.admin_card);
        if (user != null && user.isAdmin()) {
            adminCard.setVisibility(View.VISIBLE);
        } else {
            adminCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnStore.getId()) {
            Log.d(TAG, "Store button clicked");
            Intent storeIntent = new Intent(MainActivity.this, StoreActivity.class);
            startActivity(storeIntent);
            return;
        }
        if (v.getId() == btnGoToMyShop.getId()) {
            Log.d(TAG, "Go to My Shop button clicked");
            Intent goToMyShopIntent = new Intent(MainActivity.this, MyShopActivity.class);
            startActivity(goToMyShopIntent);
            return;
        }
        if (v.getId() == btnMainCart.getId()) {
            Log.d(TAG, "Cart button clicked");
            Intent cartIntent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(cartIntent);
            return;
        }
        if (v.getId() == btnToAdmin.getId()) {
            Log.d(TAG, "To admin button clicked");
            Intent adminIntent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(adminIntent);
            return;
        }
    }

}