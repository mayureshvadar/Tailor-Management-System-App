package com.example.tailormanagementsystem.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.tailormanagementsystem.network.InvoiceApi;

public class InvoiceDownloader {

    public static void downloadInvoice(Context context, Long orderId, InvoiceApi api) {
        Call<ResponseBody> call = api.downloadInvoice(orderId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // 🧵 Save PDF to cache directory
                        File pdfFile = new File(context.getCacheDir(), "invoice_" + orderId + ".pdf");
                        FileOutputStream fos = new FileOutputStream(pdfFile);
                        fos.write(response.body().bytes());
                        fos.close();

                        // 📤 Share or View using FileProvider
                        Uri uri = FileProvider.getUriForFile(
                                context,
                                context.getPackageName() + ".fileprovider",
                                pdfFile
                        );

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        context.startActivity(intent);

                    } catch (Exception e) {
                        Log.e("InvoiceDownloader", "Error saving/opening PDF: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Log.e("InvoiceDownloader", "Response failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("InvoiceDownloader", "API call failed: " + t.getMessage());
            }
        });
    }
    public static void shareInvoice(Context context, Long orderId, InvoiceApi api) {
        Call<ResponseBody> call = api.downloadInvoice(orderId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        File pdfFile = new File(context.getCacheDir(), "invoice_" + orderId + ".pdf");
                        FileOutputStream fos = new FileOutputStream(pdfFile);
                        fos.write(response.body().bytes());
                        fos.close();

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
}