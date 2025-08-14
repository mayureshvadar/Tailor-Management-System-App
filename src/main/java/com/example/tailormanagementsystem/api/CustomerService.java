package com.example.tailormanagementsystem.api;

import com.example.tailormanagementsystem.Customer;
import com.example.tailormanagementsystem.Customer;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Path;


public interface CustomerService {

    // 🔹 Get all customers
    @GET("api/customers")
    Call<List<Customer>> getAllCustomers();

    // 🔹 Add a new customer
    @POST("api/customers")
    Call<Customer> addCustomer(@Body Customer customer);

    // 🔹 Get customer by ID
    @GET("api/customers/{id}")
    Call<Customer> getCustomerById(@Path("id") Long id);

    // 🔹 Delete customer by ID
    @POST("api/customers/delete/{id}")
    Call<Void> deleteCustomer(@Path("id") Long id); // Optional: replace with @DELETE
}
