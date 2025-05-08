package com.example.zone.chatroom;

import com.example.zone.Notifications.MyResponse;
import com.example.zone.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AIzaSyCX1VSPaEGYUjA341I5O4PIRarjt2VPBYA"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
