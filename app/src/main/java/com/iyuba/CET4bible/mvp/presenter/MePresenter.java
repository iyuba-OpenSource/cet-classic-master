package com.iyuba.CET4bible.mvp.presenter;

import com.iyuba.CET4bible.mvp.model.MeModel;
import com.iyuba.CET4bible.mvp.model.bean.UserBean;
import com.iyuba.CET4bible.mvp.view.MeContract;
import com.iyuba.core.mvp.model.bean.MoreInfoBean;
import com.mob.secverify.SecVerify;

import io.reactivex.disposables.Disposable;

public class MePresenter extends BasePresenter<MeContract.MeView, MeContract.MeModel>
        implements MeContract.MePresenter {
    @Override
    protected MeContract.MeModel initModel() {
        return new MeModel();
    }

    @Override
    public void getMoreInfo(String platform, int protocol, int id, int myid, int appid, String sign) {

        Disposable disposable = model.getMoreInfo(platform, protocol, id, myid, appid, sign, new MeContract.MoreCallback() {
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

            }
        });
        addSubscribe(disposable);
    }

    @Override
    public void mobSecVerifyLogin(int protocol, int appId, String appkey, String opToken, String operator, String token) {

        Disposable disposable = model.mobSecVerifyLogin(protocol, appId, appkey, opToken, operator, token, new MeContract.RegisterCallback() {
            @Override
            public void success(UserBean userBean) {

                view.mobSecVerifyLogin(userBean);
            }

            @Override
            public void error(Exception e) {

                SecVerify.finishOAuthPage();
            }
        });
        addSubscribe(disposable);
    }
}
