package com.iyuba.core.activity;

/**
 * 手机注册完善内容界面
 *
 * @author czf
 * @version 1.0
 */

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.iyuba.biblelib.R;
import com.iyuba.core.listener.OperateCallBack;
import com.iyuba.core.listener.ProtocolResponse;
import com.iyuba.core.manager.AccountManager;
import com.iyuba.core.protocol.BaseHttpResponse;
import com.iyuba.core.protocol.base.LoginResponse;
import com.iyuba.core.protocol.message.RequestPhoneNumRegister;
import com.iyuba.core.protocol.message.ResponsePhoneNumRegister;
import com.iyuba.core.util.ExeProtocol;
import com.iyuba.core.widget.dialog.CustomDialog;
import com.iyuba.core.widget.dialog.CustomToast;
import com.iyuba.core.widget.dialog.WaittingDialog;

public class RegistSubmitActivity extends BasisActivity {
    private Context mContext;
    private EditText userNameEditText, passWordEditText;
    private Button submitButton;
    private String phonenumb, userName, passWord;
    private CustomDialog wettingDialog;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {

            switch (message.what) {
                case 0:
                    handler.sendEmptyMessage(5);
                    regist();
                    break;
                case 1:
                    handler.sendEmptyMessage(4);
                    CustomToast.showToast(mContext, R.string.regist_fail);
                    break;
                case 2:
                    Toast.makeText(RegistSubmitActivity.this, R.string.regist_success, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    handler.sendEmptyMessage(4);
                    CustomToast.showToast(mContext, R.string.regist_userid_exist);
                    break;
                case 4:
                    wettingDialog.dismiss();
                    break;
                case 5:
                    wettingDialog.show();
                    break;
                case 6:
                    gotoLogin();
                    break;
                case 7:

                    if (wettingDialog != null) {

                        wettingDialog.dismiss();
                    }
                    finish();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //        CrashApplication.getInstance().addActivity(this);
        setContentView(R.layout.regist_layout_phone_regist);

        userNameEditText = findViewById(R.id.regist_phone_username);
        passWordEditText = findViewById(R.id.regist_phone_paswd);
        submitButton = findViewById(R.id.regist_phone_submit);
        phonenumb = getIntent().getExtras().getString("phoneNumb");
        wettingDialog = WaittingDialog.showDialog(mContext);
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (verification()) {// 验证通过
                    // 开始注册
                    handler.sendEmptyMessage(0);// 在handler中注册
                }
            }

        });
        findViewById(R.id.button_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 验证
     */
    public boolean verification() {
        userName = userNameEditText.getText().toString();
        passWord = passWordEditText.getText().toString();
        if (!checkUserId(userName)) {
            userNameEditText.setError(mContext
                    .getString(R.string.regist_check_username_1));
            return false;
        }
        if (!checkUserName(userName)) {
            userNameEditText.setError(mContext
                    .getString(R.string.regist_check_username_2));
            return false;
        }
        if (!checkUserPwd(passWord)) {
            passWordEditText.setError(mContext
                    .getString(R.string.regist_check_userpwd_1));
            return false;
        }
        return true;
    }

    /**
     * 匹配用户名1
     */
    public boolean checkUserId(String userId) {
        return userId.length() >= 3 && userId.length() <= 20;
    }

    /**
     * 匹配用户名2 验证非手机号 邮箱号
     */
    public boolean checkUserName(String userId) {
        if (userId
                .matches("^([a-z0-ArrayA-Z]+[-_|\\.]?)+[a-z0-ArrayA-Z]@([a-z0-ArrayA-Z]+(-[a-z0-ArrayA-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
            return false;
        }
        return !userId.matches("^(1)\\d{10}$");
    }

    /**
     * 匹配密码
     */
    public boolean checkUserPwd(String userPwd) {
        return userPwd.length() >= 6 && userPwd.length() <= 20;
    }

    private void regist() {
        ExeProtocol.exe(new RequestPhoneNumRegister(userName, passWord, AccountManager.Instace(mContext).getId(),
                phonenumb), new ProtocolResponse() {

            @Override
            public void finish(BaseHttpResponse bhr) {

                ResponsePhoneNumRegister rr = (ResponsePhoneNumRegister) bhr;
                if (rr.isRegSuccess) {

                    runOnUiThread(() -> {

                        Toast.makeText(RegistSubmitActivity.this, R.string.regist_success, Toast.LENGTH_SHORT).show();
                        LoginResponse loginResponse = new LoginResponse();
                        loginResponse.uid = rr.uid;
                        loginResponse.amount = "0";
                        loginResponse.vip = "0";
                        loginResponse.imgsrc = rr.imgSrc;
                        loginResponse.username = rr.username;
                        loginResponse.validity = "0";

                        AccountManager.Instace(RegistSubmitActivity.this).Refresh(loginResponse);
                        if (wettingDialog != null) {

                            wettingDialog.dismiss();
                        }
                        RegistSubmitActivity.this.finish();
                    });
                } else if (rr.resultCode.equals("112")) {
                    // 提示用户已存在
                    handler.sendEmptyMessage(3);
                } else {
                    handler.sendEmptyMessage(1);// 弹出错误提示
                }
            }

            @Override
            public void error() {

                Log.d("error", "注册失败");
            }
        });
    }

    private void gotoLogin() {
        AccountManager.Instace(mContext).login(userName, passWord,
                new OperateCallBack() {
                    @Override
                    public void success(String result) {

                       /* Intent intent = new Intent(mContext, UpLoadImage.class);
                        intent.putExtra("regist", true);
                        startActivity(intent);*/
                        finish();
                    }

                    @Override
                    public void fail(String message) {
                    }
                });
    }
}
