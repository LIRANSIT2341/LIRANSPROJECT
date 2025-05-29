package com.lirans2341project.screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.lirans2341project.R;
import com.lirans2341project.model.Item;
import com.lirans2341project.model.User;
import com.lirans2341project.services.DatabaseService;
import com.lirans2341project.utils.ImageUtil;
import com.lirans2341project.utils.SharedPreferencesUtil;

public class ItemDetailsActivity extends AppCompatActivity {

    private Item currentItem;
    private User seller;
    DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        databaseService = DatabaseService.getInstance();

        String itemId = getIntent().getStringExtra("ITEM_ID");
        if (itemId == null) {
            Toast.makeText(this, "שגיאה בטעינת הפריט", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadItemDetails(itemId);
    }

    private void loadItemDetails(String itemId) {

        databaseService.getItem(itemId, new DatabaseService.DatabaseCallback<Item>() {
            @Override
            public void onCompleted(Item item) {
                currentItem = item;
                displayItemDetails();
                loadSellerDetails(item.getUserId());
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void loadSellerDetails(String sellerId) {
        databaseService.getUser(sellerId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                seller = user;
                displaySellerDetails();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ItemDetailsActivity.this, "שגיאה בטעינת פרטי המוכר", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayItemDetails() {
        ImageView itemImage = findViewById(R.id.item_image);
        TextView nameText = findViewById(R.id.item_name);
        TextView priceText = findViewById(R.id.item_price);
        TextView typeText = findViewById(R.id.item_type);
        TextView sizeText = findViewById(R.id.item_size);
        TextView colorText = findViewById(R.id.item_color);
        TextView fabricText = findViewById(R.id.item_fabric);
        TextView statusText = findViewById(R.id.item_status);
        MaterialButton addToCartButton = findViewById(R.id.add_to_cart_button);
        MaterialButton buyNowButton = findViewById(R.id.buy_now_button);

        // טעינת תמונת המוצר
        if (currentItem.getPic() == null || currentItem.getPic().isEmpty()) {
            itemImage.setImageResource(R.drawable.placeholder_image);
        } else {
            Glide.with(this)
                    .load(ImageUtil.convertFrom64base(currentItem.getPic()))
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(itemImage);
        }

        // הצגת פרטי המוצר
        nameText.setText(currentItem.getName());
        priceText.setText(String.format("₪%.2f", currentItem.getPrice()));
        typeText.setText(currentItem.getType());
        sizeText.setText(currentItem.getSize());
        colorText.setText(currentItem.getColor());
        fabricText.setText(currentItem.getFabric());

        // הצגת סטטוס המוצר והתאמת כפתורים
        if (currentItem.getStatus() == Item.PurchaseStatus.SOLD) {
            statusText.setText("נמכר");
            addToCartButton.setEnabled(false);
            buyNowButton.setEnabled(false);
        } else {
            statusText.setText("זמין");
            setupPurchaseButtons();
        }
    }

    private void displaySellerDetails() {
        TextView sellerNameText = findViewById(R.id.seller_name);
        MaterialButton contactButton = findViewById(R.id.contact_seller_button);

        sellerNameText.setText(seller.getFullName());
        
        contactButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + seller.getEmail()));
            intent.putExtra(Intent.EXTRA_SUBJECT, "שאלה לגבי " + currentItem.getName());
            startActivity(Intent.createChooser(intent, "שליחת מייל"));
        });
    }

    private void setupPurchaseButtons() {
        MaterialButton addToCartButton = findViewById(R.id.add_to_cart_button);
        MaterialButton buyNowButton = findViewById(R.id.buy_now_button);

        addToCartButton.setOnClickListener(v -> {
            SharedPreferencesUtil.addItemToCart(ItemDetailsActivity.this, currentItem);
            Toast.makeText(this, "המוצר נוסף לסל הקניות", Toast.LENGTH_SHORT).show();
        });

        buyNowButton.setOnClickListener(v -> {
            SharedPreferencesUtil.addItemToCart(ItemDetailsActivity.this, currentItem);
            Intent intent = new Intent(this, PurchaseActivity.class);
            startActivity(intent);
        });
    }
}