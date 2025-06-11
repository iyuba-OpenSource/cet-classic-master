package com.iyuba.CET4bible.clearuser;

import com.iyuba.module.toolbox.MD5;
import com.iyuba.module.toolbox.SingleParser;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleTransformer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class DataManager {
    private static DataManager sInstance = new DataManager();

    public static DataManager getInstance() {
        return sInstance;
    }

    private final ApiComService mApiComService;


    private DataManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);

        OkHttpClient client = builder.build();
        SimpleXmlConverterFactory xmlFactory = SimpleXmlConverterFactory.create();
        GsonConverterFactory gsonFactory = GsonConverterFactory.create();
        RxJava2CallAdapterFactory rxJavaFactory = RxJava2CallAdapterFactory.create();

        mApiComService = ApiComService.Creator.createService(client, gsonFactory, rxJavaFactory);

    }
    /**
     * 注销账号
     *
     * @param username 用户名
     * @param password 密码
     * @return 返回是否成功
     */
    public Single<ClearUserResponse> clearUser(String username, String password) {
        String protocol = "11005";
        String format = "json";
        String passwordMD5 = MD5.getMD5ofStr(password);
        String sign = buildV2Sign(protocol, username, passwordMD5, "iyubaV2");
        return mApiComService.clearUser(protocol, username, passwordMD5, sign, format)
                .compose(this.applyParser());
    }

    private RequestBody fromString(String text) {
        return RequestBody.create(MediaType.parse("text/plain"), text);
    }


    private MultipartBody.Part fromFile(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }


    private String buildV2Sign(String... stuffs) {
        StringBuilder sb = new StringBuilder();
        for (String stuff : stuffs) {
            sb.append(stuff);
        }
        return MD5.getMD5ofStr(sb.toString());
    }

    // ----------------------- divider ---------------------------

    @SuppressWarnings("unchecked")
    private <T, R> SingleTransformer<T, R> applyParser() {
        return (SingleTransformer<T, R>) SingleParser.parseTransformer;
    }

}
