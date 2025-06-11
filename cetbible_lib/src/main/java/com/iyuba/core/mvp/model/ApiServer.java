package com.iyuba.core.mvp.model;

import com.iyuba.core.mvp.model.bean.MoreInfoBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiServer {


    /**
     * 获取更多的用户数据
     *
     * @param platform
     * @param protocol
     * @param id
     * @param myid
     * @param appid
     * @param sign
     * @return
     */
    @GET
    Observable<MoreInfoBean> getMoreInfo(@Url String url, @Query("platform") String platform, @Query("protocol") int protocol,
                                         @Query("id") int id, @Query("myid") int myid,
                                         @Query("appid") int appid, @Query("sign") String sign);
}
