package com.example.tailormanagementsystem.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.tailormanagementsystem.Customer;
import com.example.tailormanagementsystem.models.Order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InvoiceGenerator {

    private final Context context;

    public InvoiceGenerator(Context context) {
        this.context = context;
    }


    public void generateWithActions(Customer customer, Order order) {
        File pdfFile = null;

        try {
            // 1. Create file
            String fileName = "TailorInvoice_" + customer.getName() + "_" + getDateStamp() + ".pdf";
            pdfFile = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), fileName);

            // 2. Create PDF document
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setTextSize(14);
            paint.setColor(Color.BLACK);

            // 3. Draw branded content
            canvas.drawText("🧵 Tailor Invoice", 10, 25, paint);
            canvas.drawText("Customer: " + customer.getName(), 10, 50, paint);
            canvas.drawText("Order: " + order.getClass(), 10, 75, paint);
            canvas.drawText("Amount: ₹" + order.getClass(), 10, 100, paint);
            canvas.drawText("Date: " + getDateStamp(), 10, 125, paint);

            document.finishPage(page);
            document.writeTo(new FileOutputStream(pdfFile));
            document.close();

            // 4. Toast confirmation
            Toast.makeText(context, "Invoice saved to Downloads 📂", Toast.LENGTH_SHORT).show();

            // 5. Show action sheet
            showActionSheet(pdfFile);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save invoice ❌", Toast.LENGTH_SHORT).show();
        }
    }

    private void showActionSheet(File file) {
        String[] options = {"👁️ View Invoice", "📤 Share Invoice", "📂 Open Folder"};

        new AlertDialog.Builder(context)
                .setTitle("Invoice Saved 🎉")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: viewPdf(file); break;
                        case 1: sharePdf(file); break;
                        case 2: openFolder(file); break;
                    }
                })
                .show();
    }

    public void viewPdf(File file) {
        Uri uri = FileProvider.getUriForFile(
                context,
                "com.example.tailormanagementsystem.fileprovider",
                file
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "No PDF viewer found 📄", Toast.LENGTH_SHORT).show();
        }
    }

    public void sharePdf(File file) {
        Uri uri = FileProvider.getUriForFile(
                context,
                "com.example.tailormanagementsystem.fileprovider",
                file
        );

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(intent, "📤 Share Invoice via"));
    }

    public void openFolder(File file) {
        Uri uri = Uri.parse(file.getParentFile().toURI().toString());

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "*/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "No file manager found 📁", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDateStamp() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
    public File generateInvoicePdf(String customerName, String orderDetails, String invoiceId, String totalAmount) {
        File file = null;

        try {
            // 1. Create PDF document
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            // 2. Header branding
            paint.setTextSize(20);
            paint.setColor(Color.BLACK);
            paint.setFakeBoldText(true);
            canvas.drawText("👕 Yash Mens Wear", 40, 60, paint);

            paint.setStrokeWidth(2);
            canvas.drawLine(40, 80, 550, 80, paint);

            // 3. Invoice details
            paint.setTextSize(14);
            paint.setFakeBoldText(false);
            int y = 160;
            canvas.drawText("🧵 Order ID: #12345", 40, y, paint); y += 30;
            canvas.drawText("👤 Customer: Mayuresh Vadar", 40, y, paint); y += 30;
            canvas.drawText("📅 Date: 02 Aug 2025", 40, y, paint); y += 30;
            canvas.drawText("👖 Items: 1 Pants, 1 Shirt", 40, y, paint); y += 30;
            canvas.drawText("💰 Total: ₹1000", 40, y, paint); y += 50;

            // 4. Footer
            paint.setTextSize(12);
            paint.setColor(Color.DKGRAY);
            canvas.drawText("📍 Savarkar Chowk, Brahmanpuri, Miraj - 416410", 40, 780, paint);
            canvas.drawText("📞 +91-7057327442", 40, 800, paint);
            canvas.drawText("🙏 Thank you for choosing us!", 40, 820, paint);

            document.finishPage(page);

            // 5. Save to Documents/TailorInvoices
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "TailorInvoices");
            if (!dir.exists()) dir.mkdirs();

            file = new File(dir, "Invoice_" + invoiceId + ".pdf");
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();

            Toast.makeText(context, "✅ Invoice saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "❌ Error generating invoice: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return file;
    }
}


