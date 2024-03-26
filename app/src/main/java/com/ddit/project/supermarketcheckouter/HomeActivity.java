package com.ddit.project.supermarketcheckouter;


import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_email;
import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_name;
import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_photourl;
import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_reward;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ddit.project.supermarketcheckouter.menu.DrawerAdapter;
import com.ddit.project.supermarketcheckouter.menu.DrawerItem;
import com.ddit.project.supermarketcheckouter.menu.SimpleItem;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, DrawerAdapter.OnItemSelectedListener {

    private static final int POS_DASHBOARD = 0;
    private static final int POS_HISTORY = 1;
    private static final int POS_CHECKLIST = 2;
    private static final int POS_REWARD = 3;
    private static final int POS_ABOUT = 4;
    private static final int POS_LOGOUT = 5;

    PrefStorageManager pref;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    CircleImageView profileImage;
    CircleImageView profile_image_dr;

    CardView loading_home;
    TextView username;
    TextView email;
    TextView welcome_user;
    TextView earn_coin_tv;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_drawer);

        pref = new PrefStorageManager(HomeActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.ic_drawer);
        actionbar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        earn_coin_tv = findViewById(R.id.earn_coin_tv);
        welcome_user = findViewById(R.id.welcome_user);
        loading_home = findViewById(R.id.loading_home);
        loading_home.setVisibility(View.VISIBLE);
        profileImage = findViewById(R.id.profile_image);
        profile_image_dr = findViewById(R.id.profile_image_dr);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        findViewById(R.id.profilebg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startscan = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(startscan);

            }
        });

        findViewById(R.id.scan_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isStoragePermissionGranted(HomeActivity.this)) {
                    Intent startscan = new Intent(HomeActivity.this, ScanActivity.class);
                    startActivity(startscan);
                }

            }
        });

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_HISTORY),
                createItemFor(POS_CHECKLIST),
                createItemFor(POS_REWARD),
                createItemFor(POS_ABOUT),
                createItemFor(POS_LOGOUT)));

        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_DASHBOARD);

        welcome_user.setText("Welcome " + pref.getStringvaluedef(SHARED_USER_name, ""));
        username.setText(pref.getStringvaluedef(SHARED_USER_name, ""));
        earn_coin_tv.setText(pref.getStringvaluedef(SHARED_USER_reward, "") + " Coins");
        email.setText(pref.getStringvaluedef(SHARED_USER_email, ""));
        String img_url = pref.getStringvaluedef(SHARED_USER_photourl, "");
        try {
            if (img_url != null && !img_url.equals("")) {
                Glide.with(HomeActivity.this).load(img_url).into(profileImage);
                Glide.with(HomeActivity.this).load(img_url).into(profile_image_dr);
            } else {
                Glide.with(HomeActivity.this).load(R.drawable.user_default).into(profileImage);
                Glide.with(HomeActivity.this).load(R.drawable.user_default).into(profile_image_dr);
            }
        } catch (NullPointerException e) {
            Glide.with(HomeActivity.this).load(R.drawable.user_default).into(profileImage);
            Glide.with(HomeActivity.this).load(R.drawable.user_default).into(profile_image_dr);
        }
        loading_home.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            // Android home
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return true;
    }

    @Override
    public void onItemSelected(int position) {
        if (position == POS_LOGOUT) {
            gotoMainActivity();
        }
        if (position == POS_HISTORY) {
            Intent startscan = new Intent(HomeActivity.this, User_OrderListActivity.class);
            startActivity(startscan);
        }
        if (position == POS_CHECKLIST) {
            Intent startscan = new Intent(HomeActivity.this, User_cartListActivity.class);
            startActivity(startscan);
        }
        if (position == POS_REWARD) {
            Intent startscan = new Intent(HomeActivity.this, RewardActivity.class);
            startActivity(startscan);
        }
        if (position == POS_ABOUT) {
            Intent startscan = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(startscan);
        }
        mDrawerLayout.closeDrawers();

    }

    public void click_openreward(View view){
        Intent startscan = new Intent(HomeActivity.this, RewardActivity.class);
        startActivity(startscan);
    }



    @SuppressWarnings("rawtypes")
    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.colorPrimary))
                .withTextTint(color(R.color.colorPrimary))
                .withSelectedIconTint(color(R.color.colorPrimary))
                .withSelectedTextTint(color(R.color.colorPrimary));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    private void gotoMainActivity() {
        try {
            Intent int_log = new Intent(HomeActivity.this, Logout_Activity.class);
            int_log.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(int_log);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Constant.isStoragePermissionGranted(HomeActivity.this)) {
                        Intent startscan = new Intent(HomeActivity.this, ScanActivity.class);
                        startActivity(startscan);
                    }
                } else {

                    Toast.makeText(HomeActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        finishAffinity();
    }

}