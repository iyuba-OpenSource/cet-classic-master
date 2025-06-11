package com.iyuba.CET4bible.mvp.view;



import com.iyuba.CET4bible.mvp.model.BaseModel;
import com.iyuba.CET4bible.mvp.model.bean.UserBean;
import com.iyuba.CET4bible.mvp.presenter.IBasePresenter;

import io.reactivex.disposables.Disposable;

public interface QuickRegistrationContract {


    interface QuickRegistrationView extends LoadingView {

        void registerComplete(UserBean.UserinfoDTO userinfoDTO);
    }


    interface QuickRegistrationPresenter extends IBasePresenter<QuickRegistrationView> {

        void register(int protocol, String mobile, String username, String password, String platform,
                      int appid, String app, String format, String sign);
    }


    interface QuickRegistrationModel extends BaseModel {


        Disposable register(int protocol, String mobile, String username, String password, String platform,
                            int appid, String app, String format, String sign, UserCallback userCallback);

    }

    interface UserCallback {

        void success(UserBean.UserinfoDTO userinfoDTO);

        void error(Exception e);
    }
}
