package com.iyuba.core.mvp.model;

import com.iyuba.configation.Constant;
import com.iyuba.core.mvp.model.bean.MoreInfoBean;
import com.iyuba.core.mvp.view.VipCenterContract;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VipCenterModel implements VipCenterContract.VipCenterModel {


    @Override
    public Disposable getMoreInfo(String platform, int protocol, int id, int myid, int appid, String sign, VipCenterContract.MoreInfoCallback moreInfoCallback) {

        return NetWorkManager
                .getRequest()
                .getMoreInfo(Constant.PROTOCOL, platform, protocol, id, myid, appid, sign)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MoreInfoBean>() {
                    @Override
                    public void accept(MoreInfoBean moreInfoBean) throws Exception {

                        moreInfoCallback.success(moreInfoBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        moreInfoCallback.error((Exception) throwable);
                    }
                });
    }
}
