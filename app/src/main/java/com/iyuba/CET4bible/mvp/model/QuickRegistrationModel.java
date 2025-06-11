package com.iyuba.CET4bible.mvp.model;



import com.iyuba.CET4bible.mvp.model.bean.UserBean;
import com.iyuba.CET4bible.mvp.view.QuickRegistrationContract;
import com.iyuba.configation.Constant;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class QuickRegistrationModel implements QuickRegistrationContract.QuickRegistrationModel {


    @Override
    public Disposable register(int protocol, String mobile, String username, String password,
                               String platform, int appid, String app, String format, String sign, QuickRegistrationContract.UserCallback userCallback) {

        return NetWorkManager
                .getRequest()
                .register(Constant.PROTOCOL, protocol, mobile, username, password, platform, appid, app, format, sign)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserBean.UserinfoDTO>() {
                    @Override
                    public void accept(UserBean.UserinfoDTO userinfoDTO) throws Exception {

                        userCallback.success(userinfoDTO);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        userCallback.error((Exception) throwable);
                    }
                });
    }
}
