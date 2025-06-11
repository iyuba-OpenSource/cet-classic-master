package com.iyuba.CET4bible.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.iyuba.CET4bible.activity.MainActivity;
import com.iyuba.CET4bible.activity.MainApplication;
import com.iyuba.CET4bible.databinding.ActivityQuickRegistrationBinding;
import com.iyuba.CET4bible.mvp.BaseActivity;
import com.iyuba.CET4bible.mvp.model.bean.UserBean;
import com.iyuba.CET4bible.mvp.presenter.QuickRegistrationPresenter;
import com.iyuba.CET4bible.mvp.view.QuickRegistrationContract;
import com.iyuba.CET4bible.util.MD5Util;
import com.iyuba.configation.Constant;
import com.iyuba.core.manager.AccountManager;
import com.iyuba.core.protocol.base.LoginResponse;
import com.iyuba.core.sqlite.mode.UserInfo;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;

import java.util.Random;

/**
 * 快捷注册
 */
public class QuickRegistrationActivity extends BaseActivity<QuickRegistrationContract.QuickRegistrationView, QuickRegistrationContract.QuickRegistrationPresenter>
        implements QuickRegistrationContract.QuickRegistrationView {

    private ActivityQuickRegistrationBinding binding;

    private String phone;

    private String usernameDefault;

    public static void startActivity(Activity activity, String phone) {

        Intent intent = new Intent(activity, QuickRegistrationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("PHONE", phone);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    private void getBundle() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            phone = bundle.getString("PHONE");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBundle();
        initOperation();

        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {

            int a = random.nextInt(10);
            stringBuilder.append(a);
        }

        usernameDefault = "iyuba" + stringBuilder + phone.substring(phone.length() - 4, phone.length());

        binding.qrTvUsername.setText(usernameDefault);
        binding.qrTvPassword.setText(phone.substring(phone.length() - 6, phone.length()));

        binding.qrEtUsername.setText(usernameDefault);
        binding.qrEtPassword.setText(phone.substring(phone.length() - 6, phone.length()));
    }


    private void initOperation() {

        binding.toolbar.toolbarIvTitle.setText("快捷注册");
        binding.toolbar.toolbarIvRight.setVisibility(View.INVISIBLE);
        binding.toolbar.toolbarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.qrButSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = binding.qrEtUsername.getText().toString();
                if (username.trim().length() == 0) {

                    username = usernameDefault;
                }

                String password = binding.qrEtPassword.getText().toString();
                if (password.trim().length() == 0) {

                    password = phone.substring(phone.length() - 6, phone.length());
                }

                String sign = MD5Util.MD5("11002" + username + MD5Util.MD5(password) + "iyubaV2");
                presenter.register(11002, phone, username, MD5Util.MD5(password), "android", Integer.parseInt(Constant.APPID), "concept", "json", sign);
            }
        });
    }

    @Override
    public View initLayout() {
        binding = ActivityQuickRegistrationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public QuickRegistrationContract.QuickRegistrationPresenter initPresenter() {
        return new QuickRegistrationPresenter();
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toast(String msg) {

        Toast.makeText(MainApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void registerComplete(UserBean.UserinfoDTO userinfoDTO) {


        LoginResponse loginResponse = new LoginResponse();
        loginResponse.uid = userinfoDTO.getUid() + "";
        loginResponse.amount = userinfoDTO.getAmount();
        loginResponse.vip = "0";
        loginResponse.imgsrc = userinfoDTO.getImgSrc();
        loginResponse.username = userinfoDTO.getUsername();
        loginResponse.validity = userinfoDTO.getExpireTime() + "";

        AccountManager.Instace(QuickRegistrationActivity.this).Refresh(loginResponse);

        User user = new User();
        user.vipStatus = "0";
        user.uid = userinfoDTO.getUid();
        user.name = userinfoDTO.getUsername();
        IyuUserManager.getInstance().setCurrentUser(user);

        UserInfo userInfo = new UserInfo();
        userInfo.uid = String.valueOf(userinfoDTO.getUid());
        userInfo.username = userinfoDTO.getUsername();
        userInfo.iyubi = "0";
        userInfo.vipStatus = "0";
        userInfo.isteacher = "0";
        userInfo.money = "0";

        startActivity(new Intent(QuickRegistrationActivity.this, MainActivity.class));
        finish();
    }
}