package com.example.tailormanagementsystem;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.tailormanagementsystem.AppDatabase;
import com.example.tailormanagementsystem.Customer;
import com.example.tailormanagementsystem.CustomerDao;
import com.example.tailormanagementsystem.models.Order;

@Database(entities = {Customer.class, Order.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CustomerDao customerDao();
    public abstract OrderDao orderDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE Customer ADD COLUMN chest REAL");
            db.execSQL("ALTER TABLE Customer ADD COLUMN waist REAL");
            db.execSQL("ALTER TABLE Customer ADD COLUMN hip REAL");
            db.execSQL("ALTER TABLE Customer ADD COLUMN shoulder REAL");
            db.execSQL("ALTER TABLE Customer ADD COLUMN sleeve REAL");
            db.execSQL("ALTER TABLE Customer ADD COLUMN inseam REAL");
            db.execSQL("ALTER TABLE Customer ADD COLUMN logoPath TEXT");
            db.execSQL("CREATE TABLE IF NOT EXISTS orders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "customer_name TEXT, " +
                    "garment_type TEXT, " +
                    "order_date TEXT)");

        }
    };
}
