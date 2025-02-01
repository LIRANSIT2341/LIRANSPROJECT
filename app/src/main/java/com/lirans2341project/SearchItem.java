package com.lirans2341project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.lirans2341project.model.Item;

import com.lirans2341project.model.ItemAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
public class SearchItem extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener {


    EditText etSearch2;
    Button btnSearch, btnshowall;
    ListView lvSearch1;
    ArrayList<String> arrSearch1 = new ArrayList<>();
    ArrayList<String> arrSearch2 = new ArrayList<>();
    ArrayAdapter<String> adpSearch1, adpSearch2;
    Spinner spSearch1, spSearch2;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ItemAdapter<Item> adap1;
    ArrayList<Item> all1 = new ArrayList<>();
    ArrayList<Item> search1 = new ArrayList<>();
    ArrayList<Item> showlist = new ArrayList<>();
    private String itemselect;
    private String value2, stsearch;
    private String value1;
    String[] categorarr, fabricarr, colorarr, sizearr;
    String[] nameArr={}, priceArr={};
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_item);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Items");
        etSearch2 = findViewById(R.id.etSearchItem);
        spSearch1 = findViewById(R.id.spCategory5);
        spSearch2 = findViewById(R.id.spSubCategory5);
        btnSearch = findViewById(R.id.btnSearchItems);
        lvSearch1 = findViewById(R.id.lvItem);
        btnshowall = findViewById(R.id.btnAllItems);
        arrSearch1.add("color");
        arrSearch1.add("type");
        arrSearch1.add("fabric");
        arrSearch1.add("size");
        arrSearch1.add("price");
        arrSearch1.add("name");
        adpSearch1 = new ArrayAdapter(SearchItem.this, R.layout.support_simple_spinner_dropdown_item, arrSearch1);

        spSearch1.setAdapter(adpSearch1);
        spSearch1.setOnItemSelectedListener(this);

        btnSearch.setOnClickListener(this);
        btnshowall.setOnClickListener(this);
        categorarr = getResources().getStringArray(R.array.Categoriesar2);
        fabricarr = getResources().getStringArray(R.array.Fabricar2);
        colorarr = getResources().getStringArray(R.array.Colors2);
        sizearr = getResources().getStringArray(R.array.Sizesar2);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Item value = postSnapshot.getValue(Item.class);

                    all1.add(value);
                    Log.d("Item", "Value is: " + value);
                }
                adap1 = new ItemAdapter<>(SearchItem.this, 0, 0, all1);
                lvSearch1.setAdapter(adap1);
                lvSearch1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Item item = (Item) parent.getItemAtPosition(position);
                        Intent go= new Intent(SearchItem.this, ItemProfile.class);

                        go.putExtra("data",item);
                        startActivity(go);

                    }
                });
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });


    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        itemselect = parent.getItemAtPosition(position).toString();

        if (itemselect.equals("type")) {
            value1 = "type";
            adpSearch2 = new ArrayAdapter<String>(SearchItem.this, R.layout.support_simple_spinner_dropdown_item, categorarr);


        }

        if (itemselect.equals("fabric")) {
            value1 = "fabric";
            adpSearch2 = new ArrayAdapter<String>(SearchItem.this, R.layout.support_simple_spinner_dropdown_item, fabricarr);

        }

        if (itemselect.equals("color")) {
            value1 = "color";
            adpSearch2 = new ArrayAdapter<String>(SearchItem.this, R.layout.support_simple_spinner_dropdown_item, colorarr);

        }
        if (itemselect.equals("size")) {
            value1 = "size";
            adpSearch2 = new ArrayAdapter<String>(SearchItem.this, R.layout.support_simple_spinner_dropdown_item, sizearr);

        }
        if (itemselect.equals("price")) {
            value1 = "price";
            adpSearch2 = new ArrayAdapter<String>(SearchItem.this, R.layout.support_simple_spinner_dropdown_item, priceArr);


        }
        if (itemselect.equals("name")) {
            value1 = "name";
            adpSearch2 = new ArrayAdapter<String>(SearchItem.this, R.layout.support_simple_spinner_dropdown_item, nameArr);

        }

        spSearch2.setAdapter(adpSearch2);
        spSearch2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                value2 = parent.getItemAtPosition(position).toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onClick(View v) {
        if (v == btnSearch) {
            String stSrech = etSearch2.getText().toString();
            search1.clear();

            //   eSearch1 = etSearch1.getText().toString() + "";
            for (int i = 0; i < all1.size(); i++) {
                Item current1 = all1.get(i);
                if (value1 == "type") {
                    if (((Item) current1).getType().toLowerCase().contains(value2.toLowerCase())) {
                        search1.add(current1);
                    }
                } else if (itemselect == "fabric") {

                    if (current1.getFabric().toLowerCase().contains(value2.toLowerCase())) {
                        search1.add(current1);
                    }

                } else if (itemselect == "color") {
                    if (current1.getColor().toLowerCase().contains(value2.toLowerCase())) {
                        search1.add(current1);
                    }
                } else if (itemselect == "size") {
                    if (current1.getSize().toLowerCase().contains(value2.toLowerCase())) {
                        search1.add(current1);

                    }
                } else if (itemselect == "price") {

                    int intPrice = Integer.parseInt(stSrech);

                    if (current1.getPrice() <= intPrice) {
                        search1.add(current1);
                    }


                } else if (itemselect == "name") {


                    if (current1.getName().toLowerCase().contains(stSrech.toLowerCase())) {
                        search1.add(current1);


                    }

                } else {

                    if (current1.getName().toLowerCase().contains(stSrech.toLowerCase()) || current1.getColor().toLowerCase().contains(stSrech.toLowerCase()) || current1.getFabric().toLowerCase().contains(stSrech.toLowerCase())) {
                        search1.add(current1);

                    }

                }
            }


            adap1 = new ItemAdapter<>(SearchItem.this, 0, 0, search1);
            lvSearch1.setAdapter(adap1);
            showlist = search1;

        } else if (v == btnshowall) {
            adap1 = new ItemAdapter<>(SearchItem.this, 0, 0, all1);
            lvSearch1.setAdapter(adap1);
            showlist = all1;
            Toast.makeText(SearchItem.this, "showall", Toast.LENGTH_LONG).show();

        }
        lvSearch1.setOnItemClickListener(this);
        lvSearch1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) parent.getItemAtPosition(position);
                Intent go= new Intent(SearchItem.this, ItemProfile.class);

                go.putExtra("data",item);
                startActivity(go);

            }
        });

//
    }


    public boolean onOptionsItemSelected(MenuItem menuitem) {
        int itemid = menuitem.getItemId();
        if (itemid == R.id.menuGoStore) {
            Intent goadmin = new Intent(SearchItem.this, SearchItem.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuGoAddItem) {
            Intent goadmin = new Intent(SearchItem.this, AddItem.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuGoWishList) {
            Intent goadmin = new Intent(SearchItem.this, AddItem.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuGoPersonal) {
            Intent goadmin = new Intent(SearchItem.this, UserProfile.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuGoAboutUs) {
            Intent goadmin = new Intent(SearchItem.this, AboutUs.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuGoAfterLogin) {
            Intent goadmin = new Intent(SearchItem.this, AfterLogin.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuGoAdminPage) {
            String admin = "edenkario@gmail.com";
            if(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(admin)){
                Intent go = new Intent(SearchItem.this, AdminPage.class);
                startActivity(go);
            }
            else{
                Toast.makeText(SearchItem.this,"עמוד זה למנהלים בלבד!", Toast.LENGTH_LONG).show();
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







    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
