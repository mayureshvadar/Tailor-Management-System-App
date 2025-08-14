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

public class PDFHandlerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Example usage — call this when you get PDF response from Retrofit
        // Assume responseBody is passed in or fetched earlier
        // Example filename: "bill_101.pdf"
        // Replace with your actual ResponseBody and order ID
        Long orderId = 101L;
        ResponseBody responseBody = getPdfResponse(); // <-- Replace this with actual response

        if (responseBody != null) {
            savePdfToCache(responseBody, "bill_" + orderId + ".pdf");
        } else {
            Toast.makeText(this, "No PDF data to save", Toast.LENGTH_SHORT).show();
        }
    }

    // Save PDF to internal cache directory
    private void savePdfToCache(ResponseBody body, String fileName) {
        try {
            File pdfFile = new File(getCacheDir(), fileName);
            FileOutputStream fos = new FileOutputStream(pdfFile);
            fos.write(body.bytes());
            fos.close();

            sharePdf(pdfFile); // Share the file after saving
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
        }
    }

    // Launch Android share dialog
    private void sharePdf(File file) {
        Uri uri = FileProvider.getUriForFile(
                this,
                "com.tailor.app.provider", // must match AndroidManifest authority
                file
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share Tailor Bill"));
    }

    // Dummy method for illustration — replace this with your Retrofit response
    private ResponseBody getPdfResponse() {
        // You’ll pass or receive ResponseBody from your API call instead
        return null;
    }
}