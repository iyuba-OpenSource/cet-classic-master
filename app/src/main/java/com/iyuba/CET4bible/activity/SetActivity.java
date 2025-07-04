package com.iyuba.CET4bible.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.clearuser.SetMvpView;
import com.iyuba.CET4bible.clearuser.SetPresenter;
import com.iyuba.CET4bible.event.JPLevelChangeEvent;
import com.iyuba.CET4bible.sqlite.op.NewTypeSectionAAnswerOp;
import com.iyuba.CET4bible.sqlite.op.NewTypeTextAOp;
import com.iyuba.core.eventbus.LoginEvent;
import com.iyuba.CET4bible.sqlite.op.BlogOp;
import com.iyuba.CET4bible.util.Share;
import com.iyuba.CET4bible.widget.PwdInputDialog;
import com.iyuba.CET4bible.widget.SleepDialog;
import com.iyuba.abilitytest.sqlite.TestRecordHelper;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.configation.ConstantManager;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.activity.BasisActivity;
import com.iyuba.core.manager.AccountManager;
import com.iyuba.core.me.activity.VipCenterActivity;
import com.iyuba.core.setting.SettingConfig;
import com.iyuba.core.thread.GitHubImageLoader;
import com.iyuba.core.util.FileSize;
import com.iyuba.core.util.ToastUtil;
import com.iyuba.core.util.TouristUtil;
import com.iyuba.core.widget.dialog.CustomToast;
import com.iyuba.wordtest.db.CetDataBase;
import com.iyuba.wordtest.manager.WordConfigManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Locale;

