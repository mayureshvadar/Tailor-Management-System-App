package com.example.tailormanagementsystem.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.tailormanagementsystem.Customer;
import com.example.tailormanagementsystem.models.Order;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class CsvUtils {

    public static File exportCustomerMeasurements(Context context, List<Customer> customers) {
        try {
            File folder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "TailorExports");
            if (!folder.exists()) folder.mkdirs();

            File file = new File(folder, "CustomerMeasurements.csv");
            FileWriter writer = new FileWriter(file);

            // 🧾 Header
            writer.append("Name,Mobile,Garment,Chest,Waist,Hip,Shoulder,Sleeve,Inseam\n");

            // 🧵 Rows
            for (Customer c : customers) {
                writer.append(c.getName()).append(",")
                        .append(c.getMobile()).append(",")
                        .append(c.getGarment()).append(",")
                        .append(String.valueOf(c.getChest())).append(",")
                        .append(String.valueOf(c.getWaist())).append(",")
                        .append(String.valueOf(c.getHip())).append(",")
                        .append(String.valueOf(c.getShoulder())).append(",")
                        .append(String.valueOf(c.getSleeve())).append(",")
                        .append(String.valueOf(c.getInseam())).append("\n");
            }

            writer.flush();
            writer.close();

            Log.d("CSV", "Exported to: " + file.getAbsolutePath());
            return file;

        } catch (Exception e) {
            Log.e("CSV", "Export failed", e);
            return null;
        }
    }
    public static File exportOrdersCsv(Context context, List<Order> orders) {
        try {
            File folder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "TailorExports");
            if (!folder.exists()) folder.mkdirs();

            File file = new File(folder, "OrderSummary.csv");
            FileWriter writer = new FileWriter(file);

            // 🧾 Header
            writer.append("Customer,Garment,Date,Quantity,Price\n");

            // 🧵 Rows
            for (Order o : orders) {
                writer.append(o.getCustomerName()).append(",")
                        .append(o.getGarmentType()).append(",")
                        .append(o.getOrderDate()).append(",")
                        .append(String.valueOf(o.getQuantity())).append(",")
                        .append(String.valueOf(o.getPrice())).append("\n");
            }

            writer.flush();
            writer.close();

            Log.d("CSV", "Order CSV exported to: " + file.getAbsolutePath());
            return file;

        } catch (Exception e) {
            Log.e("CSV", "Order export failed", e);
            return null;
        }
    }
}