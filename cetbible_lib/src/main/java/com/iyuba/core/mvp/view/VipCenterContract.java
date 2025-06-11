package com.iyuba.core.mvp.view;

import com.iyuba.core.mvp.model.BaseModel;
import com.iyuba.core.mvp.model.bean.MoreInfoBean;
import com.iyuba.core.mvp.presenter.IBasePresenter;

import io.reactivex.disposables.Disposable;

public interface VipCenterContract {


    interface VipCenterView extends LoadingView {

        /**
         * 成功获取更多数据
         *
         * @param moreInfoBean
         */
        void moreInfoComplete(MoreInfoBean moreInfoBean);
    }

    interface VipCenterPresenter extends IBasePresenter<VipCenterView> {

        void getMoreInfo(String platform, int protocol, int id, int myid,
                         int appid, String sign);
    }

    interface VipCenterModel extends BaseModel {

        Disposable getMoreInfo(String platform, int protocol, int id, int myid,
                               int appid, String sign, MoreInfoCallback moreInfoCallback);
    }


    interface MoreInfoCallback {

        void success(MoreInfoBean moreInfoBean);

        void error(Exception e);
    }

}
