package com.example.tailormanagementsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.tailormanagementsystem.api.ApiClient;
import com.example.tailormanagementsystem.api.CustomerService;
import com.example.tailormanagementsystem.utils.InvoicePdfGenerator;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.graphics.pdf.PdfDocument;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    EditText etCustomerName, etMobile, etAddress;
    Button btnSave, openShareInvoiceButton;
    AppDatabase db;
    CustomerService service;
    Button btnGenerateBrandedInvoice;
    final String[] selectedDate = {""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCustomerName = findViewById(R.id.etCustomerName);
        etMobile = findViewById(R.id.etMobile);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        openShareInvoiceButton = findViewById(R.id.btn_open_share_invoice);
        Button btnGenerateBrandedInvoice = findViewById(R.id.btnGenerateInvoice);
        Button btnPickDate = findViewById(R.id.btnPickDate);



        // 🧵 Navigate to ShareInvoiceActivity
        openShareInvoiceButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ShareInvoiceActivity.class);
            startActivity(intent);
        });

        // 🧵 Initialize Room DB
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "customer-db").allowMainThreadQueries().build();

        // 🧵 Initialize Retrofit
        service = ApiClient.getClient().create(CustomerService.class);

        // 🧵 Automatically fetch customers from backend on launch
        fetchCustomersFromBackend();

        // 🧵 Save + Sync button action
        btnSave.setOnClickListener(v -> {
            String name = etCustomerName.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (!name.isEmpty() && !mobile.isEmpty() && !address.isEmpty()) {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setMobile(mobile);
                customer.setAddress(address);

                // ✅ Save locally
                db.customerDao().insertCustomer(customer);
                Toast.makeText(MainActivity.this,
                        "✅ Saved to local storage", Toast.LENGTH_SHORT).show();

                // ✅ Send to backend
                sendCustomerToBackend(customer);
            } else {
                Toast.makeText(MainActivity.this,
                        "❌ Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        btnGenerateBrandedInvoice.setOnClickListener(v -> {
            String name = etCustomerName.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (name.isEmpty() || mobile.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "❌ Please fill customer details first", Toast.LENGTH_SHORT).show();
                return;
            }

            String orderDetails = "2 Shirts, 1 Pant\nDelivery: 5 Aug"; // You can make this dynamic later
            String invoiceId = "INV" + System.currentTimeMillis(); // Unique ID using timestamp

            InvoicePdfGenerator generator = new InvoicePdfGenerator(MainActivity.this);
            File savedFile = generator.generateInvoicePdf(name, orderDetails, invoiceId);

            if (savedFile != null && savedFile.exists()) {
                showInvoiceBottomSheet(savedFile);
            } else {
                Toast.makeText(this, "❌ Invoice file not found!", Toast.LENGTH_SHORT).show();
            }


        });
        btnPickDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate[0] = dayOfMonth + "/" + (month + 1) + "/" + year;
                        Toast.makeText(MainActivity.this, "📅 Delivery: " + selectedDate[0], Toast.LENGTH_SHORT).show();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });


    }

    private void sendCustomerToBackend(Customer customer) {
        Call<Customer> call = service.addCustomer(customer);
        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ Sent to backend: ID " + response.body().getId());
                    Toast.makeText(MainActivity.this,
                            "✅ Synced with backend", Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "❌ Backend response error: " + response.code());
                    Toast.makeText(MainActivity.this,
                            "❌ Backend error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Log.e(TAG, "❌ Failed to send to backend", t);
                Toast.makeText(MainActivity.this,
                        "❌ Network error: unable to sync", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCustomersFromBackend() {
        Call<List<Customer>> call = service.getAllCustomers();
        call.enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Customer> customers = response.body();
                    Log.d(TAG, "✅ Received " + customers.size() + " customers from backend");
                    Toast.makeText(MainActivity.this,
                            "✅ Fetched " + customers.size() + " customers from backend",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "❌ Error fetching customers: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {
                Log.e(TAG, "❌ Failed to fetch customers", t);
                Toast.makeText(MainActivity.this,
                        "❌ Error contacting backend", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void generateBrandedInvoice(Context context) {
        try {
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            Bitmap logo = BitmapFactory.decodeStream(context.getAssets().open("logo.png"));
            Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, 120, 120, false);
            canvas.drawBitmap(scaledLogo, 40, 40, paint);

            paint.setTextSize(18);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
            canvas.drawText("👕 Yash Mens Wear ", 180, 100, paint);

            paint.setStrokeWidth(2);
            canvas.drawLine(40, 130, 550, 130, paint);

            paint.setTextSize(14);
            paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
            paint.setColor(Color.BLACK);

            int y = 160;
            canvas.drawText("🧵 Order ID: #12345", 40, y, paint); y += 30;
            canvas.drawText("👤 Customer: Mayuresh Vadar", 40, y, paint); y += 30;
            canvas.drawText("📅 Date: 02 Aug 2025", 40, y, paint); y += 30;
            canvas.drawText("👖 Items: 1 Pants, 1 Shirt", 40, y, paint); y += 30;
            canvas.drawText("💰 Total: ₹1000", 40, y, paint); y += 50;


            paint.setTextSize(12);
            paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.ITALIC));
            paint.setColor(Color.DKGRAY);
            canvas.drawText("📍 Address: savarkar Chowk, Brahmanpuri, Miraj. - 416410", 40, 780, paint);
            canvas.drawText("📞 Contact: +91-7057327442", 40, 800, paint);
            canvas.drawText("🙏 Thank you for choosing us!", 40, 820, paint);

            document.finishPage(page);

            File file = new File(context.getExternalFilesDir(null), "invoice.pdf");
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();

            Toast.makeText(context, "✅ Invoice saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "❌ Error generating invoice: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    private void showInvoiceBottomSheet(File invoiceFile) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_invoice, null);
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();

        Button btnView = sheetView.findViewById(R.id.btnViewInvoice);
        Button btnShare = sheetView.findViewById(R.id.btnShareInvoice);
        Button btnOpen = sheetView.findViewById(R.id.btnOpenFolder);

        btnView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(invoiceFile), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(invoiceFile));
            startActivity(Intent.createChooser(shareIntent, "Share Invoice"));
        });

        btnOpen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(invoiceFile.getParentFile().toURI().toString());
            intent.setDataAndType(uri, "resource/folder");
            startActivity(intent);
        });
    }
}

