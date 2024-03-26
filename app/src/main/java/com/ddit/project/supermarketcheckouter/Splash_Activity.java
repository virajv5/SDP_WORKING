package com.ddit.project.supermarketcheckouter;

import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_TYPE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ddit.project.supermarketcheckouter.Models.Product_GetSet;

import java.util.ArrayList;
import java.util.Collections;

public class Splash_Activity extends AppCompatActivity {

    PrefStorageManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref = new PrefStorageManager(Splash_Activity.this);
        getAllClaimList();
    }

    private void getAllClaimList() {
        Log.e("callingsplash","callegetlist");
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
                        Log.e("callingsplash","calle next 1");
                        if (Constant.glob_product.size() > 0) {
                            Collections.reverse(Constant.glob_product);
                            callnextscreen();
                        } else {
                            Log.e("callingsplash","calle next 2");
                            geterrorfromfirebase();
                        }
                    } else {
                        Log.e("callingsplash","calle next 3");
                        geterrorfromfirebase();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("callingsplash","calle next 4");
                    geterrorfromfirebase();
                }
        });
    }

     public void geterrorfromfirebase(){
         callnextscreen();
    }


    public void callnextscreen() {
        Log.e("callingsplash","calle next end");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSignedIn()) {
                    if (pref.getStringvaluedef(SHARED_USER_TYPE,"").equals("0")){
                        Intent intent = new Intent(Splash_Activity.this, AdminMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(Splash_Activity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(Splash_Activity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }


    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

}
