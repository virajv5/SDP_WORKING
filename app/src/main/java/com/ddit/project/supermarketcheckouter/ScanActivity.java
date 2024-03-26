package com.ddit.project.supermarketcheckouter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.bumptech.glide.Glide;
import com.google.zxing.Result;
import com.ddit.project.supermarketcheckouter.Models.CartItem_GetSet;
import com.ddit.project.supermarketcheckouter.Models.Product_GetSet;

public class ScanActivity extends AppCompatActivity {

    PrefStorageManager pref;

    private CodeScanner mCodeScanner;
    CodeScannerView scannerView;
    RelativeLayout product_detail;
    ImageView product_image, confirm_product, goto_cart;
    TextView product_name, product_price;
    TextView item_minus, item_count, item_plus;
    int count_product = 1;
    Product_GetSet temp_product;
    CartDbHandler db_cart;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_scan);

        db_cart = new CartDbHandler(this);
        pref = new PrefStorageManager(ScanActivity.this);
        scannerView = findViewById(R.id.scan_camera);
        product_detail = findViewById(R.id.product_detail);

        product_image = findViewById(R.id.product_image);
        confirm_product = findViewById(R.id.confirm_product);
        goto_cart = findViewById(R.id.goto_cart);

        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);
        item_minus = findViewById(R.id.item_minus);
        item_count = findViewById(R.id.item_count);
        item_plus = findViewById(R.id.item_plus);

        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Constant.glob_product != null && Constant.glob_product.size() > 0) {

                            Log.e("gettinfscanproduct", "getvalue : " + result.getText().replaceAll("http://",""));
                            for (int i = 0; i < Constant.glob_product.size(); i++) {
                                Log.e("gettinfscanproduct", "list Item : "+ i +"  , : "+ Constant.glob_product.get(i).getProduct_code());
                                if (Constant.glob_product.get(i).getProduct_code().equals(result.getText().replaceAll("http://",""))) {
                                    temp_product = Constant.glob_product.get(i);
                                    break;
                                }
                            }
                            if (temp_product != null) {
                                if (!temp_product.getProduct_id().equals("")) {
                                    count_product = 1;
                                    showhide_product(true);
                                }else{
                                    Toast.makeText(ScanActivity.this, "Item Not Available!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ScanActivity.this, "Item Not Available!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ScanActivity.this, "No Product Found!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        mCodeScanner.startPreview();

        item_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count_product > 1) {
                    count_product--;
                    setcounttext(count_product + "");
                } else {
                    if (count_product == 1) {
                        Constant.show_dialog_common(ScanActivity.this, "Remove",
                                "You Want to Remove?", "Yes", "0",
                                "error", "0", "", new Constant.calling_dialogaction() {
                                    @Override
                                    public void call_action() {
                                        mCodeScanner.startPreview();
                                        showhide_product(false);
                                    }
                                });
                    }
                }
            }
        });

        item_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count_product++;
                setcounttext(count_product + "");
            }
        });

        confirm_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (temp_product != null && !temp_product.getProduct_id().equals("")) {
                    mCodeScanner.startPreview();
                    if (db_cart.getCartItemsExist(temp_product.getProduct_id())) {
                        showhide_product(false);
                        Toast.makeText(ScanActivity.this, "Item Already in Cart", Toast.LENGTH_SHORT).show();
                    } else {
                        CartItem_GetSet cartitem = new CartItem_GetSet(temp_product.getProduct_id(),
                                temp_product.getName(), temp_product.getImage(), temp_product.getCategory_id(),
                                temp_product.getCategory_name(), temp_product.getPrice(), temp_product.getProduct_code(),
                                String.valueOf(count_product));
                        db_cart.addItemTocart(cartitem);
                        Toast.makeText(ScanActivity.this, "Item Added!", Toast.LENGTH_SHORT).show();
                        showhide_product(false);
                    }
                }
            }
        });

        goto_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db_cart.getCartItemsCount() > 0){
                    Intent startscan = new Intent(ScanActivity.this, User_cartListActivity.class);
                    startActivity(startscan);
                }else{
                    Toast.makeText(ScanActivity.this, "First Add Items!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setcounttext(String value) {
        item_count.setText(value);
    }

    public void showhide_product(boolean value) {
        if (value) {
            product_detail.setVisibility(View.VISIBLE);
            setcounttext(count_product + "");
            product_name.setText(temp_product.getName());
            product_price.setText(temp_product.getPrice());
            Glide.with(ScanActivity.this).load(temp_product.getImage()).error(R.drawable.qr_code_scan).into(product_image);
        } else {
            count_product = 1;
            setcounttext(count_product + "");
            product_detail.setVisibility(View.INVISIBLE);
            product_name.setText("");
            product_price.setText("");
            product_image.setImageResource(R.drawable.qr_code_scan);
        }

    }


    public void onBackcall(View view) {
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
