package com.ddit.project.supermarketcheckouter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class ProcessPaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_payment);

        // Retrieve user information from Intent extras
        String userEmail = getIntent().getStringExtra("userEmail");
        String dateTime = getIntent().getStringExtra("dateTime");
        float totalAmount = getIntent().getFloatExtra("totalAmount", 0);
        String transactionId = generateTransactionId();

        // Find TextView to display transaction ID
        TextView transactionIdTextView = findViewById(R.id.tranID);
        TextView emailTextView = findViewById(R.id.tranemail);
        TextView dateTextView = findViewById(R.id.date);
        TextView totalAmountTextView = findViewById(R.id.bill_title);

        // Set user information to TextViews
        transactionIdTextView.setText(transactionId);
        emailTextView.setText(userEmail);
        dateTextView.setText(dateTime);
        totalAmountTextView.setText(String.valueOf(totalAmount));

        Button okButton = findViewById(R.id.okbtn);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the homepage
                Intent intent = new Intent(ProcessPaymentActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finish the current activity
            }
        });

    }
    private String generateTransactionId() {
        long timestamp = System.currentTimeMillis();
        String uniqueId = UUID.randomUUID().toString();
        return "TXN-" + timestamp + "-" + uniqueId;
    }
}
