package com.iyuba.CET4bible.mvp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.iyuba.CET4bible.activity.MainApplication;
import com.iyuba.CET4bible.mvp.presenter.IBasePresenter;
import com.iyuba.CET4bible.mvp.view.BaseView;


/**
 * fragment的基类
 */
public abstract class BaseFragment<V extends BaseView, P extends IBasePresenter<V>> extends Fragment implements BaseView {

    protected P presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = initLayout(inflater, container);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = initPresenter();
        if (presenter != null) {
            presenter.attchView((V) this);
        }
    }


    protected abstract View initLayout(LayoutInflater inflater, ViewGroup container);

    protected abstract P initPresenter();


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
    }

    public void toast(String msg) {

        Toast.makeText(MainApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
