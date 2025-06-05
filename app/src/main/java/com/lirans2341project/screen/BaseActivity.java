package com.lirans2341project.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lirans2341project.R;
import com.lirans2341project.model.User;
import com.lirans2341project.services.AuthenticationService;
import com.lirans2341project.utils.SharedPreferencesUtil;

public class BaseActivity extends AppCompatActivity {
    protected AuthenticationService authenticationService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    protected void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(4); // Add shadow under the action bar
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Show admin menu item only for admin users
        MenuItem adminItem = menu.findItem(R.id.action_admin);
        User user = SharedPreferencesUtil.getUser(this);
        if (user != null) {
            adminItem.setVisible(user.isAdmin());
        } else {
            adminItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.action_store) {
            startActivity(new Intent(this, StoreActivity.class));
            return true;
        } else if (itemId == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        } else if (itemId == R.id.action_profile) {
            startActivity(new Intent(this, UserProfileActivity.class));
            return true;
        } else if (itemId == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (itemId == R.id.action_admin) {
            startActivity(new Intent(this, AdminActivity.class));
            return true;
        } else if (itemId == R.id.action_logout) {
            // התנתקות מהמערכת
            authenticationService = AuthenticationService.getInstance();
            authenticationService.signOut();
            SharedPreferencesUtil.signOutUser(this);
            
            // מעבר למסך הנחיתה
            Intent intent = new Intent(this, LandingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
} 
