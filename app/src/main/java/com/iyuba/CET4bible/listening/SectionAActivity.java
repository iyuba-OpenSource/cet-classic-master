package com.iyuba.CET4bible.listening;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.iyuba.CET4bible.R;
import com.iyuba.core.eventbus.LoginEvent;
import com.iyuba.CET4bible.listening.presenter.PDFUtil;
import com.iyuba.CET4bible.manager.ListenDataManager;
import com.iyuba.CET4bible.protocol.StudyRecordInfo;
import com.iyuba.CET4bible.protocol.UpdateStudyRecordRequestNew;
import com.iyuba.CET4bible.sqlite.op.NewTypeTextAOp;
import com.iyuba.CET4bible.sqlite.op.NewTypeTextBOp;
import com.iyuba.CET4bible.sqlite.op.NewTypeTextCOp;
import com.iyuba.CET4bible.util.AdBannerUtil;
import com.iyuba.CET4bible.util.Share;
import com.iyuba.CET4bible.viewpager.ListenFragmentAdapter;
import com.iyuba.base.util.L;
import com.iyuba.base.util.T;
import com.iyuba.base.util.Util;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.activity.BasisActivity;
import com.iyuba.core.http.Http;
import com.iyuba.core.http.HttpCallback;
import com.iyuba.core.manager.AccountManager;
import com.iyuba.core.me.activity.VipCenterActivity;
import com.iyuba.core.sqlite.mode.test.CetAnswer;
import com.iyuba.core.sqlite.mode.test.CetText;
import com.iyuba.core.util.GetDeviceInfo;
import com.iyuba.core.widget.dialog.CustomDialog;
import com.iyuba.core.widget.dialog.WaittingDialog;
import com.iyuba.play.OnStateChangeListener;
import com.iyuba.play.Player;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

/**
 * 真题 - 题型 - 听力 - sectionA页面
 * 题目详情页面
 */
public class SectionAActivity extends BasisActivity {
    public static int REQUEST_CODE_CONTACT;

    private Context mContext;
    private TextView previous, next, submit;
    private Button pause, sheet;
    private ViewPager viewPager;
    private TextView title;
    private int curPos;
    public String section;
    //newtype_texta表的number


    private ArrayList<CetAnswer> answerList;
    private ListenFragmentAdapter listenFragmentAdapter;
    private SeekBar seekbar;
    private TextView curTime, allTime;
    private int minus;
    private boolean isNewType;
    private ImageView iv_more;
    private PopupWindow popupWindow;
    private Player extendedPlayer;

    /**
     * 分享
     */
    private static cn.sharesdk.onekeyshare.OnekeyShare oks;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    previous.setEnabled(false);
                    previous.setBackgroundResource(R.drawable.bg_disable);
                    next.setBackgroundResource(R.drawable.pre_bg);
                    next.setEnabled(true);
                    submit.setVisibility(View.GONE);
                    break;
                case 2:
                    previous.setBackgroundResource(R.drawable.pre_bg);
                    next.setBackgroundResource(R.drawable.pre_bg);
                    previous.setEnabled(true);
                    next.setEnabled(true);
                    submit.setVisibility(View.GONE);
                    break;
                case 3:
                    previous.setEnabled(true);
                    next.setEnabled(false);
                    next.setBackgroundResource(R.drawable.bg_disable);
                    previous.setBackgroundResource(R.drawable.pre_bg);

                    submit.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    public MediaPlayer getOriginFragmentPlayer() {
        return originFragmentPlayer;
    }

    public void setOriginFragmentPlayer(MediaPlayer originFragmentPlayer) {
        this.originFragmentPlayer = originFragmentPlayer;
    }

    public MediaPlayer getRecordFragmentPlayer() {
        return recordFragmentPlayer;
    }

    public void setRecordFragmentPlayer(MediaPlayer recordFragmentPlayer) {
        this.recordFragmentPlayer = recordFragmentPlayer;
    }

