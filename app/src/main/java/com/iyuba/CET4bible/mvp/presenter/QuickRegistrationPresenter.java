package com.iyuba.CET4bible.mvp.presenter;


import com.iyuba.CET4bible.mvp.model.QuickRegistrationModel;
import com.iyuba.CET4bible.mvp.model.bean.UserBean;
import com.iyuba.CET4bible.mvp.view.QuickRegistrationContract;

import java.net.UnknownHostException;

import io.reactivex.disposables.Disposable;

public class QuickRegistrationPresenter extends BasePresenter<QuickRegistrationContract.QuickRegistrationView, QuickRegistrationContract.QuickRegistrationModel>
        implements QuickRegistrationContract.QuickRegistrationPresenter {


    @Override
    protected QuickRegistrationContract.QuickRegistrationModel initModel() {
        return new QuickRegistrationModel();
    }

    @Override
    public void register(int protocol, String mobile, String username, String password, String platform, int appid, String app, String format, String sign) {

        Disposable disposable = model.register(protocol, mobile, username, password, platform,
                appid, app, format, sign, new QuickRegistrationContract.UserCallback() {
                    @Override
                    public void success(UserBean.UserinfoDTO userinfoDTO) {

                        if (userinfoDTO.getResult().equals("111")) {

                            view.registerComplete(userinfoDTO);
                        } else if (userinfoDTO.getResult().equals("115")) {

                            view.toast("此号码已注册");
                        } else {

                            view.toast(userinfoDTO.getMessage());
                        }
                    }

                    @Override
                    public void error(Exception e) {

                        if (e instanceof UnknownHostException) {

                            view.toast("请求超时");
                        }
                    }
                });
        addSubscribe(disposable);
    }
}
