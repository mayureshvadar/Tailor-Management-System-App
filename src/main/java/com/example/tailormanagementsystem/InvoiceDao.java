package com.example.tailormanagementsystem;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InvoiceDao {

    @Insert
    void insert(InvoiceEntity invoice);

    @Query("SELECT * FROM invoice_table ORDER BY id DESC")
    List<InvoiceEntity> getAllInvoices();

    @Query("DELETE FROM invoice_table WHERE id = :invoiceId")
    void deleteById(int invoiceId);
}