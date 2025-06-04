package com.lirans2341project.screen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.lirans2341project.R;
import com.lirans2341project.model.Item;
import com.lirans2341project.services.DatabaseService;
import com.lirans2341project.utils.ImageUtil;

public class EditItemActivity extends AppCompatActivity {

    private static final String TAG = "EditItemActivity";

    private ImageView itemImageView;
    private TextInputEditText nameEditText, typeEditText, sizeEditText, colorEditText, fabricEditText, priceEditText;
    private Button changeImageButton, saveButton;
    private DatabaseService databaseService;
    private Item currentItem;
    private String itemId;
    private ActivityResultLauncher<Intent> selectImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_item);

        // הוספת כפתור חזרה לאקשן בר
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("עריכת פריט");  // הוספת כותרת לעמוד
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קבלת ה-ID של הפריט מה-Intent
        itemId = getIntent().getStringExtra("ITEM_ID");
        if (itemId == null) {
            Toast.makeText(this, "שגיאה בטעינת הפריט", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupImagePicker();
        loadItem();
    }

    private void initializeViews() {
        databaseService = DatabaseService.getInstance();
        
        itemImageView = findViewById(R.id.item_image);
        nameEditText = findViewById(R.id.item_name);
        typeEditText = findViewById(R.id.item_type);
        sizeEditText = findViewById(R.id.item_size);
        colorEditText = findViewById(R.id.item_color);
        fabricEditText = findViewById(R.id.item_fabric);
        priceEditText = findViewById(R.id.item_price);
        changeImageButton = findViewById(R.id.change_image_button);
        saveButton = findViewById(R.id.save_button);

        changeImageButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void setupImagePicker() {
        selectImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        itemImageView.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading image", e);
                        Toast.makeText(this, "שגיאה בטעינת התמונה", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageLauncher.launch(intent);
    }

    private void loadItem() {
        databaseService.getItem(itemId, new DatabaseService.DatabaseCallback<Item>() {
            @Override
            public void onCompleted(Item item) {
                if (item == null) {
                    Toast.makeText(EditItemActivity.this, "הפריט לא נמצא", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                currentItem = item;
                populateFields(item);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(EditItemActivity.this, "שגיאה בטעינת הפריט", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateFields(Item item) {
        nameEditText.setText(item.getName());
        typeEditText.setText(item.getType());
        sizeEditText.setText(item.getSize());
        colorEditText.setText(item.getColor());
        fabricEditText.setText(item.getFabric());
        priceEditText.setText(String.valueOf(item.getPrice()));

        if (item.getPic() != null && !item.getPic().isEmpty()) {
            Glide.with(this)
                .load(ImageUtil.convertFrom64base(item.getPic()))
                .placeholder(R.drawable.placeholder_image)
                .into(itemImageView);
        }
    }

    private void saveChanges() {
        if (!validateInput()) {
            return;
        }

        String name = nameEditText.getText().toString();
        String type = typeEditText.getText().toString();
        String size = sizeEditText.getText().toString();
        String color = colorEditText.getText().toString();
        String fabric = fabricEditText.getText().toString();
        double price = Double.parseDouble(priceEditText.getText().toString());

        // עדכון הפריט הקיים
        currentItem.setName(name);
        currentItem.setType(type);
        currentItem.setSize(size);
        currentItem.setColor(color);
        currentItem.setFabric(fabric);
        currentItem.setPrice(price);
        
        // עדכון התמונה רק אם היא שונתה
        String imageBase64 = ImageUtil.convertTo64Base(itemImageView);
        if (imageBase64 != null) {
            currentItem.setPic(imageBase64);
        }

        // שמירת השינויים במסד הנתונים
        databaseService.updateItem(currentItem, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void result) {
                Toast.makeText(EditItemActivity.this, "הפריט עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(EditItemActivity.this, "שגיאה בעדכון הפריט", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput() {
        if (nameEditText.getText().toString().trim().isEmpty()) {
            nameEditText.setError("נא להזין שם פריט");
            return false;
        }
        if (priceEditText.getText().toString().trim().isEmpty()) {
            priceEditText.setError("נא להזין מחיר");
            return false;
        }
        try {
            Double.parseDouble(priceEditText.getText().toString());
        } catch (NumberFormatException e) {
            priceEditText.setError("נא להזין מחיר תקין");
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // יצירת התפריט
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
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
} 