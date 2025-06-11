package com.iyuba.CET4bible.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.iyuba.CET4bible.MyApplication;
import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.sqlite.ImportDatabase;
import com.iyuba.CET4bible.util.AdSplashUtil;
import com.iyuba.base.util.BrandUtil;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.activity.BasisActivity;
import com.iyuba.core.activity.Web;
import com.iyuba.core.me.sqlite.ZDBManager;
import com.iyuba.core.setting.SettingConfig;
import com.iyuba.core.sqlite.ImportLibDatabase;
import com.iyuba.core.util.CopyFileToSD;
import com.iyuba.core.util.ReadBitmap;
import com.iyuba.core.util.SaveImage;
import com.mob.MobSDK;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 起始界面Activity
 *
 * @author chentong
 */
public class WelcomeActivity extends BasisActivity {

    private Context mContext;
    private boolean isNewUser = false;
    private AdSplashUtil adSplashUtil;
    private ConfigManager configManager;
    private int lastVersion, currentVersion;
    private ImageView ad, base;
    private Button btn_skip;
    private int recLen = 5;
    private Timer timer = new Timer();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome);
        configManager = ConfigManager.Instance();
        Boolean isPrivacyAgree = configManager.loadBoolean("isPrivacyAgree");

        if (isPrivacyAgree) {

            MobSDK.submitPolicyGrantResult(true);
            initSplash();
        } else {

            showFirstDialog();
        }
    }

    Thread copyThread = new Thread(new Runnable() {

        @Override
        public void run() {
            new CopyFileToSD(mContext, "writting", Constant.picSrcAddr, () -> {
                handler.removeMessages(1);
                handler.removeMessages(3); // 5秒跳转
                handler.sendEmptyMessage(0);
            });
        }
    });

    /**
     * 0。 引导夜
     * 1。 首页
     * 3。 5秒后首页
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent;
            switch (msg.what) {
                case 0:
                    intent = new Intent(WelcomeActivity.this, HelpUse.class);
                    intent.putExtra("source", "welcome");
                    startActivity(intent);
                    finish();
                    break;
                case 1:
                    intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 2:
                    copyThread.start();
                    break;
                case 3:
                    btn_skip.setText("跳过(" + recLen + "s)");
                    if (recLen < 1) {
                        timer.cancel();
                        if (!isNewUser) {
                            startMainActivity();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            recLen--;
            handler.sendEmptyMessage(3);

        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();
    }

    @Override
    protected boolean isSwipeBackEnable() {
        return false;
    }

    /**
     * 展示隐私弹窗
     */
    private void showFirstDialog() {
//        String usageUrl = Constant.USE_URL + Constant.APPName + "&company=济南万云天教育";
//        String priUrl = Constant.PRI_URL + Constant.APPName + "&company=济南万云天教育";
        String usageUrl = Constant.USE_URL + Constant.APPName + "&company=1";
        String priUrl = Constant.PRI_URL + Constant.APPName + "&company=1";
        ClickableSpan secretClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Web.start(mContext, priUrl, "用户隐私政策");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(ContextCompat.getColor(WelcomeActivity.this, com.iyuba.configation.R.color.app_color));
            }
        };

        ClickableSpan policyClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Web.start(mContext, usageUrl, "用户使用协议");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(ContextCompat.getColor(WelcomeActivity.this, com.iyuba.configation.R.color.app_color));
            }
        };

        View view = LayoutInflater.from(mContext).inflate(R.layout.alert_text, null);
        TextView remindText = view.findViewById(R.id.remindText);
        String remindString = getResources().getString(R.string.user_protocol);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(remindString);
        spannableStringBuilder.setSpan(secretClick, remindString.indexOf("隐私政策"), remindString.indexOf("隐私政策") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(policyClick, remindString.indexOf("用户协议"), remindString.indexOf("用户协议") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        remindText.setText(spannableStringBuilder);
        remindText.setMovementMethod(LinkMovementMethod.getInstance());
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("个人信息保护政策")
                .setView(view)
                .setPositiveButton("同意", (dialog, which) -> {
                    configManager.putBoolean("isPrivacyAgree", true);
                    dialog.dismiss();

                    MyApplication.initSdk();
                    initSplash();
                })
                .setNegativeButton("不同意", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void initSplash() {
        btn_skip = findViewById(R.id.btn_skip);
        btn_skip.setVisibility(View.INVISIBLE);
        btn_skip.setOnClickListener(view -> {
            timer.cancel();
            handler.sendEmptyMessage(1);
            handler.removeMessages(3);
        });
        ad = findViewById(R.id.ad);
        ad.setOnClickListener(v -> {
            if (adSplashUtil.isRequestEnd() && !ConfigManager.Instance().loadString("startuppic_Url").equals("")) {
                Intent intent = new Intent();
                intent.setClass(mContext, Web.class);
                intent.putExtra("url", ConfigManager.Instance().loadString("startuppic_Url"));
                handler.removeMessages(1);
                startActivityForResult(intent, 0);
                timer.cancel();
            }
        });
        base = findViewById(R.id.base);
        base.setImageBitmap(ReadBitmap.readBitmap(mContext, R.drawable.base));
        if (!Constant.APP_CONSTANT.isEnglish()) {
            String type = Constant.APP_CONSTANT.TYPE();
            if ("1".equals(type)) {
                base.setImageBitmap(ReadBitmap.readBitmap(mContext, R.drawable.base));
            } else if ("2".equals(type)) {
                base.setImageBitmap(ReadBitmap.readBitmap(mContext, R.drawable.base2));
            } else {
                base.setImageBitmap(ReadBitmap.readBitmap(mContext, R.drawable.base3));
            }
        }

        adSplashUtil = new AdSplashUtil(mContext, ad);
        adSplashUtil.setCallback(new AdSplashUtil.SCallback() {
            @Override
            public void loadLocal() {
                loadLocalAd();
            }

            @Override
            public void onAdClick() {
                handler.removeMessages(1);
                handler.removeMessages(3);
                timer.cancel();
            }

            @Override
            public void startTimer() {
                try {
                    btn_skip.setVisibility(View.VISIBLE);
                    timer.schedule(timerTask, 1000, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        adSplashUtil.requestAd();

        BrandUtil.requestQQGroupNumber(mContext);

        saveIcon();

        try {
            lastVersion = ConfigManager.Instance().loadInt("version");
            currentVersion = getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            lastVersion = 0;
            e.printStackTrace();
        }

        if (lastVersion == 0)// 新用户
        {
            isNewUser = true;
            ImportDatabase db = new ImportDatabase(mContext); // cet4 、cet6
            db.setPackageName(mContext.getPackageName());
            db.setVersion(Constant.APP_CONSTANT.cetDatabaseLastVersion(), Constant.APP_CONSTANT.cetDatabaseCurVersion());// 有需要数据库更改使用
            db.openDatabase(db.getDBPath());

            ImportLibDatabase libdb = new ImportLibDatabase(mContext); // lib_database
            libdb.setPackageName(mContext.getPackageName());
            libdb.setVersion(6, 7);// 有需要数据库更改使用
            libdb.openDatabase(libdb.getDBPath());

            SettingConfig.Instance().setSyncho(true);
            ConfigManager.Instance().putInt("version", currentVersion);
            ConfigManager.Instance().putBoolean("firstuse", true);
            ConfigManager.Instance().putInt("mode", 1);
            ConfigManager.Instance().putInt("type", 2);
            ConfigManager.Instance().putInt("applanguage", 0);
            ConfigManager.Instance().putInt("isvip", 0);
            ConfigManager.Instance().putString("updateAD", "1970-01-01");
            ConfigManager.Instance().putBoolean("push", true);
            ConfigManager.Instance().putBoolean("saying", true);
            ConfigManager.Instance().putString("wordsort", "0-0");
            ConfigManager.Instance().putString("vocabulary", "study");
            SettingConfig.Instance().setHighSpeed(false);
            SettingConfig.Instance().setSyncho(true);
            SettingConfig.Instance().setLight(true);
            SettingConfig.Instance().setAutoPlay(false);
            SettingConfig.Instance().setAutoStop(true);

            ZDBManager zdbManager = new ZDBManager(mContext); // zzaidb
            zdbManager.setVersion(0, 1);
            zdbManager.openDatabase();
            handler.sendEmptyMessage(2);


        } else if (currentVersion > lastVersion) {
            ImportDatabase db = new ImportDatabase(mContext);
            db.setPackageName(this.getPackageName());
            db.setVersion(Constant.APP_CONSTANT.cetDatabaseLastVersion(), Constant.APP_CONSTANT.cetDatabaseCurVersion());// 有需要数据库更改使用
            db.openDatabase(db.getDBPath());

            ImportLibDatabase libdb = new ImportLibDatabase(mContext);
            libdb.setPackageName(this.getPackageName());
            libdb.setVersion(6, 7);// 有需要数据库更改使用
            libdb.openDatabase(libdb.getDBPath());

            ConfigManager.Instance().putBoolean("firstuse", true);
            ConfigManager.Instance().putInt("version", currentVersion);

            ZDBManager zdbManager = new ZDBManager(mContext);
            zdbManager.setVersion(0, 1);
            zdbManager.openDatabase();


            handler.sendEmptyMessage(2);
        } else {

        }
    }


    private void loadLocalAd() {
        try {
            File adPic = new File(Constant.envir + "/ad/ad.jpg");
            if (adPic.exists()) {
                int screenWidth = ((Activity) mContext).getWindowManager()
                        .getDefaultDisplay().getWidth();
                int screenHeight = ((Activity) mContext).getWindowManager()
                        .getDefaultDisplay().getHeight();
                double screenRatio = (screenHeight * 0.86) / screenWidth;
                ad.setImageBitmap(SaveImage.resizeImage(ReadBitmap
                                .readBitmap(mContext, new FileInputStream(adPic)),
                        screenRatio));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            startMainActivity();
        }
    }

    private void saveIcon() {
        try {
            InputStream is = mContext.getResources().openRawResource(
                    R.raw.icon_icon);
            BufferedInputStream bis = new BufferedInputStream(is);

            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/icon_icon.png");
            BufferedOutputStream bfos = new BufferedOutputStream(fos);
            byte[] buffer = new byte[400000];
            int count = 0;
            while ((count = bis.read(buffer)) > 0) {
                bfos.write(buffer, 0, count);
            }
            fos.close();
            is.close();
            bis.close();
            bfos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startMainActivity() {
        Intent intent = new Intent();
        intent.setClass(mContext, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (adSplashUtil.isClick()) {
                startMainActivity();
            }
        } catch (Exception e) {

        }


    }

    @Override
    protected void onDestroy() {
        if (adSplashUtil != null) {
            adSplashUtil.destroy();
        }
        super.onDestroy();
    }

}
