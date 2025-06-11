package com.iyuba.CET4bible.clearuser;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiComService {

    String ENDPOINT = "http://api.iyuba.com.cn/";

    @GET("v2/api.iyuba")
    Single<ClearUserResponse> clearUser(@Query("protocol") String protocol,
                                        @Query("username") String username,
                                        @Query("password") String password,
                                        @Query("sign") String sign,
                                        @Query("format") String format);



    class Creator {
        public static ApiComService createService(OkHttpClient client, GsonConverterFactory gsonFactory, RxJava2CallAdapterFactory rxJavaFactory) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .client(client)
                    .addConverterFactory(gsonFactory)
                    .addCallAdapterFactory(rxJavaFactory)
                    .build();
            return retrofit.create(ApiComService.class);
        }
    }
}
