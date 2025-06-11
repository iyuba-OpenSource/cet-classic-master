package com.iyuba.CET4bible.mvp.model;


import com.iyuba.CET4bible.mvp.model.bean.UserBean;
import com.iyuba.core.mvp.model.bean.MoreInfoBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiServer {




    /**
     * 获取广告
     *
     * @param appId
     * @param flag  2 广告顺序  5自家广告内容
     * @param uid
     * @return
     */
   /* @GET
    Observable<List<AdEntryBean>> getAdEntryAll(@Url String url, @Query("appId") String appId, @Query("flag") int flag, @Query("uid") String uid);

*/


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



    /**
     * mob秒验登录
     *
     * @param protocol 10010
     * @param appId
     * @param appkey
     * @param opToken
     * @param operator
     * @param token
     * @return
     */
    @GET
    Observable<UserBean> mobSecVerifyLogin(@Url String url, @Query("protocol") int protocol, @Query("appId") int appId, @Query("appkey") String appkey,
                                           @Query("opToken") String opToken, @Query("operator") String operator, @Query("token") String token);



    /**
     * 注册
     *
     * @param protocol
     * @param mobile
     * @param username
     * @param password
     * @param platform
     * @param appid
     * @param app
     * @param format
     * @param sign
     * @return
     */
    @GET
    Observable<UserBean.UserinfoDTO> register(@Url String url, @Query("protocol") int protocol, @Query("mobile") String mobile, @Query("username") String username,
                                              @Query("password") String password, @Query("platform") String platform, @Query("appid") int appid,
                                              @Query("app") String app, @Query("format") String format, @Query("sign") String sign);
}
