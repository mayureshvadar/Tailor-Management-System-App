package com.example.tailormanagementsystem;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.tailormanagementsystem.InvoiceDatabase;
@Database(entities = {InvoiceEntity.class}, version = 1)
public abstract class InvoiceDatabase extends RoomDatabase {

    private static InvoiceDatabase instance;

    public abstract InvoiceDao invoiceDao();

    public static synchronized InvoiceDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            InvoiceDatabase.class, "invoice_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // Optional: use with caution
                    .build();
        }
        return instance;
    }
}