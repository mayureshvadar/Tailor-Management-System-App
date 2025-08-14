package com.example.tailormanagementsystem;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.text.TextWatcher;
import android.text.Editable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    EditText searchBar;
    ListView listViewCustomers;
    CustomerAdapter adapter;
    List<Customer> customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Bind views
        searchBar = findViewById(R.id.searchBar);
        listViewCustomers = findViewById(R.id.listViewCustomers);

        // Fetch data from Room
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "customer-db").allowMainThreadQueries().build();

        customers = db.customerDao().getAllCustomers();

        // Setup custom adapter
        adapter = new CustomerAdapter(this, customers);
        listViewCustomers.setAdapter(adapter);

        // Optional: real-time search filter by name
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Customer> filteredList = new ArrayList<>();
                for (Customer c : customers) {
                    if (c.getName().toLowerCase().contains(s.toString().toLowerCase()) ||
                            c.getMobile().contains(s.toString()) ||
                            c.getAddress().toLowerCase().contains(s.toString().toLowerCase())) {
                        filteredList.add(c);
                    }
                }

                adapter = new CustomerAdapter(HistoryActivity.this, filteredList);
                listViewCustomers.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}