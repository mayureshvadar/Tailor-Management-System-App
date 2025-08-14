package com.example.tailormanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private List<InvoiceEntity> invoiceList;
    private final OnShareClickListener shareListener;
    private final OnDeleteClickListener deleteListener;
    private OnMeasurementClickListener measurementListener = null;

    public interface OnShareClickListener {
        void onShareClick(String filePath);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(InvoiceEntity invoice);
    }
    public interface OnMeasurementClickListener {
        void onMeasurementClick(InvoiceEntity invoice);
    }


    public InvoiceAdapter(List<InvoiceEntity> invoiceList,
                          OnShareClickListener shareListener,
                          OnDeleteClickListener deleteListener){
        this.invoiceList = invoiceList;
        this.shareListener = shareListener;
        this.deleteListener = deleteListener;
        this.measurementListener = measurementListener;

    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        InvoiceEntity invoice = invoiceList.get(position);
        holder.customerName.setText("👤 " + invoice.customerName);
        holder.orderId.setText("🧵 Order ID: " + invoice.orderId);
        holder.date.setText("📅 " + invoice.date);
        holder.quantity.setText("📦 Qty: " + invoice.quantity);
        holder.price.setText("💰 ₹" + invoice.price);



        holder.shareButton.setOnClickListener(v -> shareListener.onShareClick(invoice.filePath));
        holder.deleteButton.setOnClickListener(v -> deleteListener.onDeleteClick(invoice));
        holder.measurementButton.setOnClickListener(v -> measurementListener.onMeasurementClick(invoice));
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    public void updateList(List<InvoiceEntity> newList) {
        this.invoiceList.clear();
        this.invoiceList.addAll(newList);
        notifyDataSetChanged();
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, orderId, date, quantity, price;

        ImageButton shareButton, deleteButton, measurementButton;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.text_customer_name);
            orderId = itemView.findViewById(R.id.text_order_id);
            date = itemView.findViewById(R.id.text_date);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            shareButton = itemView.findViewById(R.id.btn_share);
            deleteButton = itemView.findViewById(R.id.btn_delete);
            measurementButton = itemView.findViewById(R.id.btn_measurement);

        }
    }
}