package com.iyuba.CET4bible.mvp.model;



import com.iyuba.CET4bible.mvp.model.bean.UserBean;
import com.iyuba.CET4bible.mvp.view.MeContract;
import com.iyuba.configation.Constant;
import com.iyuba.core.mvp.model.bean.MoreInfoBean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MeModel implements MeContract.MeModel {

    @Override
    public Disposable getMoreInfo(String platform, int protocol, int id, int myid, int appid, String sign, MeContract.MoreCallback moreCallback) {

        return NetWorkManager
                .getRequest()
                .getMoreInfo(Constant.PROTOCOL, platform, protocol, id, myid, appid, sign)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MoreInfoBean>() {
                    @Override
                    public void accept(MoreInfoBean moreInfoBean) throws Exception {

                        moreCallback.success(moreInfoBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        moreCallback.error((Exception) throwable);
                    }
                });
    }

    @Override
    public Disposable mobSecVerifyLogin(int protocol, int appId, String appkey, String opToken, String operator,
                                     String token, MeContract.RegisterCallback registerCallback) {


        return NetWorkManager
                .getRequest()
                .mobSecVerifyLogin(Constant.PROTOCOL, protocol, appId, appkey, opToken, operator, token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserBean>() {
                    @Override
                    public void accept(UserBean userBean) throws Exception {

                        registerCallback.success(userBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        registerCallback.error((Exception) throwable);
                    }
                });
    }
}
