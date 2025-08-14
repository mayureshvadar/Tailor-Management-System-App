package com.example.tailormanagementsystem;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "invoice_table")
public class InvoiceEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String customerName;
    public String orderId;
    public String filePath;
    public String date;
    public int quantity;
    public double price;
}