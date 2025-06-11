package com.iyuba.CET4bible.clearuser;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.iyuba.module.toolbox.SingleParser;

import io.reactivex.Single;

public class ClearUserResponse implements SingleParser<ClearUserResponse> {

    @SerializedName("uid")
    public int uid;
    @SerializedName("expireTime")
    public int expireTime;
    @SerializedName("result")
    public String result;
    @SerializedName("Amount")
    public String Amount;
    @SerializedName("vipStatus")
    public String vipStatus;
    @SerializedName("credits")
    public int credits;
    @SerializedName("message")
    public String message;
    @SerializedName("username")
    public String username;
    @SerializedName("email")
    public String email;
    @SerializedName("jiFen")
    public int jiFen;
    @SerializedName("imgSrc")
    public String imgSrc;
    @SerializedName("money")
    public String money;
    @SerializedName("mobile")
    public String mobile;
    @SerializedName("isteacher")
    public String isTeacher;

    @Override
    public Single<ClearUserResponse> parse() {
        if (!TextUtils.isEmpty(result)&&result.equals("101")){
            return Single.just(this);
        }
        return Single.error(new Throwable(result));
    }
}
