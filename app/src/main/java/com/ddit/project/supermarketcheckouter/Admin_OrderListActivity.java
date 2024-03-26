package com.ddit.project.supermarketcheckouter;

import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_ID;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ddit.project.supermarketcheckouter.Adapter.OrderAdminAdapter;
import com.ddit.project.supermarketcheckouter.Models.Order_GetSet;

import java.util.ArrayList;
import java.util.Collections;

public class Admin_OrderListActivity extends AppCompatActivity implements OrderAdminAdapter.clickcallback {

    PrefStorageManager pref;
    String temp_user;
    RecyclerView recview_branchlist;
    OrderAdminAdapter mAdapter;
    LinearLayout no_data_ll;
    CardView loading_cardview;
    public ArrayList<Order_GetSet> Allorder = new ArrayList<>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_admin_order_list);
        pref = new PrefStorageManager(Admin_OrderListActivity.this);
        temp_user = pref.getStringvaluedef(SHARED_USER_ID, "");


        loading_cardview = findViewById(R.id.loading_cardview);
        no_data_ll = findViewById(R.id.no_data_ll);
        no_data_ll.setVisibility(View.GONE);
        recview_branchlist = (RecyclerView) findViewById(R.id.recview_branchlist);
        recview_branchlist.setHasFixedSize(true);
        recview_branchlist.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new OrderAdminAdapter(Admin_OrderListActivity.this, this);
        recview_branchlist.setAdapter(mAdapter);


        loading_cardview.setVisibility(View.VISIBLE);
        getAllOrderList();

    }


    private void getAllOrderList() {
        FirebaseDatabase.getInstance().getReference("order").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Allorder = new ArrayList<Order_GetSet>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String id = (String) ds.child("order_id").getValue();
                        String rid = (String) ds.child("payment_refid").getValue();
                        String u_id = (String) ds.child("user_id").getValue();
                        String u_name = (String) ds.child("user_name").getValue();
                        String list = (String) ds.child("productlist").getValue();
                        String amount = (String) ds.child("total_amount").getValue();
                        String ond = (String) ds.child("ondate").getValue();
                        String p_status = (String) ds.child("payment_status").getValue();
                        String approve = (String) ds.child("admin_approve").getValue();
                        Allorder.add(new Order_GetSet(id, rid, u_id, u_name, list, amount, ond,p_status,approve));
                    }

                    if (Allorder.size() > 0) {
                        loading_cardview.setVisibility(View.GONE);
                        Collections.reverse(Allorder);
                        mAdapter.additem(Allorder);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onBackcall(View view){
        onBackPressed();
    }


    @Override
    public void clickDetailsOrder(Order_GetSet item, int pos) {

    }
}
