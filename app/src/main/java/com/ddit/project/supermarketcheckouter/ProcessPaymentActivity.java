package com.ddit.project.supermarketcheckouter;
import static com.ddit.project.supermarketcheckouter.Constant.SHARED_USER_name;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;

import com.ddit.project.supermarketcheckouter.Models.CartItem_GetSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ProcessPaymentActivity extends AppCompatActivity {
    private String userEmail;
    private String dateTime;
    private float totalAmount;
    private String transactionId;
    private ArrayList<CartItem_GetSet> cartItems;
    PrefStorageManager pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_payment);

        CartDbHandler dbHandler = new CartDbHandler(this);
        pref = new PrefStorageManager(ProcessPaymentActivity.this);

        cartItems = dbHandler.getAllCartLists();
        // Retrieve user information from Intent extras
        userEmail = getIntent().getStringExtra("userEmail");
        dateTime = getIntent().getStringExtra("dateTime");
        totalAmount = getIntent().getFloatExtra("totalAmount", 0);
        transactionId = generateTransactionId();

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

        Button generateBillButton = findViewById(R.id.generateBillButton);
        generateBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProcessPaymentActivity.this, "Bill Generating", Toast.LENGTH_SHORT).show();
                generateBill(cartItems);
            }
        });

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

    private void generateBill(ArrayList<CartItem_GetSet> cartItems) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        // Use current date and time in the file name
        String fileName = "Supermarket_Bill_" + currentDateTime + ".pdf";

        // Get the directory for storing documents
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Supermarket");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);

        try {
            // Create a file output stream for the PDF file
            FileOutputStream outputStream = new FileOutputStream(file);
            PdfDocument pdfDocument = new PdfDocument();
            android.graphics.pdf.PdfDocument.PageInfo pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            canvas.drawColor(Color.parseColor("#F5F5F5")); // Use your app's theme color

            // Set font size and style for title
            paint.setColor(Color.BLACK);
            paint.setTextSize(24);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Supermarket Bill", 50, 80, paint); // Adjust position
            // Draw a horizontal line below the title
            paint.setColor(Color.BLACK);
            canvas.drawLine(50, 110, 545, 110, paint); // Adjust position
            // Set font size and style for header information
            paint.setTextSize(12);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            canvas.drawText("Transaction ID: " + transactionId, 50, 140, paint); // Adjust position
            canvas.drawText("Date: " + dateTime, 50, 160, paint); // Adjust position
            canvas.drawText("Customer Name: " + pref.getStringvalue(SHARED_USER_name), 50, 180, paint); // Adjust position
            canvas.drawText("Email Id: " + userEmail, 50, 200, paint); // Adjust position

            // Draw a horizontal line below the header information
            canvas.drawLine(50, 220, 545, 220, paint); // Adjust position
            // Draw table headers
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Item", 50, 260, paint); // Adjust position
            canvas.drawText("Qty", 250, 260, paint); // Adjust position
            canvas.drawText("Price", 350, 260, paint); // Adjust position
            canvas.drawText("Total", 450, 260, paint); // Adjust position
            // Draw a horizontal line below the table headers
            canvas.drawLine(50, 280, 545, 280, paint); // Adjust position
            // Set font size and style for table content
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            int startY = 300; // Adjust position
            float totalBillAmount = 0;
            for (int i = 0; i < cartItems.size(); i++) {
                CartItem_GetSet item = cartItems.get(i);
                canvas.drawText(item.getName(), 50, startY + i * 40, paint); // Adjust position
                canvas.drawText(item.getProduct_items(), 250, startY + i * 40, paint); // Adjust position
                canvas.drawText("₹" + item.getPrice(), 350, startY + i * 40, paint); // Adjust position
                float totalPrice = Float.parseFloat(item.getProduct_items()) * Float.parseFloat(item.getPrice());
                totalBillAmount += totalPrice;
                canvas.drawText("₹" + String.format("%.2f", totalPrice), 450, startY + i * 40, paint); // Adjust position
            }
            // Draw a horizontal line below the table content
            canvas.drawLine(50, startY + cartItems.size() * 40 + 20, 545, startY + cartItems.size() * 40 + 20, paint); // Adjust position
            // Set font size and style for total amount
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Total Amount:", 350, startY + cartItems.size() * 40 + 50, paint); // Adjust position
            canvas.drawText("₹" + String.format("%.2f", totalBillAmount), 450, startY + cartItems.size() * 40 + 50, paint); // Adjust position
            // Draw a horizontal line below the total amount
            canvas.drawLine(350, startY + cartItems.size() * 40 + 70, 545, startY + cartItems.size() * 40 + 70, paint); // Adjust position
            // Set font size and style for footer
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            String thankYouMessage = "Thank you for shopping with us! Visit again soon!";
            Rect bounds = new Rect();
            paint.getTextBounds(thankYouMessage, 0, thankYouMessage.length(), bounds);
            int messageWidth = bounds.width();
            int messageHeight = bounds.height();
            int messageX = (pageInfo.getPageWidth() - messageWidth) / 2;
            int messageY = 750; // Adjust position as needed
            canvas.drawText(thankYouMessage, messageX, messageY, paint);

            // Set border color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);

            // Draw border around the bill content
            canvas.drawRect(30, 30, 565, 780, paint);

            // Finish writing to the PDF document
            pdfDocument.finishPage(page);

            // Write the PDF content to the output stream
            pdfDocument.writeTo(outputStream);

            // Close the PDF document and output stream
            pdfDocument.close();
            outputStream.close();

            // Notify the user that the bill has been generated
            Toast.makeText(ProcessPaymentActivity.this, "Bill generated successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Handle file IO exception
            e.printStackTrace();
            Toast.makeText(ProcessPaymentActivity.this, "Failed to generate bill", Toast.LENGTH_SHORT).show();
        }
    }



    private String generateTransactionId() {
        long timestamp = System.currentTimeMillis();
        String uniqueId = UUID.randomUUID().toString();
        return "TXN-" + timestamp + "-" + uniqueId;
    }
}
