package com.lirans2341project.screen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lirans2341project.R;
import com.lirans2341project.model.ShippingDetails;

public class PurchaseConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_confirmation);

        // קבלת פרטי הרכישה מה-Intent
        String itemName = getIntent().getStringExtra("ITEM_NAME");
        ShippingDetails shippingDetails = (ShippingDetails) getIntent().getSerializableExtra("SHIPPING_DETAILS");

        // הצגת פרטי הרכישה
        TextView itemNameText = findViewById(R.id.itemNameText);
        TextView shippingDetailsText = findViewById(R.id.shippingDetailsText);
        Button returnToStoreButton = findViewById(R.id.returnToStoreButton);

        itemNameText.setText(itemName);
        
        if (shippingDetails != null) {
            String shippingText = String.format("%s\n%s\n%s %s\nטלפון: %s",
                    shippingDetails.getFullName(),
                    shippingDetails.getStreet(),
                    shippingDetails.getCity(),
                    shippingDetails.getZipCode(),
                    shippingDetails.getPhoneNumber());
            shippingDetailsText.setText(shippingText);
        }

        // חזרה לחנות הראשית
        returnToStoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, StoreActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
} 