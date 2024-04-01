package com.ddit.project.supermarketcheckouter;

import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_ID;
import static com.ddit.project.supermarketcheckouter.Constant.pass_prdct_value;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ddit.project.supermarketcheckouter.Adapter.ProductAdapter;
import com.ddit.project.supermarketcheckouter.Models.Product_GetSet;

import java.util.ArrayList;
import java.util.Collections;

public class Admin_ProductListActivity extends AppCompatActivity implements ProductAdapter.clickcallback {

    PrefStorageManager pref;
    String temp_user;
    RecyclerView recview_branchlist;
    ProductAdapter mAdapter;
    LinearLayout no_data_ll;
    CardView loading_cardview;

    Spinner categorySpinner;
    ArrayAdapter<String> categoryAdapter;

    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_admin_product_list);

        pref = new PrefStorageManager(Admin_ProductListActivity.this);
        temp_user = pref.getStringvaluedef(SHARED_USER_ID, "");

        EditText searchEditText = findViewById(R.id.search_edittext);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString().toLowerCase();
                mAdapter.getFilter().filter(query);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        categorySpinner = findViewById(R.id.category_spinner);
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Set dropdown resource
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setPrompt(""); // Set empty prompt

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedCategory = categoryAdapter.getItem(position);
                if (selectedCategory != null && !selectedCategory.equals("Select Categories")) {
                    // Fetch products based on the selected category
                    fetchProductsByCategory(selectedCategory);
                } else {
                    // Display all products since "Select Categories" is chosen
                    mAdapter.additem(Constant.glob_product);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Display all products when nothing is selected
                mAdapter.additem(Constant.glob_product);
            }
        });


        loading_cardview = findViewById(R.id.loading_cardview);
        no_data_ll = findViewById(R.id.no_data_ll);
        no_data_ll.setVisibility(View.GONE);
        recview_branchlist = findViewById(R.id.recview_branchlist);
        recview_branchlist.setHasFixedSize(true);
        recview_branchlist.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ProductAdapter(Admin_ProductListActivity.this, this);
        recview_branchlist.setAdapter(mAdapter);

        fetchCategories();
        if (Constant.glob_product != null && Constant.glob_product.size() > 0) {
            mAdapter.additem(Constant.glob_product);
        } else {
            loading_cardview.setVisibility(View.VISIBLE);
            fetchProducts();
        }
    }


    private void fetchProducts() {
        FirebaseDatabase.getInstance().getReference("product").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Constant.glob_product = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String id = ds.child("product_id").getValue(String.class);
                        String name = ds.child("name").getValue(String.class);
                        String image = ds.child("image").getValue(String.class);
                        String category_id = ds.child("category_id").getValue(String.class);
                        String category_name = ds.child("category_name").getValue(String.class);
                        String price = ds.child("price").getValue(String.class);
                        String product_code = ds.child("product_code").getValue(String.class);
                        if (id != null && name != null && image != null && category_id != null && category_name != null && price != null && product_code != null) {
                            Constant.glob_product.add(new Product_GetSet(id, name, image, category_id, category_name, price, product_code));
                        }
                    }

                    if (!Constant.glob_product.isEmpty()) {
                        loading_cardview.setVisibility(View.GONE);
                        Collections.reverse(Constant.glob_product);
                        mAdapter.additem(Constant.glob_product);
                    } else {
                        geterrorfromfirebase();
                    }
                } else {
                    geterrorfromfirebase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                geterrorfromfirebase();
            }
        });
    }

    private void fetchCategories() {
        FirebaseDatabase.getInstance().getReference("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Clear previous categories
                    ArrayList<String> categories = new ArrayList<>();
                    categories.add("Select Categories");

                    // Add categories to the adapter
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String categoryName = ds.child("cat_name").getValue(String.class);
                        categories.add(categoryName);
                    }
                    categoryAdapter.clear();
                    categoryAdapter.addAll(categories);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

    }

    private void fetchProductsByCategory(String category) {
        mAdapter.clearItems();
        FirebaseDatabase.getInstance().getReference("product")
                .orderByChild("category_name")
                .equalTo(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String id = ds.child("product_id").getValue(String.class);
                                String name = ds.child("name").getValue(String.class);
                                String image = ds.child("image").getValue(String.class);
                                String categoryId = ds.child("category_id").getValue(String.class);
                                String categoryName = ds.child("category_name").getValue(String.class);
                                String price = ds.child("price").getValue(String.class);
                                String productCode = ds.child("product_code").getValue(String.class);
                                if (id != null && name != null && image != null && categoryId != null && categoryName != null && price != null && productCode != null) {
                                    Product_GetSet product = new Product_GetSet(id, name, image, categoryId, categoryName, price, productCode);
                                    ArrayList<Product_GetSet> productList = new ArrayList<>();
                                    productList.add(product); // Add the single product to ArrayList
                                    mAdapter.additem(productList); // Add the ArrayList to the adapter
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });
    }

    public void geterrorfromfirebase() {
        loading_cardview.setVisibility(View.GONE);
        no_data_ll.setVisibility(View.VISIBLE);
    }

    public void onBackcall(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Fetch all products whenever the activity is resumed
        fetchProducts();
    }


    public void click_addproduct(View view) {
        Constant.passing_prdct_addoredit = 0;
        pass_prdct_value = new Product_GetSet();
        Intent intent = new Intent(Admin_ProductListActivity.this, Admin_AddProductActivity.class);
        activityLauncher.launch(intent, result -> fetchProducts());
    }

    @Override
    public void clickeditProduct(Product_GetSet item, int pos) {
        Constant.passing_prdct_addoredit = 1;
        pass_prdct_value = item;
        Intent intent = new Intent(Admin_ProductListActivity.this, Admin_AddProductActivity.class);
        activityLauncher.launch(intent, result -> fetchProducts());
    }

    @Override
    public void clickdeleteProduct(Product_GetSet item, int pos) {
        Constant.show_dialog_common(Admin_ProductListActivity.this, "Delete",
                "Are You Sure You Want to Delete?", "Delete", "0",
                "warning", "0", "", new Constant.calling_dialogaction() {
                    @Override
                    public void call_action() {
                        FirebaseDatabase.getInstance().getReference().child("product").orderByChild("product_id").equalTo(pass_prdct_value.getProduct_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                        String key = datas.getKey();

                                        FirebaseDatabase.getInstance().getReference().child("product").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!Constant.glob_product.isEmpty() && Constant.glob_product.size() > pos) {
                                                    Constant.glob_product.remove(pos);
                                                    mAdapter.removeitem(pos);
                                                    Toast.makeText(Admin_ProductListActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(Admin_ProductListActivity.this, "No Item Found", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(Admin_ProductListActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(Admin_ProductListActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }
}
