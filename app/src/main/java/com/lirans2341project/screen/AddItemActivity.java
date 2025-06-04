package com.lirans2341project.screen;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.lirans2341project.R;
import com.lirans2341project.Adapters.ImageSourceAdapter;
import com.lirans2341project.model.Item;
import com.lirans2341project.services.AuthenticationService;
import com.lirans2341project.services.DatabaseService;
import com.lirans2341project.utils.ImageUtil;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity implements View.OnClickListener {

    /// tag for logging
    private static final String TAG = "AddItemActivity";

    private TextInputEditText itemNameEditText, itemPriceEditText;
    private AutoCompleteTextView itemTypeAutoComplete, itemSizeAutoComplete, itemColorAutoComplete, itemFabricAutoComplete;
    private Button addItemButton;
    private ImageView itemImageView;
    private DatabaseService databaseService;

    /// Activity result launcher for selecting image from gallery
    private ActivityResultLauncher<Intent> selectImageLauncher;
    /// Activity result launcher for capturing image from camera
    private ActivityResultLauncher<Intent> captureImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        /// set the layout for the activity
        setContentView(R.layout.activity_add_item2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// request permission for the camera and storage
        ImageUtil.requestPermission(this);

        /// get the instance of the database service
        databaseService = DatabaseService.getInstance();

        /// get the views
        initializeViews();
        setupDropdownLists();
        setupImagePickers();

        // הוספת כפתור חזרה לאקשן בר
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initializeViews() {
        itemNameEditText = findViewById(R.id.item_name);
        itemPriceEditText = findViewById(R.id.item_price);
        itemTypeAutoComplete = findViewById(R.id.item_type);
        itemSizeAutoComplete = findViewById(R.id.item_size);
        itemColorAutoComplete = findViewById(R.id.item_color);
        itemFabricAutoComplete = findViewById(R.id.item_fabric);
        addItemButton = findViewById(R.id.add_item_button);
        itemImageView = findViewById(R.id.item_image);

        /// set the tag for the image view
        /// to check if the image was changed from app:srcCompat="@drawable/image"
        itemImageView.setTag(R.drawable.image);

        /// set the on click listeners
        itemImageView.setOnClickListener(this);
        addItemButton.setOnClickListener(this);
    }

    private void setupDropdownLists() {
        // הגדרת מתאמים לרשימות הנפתחות
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.item_types, android.R.layout.simple_dropdown_item_1line);
        itemTypeAutoComplete.setAdapter(typeAdapter);
        itemTypeAutoComplete.setThreshold(1);

        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(this,
                R.array.item_sizes, android.R.layout.simple_dropdown_item_1line);
        itemSizeAutoComplete.setAdapter(sizeAdapter);
        itemSizeAutoComplete.setThreshold(1);

        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this,
                R.array.item_colors, android.R.layout.simple_dropdown_item_1line);
        itemColorAutoComplete.setAdapter(colorAdapter);
        itemColorAutoComplete.setThreshold(1);

        ArrayAdapter<CharSequence> fabricAdapter = ArrayAdapter.createFromResource(this,
                R.array.item_fabrics, android.R.layout.simple_dropdown_item_1line);
        itemFabricAutoComplete.setAdapter(fabricAdapter);
        itemFabricAutoComplete.setThreshold(1);

        // הגדרת אירועי מיקוד לפתיחת הרשימות
        setupAutoCompleteClickListener(itemTypeAutoComplete);
        setupAutoCompleteClickListener(itemSizeAutoComplete);
        setupAutoCompleteClickListener(itemColorAutoComplete);
        setupAutoCompleteClickListener(itemFabricAutoComplete);
    }

    private void setupAutoCompleteClickListener(AutoCompleteTextView autoCompleteTextView) {
        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && autoCompleteTextView.getText().toString().isEmpty()) {
                autoCompleteTextView.showDropDown();
            }
        });
    }

    private void setupImagePickers() {
        /// register the activity result launcher for selecting image from gallery
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        itemImageView.setImageURI(selectedImage);
                        /// set the tag for the image view to null
                        itemImageView.setTag(null);
                    }
                });

        /// register the activity result launcher for capturing image from camera
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        itemImageView.setImageBitmap(bitmap);
                        /// set the tag for the image view to null
                        itemImageView.setTag(null);
                    }
                }
        );
    }

    /// show the dialog to select the image source
    /// gallery or camera
    /// @see AlertDialog
    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("בחר מקור תמונה");

        final ArrayList<Map.Entry<String, Integer>> options = new ArrayList<>();
        options.add(new AbstractMap.SimpleEntry<>("גלריה", R.drawable.gallery_thumbnail));
        options.add(new AbstractMap.SimpleEntry<>("מצלמה", R.drawable.photo_camera));

        ImageSourceAdapter adapter = new ImageSourceAdapter(this, options);

        builder.setAdapter(adapter, (DialogInterface dialog, int index) -> {
            if (index == 0) {
                selectImageFromGallery();
            } else if (index == 1) {
                captureImageFromCamera();
            }
        });

        builder.show();
    }

    /// add the item to the database
    /// @see Item
    private void addItemToDatabase() {
        /// get the values from the input fields
        String name = itemNameEditText.getText().toString();
        String type = itemTypeAutoComplete.getText().toString();
        String size = itemSizeAutoComplete.getText().toString();
        String color = itemColorAutoComplete.getText().toString();
        String fabric = itemFabricAutoComplete.getText().toString();
        String priceText = itemPriceEditText.getText().toString();

        /// validate the input
        /// stop if the input is not valid
        if (!isValid(name, type, size, color, fabric, priceText, itemImageView)) return;

        String imageBase64 = ImageUtil.convertTo64Base(itemImageView);
        /// convert the price to double
        double price = Double.parseDouble(priceText);

        /// generate a new id for the food
        String id = databaseService.generateItemId();

        String userId = AuthenticationService.getInstance().getCurrentUserId();

        Log.d(TAG, "Adding item to database");
        Log.d(TAG, "ID: " + id);
        Log.d(TAG, "Name: " + name);
        Log.d(TAG, "Type: " + type);
        Log.d(TAG, "Size: " + size);
        Log.d(TAG, "Color: " + color);
        Log.d(TAG, "Fabric: " + fabric);
        Log.d(TAG, "Price: " + price);

        /// create a new item object
        Item item = new Item(id, name, type, size, color, fabric, imageBase64, userId, price);

        /// save the food to the database and get the result in the callback
        databaseService.createNewItem(item, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "Item added successfully");
                Toast.makeText(AddItemActivity.this, "הפריט נוסף בהצלחה", Toast.LENGTH_SHORT).show();
                /// clear the input fields after adding the food for the next food
                Log.d(TAG, "Clearing input fields");
                clearForm();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to add item", e);
                Toast.makeText(AddItemActivity.this, "שגיאה בהוספת הפריט", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearForm() {
        itemNameEditText.setText("");
        itemTypeAutoComplete.setText("");
        itemSizeAutoComplete.setText("");
        itemColorAutoComplete.setText("");
        itemFabricAutoComplete.setText("");
        itemPriceEditText.setText("");
        itemImageView.setImageResource(R.drawable.image);
        itemImageView.setTag(R.drawable.image);
    }

    /// select image from gallery
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageLauncher.launch(intent);
    }

    /// capture image from camera
    private void captureImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageLauncher.launch(takePictureIntent);
    }

    /// validate the input
    private boolean isValid(String name, String type, String size, String color, String fabric, String priceText, ImageView itemImageView) {
        if (name.isEmpty()) {
            Toast.makeText(this, "נא להזין שם פריט", Toast.LENGTH_SHORT).show();
            itemNameEditText.requestFocus();
            return false;
        }

        if (type.isEmpty()) {
            Toast.makeText(this, "נא לבחור סוג פריט", Toast.LENGTH_SHORT).show();
            itemTypeAutoComplete.requestFocus();
            return false;
        }

        if (size.isEmpty()) {
            Toast.makeText(this, "נא לבחור מידה", Toast.LENGTH_SHORT).show();
            itemSizeAutoComplete.requestFocus();
            return false;
        }

        if (color.isEmpty()) {
            Toast.makeText(this, "נא לבחור צבע", Toast.LENGTH_SHORT).show();
            itemColorAutoComplete.requestFocus();
            return false;
        }

        if (fabric.isEmpty()) {
            Toast.makeText(this, "נא לבחור סוג בד", Toast.LENGTH_SHORT).show();
            itemFabricAutoComplete.requestFocus();
            return false;
        }

        if (priceText.isEmpty()) {
            Toast.makeText(this, "נא להזין מחיר", Toast.LENGTH_SHORT).show();
            itemPriceEditText.requestFocus();
            return false;
        }

        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                Toast.makeText(this, "המחיר חייב להיות גדול מ-0", Toast.LENGTH_SHORT).show();
                itemPriceEditText.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "נא להזין מחיר תקין", Toast.LENGTH_SHORT).show();
            itemPriceEditText.requestFocus();
            return false;
        }

        // check if itemImageView was changed from app:srcCompat="@drawable/image"
        if (itemImageView.getTag() != null) {
            Toast.makeText(this, "נא לבחור תמונה", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == addItemButton.getId()) {
            Log.d(TAG, "Add item button clicked");
            addItemToDatabase();
            return;
        }

        if (v.getId() == itemImageView.getId()) {
            Log.d(TAG, "Select image button clicked");
            showImageSourceDialog();
            return;
        }
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