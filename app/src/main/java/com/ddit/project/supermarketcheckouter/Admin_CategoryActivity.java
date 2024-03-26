package com.ddit.project.supermarketcheckouter;

import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_ID;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ddit.project.supermarketcheckouter.Adapter.CategoryListAdapter;
import com.ddit.project.supermarketcheckouter.Models.Category_GetSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Admin_CategoryActivity extends AppCompatActivity implements CategoryListAdapter.ItemListener{

    PrefStorageManager pref;
    String temp_user;
    RecyclerView recview_branchlist;
    CategoryListAdapter mAdapter;
    LinearLayout no_data_ll;
    CardView loading_cardview;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_admin_category_list);
        pref = new PrefStorageManager(Admin_CategoryActivity.this);
        temp_user = pref.getStringvaluedef(SHARED_USER_ID, "");



        loading_cardview = findViewById(R.id.loading_cardview);
        no_data_ll = findViewById(R.id.no_data_ll);
        no_data_ll.setVisibility(View.GONE);
        recview_branchlist = (RecyclerView) findViewById(R.id.recview_branchlist);
        recview_branchlist.setHasFixedSize(true);
        recview_branchlist.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CategoryListAdapter(Admin_CategoryActivity.this, this);
        recview_branchlist.setAdapter(mAdapter);
        loading_cardview.setVisibility(View.VISIBLE);
        getcategory_data();

    }


    public void getcategory_data() {
        FirebaseDatabase.getInstance().getReference("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<Category_GetSet> temp_cat = new ArrayList<Category_GetSet>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String id = (String) ds.child("cat_id").getValue();
                        String name = (String) ds.child("cat_name").getValue();
                        temp_cat.add(new Category_GetSet(id, name));
                    }
                    if (temp_cat.size() > 0) {
                        Collections.reverse(temp_cat);
                        mAdapter.additem(temp_cat);
                        no_data_ll.setVisibility(View.GONE);
                    } else {
                        no_data_ll.setVisibility(View.VISIBLE);
                    }
                    loading_cardview.setVisibility(View.GONE);
                } else {
                    no_data_ll.setVisibility(View.VISIBLE);
                    loading_cardview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading_cardview.setVisibility(View.GONE);
                no_data_ll.setVisibility(View.VISIBLE);
                Toast.makeText(Admin_CategoryActivity.this, "Please Try Again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackcall(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    public void click_addcategory(View view) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.bottom_category_add_dialog);

        TextView close_withdraw = bottomSheetDialog.findViewById(R.id.close_withdraw);
        EditText cat_name_et = bottomSheetDialog.findViewById(R.id.cat_name_et);

        ImageView withdraw_click_tv = bottomSheetDialog.findViewById(R.id.withdraw_click_tv);

        bottomSheetDialog.show();

        close_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        withdraw_click_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String catname = cat_name_et.getText().toString();
                if (!catname.equals("")) {
                    Query lastQuery = FirebaseDatabase.getInstance().getReference().child("category").orderByKey().limitToLast(1);
                    lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String lastpro_id = "";
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    if (childSnapshot.exists()) {
                                        lastpro_id = childSnapshot.child("cat_id").getValue().toString();
                                    }
                                }
                            }
                            int newprod_id = 1;
                            if (!lastpro_id.equals("")) {
                                newprod_id = (Integer.parseInt(lastpro_id) + 1);
                            }

                            Map<String, Object> valuesclaim = new HashMap<>();
                            valuesclaim.put("cat_id", String.valueOf(newprod_id));
                            valuesclaim.put("cat_name", catname);

                            FirebaseDatabase.getInstance().getReference("category").push().setValue(valuesclaim).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    cat_name_et.setText("");
                                    bottomSheetDialog.dismiss();
                                    getcategory_data();
                                    Toast.makeText(Admin_CategoryActivity.this, "Category Added!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter Category Name", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void click_updatecategory(Category_GetSet item) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.bottom_category_add_dialog);

        TextView close_withdraw = bottomSheetDialog.findViewById(R.id.close_withdraw);
        EditText cat_name_et = bottomSheetDialog.findViewById(R.id.cat_name_et);

        ImageView withdraw_click_tv = bottomSheetDialog.findViewById(R.id.withdraw_click_tv);
        cat_name_et.setText(item.getCat_name());
        bottomSheetDialog.show();

        close_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        withdraw_click_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String catname = cat_name_et.getText().toString();
                if (!catname.equals("")) {
                    FirebaseDatabase.getInstance().getReference().child("category").orderByChild("cat_id").equalTo(item.getCat_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                    String key = datas.getKey();

                                    Map<String, Object> valuesclaim = new HashMap<>();
                                    valuesclaim.put("cat_name", catname);

                                    FirebaseDatabase.getInstance().getReference().child("category").child(key).updateChildren(valuesclaim).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            cat_name_et.setText("");
                                            bottomSheetDialog.dismiss();
                                            getcategory_data();
                                        }
                                    });
                                }
                            } else {
                                loading_cardview.setVisibility(View.VISIBLE);
                                Toast.makeText(Admin_CategoryActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loading_cardview.setVisibility(View.VISIBLE);
                            Toast.makeText(Admin_CategoryActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter Category Name", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public void onItemClick(Category_GetSet item) {
        click_updatecategory(item);
    }
}
