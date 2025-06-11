package com.iyuba.abilitytest.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OtherApi {
    @GET("getRegisterAll.jsp")
    Call<ResponseBody> onlineControl(@Query("appId")String appId, @Query("appVersion")String appVersion);
}
