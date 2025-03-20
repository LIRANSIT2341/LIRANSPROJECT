package com.example.testapp.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lirans2341project.Adapters.ItemsAdapter;
import com.lirans2341project.R;
import com.lirans2341project.Adapters.ItemSpinnerAdapter;
import com.lirans2341project.model.Cart;
import com.lirans2341project.model.Item;
import com.lirans2341project.services.AuthenticationService;
import com.lirans2341project.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class AddCartActivity extends AppCompatActivity implements View.OnClickListener {

    /// tag for logging
    private static final String TAG = "AddCartActivity";

    private Spinner itemSpinner;
    private Button addButton, createCartButton;
    private EditText etCartName;
    private ItemSpinnerAdapter ItemSpinnerAdapter;
    private ItemsAdapter itemsAdapter;
    private DatabaseService databaseService;
    private AuthenticationService authenticationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        /// set the layout for the activity
        setContentView(R.layout.activity_add_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// get the instance of the database service
        databaseService = DatabaseService.getInstance();
        authenticationService = AuthenticationService.getInstance();

        /// get the views
        addButton = findViewById(R.id.btn_add_cart);
        createCartButton = findViewById(R.id.btn_create_add_cart);
        etCartName = findViewById(R.id.edit_text_cart_name);

        /// Adapter for the item recycler view
        /// @see ArrayAdapter
        /// @see Item
        RecyclerView selectedItemsRecyclerView = findViewById(R.id.recycler_view_selected_items);
        itemsAdapter = new ItemsAdapter(new ItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {

            }
        });
        selectedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectedItemsRecyclerView.setAdapter(itemsAdapter);

        /// Adapter for the item spinner
        /// @see ItemSpinnerAdapter
        /// @see Item
        itemSpinner = findViewById(R.id.spinner_food_add_cart);
        List<Item> allItems = new ArrayList<>();
        ItemSpinnerAdapter = new ItemSpinnerAdapter(AddCartActivity.this, android.R.layout.simple_spinner_item, allItems);
        itemSpinner.setAdapter(ItemSpinnerAdapter);

        /// get all the items from the database
        databaseService.getItemList(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> object) {
                Log.d(TAG, "onCompleted: " + object);
                allItems.clear();
                allItems.addAll(object);
                /// notify the adapter that the data has changed
                /// this specifies that the data has changed
                /// and the adapter should update the view
                /// @see FoodSpinnerAdapter#notifyDataSetChanged()
                ItemSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "onFailed: ", e);
            }
        });

        /// set the click listeners
        addButton.setOnClickListener(this);
        createCartButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == addButton.getId()) {
            addFoodToCart();
            return;
        }
        if (v.getId() == createCartButton.getId()) {
            addCartToDatabase();
            return;
        }
    }

    private void addFoodToCart() {
        /// get the selected food from the spinner
        Item selectedItem = (Item) itemSpinner.getSelectedItem();
        if (selectedItem == null) return;
        /// add the selected item to the list of selected foods
        itemsAdapter.addItem(selectedItem);
    }

    private void addCartToDatabase() {
        String cartName = etCartName.getText().toString().trim();
        List<Item> selectedItems = itemsAdapter.getItems();
        if (!isValid(cartName, selectedItems)) {
            return;
        }

        /// generate a new id for the new cart
        String cartId = databaseService.generateCartId();
        String userId = authenticationService.getCurrentUserId();
        /// create a new cart
        Cart cart = new Cart(cartId, cartName,  selectedItems, userId);
        /// save the cart to the database and get the result in the callback
        databaseService.createNewCart(cart, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Log.d(TAG, "Cart created successfully");
                /// clear the selected foods
                int itemCount = selectedItems.size();
                selectedItems.clear();
                etCartName.setText("");
                /// notify the adapter that the data has changed
                /// this specifies that the items from 0 to itemCount have been removed
                /// and the adapter should update the view
                /// @see FoodsAdapter#notifyItemRangeRemoved(int, int)
                /// @see itemCount
                itemsAdapter.notifyItemRangeRemoved(0, itemCount);
                Toast.makeText(AddCartActivity.this, "Cart created successfully", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to create cart", e);
                Toast.makeText(AddCartActivity.this, "Failed to create cart", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private boolean isValid(String cartName, List<Item> selectedItems) {
        if (cartName.isEmpty()) {
            etCartName.setError("Cart name cannot be empty");
            etCartName.requestFocus();
            return false;
        }
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Please add item to the cart", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}