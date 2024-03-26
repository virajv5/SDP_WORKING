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
import com.ddit.project.supermarketcheckouter.Adapter.UserListAdapter;
import com.ddit.project.supermarketcheckouter.Models.User;

import java.util.ArrayList;
import java.util.Collections;

public class Admin_UserListActivity extends AppCompatActivity{

    PrefStorageManager pref;
    String temp_user;
    RecyclerView recview_branchlist;
    UserListAdapter mAdapter;
    LinearLayout no_data_ll;
    CardView loading_cardview;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_admin_user_list);
        pref = new PrefStorageManager(Admin_UserListActivity.this);
        temp_user = pref.getStringvaluedef(SHARED_USER_ID, "");


        loading_cardview = findViewById(R.id.loading_cardview);
        no_data_ll = findViewById(R.id.no_data_ll);
        no_data_ll.setVisibility(View.GONE);
        recview_branchlist = (RecyclerView) findViewById(R.id.recview_branchlist);
        recview_branchlist.setHasFixedSize(true);
        recview_branchlist.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new UserListAdapter(Admin_UserListActivity.this);
        recview_branchlist.setAdapter(mAdapter);

        loading_cardview.setVisibility(View.VISIBLE);
        getAllUserList();

    }


    private void getAllUserList() {
        FirebaseDatabase.getInstance().getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<User> temp_user = new ArrayList<User>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String userid = (String) ds.child("userid").getValue();
                        String name = (String) ds.child("name").getValue();
                        String email = (String) ds.child("email").getValue();
                        String photourl = (String) ds.child("photourl").getValue();
                        String serverauthcode = (String) ds.child("serverauthcode").getValue();
                        String reward = (String) ds.child("reward").getValue();
                        String type = (String) ds.child("type").getValue();
                        String createdate = (String) ds.child("createdate").getValue();
                        String active = (String) ds.child("active").getValue();
                        temp_user.add(new User(userid, name, email, photourl, serverauthcode, reward, type, createdate, active));
                    }

                    if (temp_user.size() > 0) {
                        loading_cardview.setVisibility(View.GONE);
                        Collections.reverse(temp_user);
                        mAdapter.additem(temp_user);
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

    public void onBackcall(View view){
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
