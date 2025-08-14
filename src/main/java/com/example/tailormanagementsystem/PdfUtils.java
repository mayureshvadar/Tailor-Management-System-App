package com.example.tailormanagementsystem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;

import com.example.tailormanagementsystem.models.Order;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
public class PdfUtils {

    public static <AppDatabase> File generateCustomerSummaryPdf(Context context, Customer customer) {
        try {
            // 🧠 Fetch orders from Room DB
            AppDatabase db = (AppDatabase) DatabaseClient.getInstance(context).getAppDatabase();
            List<Order> orders = ((com.example.tailormanagementsystem.AppDatabase) db).orderDao().getOrdersByCustomerName(customer.getName());

            // 🧾 Create PDF document
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            Paint paint = new Paint();
            int y = 60;

            // 🎨 Header
            paint.setTextSize(20);
            paint.setFakeBoldText(true);
            canvas.drawText("🧵 Tailor Management System", 40, y, paint);
            y += 30;
            paint.setTextSize(14);
            canvas.drawText("📄 Customer Order Summary", 40, y, paint);
            y += 30;

            // 👤 Customer Info
            canvas.drawText("👤 Name: " + customer.getName(), 40, y, paint);
            y += 20;
            canvas.drawText("📞 Contact: " + customer.getName(), 40, y, paint);
            y += 30;

            paint.setTextSize(14f);
            paint.setFakeBoldText(false);

            canvas.drawText("📐 Measurements:", 40, y, paint);
            y += 20;

            canvas.drawText("Chest: " + customer.getChest() + " cm", 40, y, paint);
            canvas.drawText("Waist: " + customer.getWaist() + " cm", 200, y, paint);
            canvas.drawText("Hip: " + customer.getHip() + " cm", 360, y, paint);
            y += 20;

            canvas.drawText("Shoulder: " + customer.getShoulder() + " cm", 40, y, paint);
            canvas.drawText("Sleeve: " + customer.getSleeve() + " cm", 200, y, paint);
            canvas.drawText("Inseam: " + customer.getInseam() + " cm", 360, y, paint);
            y += 30;

            if (customer.getChest() > 0 || customer.getWaist() > 0 || customer.getHip() > 0 ||
                    customer.getShoulder() > 0 || customer.getSleeve() > 0 || customer.getInseam() > 0) {
                // draw measurement block
            }

            // 📋 Table Header
            paint.setFakeBoldText(true);
            canvas.drawText("Item", 40, y, paint);
            canvas.drawText("Qty", 200, y, paint);
            canvas.drawText("Price", 300, y, paint);
            canvas.drawText("Date", 400, y, paint);
            y += 20;
            paint.setFakeBoldText(false);

            // 🧾 Table Rows
            double total = 0;
            for (Order order : orders) {
                canvas.drawText(order.garmentType, 40, y, paint);
                canvas.drawText(String.valueOf(order.quantity), 200, y, paint);
                canvas.drawText("₹" + order.price, 300, y, paint);
                canvas.drawText(order.orderDate, 400, y, paint);
                total += order.price;
                y += 20;
                if (y > 800) break; // prevent overflow
            }

            // 💰 Total
            y += 30;
            paint.setFakeBoldText(true);
            canvas.drawText("Total Amount: ₹" + total, 40, y, paint);

            document.finishPage(page);

            // 💾 Save PDF
            File folder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "TailorInvoices");
            if (!folder.exists()) folder.mkdirs();

            String fileName = "Summary_" + customer.getName().replace(" ", "_") + ".pdf";
            File file = new File(folder, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();

            Log.d("PDF", "Saved to: " + file.getAbsolutePath());
            return file;

        } catch (Exception e) {
            Log.e("PDF", "Error generating summary PDF", e);
            return null;
        }
    }
}