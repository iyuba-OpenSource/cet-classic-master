package com.iyuba.CET4bible.mvp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.iyuba.CET4bible.mvp.presenter.IBasePresenter;
import com.iyuba.CET4bible.mvp.view.BaseView;


public abstract class BaseActivity<V extends BaseView, P extends IBasePresenter<V>> extends AppCompatActivity implements BaseView {

    protected P presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayout());
        presenter = initPresenter();
        if (presenter != null) {
            presenter.attchView((V) this);
        }
    }

    public abstract View initLayout();

    public abstract P initPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
    }


/*    @Override
    public Resources getResources() {

        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return super.getResources();
    }*/


}
