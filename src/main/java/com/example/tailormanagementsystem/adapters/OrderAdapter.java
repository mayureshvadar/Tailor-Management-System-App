package com.example.tailormanagementsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tailormanagementsystem.R;
import com.example.tailormanagementsystem.models.Order;
import com.example.tailormanagementsystem.network.InvoiceApi;
import com.example.tailormanagementsystem.utils.InvoiceDownloader;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private List<Order> fullOrderList;

    private Context context;
    private InvoiceApi api;

    public OrderAdapter(Context context, List<Order> orderList, InvoiceApi api) {
        this.context = context;
        this.orderList = orderList;
        this.api = api;
    }

    public OrderAdapter(List<Order> orders) {
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvCustomerName.setText(order.customerName);
        holder.tvGarmentType.setText(order.garmentType);
        holder.tvPrice.setText("Price: ₹" + order.price);
        holder.tvDeliveryDate.setText("Delivery: " + order.orderDate);

        holder.btnDownload.setOnClickListener(v -> {
            InvoiceDownloader.downloadInvoice(context, order.id, api);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrders(List<Order> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }
    public void filter(String query) {
        List<Order> filtered = new ArrayList<>();
        for (Order order : fullOrderList) {
            if (order.customerName.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(order);
            }
        }
        this.orderList = filtered;
        notifyDataSetChanged();
    }


    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvGarmentType, tvPrice, tvDeliveryDate;
        Button btnDownload;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvGarmentType = itemView.findViewById(R.id.tvGarmentType);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDeliveryDate = itemView.findViewById(R.id.tvDeliveryDate);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }

        }
    }
