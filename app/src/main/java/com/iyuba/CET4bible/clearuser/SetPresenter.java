package com.iyuba.CET4bible.clearuser;

import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.module.toolbox.RxUtil;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class SetPresenter extends BasePresenter<SetMvpView> {
    private final DataManager mDataManager;

    private Disposable mDisposable;

    public SetPresenter() {
        mDataManager = DataManager.getInstance();
    }

    @Override
    public void detachView() {
        RxUtil.dispose(mDisposable);
        super.detachView();
    }

    public void clearUser(String username, String password) {
        RxUtil.dispose(mDisposable);
        mDisposable = mDataManager.clearUser(username, password)
                .compose(RxUtil.applySingleIoScheduler())
                .subscribe(new Consumer<ClearUserResponse>() {
                    @Override
                    public void accept(ClearUserResponse list) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().clearSuccess();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            getMvpView().showMessage("网络请求失败!");
                        }
                    }
                });
    }

}
