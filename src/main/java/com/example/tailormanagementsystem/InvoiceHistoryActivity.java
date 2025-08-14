package com.example.tailormanagementsystem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvoiceHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private InvoiceAdapter adapter;
    private List<InvoiceEntity> fullList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_history);

        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_invoices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button exportButton = findViewById(R.id.btn_export_summary);
        exportButton.setOnClickListener(v -> exportSummaryPdf());

        Button openFolderButton = findViewById(R.id.btn_open_folder);
        openFolderButton.setOnClickListener(v -> openDownloadsFolder());

        loadInvoices();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterInvoices(newText);
                return true;
            }
        });
    }

    private void loadInvoices() {
        fullList = InvoiceDatabase.getInstance(this).invoiceDao().getAllInvoices();
        adapter = new InvoiceAdapter(fullList,
                this::shareInvoice,
                this::deleteInvoice);
        recyclerView.setAdapter(adapter);
    }

    private void filterInvoices(String query) {
        List<InvoiceEntity> filtered = new ArrayList<>();
        for (InvoiceEntity invoice : fullList) {
            if (invoice.customerName.toLowerCase().contains(query.toLowerCase()) ||
                    invoice.orderId.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(invoice);
            }
        }
        adapter.updateList(filtered);
    }

    private void shareInvoice(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    file
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Share Invoice via"));
        } else {
            Toast.makeText(this, "📁 Invoice file not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteInvoice(InvoiceEntity invoice) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Invoice")
                .setMessage("Are you sure you want to delete this invoice?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    File file = new File(invoice.filePath);
                    if (file.exists()) {
                        boolean deleted = file.delete();
                        if (!deleted) {
                            Toast.makeText(this, "❌ Failed to delete file.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    InvoiceDatabase.getInstance(this).invoiceDao().deleteById(invoice.id);
                    Toast.makeText(this, "🗑️ Invoice deleted.", Toast.LENGTH_SHORT).show();
                    loadInvoices();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void exportSummaryPdf() {
        List<InvoiceEntity> invoices = InvoiceDatabase.getInstance(this).invoiceDao().getAllInvoices();

        if (invoices.isEmpty()) {
            Toast.makeText(this, "⚠️ No invoices found to export.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File file = new File(getExternalFilesDir(null), "invoice_summary.pdf");
            FileOutputStream fos = new FileOutputStream(file);

            Document document = new Document();
            PdfWriter.getInstance(document, fos);
            document.open();

            document.add(new Paragraph("🧾 Invoice Summary"));
            document.add(new Paragraph("Generated on: " +
                    new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date())));
            document.add(new Paragraph("\n"));

            for (InvoiceEntity invoice : invoices) {
                String line = "👤 " + invoice.customerName +
                        " | 🧵 Order ID: " + invoice.orderId +
                        " | 📅 " + invoice.date;
                document.add(new Paragraph(line));
            }

            document.close();
            fos.close();

            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    file
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Share Summary PDF via"));

        } catch (Exception e) {
            Toast.makeText(this, "❌ Error generating summary PDF.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void openDownloadsFolder() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
            intent.setDataAndType(uri, "*/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "📂 Unable to open Downloads folder.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}