package com.ddit.project.supermarketcheckouter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ddit.project.supermarketcheckouter.Adapter.CartItemAdapter;
import com.ddit.project.supermarketcheckouter.Models.CartItem_GetSet;

import java.util.ArrayList;

public class User_cartListActivity extends AppCompatActivity {

    public CartItemAdapter mWithdrawalListAdapter;

    public RecyclerView basketRecycler;
    PrefStorageManager pref;
    CardView loading_cardview;
    LinearLayout no_data_ll;
    CartDbHandler db_cart;
    TextView countTextView, totalPriceTextView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_cart);

        db_cart = new CartDbHandler(this);
        pref = new PrefStorageManager(User_cartListActivity.this);
        this.basketRecycler = (RecyclerView) findViewById(R.id.basketRecycler);

        this.loading_cardview = findViewById(R.id.loading_cardview);
        this.no_data_ll = findViewById(R.id.no_data_ll);
        this.countTextView = findViewById(R.id.countTextView);
        this.totalPriceTextView = findViewById(R.id.totalPriceTextView);
        no_data_ll.setVisibility(View.GONE);
        loading_cardview.setVisibility(View.VISIBLE);
        basketRecycler.setVisibility(View.VISIBLE);

        this.mWithdrawalListAdapter = new CartItemAdapter(this, new CartItemAdapter.clickcallback() {

            @Override
            public void clickTotalAmount(ArrayList<CartItem_GetSet> list) {
                updatetotalcounts(list);
            }
        });

        this.basketRecycler.setLayoutManager(new LinearLayoutManager(this));

        getAllClaimList();

        findViewById(R.id.checkoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db_cart.getCartItemsCount() > 0){
                    Intent startscan = new Intent(User_cartListActivity.this, User_CheckOutActivity.class);
                    startActivity(startscan);
                }else{
                    Toast.makeText(User_cartListActivity.this, "No Items Found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getAllClaimList() {
        if (db_cart.getCartItemsCount() > 0) {
            basketRecycler.setAdapter(mWithdrawalListAdapter);
            no_data_ll.setVisibility(View.GONE);
            loading_cardview.setVisibility(View.GONE);
            basketRecycler.setVisibility(View.VISIBLE);
            updatetotalcounts(db_cart.getAllCartLists());
        } else {
            no_data_ll.setVisibility(View.VISIBLE);
            loading_cardview.setVisibility(View.GONE);
            basketRecycler.setVisibility(View.GONE);
        }
    }

    public void updatetotalcounts(ArrayList<CartItem_GetSet> list) {
        if (list.size() > 0) {
            float countitems = 0;
            float counttotal_price = 0;
            for (int i = 0; i < list.size(); i++) {
                countitems = (countitems + Float.parseFloat(list.get(i).getProduct_items()));
                counttotal_price = (counttotal_price + (Float.parseFloat(list.get(i).getProduct_items()) * Float.parseFloat(list.get(i).getPrice())));
            }
            countTextView.setText(countitems + "");
            totalPriceTextView.setText(counttotal_price + "");
        } else {
            countTextView.setText("0");
            totalPriceTextView.setText("0");
            no_data_ll.setVisibility(View.VISIBLE);
            basketRecycler.setVisibility(View.GONE);
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
