package com.ddit.project.supermarketcheckouter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ddit.project.supermarketcheckouter.Models.Product_GetSet;
import com.ddit.project.supermarketcheckouter.Models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Constant {

    public static Product_GetSet pass_prdct_value = new Product_GetSet();
    public static float pass_payment_value = 0;
    public static int passing_prdct_addoredit = 0;
    public static String getupi_id = "";
    public static boolean update_reward = false;

    public static final String SHARED_USER_ID = "GLOB_USER_ID";
    public static final String SHARED_USER_name = "GLOB_USER_name";
    public static final String SHARED_USER_email = "GLOB_USER_email";
    public static final String SHARED_USER_photourl = "GLOB_USER_photourl";
    public static final String SHARED_USER_serverauthcode = "GLOB_USER_serverauthcode";
    public static final String SHARED_USER_reward = "GLOB_USER_userREWARD";
    public static final String SHARED_USER_TYPE = "GLOB_USER_TYPE";
    public static final String SHARED_USER_isACTIVE = "GLOB_USER_isactive";

    static AlertDialog alertDialog;

    public static void show_dialog_common(Context mContext, String Title, String Sub_title, String Action_yes, String cancellable, String type, String ads_bool, String image_url, calling_dialogaction dialog_act) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_common_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);
        TextView text_title = view.findViewById(R.id.textTitle);
        TextView text_message = view.findViewById(R.id.textMessage);
        Button button_Action = view.findViewById(R.id.buttonAction);
        ImageView imageIcon = view.findViewById(R.id.imageIcon);
        ImageView imageglob = view.findViewById(R.id.imageglob);
        TextView ads_icon = view.findViewById(R.id.ads_icon);
        imageglob.setVisibility(View.GONE);
        ads_icon.setVisibility(View.GONE);
        if (ads_bool.equals("1")) {
            ads_icon.setVisibility(View.VISIBLE);
        }

        if (image_url != null && !image_url.equals("")) {
            imageglob.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(image_url).into(imageglob);
        }

        text_title.setText(Title);
        text_message.setText(Sub_title);
        button_Action.setText(Action_yes);
        imageIcon.setImageResource(R.drawable.close_done);

        if (cancellable.equals("1")) {
            imageIcon.setVisibility(View.GONE);
        } else {
            imageIcon.setVisibility(View.VISIBLE);
        }

        if (type.equals("success")) {
            text_title.setBackground(mContext.getResources().getDrawable(R.drawable.success_background));
            button_Action.setBackground(mContext.getResources().getDrawable(R.drawable.button_success_background));
        } else if (type.equals("warning")) {
            text_title.setBackground(mContext.getResources().getDrawable(R.drawable.warning_background));
            button_Action.setBackground(mContext.getResources().getDrawable(R.drawable.button_warning_background));
        } else if (type.equals("error")) {
            text_title.setBackground(mContext.getResources().getDrawable(R.drawable.error_background));
            button_Action.setBackground(mContext.getResources().getDrawable(R.drawable.button_error_background));
        }

        alertDialog = builder.create();
        button_Action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                dialog_act.call_action();
            }
        });

        imageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(layoutParams);
    }

    public static void call_dismisslastDialog() {
        if (alertDialog != null) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
                alertDialog = null;
            }
        }
    }

    public interface calling_dialogaction {
        public void call_action();
    }

    public static boolean IS_DEVELOPMENT = true;

    public static ArrayList<Product_GetSet> glob_product = new ArrayList<>();


    public static boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                psLog("Permission is granted");
                return true;
            } else {
                psLog("Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            psLog("Permission is granted");
            return true;
        }
    }


    public static void psLog(String log) {
        if (IS_DEVELOPMENT) {
            Log.d("TEAMPS", log);
        }
    }


    public static void snackbarwith_ActionView(Context mcontext, View commonlayout, String snackmsg, String Actionname, final callsnackresponse callsnack) {
        Snackbar snackbar = Snackbar.make(commonlayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction(Actionname, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callsnack.onsnackbarclick();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorAccent));
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.WHITE);
        snackbar.setActionTextColor(Color.GRAY);
        snackbar.show();
    }

    public interface callsnackresponse {
        void onsnackbarclick();
    }

    public static User user1;
    public static GoogleSignInResult result;

    public static void handleSignInCommon(Activity activity, callbackusersignin callback) {
        Log.e("getsigningcall","1");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        Log.e("getsigningcall","2");
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            result = opr.get();
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    result = googleSignInResult;
                }
            });
        }

        Log.e("getsigningcall","3");
        PrefStorageManager pref = new PrefStorageManager(activity);
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            Log.e("getsigningcall","4 success");
            FirebaseDatabase.getInstance().getReference("user").child(account.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot singleSnapshot) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss aa");
                    String currentDateTimeString = sdf.format(new Date()).replace("AM", "am").replace("PM", "pm");
                    if (singleSnapshot.exists()) {
                        user1 = singleSnapshot.getValue(User.class);
                    } else {
                        user1 = new User(account.getId(), account.getDisplayName(), account.getEmail(), account.getPhotoUrl().toString(), account.getServerAuthCode(), "0", "1", currentDateTimeString, "true");
                        FirebaseDatabase.getInstance().getReference("user").child(account.getId()).setValue(user1);
                    }

                    pref.setStringprefrence(SHARED_USER_ID, user1.getUserid());
                    pref.setStringprefrence(SHARED_USER_name, user1.getName());
                    pref.setStringprefrence(SHARED_USER_email, user1.getEmail());
                    pref.setStringprefrence(SHARED_USER_photourl, user1.getPhotourl());
                    pref.setStringprefrence(SHARED_USER_serverauthcode, user1.getServerauthcode());
                    pref.setStringprefrence(SHARED_USER_reward, user1.getReward());
                    pref.setStringprefrence(SHARED_USER_TYPE, user1.getType());
                    pref.setStringprefrence(SHARED_USER_isACTIVE, user1.getActive());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("getsigningcall","7 callback call");
                            callback.returnusersignin("true");
                        }
                    },500);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("getsigningcall","6 cancle");
                    callback.returnusersignin("false");
                }
            });
        } else {
            Log.e("getsigningcall","5 fail");
            callback.returnusersignin("false");
        }
    }

    public interface callbackusersignin {
        public void returnusersignin(String status);
    }
}
