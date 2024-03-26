package com.ddit.project.supermarketcheckouter;

import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_ID;
import static com.ddit.project.supermarketcheckouter.Constant.pass_prdct_value;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ddit.project.supermarketcheckouter.Adapter.CategoryBottomAdapter;
import com.ddit.project.supermarketcheckouter.Models.Category_GetSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Admin_AddProductActivity extends AppCompatActivity {

    PrefStorageManager pref;
    String temp_user;

    TextView receiver_category;
    String receiver_cid = "";
    String receiver_cname = "";
    ImageView product_upload_img;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    private String uploaded_uri;
    LinearLayout no_data_ll;
    CardView loading_cardview;
    ArrayList<Category_GetSet> temp_cat;
    boolean edit = false;
    ImageView confirm_product;
    EditText et_product_name;
    EditText et_product_price;
    EditText et_product_code;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_admin_add_product);
        pref = new PrefStorageManager(Admin_AddProductActivity.this);

        this.receiver_category = findViewById(R.id.receiver_category);
        temp_user = pref.getStringvaluedef(SHARED_USER_ID, "");

        confirm_product = findViewById(R.id.confirm_product);
        et_product_code = findViewById(R.id.et_product_code);
        et_product_name = findViewById(R.id.et_product_name);
        et_product_price = findViewById(R.id.et_product_price);
        loading_cardview = findViewById(R.id.loading_cardview);
        no_data_ll = findViewById(R.id.no_data_ll);
        no_data_ll.setVisibility(View.GONE);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        product_upload_img = findViewById(R.id.product_upload_img);

        if (Constant.passing_prdct_addoredit == 1) {
            edit = true;
            confirm_product.setImageResource(R.drawable.save_img);
            if (pass_prdct_value != null) {
                et_product_code.setText(pass_prdct_value.getProduct_code());
                et_product_name.setText(pass_prdct_value.getName());
                et_product_price.setText(pass_prdct_value.getPrice());
                receiver_cid = pass_prdct_value.getCategory_id() + "";
                receiver_cname = pass_prdct_value.getCategory_name() + "";
                receiver_category.setText(receiver_cname);
                uploaded_uri = pass_prdct_value.getImage() + "";
            } else {
                Toast.makeText(Admin_AddProductActivity.this, "Data Not Found", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading_cardview.setVisibility(View.GONE);
                        onBackPressed();
                    }
                }, 500);
            }
        } else {
            edit = false;
            confirm_product.setImageResource(R.drawable.add_img);
        }


        receiver_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opencategorydialog();
            }
        });


        product_upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(Admin_AddProductActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(512, 512)
                        .start();
            }
        });

        loading_cardview.setVisibility(View.VISIBLE);
        getcategory_data();

        findViewById(R.id.upload_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filePath != null) {
                    uploadImage();
                } else {
                    Toast.makeText(Admin_AddProductActivity.this, "Please select Image first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        confirm_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getpro_name = et_product_name.getText().toString();
                String getpro_price = et_product_price.getText().toString();
                if (getpro_name.length() > 0) {
                    if (getpro_price.length() > 0) {
                        if (uploaded_uri != null && !uploaded_uri.equals("")) {
                            if (receiver_cid != null && !receiver_cid.equals("")) {
                                loading_cardview.setVisibility(View.VISIBLE);
                                if (edit) {
                                    if (pass_prdct_value.getProduct_id() != null && !pass_prdct_value.getProduct_id().equals("")) {
                                        FirebaseDatabase.getInstance().getReference().child("product").orderByChild("product_id").equalTo(pass_prdct_value.getProduct_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                                        String key = datas.getKey();
                                                        String getpro_code = et_product_code.getText().toString();
                                                        if (getpro_code.length() <= 0) {
                                                            getpro_code = pass_prdct_value.getProduct_code();
                                                        }
                                                        Map<String, Object> valuesclaim = new HashMap<>();
                                                        valuesclaim.put("name", getpro_name);
                                                        valuesclaim.put("image", uploaded_uri);
                                                        valuesclaim.put("category_id", receiver_cid);
                                                        valuesclaim.put("category_name", receiver_cname);
                                                        valuesclaim.put("price", String.valueOf(getpro_price));
                                                        valuesclaim.put("product_code", getpro_code);

                                                        FirebaseDatabase.getInstance().getReference().child("product").child(key).updateChildren(valuesclaim).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                clearafterupdateproduct();
                                                            }
                                                        });

                                                    }
                                                } else {
                                                    loading_cardview.setVisibility(View.VISIBLE);
                                                    Toast.makeText(Admin_AddProductActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                loading_cardview.setVisibility(View.VISIBLE);
                                                Toast.makeText(Admin_AddProductActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        loading_cardview.setVisibility(View.VISIBLE);
                                        Toast.makeText(Admin_AddProductActivity.this, "Data Not Found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Query lastQuery = FirebaseDatabase.getInstance().getReference().child("product").orderByKey().limitToLast(1);
                                    lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String lastpro_id = "";
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                    if (childSnapshot.exists()) {
                                                        lastpro_id = childSnapshot.child("product_id").getValue().toString();
                                                    }
                                                    Log.e("getlast_catdata", "get key :  " + childSnapshot.getKey());
                                                    Log.e("getlast_catdata", "getvaluev :  " + childSnapshot.child("product_id").getValue());
                                                }
                                            }
                                            int newprod_id = 1;
                                            if (!lastpro_id.equals("")) {
                                                newprod_id = (Integer.parseInt(lastpro_id) + 1);
                                            }
                                            String getpro_code = et_product_code.getText().toString();
                                            if (getpro_code.length() <= 0) {
                                                getpro_code = String.valueOf(newprod_id) + getpro_name + receiver_cid;
                                            }

                                            Map<String, Object> valuesclaim = new HashMap<>();
                                            valuesclaim.put("product_id", String.valueOf(newprod_id));
                                            valuesclaim.put("name", getpro_name);
                                            valuesclaim.put("image", uploaded_uri);
                                            valuesclaim.put("category_id", receiver_cid);
                                            valuesclaim.put("category_name", receiver_cname);
                                            valuesclaim.put("price", String.valueOf(getpro_price));
                                            valuesclaim.put("product_code", getpro_code);
                                            FirebaseDatabase.getInstance().getReference("product").push().setValue(valuesclaim).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    clearafterupload();
                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            loading_cardview.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(Admin_AddProductActivity.this, "Please Select Category!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Admin_AddProductActivity.this, "Please upload Image First!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Admin_AddProductActivity.this, "Please write product price", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Admin_AddProductActivity.this, "Please write product name", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void opencategorydialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.bottom_category_select);

        ImageView sheet_close = bottomSheetDialog.findViewById(R.id.sheet_close);
        RecyclerView recview_branchlist = bottomSheetDialog.findViewById(R.id.recview_branchlist);
        EditText search_branch = bottomSheetDialog.findViewById(R.id.search_branch);

        recview_branchlist = (RecyclerView) bottomSheetDialog.findViewById(R.id.recview_branchlist);
        recview_branchlist.setHasFixedSize(true);
        recview_branchlist.setLayoutManager(new LinearLayoutManager(this));
        CategoryBottomAdapter mAdapter = new CategoryBottomAdapter(Admin_AddProductActivity.this, new CategoryBottomAdapter.ItemListener() {
            @Override
            public void onItemClick(Category_GetSet item) {
                receiver_cid = item.getCat_id();
                receiver_cname = item.getCat_name();
                receiver_category.setText(receiver_cname);
                bottomSheetDialog.dismiss();
            }
        });
        recview_branchlist.setAdapter(mAdapter);

        mAdapter.additem(temp_cat);

        bottomSheetDialog.show();

        sheet_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        search_branch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String query = search_branch.getText().toString();
                if (query != null) {
                    mAdapter.getFilter().filter(query);
                }

            }
        });
    }

    public void getcategory_data() {
        FirebaseDatabase.getInstance().getReference("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    temp_cat = new ArrayList<Category_GetSet>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String id = (String) ds.child("cat_id").getValue();
                        String name = (String) ds.child("cat_name").getValue();
                        temp_cat.add(new Category_GetSet(id, name));
                    }
                    if (temp_cat.size() > 0) {
                        Collections.reverse(temp_cat);
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
                Toast.makeText(Admin_AddProductActivity.this, "Please Try Again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackcall(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void clearafterupload() {
        Toast.makeText(Admin_AddProductActivity.this, "Product Upload Successfully!", Toast.LENGTH_SHORT).show();
        et_product_name.setText("");
        et_product_price.setText("");
        et_product_code.setText("");
        receiver_category.setText("");
        receiver_cid = "";
        receiver_cname = "";
        filePath = null;
        uploaded_uri = null;
        loading_cardview.setVisibility(View.GONE);
    }

    public void clearafterupdateproduct() {
        Toast.makeText(Admin_AddProductActivity.this, "Product Updated!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading_cardview.setVisibility(View.GONE);
                onBackPressed();
            }
        }, 500);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            uploaded_uri = null;
            filePath = data.getData();
            product_upload_img.setImageURI(filePath);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            Log.e("getlast_catdata", "uploading  :  ");
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    loading_cardview.setVisibility(View.VISIBLE);
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploaded_uri = String.valueOf(uri);
                            Toast.makeText(Admin_AddProductActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            loading_cardview.setVisibility(View.GONE);
                        }
                    });
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Admin_AddProductActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }
}
