package com.example.tailormanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tailormanagementsystem.network.InvoiceApi;
import com.example.tailormanagementsystem.utils.InvoiceDownloader;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadInvoiceActivity extends AppCompatActivity {

    private EditText orderIdInput;
    private Button downloadButton;
    private InvoiceApi invoiceApi;
    private Button shareButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_invoice);

        EditText orderIdInput;
        Button downloadButton;
        Button shareButton;
        orderIdInput = findViewById(R.id.edit_order_id);
        downloadButton = findViewById(R.id.btn_download_invoice);
        shareButton = findViewById(R.id.btn_share_invoice);

        // 🧵 Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tailor-management-system-1d795.web.app") // Replace with your actual IP
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        InvoiceApi api = retrofit.create(InvoiceApi.class);

        // 📲 Button Click Listener
        downloadButton.setOnClickListener(v -> {
            String idText = orderIdInput.getText().toString().trim();
            if (!idText.isEmpty()) {
                Long orderId = Long.parseLong(idText);
                InvoiceDownloader.downloadInvoice(this, orderId, invoiceApi);
            } else {
                Toast.makeText(this, "Please enter a valid Order ID.", Toast.LENGTH_SHORT).show();
            }
        });

        shareButton.setOnClickListener(v -> {
            String idText = orderIdInput.getText().toString().trim();
            if (!idText.isEmpty()) {
                Long orderId = Long.parseLong(idText);
                InvoiceDownloader.shareInvoice(this, orderId, invoiceApi);
            } else {
                Toast.makeText(this, "Please enter a valid Order ID.", Toast.LENGTH_SHORT).show();
            }
        });



    }
}