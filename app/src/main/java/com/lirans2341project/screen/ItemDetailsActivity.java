package com.lirans2341project.screen;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lirans2341project.R;
import com.lirans2341project.model.Item;
import com.lirans2341project.services.DatabaseService;
import com.lirans2341project.utils.ImageUtil;

public class ItemDetailsActivity extends AppCompatActivity {

    private DatabaseService databaseService;
    private TextView tvName, tvType, tvSize, tvColor, tvFabric, tvPrice;
    private ImageView ivItemImage;
    private ProgressBar progressBar;
    private Button btnDeleteItem;  // כפתור מחיקה

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();
        initViews();

        String itemId = getIntent().getStringExtra("ITEM_ID");
        if (itemId == null) {
            Toast.makeText(this, "Item ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // קבלת הפריט מהמאגר
        databaseService.getItem(itemId, new DatabaseService.DatabaseCallback<Item>() {
            @Override
            public void onCompleted(Item item) {
                progressBar.setVisibility(View.GONE);
                if (item == null) {
                    Toast.makeText(ItemDetailsActivity.this, "Item not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                bindItemToView(item);
            }

            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ItemDetailsActivity.this, "Error loading item", Toast.LENGTH_SHORT).show();
            }
        });

        // הגדרת לחיצה על כפתור המחיקה
        btnDeleteItem.setOnClickListener(v -> {
            deleteItemFromDatabase(itemId);
        });
    }

    private void initViews() {
        tvName = findViewById(R.id.tv_item_name);
        tvType = findViewById(R.id.tv_item_type);
        tvSize = findViewById(R.id.tv_item_size);
        tvColor = findViewById(R.id.tv_item_color);
        tvFabric = findViewById(R.id.tv_item_fabric);
        tvPrice = findViewById(R.id.tv_item_price);
        ivItemImage = findViewById(R.id.iv_item_image);
        progressBar = findViewById(R.id.item_details_progress_bar);
        btnDeleteItem = findViewById(R.id.btn_delete_item);  // אתחול כפתור המחיקה
    }

    private void bindItemToView(Item item) {
        tvName.setText(item.getName());
        tvType.setText(item.getType());
        tvSize.setText(item.getSize());
        tvColor.setText(item.getColor());
        tvFabric.setText(item.getFabric());
        tvPrice.setText("₪" + item.getPrice());

        if (item.getPic() != null && !item.getPic().isEmpty()) {
            ivItemImage.setImageBitmap(ImageUtil.convertFrom64base(item.getPic()));
        }
    }

    private void deleteItemFromDatabase(String itemId) {
        if (itemId != null) {
            databaseService.deleteItem(itemId, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void result) {
                    Toast.makeText(ItemDetailsActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();  // סיום הפעילות (מעבר אחורה)
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(ItemDetailsActivity.this, "Error deleting item", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}