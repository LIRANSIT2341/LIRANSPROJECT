package com.lirans2341project.screen;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import com.lirans2341project.R;
import com.lirans2341project.Adapters.ImageSourceAdapter;
import com.lirans2341project.model.Item;
import com.lirans2341project.services.AuthenticationService;
import com.lirans2341project.services.DatabaseService;
import com.lirans2341project.utils.ImageUtil;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity implements View.OnClickListener {

    /// tag for logging
    private static final String TAG = "AddItemActivity";

    private EditText itemNameEditText, itemPriceEditText,itemTypeEditText,itemSizeEditText,itemColorEditText,itemFabricEditText;
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
        itemNameEditText = findViewById(R.id.item_name);
        itemPriceEditText = findViewById(R.id.item_price);
        addItemButton = findViewById(R.id.add_item_button);
        itemImageView = findViewById(R.id.item_image);
        itemTypeEditText= findViewById(R.id.item_type);
        itemFabricEditText= findViewById(R.id.item_fabric);
        itemColorEditText= findViewById(R.id.item_color);
        itemSizeEditText= findViewById(R.id.item_size);


        /// set the tag for the image view
        /// to check if the image was changed from app:srcCompat="@drawable/image"
        itemImageView.setTag(R.drawable.image);

        /// set the on click listeners
       itemImageView.setOnClickListener(this);
        addItemButton.setOnClickListener(this);

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
//                        itemImageView.set
                    }
                }
        );


    }

    /// show the dialog to select the image source
    /// gallery or camera
    /// @see AlertDialog
    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");

        final ArrayList<Map.Entry<String, Integer>> options = new ArrayList<>();
        options.add(new AbstractMap.SimpleEntry<>("Gallery", R.drawable.gallery_thumbnail));
        options.add(new AbstractMap.SimpleEntry<>("Camera", R.drawable.photo_camera));

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
        String priceText = itemPriceEditText.getText().toString();
        String type = itemTypeEditText.getText().toString();
        String fabric = itemFabricEditText.getText().toString();
        String color= itemColorEditText.getText().toString();
        String size = itemSizeEditText.getText().toString();



        /// validate the input
        /// stop if the input is not valid
        if (!isValid(name, priceText, itemImageView)) return;

        String imageBase64 = ImageUtil.convertTo64Base(itemImageView);
        /// convert the price to double
        double price = Double.parseDouble(priceText);

        /// generate a new id for the food
        String id = databaseService.generateItemId();

        String userId = AuthenticationService.getInstance().getCurrentUserId();

        Log.d(TAG, "Adding item to database");
        Log.d(TAG, "ID: " + id);
        Log.d(TAG, "Name: " + name);
        Log.d(TAG, "Price: " + price);
        Log.d(TAG, "Color: " + color);
        Log.d(TAG, "Type: " + type);
        Log.d(TAG, "Fabric: " + fabric);
        Log.d(TAG, "Size: " + size);
//        Log.d(TAG, "Image: " + imageBase64);

        /// create a new item object
        Item item = new Item(id, name,type,size,color,fabric,imageBase64, userId, price);

        /// save the food to the database and get the result in the callback
        databaseService.createNewItem(item, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "Item added successfully");
                Toast.makeText(AddItemActivity.this, "item added successfully", Toast.LENGTH_SHORT).show();
                /// clear the input fields after adding the food for the next food
                Log.d(TAG, "Clearing input fields");
                itemNameEditText.setText("");
                itemPriceEditText.setText("");
                itemImageView.setImageBitmap(null);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to add item", e);
                Toast.makeText(AddItemActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
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
    private boolean isValid(String name, String priceText, ImageView itemImageView) {
        if (name.isEmpty()) {
            Log.e(TAG, "Name is empty");
            itemNameEditText.setError("Name is required");
            itemNameEditText.requestFocus();
            return false;
        }

        if (priceText.isEmpty()) {
            Log.e(TAG, "Price is empty");
           itemPriceEditText.setError("Price is required");
            itemPriceEditText.requestFocus();
            return false;
        }

        // check if itemImageView was changed from app:srcCompat="@drawable/image"
        if (itemImageView.getTag() != null) {
            Log.e(TAG, "Image is required");
            Toast.makeText(this, "Image is required", Toast.LENGTH_SHORT).show();
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
}