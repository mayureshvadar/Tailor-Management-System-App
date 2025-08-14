package com.example.tailormanagementsystem.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import kotlin.text.UStringsKt;

@Entity(tableName = "orders")
public class Order {

    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "customer_name")
    public String customerName;

    @ColumnInfo(name = "garment_type")
    public String garmentType;

    @ColumnInfo(name = "order_date")
    public String orderDate;
    @ColumnInfo(name = "quantity")
    public int quantity;

    @ColumnInfo(name = "price")
    public double price;





    public Order(Long id, String customerName, String garmentType, String orderDate, int quantity, double price ) {
        this.id = id;
        this.customerName = customerName;
        this.garmentType = garmentType;
        this.orderDate = orderDate;
        this.quantity = quantity;
        this.price = price;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getGarmentType() {
        return garmentType;
    }

    public void setGarmentType(String garmentType) {
        this.garmentType = garmentType;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}