public class SetActivity extends BasisActivity implements SetMvpView {
    private static int hour, minute, totaltime;// total用于计算时间，volume用于调整音量,睡眠模式用到的
    private static boolean isSleep = false;// 睡眠模式是否开启
    private Context mContext;
    private SetPresenter mPresenter;
    Handler sleepHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int count = 0;
            AudioManager am = (AudioManager) mContext
                    .getSystemService(Context.AUDIO_SERVICE);
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (hour + minute != 0) {// 时间没结束
                        count++;
                        if (count % 10 == 0) {
                            if (am.getStreamVolume(AudioManager.STREAM_MUSIC) > 2) {
                                am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_LOWER, 0);// 第三参数为0代表不弹出提示。
                            }
                        }
                        totaltime--;
                        ((TextView) findViewById(R.id.sleep_state))
                                .setText(String.format(Locale.CHINA, "%02d:%02d", hour, minute));
                        hour = totaltime / 60;
                        minute = totaltime % 60;
                        sleepHandler.sendEmptyMessageDelayed(0, 60000);
                    } else {// 到结束时间
                        isSleep = false;
                        ((TextView) findViewById(R.id.sleep_state))
                                .setText(R.string.setting_sleep_state_off);
                        Intent intent = new Intent();
                        intent.setAction("gotosleep");
                        mContext.sendBroadcast(intent);
                    }
                    break;
                default:
                    break;
            }
        }

    };
    private CheckBox checkBox_Download, checkBox_Push, checkBox_night;
    private View aboutBtn, btn_download, btn_push, btn_night, btn_clear_pic,
            btn_help_use, btn_clear_video, recommendButton, sleepButton,
            language, savePathBtn, user_cancel_layout;
    private Button logout;
    OnClickListener ocl = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent;
            Dialog dialog;
            int id = arg0.getId();
            if (id == R.id.button_back) {
                finish();
            } else if (id == R.id.btn_download) {
                setDownload();
            } else if (id == R.id.btn_push) {
                setPush();
                //			case R.id.night_mod:
//				setNight();
//				break;
            /*case R.id.play_set_btn:
                intent = new Intent(mContext, PlaySet.class);
				startActivity(intent);
				break;*/
            } else if (id == R.id.clear_pic) {
                CustomToast
                        .showToast(mContext, R.string.setting_deleting, 2000);// 这里可以改为引用资源文件
                GitHubImageLoader.Instace(mContext).clearCache();
                handler.sendEmptyMessage(4);
            } else if (id == R.id.clear_video) {
                startActivity(new Intent(mContext, DeleteAudioActivity.class));
//                    dialog = new AlertDialog.Builder(mContext)
//                            .setTitle(getResources().getString(R.string.alert_title))
//                            .setMessage(getResources()
//                                    .getString(R.string.setting_alert))
//                            .setPositiveButton(getResources().getString(R.string.alert_btn_ok), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                                    int whichButton) {
//                                    CustomToast.showToast(mContext,
//                                            R.string.setting_deleting, 2000);// 这里可以改为引用资源文件
//                                    new CleanBufferAsyncTask().execute();
//                                }
//                            })
//                            .setNeutralButton(getResources().getString(
//                                    R.string.alert_btn_cancel), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            }).create();
//                    dialog.show();
            } else if (id == R.id.help_use_btn) {
                intent = new Intent(mContext, HelpUse.class);
                intent.putExtra("source", "set");
                startActivity(intent);
            } else if (id == R.id.logout) {
                if (logout.getText().equals(mContext.getText(com.iyuba.biblelib.R.string.exit))) {
                    dialog = new AlertDialog.Builder(mContext)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getResources().getString(com.iyuba.biblelib.R.string.alert_title))
                            .setMessage(getResources().getString(R.string.setting_logout_alert))
                            .setPositiveButton(
                                    getResources().getString(
                                            com.iyuba.biblelib.R.string.alert_btn_ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int whichButton) {
                                            handler.sendEmptyMessage(9);
                                        }
                                    })
                            .setNeutralButton(
                                    getResources().getString(
                                            com.iyuba.biblelib.R.string.alert_btn_cancel),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                        }
                                    }).create();
                    dialog.show();
                } else if (logout.getText().equals(
                        mContext.getText(com.iyuba.biblelib.R.string.no_login))) {

                    EventBus.getDefault().post(new LoginEvent());
                }
            } else if (id == R.id.sleep_mod) {
                intent = new Intent(mContext, SleepDialog.class);
                startActivityForResult(intent, 23);
            } else if (id == R.id.recommend_btn) {
                String text = getResources().getString(R.string.setting_share1)
                        + Constant.APPName
                        + getResources().getString(R.string.setting_share2);
                showShare("我正在使用" + Constant.APP_CONSTANT.APPName(), text, "https://sj.qq.com/myapp/detail.htm?apkName="
                        + mContext.getPackageName());
//                    prepareMessage();
            } else if (id == R.id.save_path_btn) {//				mContext.startActivity(new Intent(mContext,
//						FileBrowserActivity.class));
            } else if (id == R.id.about_btn) {
                intent = new Intent();
                intent.setClass(mContext, AboutActivity.class);
                startActivity(intent);
            }
        }
    };
    private TextView picSize, soundSize, savePath;
    private int appLanguage;
    private TextView languageText;
    private Button button_back;
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    ((TextView) findViewById(R.id.sleep_state))
                            .setText(R.string.setting_sleep_state_off);
                    break;
                case 3:
                    break;
                case 4:
                    picSize.setText(getSize(0));
                    break;
                case 5:
                    initLanguage();
                    break;
                case 7:
                    initText();
                    initCheckBox();
                    break;
                case 8:
                    initSleep();
                    break;
                case 9:

                    new NewTypeTextAOp(SetActivity.this).clearRecord();
                    //清楚答题记录
                    new NewTypeSectionAAnswerOp(SetActivity.this).resetAnswer();
                    //清楚闯关记录
                    CetDataBase db = CetDataBase.getInstance(SetActivity.this);
                    db.getCetRootWordDao().reset();
                    WordConfigManager.Instance(SetActivity.this).putInt("stage", 1);

                    TestRecordHelper.getInstance(SetActivity.this).clearAbilityResult();
                    AccountManager.Instace(mContext).loginOut();
                    CustomToast.showToast(mContext,
                            R.string.setting_loginout_success);
                    SettingConfig.Instance().setHighSpeed(false);
                    checkBox_Download.setChecked(false);
                    logout.setText(com.iyuba.biblelib.R.string.no_login);
                    finish();
                    break;
                default:
                    break;
            }
        }

    };

    private ViewGroup llLanguageLevel;
    private TextView tvCurrentLevel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.layout_set);
        initWidget();
        handler.sendEmptyMessage(5);
        mPresenter = new SetPresenter();
        mPresenter.attachView(this);
    }

    private void initCheckBox() {
        if (!AccountManager.Instace(mContext).checkUserLogin()) {
            SettingConfig.Instance().setHighSpeed(false);
        }
        checkBox_Download = findViewById(R.id.CheckBox_Download);
        checkBox_Download.setChecked(SettingConfig.Instance().isHighSpeed());
        checkBox_Download
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        setDownload();
                    }
                });
        checkBox_Push = findViewById(R.id.CheckBox_Push);
        checkBox_Push.setChecked(SettingConfig.Instance().isPush());
        checkBox_Push.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                setPush();
            }
        });
