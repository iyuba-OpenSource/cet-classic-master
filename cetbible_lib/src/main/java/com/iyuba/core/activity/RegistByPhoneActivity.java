package com.iyuba.core.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iyuba.biblelib.R;
import com.iyuba.configation.Constant;
import com.iyuba.core.http.Http;
import com.iyuba.core.util.TelNumMatch;
import com.iyuba.core.util.ToastUtil;
import com.iyuba.core.widget.dialog.CustomDialog;
import com.iyuba.core.widget.dialog.CustomToast;
import com.iyuba.core.widget.dialog.WaittingDialog;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 手机注册界面
 *
 * @author czf
 * @version 1.0
 */


public class RegistByPhoneActivity extends BasisActivity {
    private Context mContext;
    private EditText phoneNum, messageCode;
    private Button getCodeButton;
    private TextView toEmailButton;
    private String phoneNumString = "", messageCodeString = "";

    private Timer timer;
    private CheckBox protocol;
    private EventHandler eh;
    private TimerTask timerTask;
    private CustomDialog waittingDialog;

    private EditTextWatch editTextWatch;
    private Button nextstep_unfocus;
    private Button nextstep_focus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        editTextWatch = new EditTextWatch();
        setContentView(R.layout.regist_layout_phone);


        //CrashApplication.getInstance().addActivity(this);
        waittingDialog = WaittingDialog.showDialog(mContext);
        messageCode = findViewById(R.id.regist_phone_code);
        messageCode.addTextChangedListener(editTextWatch);
        phoneNum = findViewById(R.id.regist_phone_numb);
        phoneNum.addTextChangedListener(editTextWatch);
        getCodeButton = findViewById(R.id.regist_getcode);
        nextstep_unfocus = findViewById(R.id.nextstep_unfocus);

        nextstep_unfocus.setEnabled(false);
        nextstep_focus = findViewById(R.id.nextstep_focus);
        protocol = findViewById(R.id.protocol);
//        MobSDK.init(getApplicationContext(), Constant.APP_CONSTANT.MOB_APPKEY(), Constant.APP_CONSTANT.MOB_APP_SECRET());
        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                // TODO 此处为子线程！不可直接处理UI线程！处理后续操作需传到主线程中操作！
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //成功回调
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交短信、语音验证码成功
                        // 短信注册成功后，返回MainActivity,然后提示新好友

