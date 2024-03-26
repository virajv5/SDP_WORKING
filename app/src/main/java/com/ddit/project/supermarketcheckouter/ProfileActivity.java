package com.ddit.project.supermarketcheckouter;

import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_ID;
import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_email;
import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_name;
import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_photourl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profile_update;
    EditText et_user_name;
    TextView tv_user_email;
    TextView upload_image;
    TextView update_name;
    Uri new_profile_link = null;
    PrefStorageManager pref;
    String old_name = "";
    FirebaseStorage storage;
    StorageReference storageReference;
    CardView loading_home;
    String temp_user;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_profile);
        pref = new PrefStorageManager(ProfileActivity.this);

        temp_user = pref.getStringvaluedef(SHARED_USER_ID, "");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        profile_update = findViewById(R.id.profile_update);
        loading_home = findViewById(R.id.loading_home);
        loading_home.setVisibility(View.GONE);
        et_user_name = findViewById(R.id.et_user_name);
        tv_user_email = findViewById(R.id.tv_user_email);
        upload_image = findViewById(R.id.upload_image);
        update_name = findViewById(R.id.update_name);
        old_name = pref.getStringvaluedef(SHARED_USER_name, "");
        String old_profile = pref.getStringvaluedef(SHARED_USER_photourl, "");
        et_user_name.setText(old_name);
        tv_user_email.setText(pref.getStringvaluedef(SHARED_USER_email, ""));
        try {
            if (old_profile != null && !old_profile.equals("")) {
                Glide.with(ProfileActivity.this).load(old_profile).into(profile_update);
            } else {
                Glide.with(ProfileActivity.this).load(R.drawable.user_default).into(profile_update);
            }
        } catch (NullPointerException e) {
            Glide.with(ProfileActivity.this).load(R.drawable.user_default).into(profile_update);
        }

        profile_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(ProfileActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(512, 512)
                        .start();
            }
        });

        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new_profile_link == null) {
                    Toast.makeText(ProfileActivity.this, "Please Select Profile Image First", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }
            }
        });

        update_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_user_name.getText().length() <= 0) {
                    Toast.makeText(ProfileActivity.this, "Please Insert Name Properly!", Toast.LENGTH_SHORT).show();
                } else if (et_user_name.getText().toString().equals(old_name)) {
                    Toast.makeText(ProfileActivity.this, "Please Insert new name!", Toast.LENGTH_SHORT).show();
                } else {
                    updatename(et_user_name.getText().toString());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            new_profile_link = null;
            new_profile_link = data.getData();
            profile_update.setImageURI(new_profile_link);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {

        if (temp_user != null && !temp_user.equals("")) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            Log.e("getlast_catdata", "uploading  :  ");
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(new_profile_link).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    loading_home.setVisibility(View.VISIBLE);
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String uploaded_uri = String.valueOf(uri);
                            Map<String, Object> valuesclaim = new HashMap<>();
                            valuesclaim.put("photourl", uploaded_uri);

                            FirebaseDatabase.getInstance().getReference().child("user").child(temp_user).updateChildren(valuesclaim).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    pref.setStringprefrence(SHARED_USER_photourl,uploaded_uri);
                                    loading_home.setVisibility(View.GONE);
                                    Toast.makeText(ProfileActivity.this, "Profile Photo Update SuccessFully", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        } else {
            Toast.makeText(ProfileActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
        }
    }


    private void updatename(String up_name) {

        if (temp_user != null && !temp_user.equals("")) {
            loading_home.setVisibility(View.VISIBLE);
            Map<String, Object> valuesclaim = new HashMap<>();
            valuesclaim.put("name", up_name);

            FirebaseDatabase.getInstance().getReference().child("user").child(temp_user).updateChildren(valuesclaim).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pref.setStringprefrence(SHARED_USER_name,up_name);
                    loading_home.setVisibility(View.GONE);
                    Toast.makeText(ProfileActivity.this, "Name Update SuccessFully", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ProfileActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
        }
    }

}