//		checkBox_night = (CheckBox) findViewById(R.id.CheckBox_night);
//		checkBox_night.setChecked(SettingConfig.Instance().isNight());
//		checkBox_night
//				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//					@Override
//					public void onCheckedChanged(CompoundButton buttonView,
//							boolean isChecked) {
//						setNight();
//					}
//				});
    }

    /**
     *
     */
    private void initWidget() {
        button_back = findViewById(R.id.button_back);
        btn_download = findViewById(R.id.btn_download);
        btn_push = findViewById(R.id.btn_push);
//		btn_night = findViewById(R.id.night_mod);
        //btn_play_set = findViewById(R.id.play_set_btn);
        btn_clear_pic = findViewById(R.id.clear_pic);
        btn_clear_video = findViewById(R.id.clear_video);
        btn_help_use = findViewById(R.id.help_use_btn);
        user_cancel_layout = findView(R.id.user_cancel_layout);
        savePathBtn = findViewById(R.id.save_path_btn);
        savePath = findViewById(R.id.save_path);
        picSize = findViewById(R.id.picSize);
        soundSize = findViewById(R.id.soundSize);
        logout = findViewById(R.id.logout);
        sleepButton = findViewById(R.id.sleep_mod);
        aboutBtn = findViewById(R.id.about_btn);
        recommendButton = findViewById(R.id.recommend_btn);

        llLanguageLevel = findView(R.id.rl_change_jp_language);
        tvCurrentLevel = findView(R.id.tv_jp_level);

        findView(R.id.night_mod).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showNightModeChangeDialog();
            }
        });
        TextView tvNightState = findView(R.id.night_state);
        tvNightState.setText(simpleNightMode.isNightMode() ? "开" : "关");

        int jpLevel = ConstantManager.getAppType();
        if (jpLevel > 10) {
            llLanguageLevel.setVisibility(View.VISIBLE);
        } else {
            llLanguageLevel.setVisibility(View.GONE);
        }
        jpLevel = (jpLevel - 4) % 10;
        tvCurrentLevel.setText(String.format(Locale.CHINA, "日语N%d", jpLevel));

        llLanguageLevel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageLevelDialog();
            }
        });

        initListener();
    }

    private void initText() {
        if (AccountManager.Instace(mContext).checkUserLogin()) {
            if (TouristUtil.isTourist()) {
                logout.setVisibility(View.INVISIBLE);
            } else {
                logout.setVisibility(View.VISIBLE);
                logout.setText(com.iyuba.biblelib.R.string.exit);
            }
        } else {
            logout.setVisibility(View.VISIBLE);
            logout.setText(com.iyuba.biblelib.R.string.no_login);
        }
        savePath.setText(Constant.envir);
        picSize.setText(getSize(0));
//        soundSize.setText(getSize(1));
        soundSize.setText("");
        //
    }

    /**
     *
     */
    private void initListener() {
        button_back.setOnClickListener(ocl);
        btn_download.setOnClickListener(ocl);
        btn_push.setOnClickListener(ocl);
//		btn_night.setOnClickListener(ocl);
        logout.setOnClickListener(ocl);
        btn_clear_pic.setOnClickListener(ocl);
        btn_clear_video.setOnClickListener(ocl);
        btn_help_use.setOnClickListener(ocl);
        sleepButton.setOnClickListener(ocl);
        recommendButton.setOnClickListener(ocl);
        aboutBtn.setOnClickListener(ocl);
        savePathBtn.setOnClickListener(ocl);
        user_cancel_layout.setOnClickListener(v -> {

            if (AccountManager.Instace(mContext).checkUserLogin()) {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("注销账号后将无法登录，并且将不再会保存个人信息，账户信息将会被清除，确定要注销账号吗？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            showContinueWrittenOffDialog();
                            dialog.dismiss();
                        })
                        .setNegativeButton("取消", null)
                        .show();
            } else {
                Toast.makeText(mContext, "未登录，请先登录", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showContinueWrittenOffDialog() {

        //  弹验证密码的弹窗
        final PwdInputDialog dialogTip = new PwdInputDialog(SetActivity.this);
        dialogTip.setTitleStr(getString(R.string.input_pwd_to_written_off));
        dialogTip.setCancelStr(getString(com.iyuba.biblelib.R.string.cancel));
        dialogTip.setConfirmStr(getString(R.string.confirm_written_off));
        dialogTip.setShowInputPwd(true);
        dialogTip.setListener(new PwdInputDialog.OnBtnClickListener() {
            @Override
            public void onCancelClick() {
                dialogTip.dismiss();
            }

            @Override
            public void onConfirmClick() {
                dialogTip.dismiss();
            }

            @Override
            public void onCheckInputPassWord(final String password) {

//                String[] nameAndPwd = AccountManager.Instace(mContext).getUserNameAndPwd();
//                String userName = nameAndPwd[0];

                String userName = AccountManager.Instace(mContext).userInfo.username;
                if (TextUtils.equals(password, password)) {
                    mPresenter.clearUser(userName, password);
                    dialog.dismiss();
                } else {
                    ToastUtil.showToast(SetActivity.this, "密码错误请重新输入");
                }
            }
        });
        dialogTip.show();
    }


    private void initSleep() {
        if (!isSleep) {
            ((TextView) findViewById(R.id.sleep_state))
                    .setText(R.string.setting_sleep_state_off);
        } else {
            ((TextView) findViewById(R.id.sleep_state))
                    .setText(String.format(Locale.CHINA, "%02d:%02d", hour, minute));
        }
    }

    private void initLanguage() {
        language = findViewById(R.id.set_language);
        language.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setLanguage();
            }
        });
        languageText = findViewById(R.id.curr_language);
        // 根据设置初始化
        appLanguage = ConfigManager.Instance().loadInt("applanguage");
        String[] languages = mContext.getResources().getStringArray(
                R.array.language);
        languageText.setText(languages[appLanguage]);
    }

    private void setLanguage() {
        String[] languages = mContext.getResources().getStringArray(
                R.array.language);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(com.iyuba.biblelib.R.string.alert_title);
        builder.setSingleChoiceItems(languages, appLanguage,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        switch (index) {
                            case 0:// 系统默认语言
                                appLanguage = 0;
                                break;
                            case 1:// 简体中文
                                appLanguage = 1;
                                break;
                            case 2:// 英语
                                appLanguage = 2;
                                break;
                            case 3:// 后续
                                break;
                            default:
                                appLanguage = 0;
                                break;
                        }
                        ConfigManager.Instance().putInt("applanguage",
                                appLanguage);
                        Intent intent = new Intent();
                        intent.setAction("changeLanguage");
                        mContext.sendBroadcast(intent);
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(com.iyuba.biblelib.R.string.alert_btn_cancel, null);
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 23 && resultCode == 1) {// 睡眠模式设置的返回结果
            hour = data.getExtras().getInt("hour");
            minute = data.getExtras().getInt("minute");
            if (hour + minute == 0) {
                isSleep = false;
                hour = 0;
                minute = 0;
                totaltime = 0;
                sleepHandler.removeMessages(0);
                handler.sendEmptyMessage(2);
            } else {
                sleepHandler.removeMessages(0);
                isSleep = true;
                totaltime = hour * 60 + minute;
                sleepHandler.sendEmptyMessage(0);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sleepHandler.removeMessages(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.sendEmptyMessage(5);
        handler.sendEmptyMessage(7);
        handler.sendEmptyMessage(8);
    }

    private String getSize(int type) {
        if (type == 0) {
            return FileSize.getInstance().getFormatFolderSize(
                    new File(RuntimeManager.getContext()
                            .getExternalCacheDir() == null ? "" : RuntimeManager.getContext()
                            .getExternalCacheDir().getAbsolutePath()));
        } else if (type == 1) {
            return FileSize.getInstance().getFormatFolderSize(
                    new File(Constant.videoAddr));
        } else {
            return FileSize.getInstance().getFormatFolderSize(
                    new File(Constant.microAddr));
        }
    }

    private void setDownload() {
        if (AccountManager.Instace(mContext).checkUserLogin()
                && (ConfigManager.Instance().loadInt("isvip") > 0)) {
            if (checkBox_Download.isChecked()) {
                SettingConfig.Instance().setHighSpeed(true);
            } else {
                SettingConfig.Instance().setHighSpeed(false);
            }
        } else {
            AlertDialog alert = new AlertDialog.Builder(mContext).create();
            alert.setTitle(com.iyuba.biblelib.R.string.alert_title);
            alert.setMessage(mContext.getText(R.string.setting_vip_download));
            alert.setIcon(android.R.drawable.ic_dialog_alert);
            alert.setButton(AlertDialog.BUTTON_POSITIVE, getResources()
                            .getString(com.iyuba.biblelib.R.string.alert_btn_buy),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(mContext, VipCenterActivity.class);
                            startActivity(intent);
                        }
                    });
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, getResources()
                            .getString(com.iyuba.biblelib.R.string.alert_btn_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            alert.show();
        }
        checkBox_Download.setChecked(SettingConfig.Instance().isHighSpeed());
    }

    private void setPush() {
        if (checkBox_Push.isChecked()) {
            SettingConfig.Instance().setPush(true);
           /* PushAgent.getInstance(mContext).enable(new UPushSettingCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(String s, String s1) {

                }
            });*/
        } else {
            SettingConfig.Instance().setPush(false);
/*            PushAgent.getInstance(mContext).disable(new UPushSettingCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(String s, String s1) {

                }
            });*/
        }
    }


    /**
     * 修改日语N123
     */
    private void showLanguageLevelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("修改当前日语考试等级");
        String[] array = {"日语N1", "日语N2", "日语N3"};

        final int level = ConstantManager.getAppType();

        builder.setSingleChoiceItems(array, (level - 5) % 10,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        int pos = which + 1 + 4 + 10;
                        if (pos != level) {
                            ConstantManager.init(pos);
//                            IMooc.initAPP(Constant.APPID, AccountManager.Instance(mContext).getId(),
//                                    AccountManager.Instance(mContext).getVipStatus() + "");
//                            IMooc.setOid(Constant.VIP_STATUS + "");
                            tvCurrentLevel.setText(String.format(Locale.CHINA, "日语N%d", which + 1));
                            // 修改主页
                            EventBus.getDefault().post(new JPLevelChangeEvent());

                            new BlogOp(mContext).removeAllData();
                        }
                    }
                });
        builder.setNegativeButton(com.iyuba.biblelib.R.string.alert_btn_ok, null);
        builder.create().show();
    }

    private void showNightModeChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("提示");
        builder.setMessage("是否切换夜间模式？");
        builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simpleNightMode.setNightMode(true);
            }
        }).setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simpleNightMode.setNightMode(false);
            }
        });
        builder.show();
    }


    public void showShare(String title, String text, String url) {
        String imagePath = Constant.iconAddr;

        Share share = new Share(getApplicationContext());
        share.shareMessage(imagePath, text, url, title);
    }

    @Override
    public void clearSuccess() {
//        InitPush.getInstance().unRegisterToken(this, Integer.parseInt(AccountManager.Instace(mContext).userId));
        AccountManager.Instace(this).loginOut();
        SettingConfig.Instance().setHighSpeed(false);
        CustomToast.showToast(this, com.iyuba.biblelib.R.string.loginout_success);
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("账户被注销！账户信息清除")
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                }).show();
    }

    @Override
    public void showMessage(String msg) {
        ToastUtil.showToast(this, msg);
    }
}
