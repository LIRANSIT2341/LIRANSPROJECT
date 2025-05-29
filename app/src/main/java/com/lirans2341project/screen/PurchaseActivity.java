package com.lirans2341project.screen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.lirans2341project.R;
import com.lirans2341project.model.Cart;
import com.lirans2341project.model.Item;
import com.lirans2341project.model.ShippingDetails;
import com.lirans2341project.services.DatabaseService;
import com.lirans2341project.utils.SharedPreferencesUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PurchaseActivity extends AppCompatActivity {

    private TextInputEditText fullNameInput, streetInput, cityInput, zipCodeInput, phoneInput, additionalInfoInput;
    private TextInputEditText creditCardInput, expiryInput, cvvInput;
    private TextView totalPriceText;
    private Button confirmPurchaseButton;
    private DatabaseService databaseService;

    Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        cart = SharedPreferencesUtil.getCart(this);

        if (cart.getItems().isEmpty()) {
            finish();
            return;
        }
        databaseService = DatabaseService.getInstance();

        // אתחול שדות הטופס
        initializeViews();

        totalPriceText.setText(String.format("סה״כ לתשלום: ₪%.2f", cart.getTotalPrice()));

        // הגדרת כפתור אישור רכישה
        confirmPurchaseButton.setOnClickListener(v -> {
            if (validateForm()) {
                processPurchase();
            }
        });
    }

    private void initializeViews() {
        fullNameInput = findViewById(R.id.fullNameInput);
        streetInput = findViewById(R.id.streetInput);
        cityInput = findViewById(R.id.cityInput);
        zipCodeInput = findViewById(R.id.zipCodeInput);
        phoneInput = findViewById(R.id.phoneInput);
        additionalInfoInput = findViewById(R.id.additionalInfoInput);
        creditCardInput = findViewById(R.id.creditCardInput);
        expiryInput = findViewById(R.id.expiryInput);
        cvvInput = findViewById(R.id.cvvInput);
        totalPriceText = findViewById(R.id.totalPriceText);
        confirmPurchaseButton = findViewById(R.id.confirmPurchaseButton);
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (TextUtils.isEmpty(fullNameInput.getText())) {
            fullNameInput.setError("שדה חובה");
            isValid = false;
        }

        if (TextUtils.isEmpty(streetInput.getText())) {
            streetInput.setError("שדה חובה");
            isValid = false;
        }

        if (TextUtils.isEmpty(cityInput.getText())) {
            cityInput.setError("שדה חובה");
            isValid = false;
        }

        if (TextUtils.isEmpty(zipCodeInput.getText())) {
            zipCodeInput.setError("שדה חובה");
            isValid = false;
        }

        if (TextUtils.isEmpty(phoneInput.getText())) {
            phoneInput.setError("שדה חובה");
            isValid = false;
        }

        String creditCard = creditCardInput.getText().toString();
        if (TextUtils.isEmpty(creditCard) || creditCard.length() != 16) {
            creditCardInput.setError("נא להזין 16 ספרות");
            isValid = false;
        }

        String expiry = expiryInput.getText().toString();
        if (TextUtils.isEmpty(expiry) || expiry.length() != 5) {
            expiryInput.setError("נא להזין בפורמט MM/YY");
            isValid = false;
        }

        String cvv = cvvInput.getText().toString();
        if (TextUtils.isEmpty(cvv) || cvv.length() != 3) {
            cvvInput.setError("נא להזין 3 ספרות");
            isValid = false;
        }

        return isValid;
    }

    private void processPurchase() {
        // בשלב זה היינו מבצעים את עיבוד התשלום מול שרת האשראי
        // לצורך הדוגמה נניח שהתשלום הצליח

        // שמירת פרטי המשלוח
        ShippingDetails shippingDetails = new ShippingDetails(
                fullNameInput.getText().toString(),
                streetInput.getText().toString(),
                cityInput.getText().toString(),
                zipCodeInput.getText().toString(),
                phoneInput.getText().toString(),
                additionalInfoInput.getText().toString()
        );


        List<Item> items = cart.getItems();

        int totalItems = items.size();
        AtomicInteger completedCount = new AtomicInteger(0);
        AtomicBoolean hasFailed = new AtomicBoolean(false);
        for (Item item : items) {
            item.setStatus(Item.PurchaseStatus.SOLD);
            databaseService.createNewItem(item, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void result) {
                    if (hasFailed.get()) return;// Skip if any failure already occurred

                    int finished = completedCount.incrementAndGet();
                    if (finished == totalItems) {
                        // All items processed successfully
                        SharedPreferencesUtil.clearCart(PurchaseActivity.this);
                        Intent intent = new Intent(PurchaseActivity.this, PurchaseConfirmationActivity.class);
                        intent.putExtra("ITEM_NAME", items.get(0).getName()); // Or pass list
                        intent.putExtra("SHIPPING_DETAILS", shippingDetails);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    if (hasFailed.compareAndSet(false, true)) {
                        Toast.makeText(PurchaseActivity.this, "שגיאה בעיבוד הרכישה", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
} 