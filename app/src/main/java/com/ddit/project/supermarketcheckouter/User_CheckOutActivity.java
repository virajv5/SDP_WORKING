package com.ddit.project.supermarketcheckouter;

import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_reward;
import static com.ddit.project.supermarketcheckouter.Constant.update_reward;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ddit.project.supermarketcheckouter.Models.Order_GetSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ddit.project.supermarketcheckouter.Adapter.BilltemAdapter;
import com.ddit.project.supermarketcheckouter.Models.CartItem_GetSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.ArrayList;
import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_ID;
import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_email;
import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_name;
import static com.ddit.project.supermarketcheckouter.Constant.update_reward;
public class User_CheckOutActivity extends AppCompatActivity implements PaymentResultListener {

    public BilltemAdapter mBillListAdapter;

    public RecyclerView basketRecycler;
    PrefStorageManager pref;
    CardView loading_cardview;
    LinearLayout no_data_ll;
    CartDbHandler db_cart;
    TextView totalPriceTextView;
    RelativeLayout reward_rl;
    RelativeLayout total_main_rl;
    TextView rewardamount;
    TextView final_pay;
    String reward_coins = "";
    boolean check_use_reward = false;
    CheckBox use_reward;
    float counttotal_price = 0;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_bill_payment);

        Constant.getupi_id = "";
        Constant.pass_payment_value = 0;
        db_cart = new CartDbHandler(this);
        pref = new PrefStorageManager(User_CheckOutActivity.this);
        this.basketRecycler = (RecyclerView) findViewById(R.id.basketRecycler);
        reward_coins = pref.getStringvaluedef(SHARED_USER_reward, "");
        this.final_pay = findViewById(R.id.final_pay);
        this.rewardamount = findViewById(R.id.rewardamount);
        this.total_main_rl = findViewById(R.id.total_main_rl);
        this.reward_rl = findViewById(R.id.reward_rl);
        this.use_reward = findViewById(R.id.use_reward);

        rewardamount.setText("0.0");
        reward_rl.setVisibility(View.GONE);
        total_main_rl.setVisibility(View.GONE);

        if (!reward_coins.isEmpty()) {
            if (Float.parseFloat(reward_coins) > 0) {
                check_use_reward = true;
                use_reward.setChecked(check_use_reward);
                total_main_rl.setVisibility(View.VISIBLE);
                reward_rl.setVisibility(View.VISIBLE);
                rewardamount.setText(reward_coins);
            }
        }

        use_reward.setOnCheckedChangeListener((compoundButton, b) -> {
            check_use_reward = !check_use_reward;
            updatefinalAmount();
        });

        this.loading_cardview = findViewById(R.id.loading_cardview);
        this.no_data_ll = findViewById(R.id.no_data_ll);
        this.totalPriceTextView = findViewById(R.id.totalPriceTextView);
        no_data_ll.setVisibility(View.GONE);

        loading_cardview.setVisibility(View.VISIBLE);
        basketRecycler.setVisibility(View.VISIBLE);

        this.mBillListAdapter = new BilltemAdapter(this);

        this.basketRecycler.setLayoutManager(new LinearLayoutManager(this));
        getAllClaimList();
        findViewById(R.id.checkoutButton).setOnClickListener(view -> {
            loading_cardview.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference().child("default").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Constant.getupi_id = snapshot.child("upi_id").getValue().toString();
                    }
                    if (!Constant.getupi_id.equals("") && Constant.pass_payment_value > 0) {
                        update_reward = check_use_reward;
                        totalPriceTextView.getText();
                        updatefinalAmount();
                        startPayment();
//                            Intent intent = new Intent(User_CheckOutActivity.this, User_PaymentActivity.class);
//                            intent.putExtra("totalAmount", Constant.pass_payment_value); // Pass total amount to PaymentActivity
//                            startActivity(intent);
//                            finish();

                    } else {
                        loading_cardview.setVisibility(View.GONE);
                        Toast.makeText(User_CheckOutActivity.this, "Please Try Again!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loading_cardview.setVisibility(View.GONE);
                    Toast.makeText(User_CheckOutActivity.this, "Please Try Again!", Toast.LENGTH_SHORT).show();
                }

            });

        });
    }

    private void getAllClaimList() {
        if (db_cart.getCartItemsCount() > 0) {
            basketRecycler.setAdapter(mBillListAdapter);
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
        if (!list.isEmpty()) {
            counttotal_price = 0;
            for (int i = 0; i < list.size(); i++) {
                counttotal_price = (counttotal_price + (Float.parseFloat(list.get(i).getProduct_items()) * Float.parseFloat(list.get(i).getPrice())));
            }
            totalPriceTextView.setText(counttotal_price + "");
            updatefinalAmount();
        } else {
            totalPriceTextView.setText("0");
            no_data_ll.setVisibility(View.VISIBLE);
            basketRecycler.setVisibility(View.GONE);
        }
    }

    public void updatefinalAmount() {
        if (check_use_reward) {
            Constant.pass_payment_value = (counttotal_price - Float.parseFloat(reward_coins));
        } else {
            Constant.pass_payment_value = counttotal_price;
        }
        final_pay.setText(Constant.pass_payment_value + "");
    }

    public void onBackcall(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_zwNmecWUvEydiP");
        String userName = getIntent().getStringExtra("userName");

        JSONObject object = new JSONObject();
        try {
            object.put("name", pref.getStringvalue(SHARED_USER_name));
            object.put("description", "Test payment");
            object.put("theme.color", "");
            object.put("currency", "INR");
            object.put("amount", (int) (Constant.pass_payment_value * 100)); // Convert amount to paisa
            object.put("prefill.contact", "8140559995");
            object.put("prefill.email", pref.getStringvalue(SHARED_USER_email));
            object.put("payment.method", "upi");
//            object.put("payment_status", status_glob);
            checkout.open(User_CheckOutActivity.this, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        String userEmail = pref.getStringvalue(SHARED_USER_email);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String json_product = gson.toJson(db_cart.getAllCartLists());

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Fetch the highest current order_id
        ordersRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int lastOrderId = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                        Order_GetSet order = orderSnapshot.getValue(Order_GetSet.class);
                        if (order != null && order.getOrder_id() != null && !order.getOrder_id().isEmpty()) {
                            try {
                                lastOrderId = Integer.parseInt(order.getOrder_id());
                            } catch (NumberFormatException e) {
                                Log.e("OrderIDError", "Failed to parse order_id to integer", e);
                            }
                        }
                    }
                }

                int newOrderId = lastOrderId + 1;
                String dateTime = java.text.DateFormat.getDateTimeInstance().format(new Date());

                Order_GetSet newOrder = new Order_GetSet(
                        String.valueOf(newOrderId),
                        s,
                        pref.getStringvalue(SHARED_USER_ID),
                        pref.getStringvalue(SHARED_USER_name),
                        json_product,
                        String.valueOf(Constant.pass_payment_value),
                        dateTime,
                        "success",
                        "0"
                );

                // Push the new order to Firebase and then navigate to success page
                ordersRef.push().setValue(newOrder).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully recorded in Firebase
                        Toast.makeText(User_CheckOutActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();

                        // Navigate to ProcessPaymentActivity
                        Intent intent = new Intent(User_CheckOutActivity.this, ProcessPaymentActivity.class);
                        intent.putExtra("userEmail", userEmail);
                        intent.putExtra("dateTime", dateTime);
                        intent.putExtra("totalAmount", Constant.pass_payment_value);
                        startActivity(intent);
                        finish();
                    } else {
                        // Handle failure
                        Toast.makeText(User_CheckOutActivity.this, "Failed to place order: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch orders", error.toException());
            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
    }
}
