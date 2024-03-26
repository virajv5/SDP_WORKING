package com.ddit.project.supermarketcheckouter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.ddit.project.supermarketcheckouter.R;

public class Logout_Activity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    ImageView logout_yesbtn;
    ImageView logout_nobtn;
    TextView logout_tc;
    PrefStorageManager pref;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        logout_yesbtn = findViewById(R.id.logout_yesbtn);
        logout_nobtn = findViewById(R.id.logout_nobtn);
        logout_tc = findViewById(R.id.logout_tc);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        logout_yesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        status -> {
                            if (status.isSuccess()) {
                                try {
                                    if (pref == null) {
                                        pref = new PrefStorageManager(Logout_Activity.this);
                                    }
                                    pref.Logoutpref();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(Logout_Activity.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Logout_Activity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Session not close", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        logout_nobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Logout_Activity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}