package com.ddit.project.supermarketcheckouter;

import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_name;
import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_photourl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdminMainActivity extends AppCompatActivity {


    PrefStorageManager pref;
    TextView welcome_user;
    TextView logout_tc;
    CircleImageView profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_adminmain);
        pref = new PrefStorageManager(AdminMainActivity.this);

        profile_image = findViewById(R.id.profile_image);
        welcome_user = findViewById(R.id.welcome_user);
        logout_tc = findViewById(R.id.logout_tc);

        welcome_user.setText("Welcome\n" + pref.getStringvalue(SHARED_USER_name));
        logout_tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.Logoutpref();
                Intent int_log = new Intent(AdminMainActivity.this, LoginActivity.class);
                int_log.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(int_log);
                finish();
            }
        });

        String img_url = pref.getStringvaluedef(SHARED_USER_photourl, "");
        try {
            if (img_url != null && !img_url.equals("")) {
                Glide.with(AdminMainActivity.this).load(img_url).into(profile_image);
            } else {
                Glide.with(AdminMainActivity.this).load(R.drawable.user_default).into(profile_image);
            }
        } catch (NullPointerException e) {
            Glide.with(AdminMainActivity.this).load(R.drawable.user_default).into(profile_image);
        }

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startscan = new Intent(AdminMainActivity.this, ProfileActivity.class);
                startActivity(startscan);

            }
        });


        findViewById(R.id.user_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, Admin_UserListActivity.class));
            }
        });

        findViewById(R.id.order_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, Admin_OrderListActivity.class));
            }
        });

        findViewById(R.id.product_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, Admin_ProductListActivity.class));
            }
        });

        findViewById(R.id.category_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, Admin_CategoryActivity.class));

            }
        });

        try {
            getAllTotalCount();
        } catch (Exception e) {
            Log.e("callingcountdata", "catch " + e.getMessage());
        }

    }

    private void getAllTotalCount() {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("user").exists()) {
                        long countuser = snapshot.child("user").getChildrenCount();
                        Log.e("callingcountdata", "call user count " + countuser);
                        ((TextView) findViewById(R.id.total_user)).setText(String.valueOf(countuser));

                    }
                    if (snapshot.child("category").exists()) {
                        long countcategory = snapshot.child("category").getChildrenCount();
                        Log.e("callingcountdata", "call category count " + countcategory);
                        ((TextView) findViewById(R.id.total_category)).setText(String.valueOf(countcategory));

                    }
                    if (snapshot.child("product").exists()) {
                        long countproduct = snapshot.child("product").getChildrenCount();
                        Log.e("callingcountdata", "call product count " + countproduct);
                        ((TextView) findViewById(R.id.total_product)).setText(String.valueOf(countproduct));

                    }
                    if (snapshot.child("order").exists()) {
                        long countorder = snapshot.child("order").getChildrenCount();
                        Log.e("callingcountdata", "call order count " + countorder);
                        ((TextView) findViewById(R.id.total_order)).setText(String.valueOf(countorder));
                    }
                    /*for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        Log.e("callingcountdata","call 1");

                    }*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}