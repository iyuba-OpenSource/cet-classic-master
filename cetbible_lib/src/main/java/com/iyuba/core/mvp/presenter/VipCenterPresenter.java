package com.iyuba.core.mvp.presenter;

import com.iyuba.core.mvp.model.VipCenterModel;
import com.iyuba.core.mvp.model.bean.MoreInfoBean;
import com.iyuba.core.mvp.view.VipCenterContract;

import java.net.UnknownHostException;

import io.reactivex.disposables.Disposable;

public class VipCenterPresenter extends BasePresenter<VipCenterContract.VipCenterView, VipCenterContract.VipCenterModel>
        implements VipCenterContract.VipCenterPresenter {

    @Override
    protected VipCenterContract.VipCenterModel initModel() {
        return new VipCenterModel();
    }

    @Override
    public void getMoreInfo(String platform, int protocol, int id, int myid, int appid, String sign) {

        Disposable disposable = model.getMoreInfo(platform, protocol, id, myid, appid, sign, new VipCenterContract.MoreInfoCallback() {
            @Override
            public void success(MoreInfoBean moreInfoBean) {

                if (moreInfoBean.getResult() == 201) {//成功获取

                    view.moreInfoComplete(moreInfoBean);
                } else {
                    view.toast(moreInfoBean.getMessage());
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
