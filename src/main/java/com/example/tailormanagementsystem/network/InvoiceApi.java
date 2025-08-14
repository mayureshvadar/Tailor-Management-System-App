package com.example.tailormanagementsystem.network;

import com.example.tailormanagementsystem.models.Order;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface InvoiceApi {
    @GET("orders/{id}/bill")
    Call<ResponseBody> downloadInvoice(@Path("id") Long orderId);
    Call<List<Order>> getOrders();

    @PUT("/orders/{id}")
    Call<Void> updateOrder(@Path("id") Long id, @Body Order order);
    @GET("orders")
    Call<List<Order>> getAllOrders();

    @GET("orders/{id}")
    Call<Order> getOrderById(@Path("id") int id);

    @POST("orders")
    Call<Order> createOrder(@Body Order order);

    @PUT("orders/{id}")
    Call<Order> updateOrder(@Path("id") int id, @Body Order order);

    @DELETE("orders/{id}")
    Call<Void> deleteOrder(@Path("id") int id);

    @GET("orders/{id}/bill")
    Call<ResponseBody> downloadInvoice(@Path("id") int id);

    @GET("orders/export")
    Call<ResponseBody> exportCSV();

}
