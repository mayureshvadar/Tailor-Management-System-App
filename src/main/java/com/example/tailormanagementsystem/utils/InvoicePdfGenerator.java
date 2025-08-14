package com.example.tailormanagementsystem.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.graphics.Typeface;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class InvoicePdfGenerator {

    private Context context;

    public InvoicePdfGenerator(Context context) {
        this.context = context;
    }

    public File generateInvoicePdf(String customerName, String orderDetails, String invoiceNumber) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        try {
            // 🖼️ Load and draw logo
            Bitmap logo = BitmapFactory.decodeStream(context.getAssets().open("ic_logo"));
            Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, 120, 120, false);
            canvas.drawBitmap(scaledLogo, 40, 40, paint);

            // 🧵 Draw shop name
            paint.setTextSize(20);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
            canvas.drawText("👕 Yash Mens Wear ", 180, 100, paint);

            // 📄 Draw invoice header
            paint.setTextSize(16);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            canvas.drawText("Invoice No: " + invoiceNumber, 40, 180, paint);
            canvas.drawText("Customer: " + customerName, 40, 210, paint);
            // 📋 Draw order details
            canvas.drawText("Order Details:", 40, 250, paint);
            canvas.drawText(orderDetails, 40, 280, paint);

            // ✅ Finish page
            document.finishPage(page);

            // 📁 Save PDF to external storage
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "TailorInvoices");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "Invoice_" + invoiceNumber + ".pdf");
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();

            Toast.makeText(context, "Invoice saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error generating invoice PDF", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}