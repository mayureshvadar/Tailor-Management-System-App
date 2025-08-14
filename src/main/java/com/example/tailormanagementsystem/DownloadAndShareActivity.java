package com.example.tailormanagementsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class DownloadAndShareActivity extends AppCompatActivity {

    interface TailorApiService {
        @GET("orders/bill/{orderId}")
        Call<ResponseBody> downloadBill(@Path("orderId") Long orderId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // You can also attach this logic to a button click!

        Long orderId = 1L; // Replace with actual order ID
        fetchAndShareBill(orderId);
    }

    private void fetchAndShareBill(Long orderId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tailor-management-system-1d795.web.app") // Replace with your backend URL
                .build();

        TailorApiService api = retrofit.create(TailorApiService.class);
        Call<ResponseBody> call = api.downloadBill(orderId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    savePdfToCache(response.body(), "bill_" + orderId + ".pdf");
                } else {
                    Toast.makeText(DownloadAndShareActivity.this, "Failed to download bill", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(DownloadAndShareActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePdfToCache(ResponseBody body, String fileName) {
        try {
            File file = new File(getCacheDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(body.bytes());
            fos.close();

            sharePdf(file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save bill", Toast.LENGTH_SHORT).show();
        }
    }

    private void sharePdf(File file) {
        Uri uri = FileProvider.getUriForFile(
                this,
                "com.tailor.management.provider", // Replace with your authority from Manifest
                file
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Tailor Bill"));
    }
}