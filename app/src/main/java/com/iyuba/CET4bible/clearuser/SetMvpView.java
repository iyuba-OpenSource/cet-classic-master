package com.iyuba.CET4bible.clearuser;

import com.iyuba.module.mvp.MvpView;

public interface SetMvpView extends MvpView {

    void clearSuccess();

    void showMessage(String msg);
}
