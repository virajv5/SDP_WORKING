package com.ddit.project.supermarketcheckouter;

import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_reward;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RewardActivity extends AppCompatActivity {

    PrefStorageManager pref;

    ImageView redeem_now;
    TextView earn_coin_tv;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_reward);


        pref = new PrefStorageManager(RewardActivity.this);

        redeem_now = findViewById(R.id.redeem_now);
        earn_coin_tv = findViewById(R.id.earn_coin_tv);
        earn_coin_tv.setText(pref.getStringvaluedef(SHARED_USER_reward, "") +" Coins");

        redeem_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isStoragePermissionGranted(RewardActivity.this)) {
                    Intent startscan = new Intent(RewardActivity.this, ScanActivity.class);
                    startActivity(startscan);
                }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Constant.isStoragePermissionGranted(RewardActivity.this)) {
                        Intent startscan = new Intent(RewardActivity.this, ScanActivity.class);
                        startActivity(startscan);
                    }
                } else {

                    Toast.makeText(RewardActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}
