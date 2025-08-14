package com.example.tailormanagementsystem;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TailorApiService {
    @GET("orders/bill/{orderId}")
    Call<ResponseBody> downloadBill(@Path("orderId") Long orderId);
}
