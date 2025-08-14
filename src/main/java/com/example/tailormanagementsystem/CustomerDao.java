package com.example.tailormanagementsystem;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CustomerDao {
    @Insert
    void insertCustomer(Customer customer);

    @Query("SELECT * FROM Customer")
    List<Customer> getAllCustomers();
}