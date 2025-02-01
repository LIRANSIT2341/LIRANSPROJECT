package com.lirans2341project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.lirans2341project.model.Item;
import com.lirans2341project.model.User;
import com.lirans2341project.services.AuthenticationService;
import com.lirans2341project.services.DatabaseService;
import com.lirans2341project.utils.ImageUtil;
import com.lirans2341project.utils.SharedPreferencesUtil;

import java.io.IOException;

public class AddItem extends AppCompatActivity implements View.OnClickListener {
    Spinner spinnertype,spinnerfabric,spinnersize,spinnercolor, spinnerPurpes;

    EditText etItemName, etPrice;
    String itemName, stPrice, type;

    int price;
    String color;
    String size;
    String fabric,imageRef;
    String purpes;
    Bitmap bitmap;

    Button btnGallery,btnTakePic, btnAddItem;
    ImageView iv;

    public static final int GALLERY_INTENT=2;

    DatabaseService databaseService;
    AuthenticationService authenticationService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        initViews();

        databaseService = DatabaseService.getInstance();
        authenticationService = AuthenticationService.getInstance();

        btnTakePic.setOnClickListener(this);
        btnAddItem.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
    }

    private void initViews() {

        btnAddItem=findViewById(R.id.btnAddItem);
        btnGallery=findViewById(R.id.btnGalleryD);
        btnTakePic=findViewById(R.id.btnTakePicD);
        iv=findViewById(R.id.ivD);


        spinnertype=findViewById(R.id.spType);
        spinnerfabric=findViewById(R.id.spFabric);
        spinnersize=findViewById(R.id.spSizes);
        spinnercolor=findViewById(R.id.spColor);
        spinnerPurpes=findViewById(R.id.spBuyOrRent);
        etItemName=findViewById(R.id.etItemName);
        etPrice=findViewById(R.id.etPrice);
    }

    @Override
    public void onClick(View view) {

        if(view==btnTakePic)
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 0);

        }
        if(view==btnGallery) {

            Intent intent2=new Intent(Intent.ACTION_PICK);
            intent2.setType("image/*");
            startActivityForResult(intent2,GALLERY_INTENT);

        }
        if(view==btnAddItem)
        {

            size=spinnersize.getSelectedItem().toString();
            type=spinnertype.getSelectedItem().toString();
            fabric=spinnerfabric.getSelectedItem().toString();
            color=spinnercolor.getSelectedItem().toString();
            purpes=spinnerPurpes.getSelectedItem().toString();
            itemName=etItemName.getText()+"";

            stPrice=etPrice.getText().toString();

            price=Integer.parseInt(stPrice);

            if (bitmap != null) {
                String itemid= databaseService.generateItemId();
                String base64IV = ImageUtil.convertTo64Base(iv);
                Item newItem= new Item(itemid,itemName,type,size,color,fabric,base64IV,price, authenticationService.getCurrentUserId(), "");

                databaseService.createNewItem(newItem, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {
                        Intent go=new Intent(getApplicationContext(), AfterLogin.class);
                        startActivity(go);
                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });
            } else {
                Toast.makeText(AddItem.this, "Please take pic!", Toast.LENGTH_SHORT).show();
            }
        }

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0)//coming from camera
        {
            if (resultCode == RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                iv.setImageBitmap(bitmap);
            }
        }

        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK && data !=null) {
            Uri uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                iv.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public boolean onOptionsItemSelected(MenuItem menuitem) {
        int itemid = menuitem.getItemId();
        if (itemid == R.id.menuGoStore) {
            Intent goadmin = new Intent(AddItem.this, SearchItem.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuGoAddItem) {
            Intent goadmin = new Intent(AddItem.this, AddItem.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuGoWishList) {
            Intent goadmin = new Intent(AddItem.this, WishList.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuGoPersonal) {
            Intent goadmin = new Intent(AddItem.this, UserProfile.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuGoAboutUs) {
            Intent goadmin = new Intent(AddItem.this, AboutUs.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuGoAfterLogin) {
            Intent goadmin = new Intent(AddItem.this, AfterLogin.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuGoAdminPage) {
            User user = SharedPreferencesUtil.getUser(this);
            if(user != null && user.isAdmin()){
                Intent go = new Intent(AddItem.this, AdminPage.class);
                startActivity(go);
            }
            else{
                Toast.makeText(AddItem.this,"עמוד זה למנהלים בלבד!", Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(menuitem);
    }


    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }




}