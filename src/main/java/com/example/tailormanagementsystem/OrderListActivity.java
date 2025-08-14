package com.example.tailormanagementsystem;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tailormanagementsystem.adapters.OrderAdapter;
import com.example.tailormanagementsystem.models.Order;
import com.example.tailormanagementsystem.network.InvoiceApi;
import com.example.tailormanagementsystem.utils.CsvUtils;
import com.example.tailormanagementsystem.utils.InvoiceDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderListActivity extends AppCompatActivity {
    private EditText searchInput;
    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private InvoiceApi invoiceApi;
    private List<Order> fullOrderList = new ArrayList<>();
    private OrderDao api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        searchInput = findViewById(R.id.searchInput);

        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Button btnExportCsv = findViewById(R.id.btnExportCsv);
        Button btnExportOrders = findViewById(R.id.btnExportOrders);


        //request runtime permission for CSV
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            }
        }


        // 🧵 Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tailor-management-system-1d795.web.app") // Replace with your backend IP
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        invoiceApi = retrofit.create(InvoiceApi.class);
        fetchOrders();


        // 🧵 Load mock orders (replace with real data later)
        List<Order> mockOrders = new ArrayList<>();
        mockOrders.add(new Order(1L, "Ravi Tailor", "Kurta", "2024-07-18", 2, 1200.0));
        mockOrders.add(new Order(2L, "Sneha Designs", "Blouse", "2024-07-17", 3, 750.0));
        mockOrders.add(new Order(3L, "Madati Tailors", "Sherwani", "2024-07-16", 1, 3500.0));

        // 🧵 Set adapter
        orderAdapter = new OrderAdapter(this, mockOrders, invoiceApi);
        orderRecyclerView.setAdapter(orderAdapter);

        // 🔍 Search listener
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //export CSV
        btnExportCsv.setOnClickListener(v -> {
            AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
            List<Customer> customers = db.customerDao().getAllCustomers();

            File csvFile = CsvUtils.exportCustomerMeasurements(this, customers);

            if (csvFile != null) {
                Toast.makeText(this, "CSV exported ✅\n" + csvFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Export failed ❌", Toast.LENGTH_SHORT).show();
            }
        });
        btnExportOrders.setOnClickListener(v -> {
            File csvFile = CsvUtils.exportOrdersCsv(this, fullOrderList);  // or your current order list

            if (csvFile != null) {
                Toast.makeText(this, "Orders CSV exported ✅\n" + csvFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Export failed ❌", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted ✅", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied ❌", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void filterOrders(String query) {
        List<Order> filtered = new ArrayList<>();
        for (Order order : fullOrderList) {
            if (order.customerName.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(order);
            }
        }
        orderAdapter.setOrders(filtered);
    }

    private void fetchOrders() {


        api.getAllOrders().equals(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    List<Order> orders = response.body();
                    OrderAdapter adapter = new OrderAdapter(orders);
                    orderRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(OrderListActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(OrderListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}