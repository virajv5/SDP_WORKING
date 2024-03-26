package com.ddit.project.supermarketcheckouter;

import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_ID;
import static com.ddit.project.supermarketcheckouter.Constant.pass_prdct_value;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_admin_product_list);
        pref = new PrefStorageManager(Admin_ProductListActivity.this);
        temp_user = pref.getStringvaluedef(SHARED_USER_ID, "");


        loading_cardview = findViewById(R.id.loading_cardview);
        no_data_ll = findViewById(R.id.no_data_ll);
        no_data_ll.setVisibility(View.GONE);
        recview_branchlist = (RecyclerView) findViewById(R.id.recview_branchlist);
        recview_branchlist.setHasFixedSize(true);
        recview_branchlist.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ProductAdapter(Admin_ProductListActivity.this, this);
        recview_branchlist.setAdapter(mAdapter);

        if (Constant.glob_product != null && Constant.glob_product.size() > 0) {
            mAdapter.additem(Constant.glob_product);
        } else {
            loading_cardview.setVisibility(View.VISIBLE);
            getAllProductList();
        }
    }


    private void getAllProductList() {
        FirebaseDatabase.getInstance().getReference("product").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Constant.glob_product = new ArrayList<Product_GetSet>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String id = (String) ds.child("product_id").getValue();
                        String name = (String) ds.child("name").getValue();
                        String image = (String) ds.child("image").getValue();
                        String category_id = (String) ds.child("category_id").getValue();
                        String category_name = (String) ds.child("category_name").getValue();
                        String price = (String) ds.child("price").getValue();
                        String product_code = (String) ds.child("product_code").getValue();
                        Constant.glob_product.add(new Product_GetSet(id, name, image, category_id, category_name, price, product_code));
                    }

                    if (Constant.glob_product.size() > 0) {
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


    public void click_addproduct(View view) {
        Constant.passing_prdct_addoredit = 0;
        pass_prdct_value = new Product_GetSet();
        Intent intent = new Intent(Admin_ProductListActivity.this, Admin_AddProductActivity.class);
        activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                getAllProductList();
            }
        });
    }

    @Override
    public void clickeditProduct(Product_GetSet item, int pos) {
        Constant.passing_prdct_addoredit = 1;
        pass_prdct_value = new Product_GetSet();
        pass_prdct_value = item;
        Intent intent = new Intent(Admin_ProductListActivity.this, Admin_AddProductActivity.class);
        activityLauncher.launch(intent, new BetterActivityResult.OnActivityResult<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                getAllProductList();
            }
        });
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
                                                if (Constant.glob_product.size() > 0 && Constant.glob_product.size() > pos) {
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
