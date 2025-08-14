package com.example.tailormanagementsystem;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.tailormanagementsystem.models.Order;

import java.util.List;

@Dao
public interface OrderDao {

    @Insert
    void insertOrder(Order order);

    @Update
    void updateOrder(Order order);

    @Delete
    void deleteOrder(Order order);

    @Query("SELECT * FROM orders WHERE customer_name = :customerName")
    List<Order> getOrdersByCustomerName(String customerName);

    @Query("SELECT * FROM orders")
    List<Order> getAllOrders();
}