    private MediaPlayer originFragmentPlayer;
    private MediaPlayer recordFragmentPlayer;
    Handler videoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    int i = extendedPlayer.getCurrentPosition();
                    seekbar.setProgress(i);
                    curTime.setText(formatTime(i));
                    videoHandler.sendEmptyMessageDelayed(0, 1000);
                    setPauseBackground();
                    break;
                case 1:
                    seekbar.setSecondaryProgress(msg.arg1 * seekbar.getMax() / 100);
                    break;
                case 2:
                    Log.e("onCompletion", "onCompletion");
                    viewPager.setCurrentItem(0, true);
                    setContent(1, true, false);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private TextView tv_speed;
    private AdBannerUtil adBannerUtil;
    //是否为解析返回的
    private boolean isExplain = false;
    OnClickListener ocl = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent;
            int id = arg0.getId();
            if (id == R.id.button_back) {
                onBackPressed();
            } else if (id == R.id.pause) {
                try {
                    if (extendedPlayer.isPlaying()) {

                        extendedPlayer.pause();
                    } else {

                        extendedPlayer.start();

                        for (int i = 0; i < listenFragmentAdapter.getCount(); i++) {

                            Fragment fragment = listenFragmentAdapter.getItem(i);
                            if (fragment instanceof ListenTestFragment) {

                                ListenTestFragment listenTestFragment = (ListenTestFragment) fragment;
                                listenTestFragment.pausePlayer();
                                break;
                            }
                        }
                    }
                    setPauseBackground();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            } else if (id == R.id.preview) {
                if (isExplain) {
                    // 是否为查看解析
                    viewPager.setCurrentItem(0, true);
                } else {
                    viewPager.setCurrentItem(0, true);
                }
                setContent(-1, false, false);
            } else if (id == R.id.next) {
                if (isExplain) {
                    // 是否为查看解析
                    viewPager.setCurrentItem(0, true);
                } else {
                    viewPager.setCurrentItem(0, true);
                }
                setContent(1, false, false);
            } else if (id == R.id.submit) {
                intent = new Intent(mContext, ListenSubmit.class);
                intent.putExtra("finish", true);
                startActivityForResult(intent, 0);
            }
        }
    };
    private float speed = 1.0f;
    private int count = 1;
    //学习纪录
    private StudyRecordInfo studyRecordInfo;
    private GetDeviceInfo deviceInfo;
    private long startTime;
    private CustomDialog waitingDialog;

    private PDFUtil pdfUtil;
    public String mExamTime;
    private TextView tvAB;
    //    private OnekeyShare oks;
    private LinearLayout play_control;

    private NewTypeTextAOp newTypeTextAOp;

    private NewTypeTextBOp newTypeTextBOp;

    private NewTypeTextCOp newTypeTextCOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_a);
        mContext = this;
        //        CrashApplication.getInstance().addActivity(this);

        newTypeTextAOp = new NewTypeTextAOp(mContext);
        newTypeTextBOp = new NewTypeTextBOp(mContext);
        newTypeTextCOp = new NewTypeTextCOp(mContext);

        extendedPlayer = new Player();

        section = getIntent().getStringExtra("section");
        isNewType = getIntent().getBooleanExtra("isNewType", false);

        answerList = ListenDataManager.Instance().answerList;

        pdfUtil = new PDFUtil();
        waitingDialog = WaittingDialog.showDialog(mContext);

        deviceInfo = new GetDeviceInfo(mContext);
        studyRecordInfo = new StudyRecordInfo();
        studyRecordInfo.uid = AccountManager.Instace(mContext).getId();
        studyRecordInfo.IP = deviceInfo.getLocalIPAddress();
        studyRecordInfo.DeviceId = deviceInfo.getLocalMACAddress();
        studyRecordInfo.Device = deviceInfo.getLocalDeviceType();
        studyRecordInfo.updateTime = "   ";
        studyRecordInfo.EndFlg = " ";
        studyRecordInfo.Lesson = Constant.APPName;
        studyRecordInfo.LessonId = ListenDataManager.Instance().year;
        studyRecordInfo.BeginTime = deviceInfo.getCurrentTime();
        startTime = System.currentTimeMillis();
        mExamTime = ListenDataManager.Instance().year;
        tvAB = findView(R.id.tv_ab);
        tvAB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                abRepeat();
            }
        });
        findViewById(R.id.button_back).setOnClickListener(ocl);
        title = findViewById(R.id.play_title_info);
        iv_more = findViewById(R.id.iv_more);
        iv_more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow(v);
            }
        });

        if (TextUtils.isEmpty(ListenDataManager.Instance().year)) {
            finish();
            return;
        }

        if (isNewType) {
            String strTitle = ListenDataManager.Instance().year;
            StringBuilder sb = new StringBuilder();
            sb.append(strTitle.substring(0, 4)).append("年").append(strTitle.substring(4, 6)).append("月")
                    .append("(").append(strTitle.substring(strTitle.length() - 1, strTitle.length())).append(")");
            title.setText(sb.toString());
        } else {
            title.setText(ListenDataManager.Instance().year);
        }

        play_control = findView(R.id.play_control);
        viewPager = findViewById(R.id.viewpager);
        listenFragmentAdapter = new ListenFragmentAdapter(mContext, getSupportFragmentManager(), section, mExamTime);
        viewPager.setAdapter(listenFragmentAdapter);
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 2 || position == 3) {
                    previous.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                    play_control.setVisibility(View.GONE);
                    submit.setVisibility(View.GONE);
                    if (extendedPlayer.isPlaying()) {
                        extendedPlayer.pause();
                    } else if (extendedPlayer.isPreparing()) {
                        extendedPlayer.setInstantPlay(false);
                    }
                } else {
                    previous.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    play_control.setVisibility(View.VISIBLE);
                    if (originFragmentPlayer != null && originFragmentPlayer.isPlaying()) {
                        originFragmentPlayer.pause();
                    }
                    if (recordFragmentPlayer != null && recordFragmentPlayer.isPlaying()) {
                        recordFragmentPlayer.pause();
                    }
                }

                //停止评测页面的播放和录音
                ReadEvaluateFragment readEvaluateFragment = null;
                for (int i = 0; i < listenFragmentAdapter.getCount(); i++) {

                    Fragment fragment = listenFragmentAdapter.getItem(i);
                    if (fragment instanceof ReadEvaluateFragment) {

                        readEvaluateFragment = (ReadEvaluateFragment) fragment;
                    }
                }
                if (readEvaluateFragment != null) {

                    readEvaluateFragment.stopEvaluateJobs();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = findView(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        initView();
        /**
         * 广告
         */
        adBannerUtil = new AdBannerUtil(mContext);
        adBannerUtil.setView(findViewById(R.id.youdao_ad), (ImageView) findViewById(R.id.photoImage), (TextView) findViewById(R.id.close));
        adBannerUtil.setMiaozeView((ViewGroup) findById(R.id.adMiaozeParent));
        adBannerUtil.loadAd();
        viewPager.setCurrentItem(0, true);
    }

    int abRepeatStatus = 0;
    int abRepeatStart = 0;
    long abRepeatEnd = 0;
    Disposable abRepeatSub;


    private void abRepeat() {
        if (abRepeatStatus == 0) {
            if (!extendedPlayer.isPlaying()) {
                return;
            }
            abRepeatStart = extendedPlayer.getCurrentPosition();
            abRepeatStatus = 1;
            tvAB.setText("A-");
            showShort("开始记录A- ，再次点击即可区间播放");
            L.e("start ----  " + abRepeatStart + "    ");
        } else if (abRepeatStatus == 1) {
            abRepeatEnd = extendedPlayer.getCurrentPosition();
            if (abRepeatEnd < abRepeatStart) {
                resetABRepeat();
                return;
            }
            abRepeatStatus = 2;
            tvAB.setText("A-B");
            showShort("开始播放 A-B");
            L.e("end  ---  " + abRepeatEnd + "    ");
            extendedPlayer.seekTo(abRepeatStart);
            abRepeatSub = Observable.interval(abRepeatEnd - abRepeatStart, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            extendedPlayer.seekTo(abRepeatStart);
                        }
                    });

        } else if (abRepeatStatus == 2) {
            abRepeatStatus = 0;
            tvAB.setText("AB");
            stopABRepeat();
            showShort("已取消播放A-B");
        }
    }

    private void resetABRepeat() {
        tvAB.setText("AB");
        abRepeatStatus = 0;
        stopABRepeat();
    }

    private void stopABRepeat() {
        if (abRepeatSub != null && !abRepeatSub.isDisposed()) {
            abRepeatSub.dispose();
            abRepeatSub = null;
            L.e("--- ab --- repeat --- unsubscribe");
        }
    }

    private void initPopupWindow(View v) {
        View view = getLayoutInflater().inflate(R.layout.popup_window, null);

        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));

        popupWindow.showAsDropDown(v, 0, -20);

        View choose = view.findViewById(R.id.lv_vip_testlib);
        View share = view.findViewById(R.id.lv_server);
        View sheet = view.findViewById(R.id.lv_sheet);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                if (AccountManager.Instace(mContext).checkUserLogin()) {
                    createDialog();
                } else {

                    EventBus.getDefault().post(new LoginEvent());
                }
            }
        });

        share.setOnClickListener(view1 -> {
            String imagePath = Constant.iconAddr;
            popupWindow.dismiss();
//            Share share1 = new Share(getApplicationContext());
//            share1.setListener(getApplicationContext(), mExamTime);
//            share1.shareMessage(imagePath, Constant.APPName,
//                    "http://m.iyuba.cn/ncet/t.jsp?l=" + Constant.APP_CONSTANT.TYPE()
//                            + "&i=" + ListenDataManager.Instance().year + "&s=" + section,title.getText().toString()+"听力真题");
            String url = "http://m.iyuba.cn/ncet/t.jsp?l=" + Constant.APP_CONSTANT.TYPE() + "&i=" + ListenDataManager.Instance().year + "&s=" + section;
            String imgUrl = "http://app.iyuba.cn/android/images/cet6/cet6.png";
            showShare(Constant.APPName, Constant.APPName, url, imgUrl);
        });


        sheet.setOnClickListener(view12 -> {
            popupWindow.dismiss();
            Intent intent = new Intent(mContext, ListenSubmit.class);
            intent.putExtra("finish", false);
            startActivityForResult(intent, 0);
        });
        view.findViewById(R.id.tv_pdf).setOnClickListener(v1 -> {
            if (!AccountManager.Instace(this).checkUserLogin()) {
                ToastUtils.showShort("请登录后下载");
                return;
            }
            if (!AccountManager.isVip()) {
                showPdfConfirmDialog();
            } else {
                showPdfDialog();
            }
        });
        view.findViewById(R.id.tv_feedback).setOnClickListener(v12 -> startActivity(new Intent(mContext, ErrorFeedbackActivity.class)));
    }


    public void showShare(String title, String text, String url, String imgUrl) {
        oks = new cn.sharesdk.onekeyshare.OnekeyShare();
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setText(text);
        oks.setImageUrl(imgUrl);
        oks.setUrl(url);
        oks.setSilent(true);
        oks.show(this);
    }

    /**
     * 测试分享
     */
    public void testShare() {

    }

    private void showPDFDialog(final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.setTitle("PDF已生成 请妥善保存。")
                .setMessage("下载链接：" + url + "\n[已复制到剪贴板]\n")
                .setNegativeButton("下载", null)
                .setPositiveButton("关闭", null)
                .setNeutralButton("发送", null)
                .create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        try {
            View v = dialog.getWindow().getDecorView().findViewById(android.R.id.message);
            if (v != null) {
                v.setOnClickListener(v1 -> {
                    Util.copy2ClipBoard(mContext, url);
                    showLong("PDF下载链接已复制到剪贴板");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button negative = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        negative.setOnClickListener(v -> dialog.dismiss());

        Button positive = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showLong("文件将会下载到" + "iyuba/" + Constant.AppName + "/ 目录下");
                    pdfUtil.download(mExamTime, url, mContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        neutral.setOnClickListener(v -> {
            String title = mExamTime + " PDF";
            new Share(mContext).shareMessage(Constant.iconAddr, "", url, title);
        });
        Util.copy2ClipBoard(mContext, url);
        showLong("PDF下载链接已复制到剪贴板");
    }

    private void showPdfConfirmDialog() {
        new AlertDialog.Builder(mContext).setTitle("提示")
                .setMessage("试题生成PDF需消耗20积分，请确认是否生成")
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("生成", (dialog, which) -> showPdfDialog()).show();
    }

    private void showPdfDialog() {
        pdfUtil.getPDF(mContext, pdfUtil.getPDFId(mExamTime), true, new PDFUtil.Callback() {
            @Override
            public void result(boolean result, String message) {
                if (result) {
                    showPDFDialog(message);
                } else {
                    showLong(message);
                }
            }
        });
    }


    private void createDialog() {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.choose_player);
        int item;
        if (ConfigManager.Instance().loadBoolean("isChangePlayer")) {
            item = 1;
        } else {
            item = 0;
        }
        builder.setSingleChoiceItems(R.array.choose_player, item,
                (dialog1, which) -> {
//                        initSoundUrl(answerABs);
                    if (which == 1) {   //调速播放器
                        if (AccountManager.Instace(mContext).userInfo.vipStatus.equals("0")) {
                            showVipDialog();
                        } else {
                            ConfigManager.Instance().putBoolean("isChangePlayer", true);
                            tv_speed.setVisibility(View.VISIBLE);
                            speed = 1.0f;
                            tv_speed.setText("x" + String.valueOf(speed).substring(0, 3));
                            extendedPlayer.setPlaySpeed(speed);
                        }
                    } else if (which == 0) {
                        ConfigManager.Instance().putBoolean("isChangePlayer", false);
                        tv_speed.setVisibility(View.GONE);
                        speed = 1.0f;
                        extendedPlayer.setPlaySpeed(speed);
                    }
                    dialog1.dismiss();
                });
        builder.setNegativeButton(com.iyuba.biblelib.R.string.alert_btn_cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    private void showVipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //设置对话框标题
        builder.setTitle("提示");
        //设置对话框内的文本
        builder.setMessage("VIP用户才可以享受调节语速功能,是否要成为VIP用户?");
        //设置确定按钮，并给按钮设置一个点击侦听，注意这个OnClickListener使用的是DialogInterface类里的一个内部接口
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mContext, VipCenterActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 执行点击确定按钮的业务逻辑
                dialog.dismiss();
            }
        });
        //使用builder创建出对话框对象
        AlertDialog dialog = builder.create();
        //显示对话框
        dialog.show();
    }

    private void initView() {
        curPos = -1;
        tv_speed = findViewById(R.id.tv_speed);
        curTime = findViewById(R.id.cur_time);
        allTime = findViewById(R.id.all_time);
        previous = findViewById(R.id.preview);
        next = findViewById(R.id.next);
        pause = findViewById(R.id.pause);
        submit = findViewById(R.id.submit);
        previous.setOnClickListener(ocl);
        next.setOnClickListener(ocl);
        pause.setOnClickListener(ocl);
        submit.setOnClickListener(ocl);

        seekbar = findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    if (extendedPlayer.isPlaying()) {
                        extendedPlayer.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        controlVideo();
        setContent(1, false, true);  // 在这里开启播放

        if (ConfigManager.Instance().loadBoolean("isChangePlayer")) {
            tv_speed.setVisibility(View.VISIBLE);
        } else {
            tv_speed.setVisibility(View.GONE);
        }

        tv_speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccountManager.Instace(mContext).checkUserLogin()) {
                    if (AccountManager.Instace(mContext).userInfo.vipStatus.equals("0")) {
                        showVipDialog();
                    } else {  //调速功能使用
                        switch (count) {
                            case 0:
                                speed = 1.0f;
                                count = 1;
                                break;
                            case 1:
                                speed = 1.2f;
                                count = 2;
                                break;
                            case 2:
                                speed = 1.3f;
                                count = 3;
                                break;
                            case 3:
                                speed = 1.5f;
                                count = 4;
                                break;
                            case 4:
                                speed = 0.8f;
                                count = 5;
                                break;
                            case 5:
                                speed = 0.9f;
                                count = 0;
                                break;
                            default:
                                break;
                        }
                        extendedPlayer.setPlaySpeed(speed);
                        tv_speed.setText(String.format("x%s", String.valueOf(speed).substring(0, 3)));
                        Toast.makeText(mContext, String.valueOf(speed).substring(0, 3) + "倍速播放", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    EventBus.getDefault().post(new LoginEvent());
                }
            }
        });
    }

    private void setPauseBackground() {
        if (extendedPlayer.isPlaying()) {
            pause.setBackgroundResource(com.iyuba.biblelib.R.drawable.pause);
        } else {
            pause.setBackgroundResource(com.iyuba.biblelib.R.drawable.play);
        }
    }

    private void controlVideo() {

        extendedPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                try {
                    waitingDialog.dismiss();
                    setPauseBackground();
                    int i = extendedPlayer.getDuration();
                    seekbar.setMax(i);
                    allTime.setText(formatTime(i));
                    videoHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        extendedPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {

                Message msg = Message.obtain();
                msg.arg1 = percent;
                msg.what = 1;
                videoHandler.sendMessage(msg);
            }
        });
        extendedPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                videoHandler.sendEmptyMessageDelayed(2, 1000);
            }
        });
        extendedPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                if (waitingDialog.isShowing()) {
                    waitingDialog.dismiss();
                }
                return false;
            }
        });
        extendedPlayer.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(int status) {
                switch (status) {
                    case 4: //播放
                        if (waitingDialog.isShowing()) {
                            waitingDialog.dismiss();
                        }

                        break;
                    case 7: //完成
                        studyRecordInfo.EndFlg = "1";
                        uploadStudyRecord(studyRecordInfo);
                        break;

                    case 5: //暂停
                        studyRecordInfo.EndFlg = "0";
                        uploadStudyRecord(studyRecordInfo);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void uploadStudyRecord(StudyRecordInfo studyRecordInfo) {
        if (System.currentTimeMillis() - startTime < 1000 * 15) {
            e("--- 时间不够15秒 ---");
            return;
        }
        studyRecordInfo.EndTime = deviceInfo.getCurrentTime();
        if (TextUtils.isEmpty(studyRecordInfo.uid) || studyRecordInfo.uid.equals("0")) {
            return;
        }
        Http.get(UpdateStudyRecordRequestNew.getUrl(studyRecordInfo, "0", "0"), new HttpCallback() {
            @Override
            public void onSucceed(Call call, String response) {
            }

            @Override
            public void onError(Call call, Exception e) {
            }
        });
    }

    private String formatTime(int time) {
        int i = time / 1000;
        int minute = i / 60;
        int second = i % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }

    /**
     * @param to           问题位置的增加或者减少   1:增加1   其他：减少1
     * @param nextQuestion 是否获取下一组开始位置
     * @param change
     */
    private void setContent(int to, boolean nextQuestion, boolean change) {
        if (nextQuestion) {
            curPos = getNextQuestion(curPos);
        }
        if (to == 1) {
            curPos++;
        } else {
            curPos--;
        }
        if (answerList == null || answerList.size() == 0) {
            showLong("题库加载失败...");
            finish();
            return;
        }

        if (curPos >= answerList.size()) {
            return;
        }

        String questionId = null;
        try {
            questionId = answerList.get(curPos).id;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // 切换上一题和下一题的按钮图片
        if (curPos == 0) {
            handler.sendEmptyMessage(1);
        } else if (curPos == answerList.size() - 1) {
            handler.sendEmptyMessage(3);
        } else {
            handler.sendEmptyMessage(2);
        }

        //处理问题标题 例如：1-4
        CetAnswer cetAnswer = answerList.get(curPos);
        ListenDataManager.Instance().rowString = "第 " + cetAnswer.sound.replace(".mp3", "") + " 题";
        //上一个数据
        CetAnswer preCetAnswer = answerList.get(ListenDataManager.curPos);


        if (change || !preCetAnswer.sound.equals(cetAnswer.sound)) {//音频不一样，播放音频
            ListenDataManager.Instance().para = questionId;
            try {
                resetABRepeat();
                startToPlay();
            } catch (Exception e) {
                e.printStackTrace();
                T.showLong(mContext, "听力播放失败，可尝试在设置中清除音频缓存，重新下载");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 500);
                return;
            }

            studyRecordInfo.TestNumber = answerList.get(curPos).id;
            studyRecordInfo.BeginTime = deviceInfo.getCurrentTime();

            if (section.equals("A") && !isNewType) {

                int number = newTypeTextAOp.getNumber(ListenDataManager.Instance().year, cetAnswer.sound);
                if (number != -1) {

                    ArrayList<CetText> list = newTypeTextAOp.selectTextData(ListenDataManager.Instance().year, String.valueOf(number));
                    if (list != null && list.size() != 0) {

                        ListenDataManager.Instance().textList = list;
                        listenFragmentAdapter.getReadEvaluateFragment().updateDataList();
                    }
                }
            } else if (section.equals("B") && !isNewType) {

                int number = newTypeTextBOp.getNumber(ListenDataManager.Instance().year, cetAnswer.sound);
                if (number != -1) {

                    ArrayList<CetText> list = newTypeTextBOp.selectTextData(ListenDataManager.Instance().year, String.valueOf(number));
                    if (list != null && list.size() != 0) {

                        ListenDataManager.Instance().textList = list;
                        listenFragmentAdapter.getReadEvaluateFragment().updateDataList();
                    }
                }
            } else if (section.equals("A") && isNewType) {

                ArrayList<CetText> list = null;
                //根据音频文件的名称及TestTime来确定句子的number
                int number = newTypeTextAOp.getNumber(ListenDataManager.Instance().year, cetAnswer.sound);
                if (number != -1) {

                    list = newTypeTextAOp.selectTextData(ListenDataManager.Instance().year, String.valueOf(number));
                    if (list != null && list.size() != 0) {

                        ListenDataManager.Instance().textList = list;

                        ReadEvaluateFragment readEvaluateFragment = listenFragmentAdapter.getReadEvaluateFragment();
                        if (readEvaluateFragment != null) {

                            readEvaluateFragment.updateDataList();
                        }
                    }
                }
            } else if (section.equals("B") && isNewType) {

                int number = newTypeTextBOp.getNumber(ListenDataManager.Instance().year, cetAnswer.sound);
                if (number != -1) {

                    ArrayList<CetText> list = newTypeTextBOp.selectTextData(ListenDataManager.Instance().year, String.valueOf(number));
                    if (list != null && list.size() != 0) {

                        ListenDataManager.Instance().textList = list;
                        ReadEvaluateFragment readEvaluateFragment = listenFragmentAdapter.getReadEvaluateFragment();
                        if (readEvaluateFragment != null) {

                            readEvaluateFragment.updateDataList();
                        }
                    }
                }
            } else if (section.equals("C") && isNewType) {

                int number = newTypeTextCOp.getNumber(ListenDataManager.Instance().year, cetAnswer.sound);
                if (number != -1) {

                    ArrayList<CetText> list = newTypeTextCOp.selectTextData(ListenDataManager.Instance().year, String.valueOf(number));
                    if (list != null && list.size() != 0) {

                        ListenDataManager.Instance().textList = list;
                        ReadEvaluateFragment readEvaluateFragment = listenFragmentAdapter.getReadEvaluateFragment();
                        if (readEvaluateFragment != null) {

                            readEvaluateFragment.updateDataList();
                        }
                    }
                }
            }
        }

        ListenDataManager.curPos = curPos;
        listenFragmentAdapter.notifyDataSetChanged();
    }

    private void startToPlay() {
        try {
            final String path = getSoundPath();
            Log.e("mp3 path: ", path);
            if (path.startsWith("http")) {
                waitingDialog.show();
            }

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        extendedPlayer.startToPlay(path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        } catch (Exception e) {
            e.printStackTrace();
            T.showLong(mContext, "播放失败，请尝试在设置中清除音频缓存，重新下载");
            return;
        }

    }

    @NonNull
    public String getSoundPath() {
       /* if (!PermissionUtil.checkStoragePermission(mContext)) {
            PermissionUtil.requestPermission(mContext);
            return "";
        }*/
        if (checkLocalFiles()) {
            return Constant.videoAddr
                    + mExamTime + File.separator
                    + answerList.get(curPos).sound;
        } else {
            String type = Constant.APP_CONSTANT.TYPE();
            String path = "http://cetsounds.iyuba.cn/" + type
                    + "/" + mExamTime + "/" + answerList.get(curPos).sound;
            return path;
        }
    }

    private boolean checkLocalFiles() {
        String year = mExamTime;
        String fileNoAppend = Constant.videoAddr + year + ".cet4";
        String folder = Constant.videoAddr + year;
        File file1 = new File(folder);
        File file2 = new File(fileNoAppend);
        if (file1.exists()) {
            if (file1.list() == null || file1.list().length <= 0) {
                file1.delete();
                return false;
            }
            // complete
            return true;
        } else if (file2.exists()) {
            // downloading
            return false;
        } else {
            // no down
            return false;
        }
    }

    private int getNextQuestion(int curPos) {
        String sound = answerList.get(curPos).sound;
        for (int i = curPos; i < answerList.size(); i++) {

            if (!sound.equals(answerList.get(i).sound)) {
                return i - 1;
            }
        }
        return curPos;
    }

    @Override
    public void onBackPressed() {
        AlertDialog alert = new AlertDialog.Builder(mContext).create();
        alert.setTitle(com.iyuba.biblelib.R.string.alert_title);
        alert.setMessage(mContext.getString(R.string.exit_test));
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(com.iyuba.biblelib.R.string.alert_btn_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        SectionAActivity.this.finish();
                    }
                });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE,
                getResources().getString(com.iyuba.biblelib.R.string.alert_btn_cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alert.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoHandler.removeCallbacksAndMessages(null);
        if (extendedPlayer != null) {
            extendedPlayer.stopAndRelease();
        }
        adBannerUtil.destroy();
        stopABRepeat();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 0:
                if (resultCode == 100) {

                } else {
                    if (intent != null) {
                        curPos = intent.getExtras().getInt("curPos", 0);
                        curPos--;
                    }
                    if (resultCode == 0) {
                        viewPager.setCurrentItem(0, true);
                    } else if (resultCode == 1) {
                        isExplain = true;
                        viewPager.setCurrentItem(0, true);
                    }
                    setContent(1, false, true);
                }
                break;
            default:
                break;
        }
    }

    public Player getPlayer() {
        if (extendedPlayer == null) {

        }
        return extendedPlayer;
    }

    public void showShare(String title, String text, String url) {
        String imagePath = Constant.iconAddr;
        Share share = new Share(getApplicationContext());
        share.setListener(getApplicationContext(), mExamTime);
        share.shareMessage(imagePath, text, url + section, title);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                ToastUtils.showShort("权限被拒绝");
            }
        }
    }


}
