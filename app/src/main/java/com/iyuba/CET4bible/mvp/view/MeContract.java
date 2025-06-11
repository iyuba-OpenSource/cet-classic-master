package com.iyuba.CET4bible.mvp.view;



import com.iyuba.CET4bible.mvp.model.BaseModel;
import com.iyuba.CET4bible.mvp.model.bean.UserBean;
import com.iyuba.CET4bible.mvp.presenter.IBasePresenter;
import com.iyuba.core.mvp.model.bean.MoreInfoBean;

import io.reactivex.disposables.Disposable;

public interface MeContract {


    interface MeView extends LoadingView {

        void moreInfoComplete(MoreInfoBean moreInfoBean);

        void mobSecVerifyLogin(UserBean userBean);
    }

    interface MePresenter extends IBasePresenter<MeView> {

        void getMoreInfo(String platform, int protocol, int id, int myid, int appid, String sign);

        void mobSecVerifyLogin(int protocol, int appId, String appkey, String opToken, String operator,
                               String token);
    }

    interface MeModel extends BaseModel {

        Disposable getMoreInfo(String platform, int protocol, int id, int myid, int appid, String sign, MoreCallback moreCallback);

        Disposable mobSecVerifyLogin(int protocol, int appId, String appkey, String opToken, String operator,
                                     String token, RegisterCallback registerCallback);
    }

    interface RegisterCallback {

        void success(UserBean userBean);

        void error(Exception e);

    }

    interface MoreCallback {

        void success(MoreInfoBean moreInfoBean);

        void error(Exception e);
    }
}
