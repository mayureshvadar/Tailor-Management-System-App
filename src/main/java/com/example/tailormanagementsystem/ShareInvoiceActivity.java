package com.example.tailormanagementsystem;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.tailormanagementsystem.network.InvoiceApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShareInvoiceActivity extends AppCompatActivity {

    private EditText orderIdInput;
    private Button shareButton;
    private InvoiceApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_invoice);

        orderIdInput = findViewById(R.id.edit_order_id);
        shareButton = findViewById(R.id.btn_share_invoice);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tailor-management-system-1d795.web.app") // Replace with your backend IP
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(InvoiceApi.class);

        // Request permission for Android < 10
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }
        }

        shareButton.setOnClickListener(v -> {
            String idText = orderIdInput.getText().toString().trim();
            if (!idText.isEmpty()) {
                Long orderId = Long.parseLong(idText);
                downloadAndSharePdf(this, orderId);
            } else {
                Toast.makeText(this, "Please enter a valid Order ID.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadAndSharePdf(Context context, Long orderId) {
        Call<ResponseBody> call = api.downloadInvoice(orderId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String fileName = "invoice_" + orderId + ".pdf";
                        File pdfFile = savePdfToDownloads(context, response.body().bytes(), fileName);

                        // Save invoice metadata
                        String customerName = "mayuresh vadar"; // Replace with actual name
                        String savedFilePath = pdfFile.getAbsolutePath();
                        saveInvoiceToDatabase(context, customerName, String.valueOf(orderId), savedFilePath);

                        // Share intent
                        Uri uri = FileProvider.getUriForFile(
                                context,
                                context.getPackageName() + ".fileprovider",
                                pdfFile
                        );

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("application/pdf");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        context.startActivity(Intent.createChooser(shareIntent, "Share Invoice via"));

                    } catch (Exception e) {
                        Toast.makeText(context, "Error sharing invoice.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch invoice.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Network error while sharing.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File savePdfToDownloads(Context context, byte[] pdfBytes, String fileName) throws Exception {
        File file;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) throw new Exception("Failed to create file URI");

            try (OutputStream out = context.getContentResolver().openOutputStream(uri)) {
                out.write(pdfBytes);
            }

            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        } else {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            file = new File(downloadsDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(pdfBytes);
            }
        }

        return file;
    }

    private void saveInvoiceToDatabase(Context context, String customerName, String orderId, String savedFilePath) {
        InvoiceEntity invoice = new InvoiceEntity();
        invoice.customerName = customerName;
        invoice.orderId = orderId;
        invoice.filePath = savedFilePath;
        invoice.date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        InvoiceDatabase.getInstance(context).invoiceDao().insert(invoice);
    }
}