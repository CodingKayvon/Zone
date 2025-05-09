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
            "Authorization:key=BDzahoGC-AalDLU8hopXjlzpLqx8YYJIQJgc_Qt45-pulcPEMvx1GHz3Pk4IZRF7LTZtY5Ca-shR49xKu888z7s"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