                        runOnUiThread(() -> {

                            Toast.makeText(RegistByPhoneActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setClass(mContext, RegistSubmitActivity.class);
                            intent.putExtra("phoneNumb", phoneNumString);
                            startActivity(intent);
                            finish();
                        });

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取短信验证码成功
                        handler_verify.sendEmptyMessage(1);

                    } else if (event == SMSSDK.EVENT_GET_VERIFY_TOKEN_CODE) {
                        //本机验证获取token成功

                    } else if (event == SMSSDK.EVENT_VERIFY_LOGIN) {
                        //本机验证登陆成功

                    }
                } else if (result == SMSSDK.RESULT_ERROR) {

                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                        String messageStr = "验证码错误";
                        Message message = new Message();
                        message.obj = messageStr;
                        message.what = 5;
                        handler_waitting.sendMessage(message);
                    }
                } else {
                    //其他失败回调
                    ((Throwable) data).printStackTrace();
                    handler_waitting.sendEmptyMessage(2);
                    Message message = new Message();
                    message.obj = data;
                    handler_waitting.sendMessage(message);
                }
            }
        };
        SMSSDK.registerEventHandler(eh);


        String remindString = "我已阅读并同意使用条款和隐私政策";
        String usageUrl = Constant.USE_URL + Constant.APPName + "&company=1";
        String priUrl = Constant.PRI_URL + Constant.APPName + "&company=1";


        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(remindString);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Web.start(mContext, usageUrl, "使用条款");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
            }
        };
        spannableStringBuilder.setSpan(clickableSpan, remindString.indexOf("使用条款"), remindString.indexOf("使用条款") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan priClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Web.start(mContext, priUrl, "用户隐私协议");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
            }
        };
        spannableStringBuilder.setSpan(priClickableSpan, remindString.indexOf("隐私政策"), remindString.indexOf("隐私政策") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        protocol.setText(spannableStringBuilder);
        protocol.setMovementMethod(LinkMovementMethod.getInstance());


        findViewById(R.id.button_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toEmailButton = findViewById(R.id.regist_email);
        toEmailButton.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        toEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, RegistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        getCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setCodeEnable(false);
                if (!protocol.isChecked()) {
                    ToastUtil.showToast(RegistByPhoneActivity.this, "必须先同意使用条款和隐私政策");
                    setCodeEnable(true);
                    return;
                }
                if (verificationNum()) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    handler_waitting.sendEmptyMessage(1);
                    phoneNumString = phoneNum.getText().toString();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    String url = "http://api.iyuba.com.cn/v2/api.iyuba?format=json&protocol=10009&username=" + phoneNumString;
                    Http.get(url, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            handler_waitting.sendEmptyMessage(2);
                            setCodeEnable(true);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            runOnUiThread(() -> waittingDialog.dismiss());

                            String resStr = response.body().string();
                            Gson gson = new Gson();
                            JsonElement element = gson.fromJson(resStr, JsonElement.class);
                            JsonObject jsonObject = element.getAsJsonObject();
                            if (!jsonObject.get("result").getAsString().equals("101")) {
                                SMSSDK.getVerificationCode("86", phoneNumString);
                            } else {
                                runOnUiThread(() -> setCodeEnable(true));
                                handler_waitting.sendEmptyMessage(3);
                            }
                        }
                    });

                } else {
                    setCodeEnable(true);
                    handler_waitting.sendEmptyMessage(4);
                }
            }
        });
        nextstep_focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!protocol.isChecked()) {

                    CustomToast.showToast(mContext, "请勾选隐私协议");
                    return;
                }

                if (verification()) {
                    SMSSDK.submitVerificationCode("86", phoneNumString, messageCode.getText().toString());
                } else {
                    CustomToast.showToast(mContext, "验证码不能为空");
                }
            }
        });
    }

    private void setCodeEnable(boolean isEnable) {

        if (isEnable) {

            getCodeButton.setEnabled(true);
            getCodeButton.setBackgroundResource(com.iyuba.configation.R.drawable.button_background_normal);
        } else {

            getCodeButton.setEnabled(false);
            getCodeButton.setBackgroundResource(com.iyuba.configation.R.drawable.button_background_gray);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler_time = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Handler处理消息
            if (msg.what > 0) {
                getCodeButton.setText("重新发送(" + msg.what + "s)");
                setCodeEnable(false);
            } else {
                timer.cancel();
                setCodeEnable(true);
                getCodeButton.setText("获取验证码");
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler_waitting = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    waittingDialog.show();
                    break;
                case 2:
                    CustomToast.showToast(mContext, "网络请求错误，请稍后再试");
                    waittingDialog.dismiss();
                    break;
                case 3:
                    CustomToast.showToast(mContext, "手机号已注册，请换一个号码试试~", 2000);
                    waittingDialog.dismiss();
                    break;
                case 4:
                    CustomToast.showToast(mContext, "电话不能为空");
                    break;
                case 5:
                    if (msg.obj != null) {
                        if (!(msg.obj).equals("每分钟发送次数超限")) {
                            CustomToast.showToast(mContext, (String) msg.obj);
                        }
                    }
                    waittingDialog.dismiss();
                    break;
            }
        }
    };
    @SuppressLint("HandlerLeak")
    Handler handler_verify = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // Handler处理消息
            if (msg.what == 0) {
                timer.cancel();
                /*
                 * getCodeButton.setText("下一步"); getCodeButton.setEnabled(true);
                 */
                String verifyCode = (String) msg.obj;
                messageCode.setText(verifyCode);
                nextstep_focus.setVisibility(View.VISIBLE);
                nextstep_focus.setEnabled(true);
            } else if (msg.what == 1) {

                timer = new Timer();
                timerTask = new TimerTask() {
                    int i = 60;

                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = i--;
                        handler_time.sendMessage(msg);
                    }
                };
                timer.schedule(timerTask, 1000, 1000);
                getCodeButton.setTextColor(Color.WHITE);
                /*getCodeButton.setEnabled(false);*/
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    public boolean verification() {
        phoneNumString = phoneNum.getText().toString();
        messageCodeString = messageCode.getText().toString();
        if (phoneNumString.length() == 0) {
            phoneNum.setError("手机号不能为空");
            return false;
        }
        if (!checkPhoneNum(phoneNumString)) {
            phoneNum.setError("手机号输入错误");
            return false;
        }
        if (messageCodeString.length() == 0) {
            messageCode.setError("验证码不能为空");
            return false;
        }
        return true;
    }

    /**
     * 验证
     */
    public boolean verificationNum() {
        phoneNumString = phoneNum.getText().toString();
        messageCodeString = messageCode.getText().toString();
        if (phoneNumString.length() == 0) {
            phoneNum.setError("手机号不能为空");
            return false;
        }
        if (!checkPhoneNum(phoneNumString)) {
            phoneNum.setError("手机号输入错误");
            return false;
        }

        return true;
    }

    public boolean checkPhoneNum(String userId) {
        if (userId.length() < 2)
            return false;
        TelNumMatch match = new TelNumMatch(userId);
        int flag = match.matchNum();
        /*不check 号码的正确性，只check 号码的长度*/
        /*if (flag == 1 || flag == 2 || flag == 3) {
            return true;
		} else {
			return false;
		}*/
        return flag != 5;
    }

    public class EditTextWatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if ((s.toString().length() >= 4 && verificationNum()) || (verificationNum() && messageCode.getText().toString().length() >= 4)) {
                if (timer != null) {
                    timer.cancel();
                }
                nextstep_focus.setVisibility(View.VISIBLE);
                nextstep_focus.setEnabled(true);
            } else {
                nextstep_focus.setVisibility(View.GONE);
                nextstep_focus.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
        }

    }
}
