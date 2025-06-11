package com.iyuba.CET4bible;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.iyuba.CET4bible.mvp.model.NetWorkManager;
import com.iyuba.CET4bible.util.MyOaidHelper;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.configation.ConstantManager;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.activity.CrashHandler;
import com.iyuba.core.manager.AccountManager;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.data.local.HeadlineInfoHelper;
import com.iyuba.headlinelibrary.ui.title.HolderType;
import com.iyuba.imooclib.data.local.IMoocDBManager;
import com.iyuba.module.dl.BasicDLDBManager;
import com.iyuba.module.favor.BasicFavor;
import com.iyuba.module.favor.data.local.BasicFavorDBManager;
import com.iyuba.module.favor.data.local.BasicFavorInfoHelper;
import com.iyuba.module.movies.data.local.InfoHelper;
import com.iyuba.module.movies.data.local.db.DBManager;
import com.iyuba.module.privacy.IPrivacy;
import com.iyuba.module.privacy.PrivacyInfoHelper;
import com.iyuba.share.ShareExecutor;
import com.iyuba.share.mob.MobShareExecutor;
import com.iyuba.trainingcamp.ITraining;
import com.iyuba.trainingcamp.data.local.db.TCDBManager;
import com.iyuba.trainingcamp.data.sys.TypefaceProvider;
import com.iyuba.widget.unipicker.IUniversityPicker;
import com.jn.ad_list.AdListLibInit;
import com.jn.ad_list.db.AdLog;
import com.jn.ad_list.entity.Ad;
import com.mob.MobSDK;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.commonsdk.UMConfigure;
import com.yd.saas.ydsdk.manager.YdConfig;
import com.youdao.sdk.common.OAIDHelper;
import com.youdao.sdk.common.YouDaoAd;
import com.youdao.sdk.common.YoudaoSDK;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.data.local.HLDBManager;
import personal.iyuba.personalhomelibrary.ui.widget.dialog.ShareBottomDialog;

public class MyApplication extends Application {

    private static MyApplication mInstance = null;
    private List<Activity> activityList = new LinkedList<>();

    private int mCount;
    private long quitTime, backTime;
    // 全局volley请求队列队列
    private com.android.volley.RequestQueue queue;

    public static String UM_APPKEY = "58b3d5c9aed1790133002651";

    @Override
    public void onCreate() {
        super.onCreate();


        UMConfigure.preInit(this, UM_APPKEY, null);

        RuntimeManager.setApplicationContext(getApplicationContext());
        RuntimeManager.setApplication(this);
        mInstance = this;

        NetWorkManager.getInstance().init();

        TCDBManager.init(this);
        com.iyuba.trainingcamp.data.local.InfoHelper.init(this);
        TypefaceProvider.init(this);


        ConstantManager.init(BuildConfig.FLAVOR, this);
        //设置测评的下载地址
        Constant.APP_DATA_PATH = getExternalFilesDir(null) + "/iyuba/" + Constant.APP_CONSTANT.APP_DATA_PATH() + "/";

//        PushApplication.initPush(this);//推送初始化
        LitePal.initialize(this);
        /*
         * 必须在 Application 的 onCreate 方法中执行 BGASwipeBackHelper.init 来初始化滑动返回
         * 第一个参数：应用程序上下文
         * 第二个参数：如果发现滑动返回后立即触摸界面时应用崩溃，请把该界面里比较特殊的 View 的 class 添加到该集合中，目前在库中已经添加了 WebView 和 SurfaceView
         */
        BGASwipeBackHelper.init(this, null);
//        String usageUrl = Constant.USE_URL + Constant.APPName + "&company="+Constant.COMPANY_NAME;
//        String priUrl = Constant.PRI_URL + Constant.APPName + "&company="+Constant.COMPANY_NAME;
        String usageUrl = Constant.USE_URL + Constant.APPName + "&company=1";
        String priUrl = Constant.PRI_URL + Constant.APPName + "&company=1";
        IPrivacy.init(getApplicationContext(), usageUrl, priUrl);
        BasicFavor.init(this, Constant.APPID);
        BasicFavorDBManager.init(this);
        InfoHelper.init(this);
        DLManager.init(this, 5);
        DBManager.init(this);
        HLDBManager.init(this);
        HeadlineInfoHelper.init(this);
        IMoocDBManager.init(this);
        BasicFavorInfoHelper.init(this);
        BasicDLDBManager.init(this);
        PersonalHome.init(getApplicationContext(), Constant.APPID, "cet6");
        queue = Volley.newRequestQueue(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        String extraUrl = "http://iuserspeech.iyuba.cn:9001/test/ai/";
        String extraMergeUrl = "http://iuserspeech.iyuba.cn:9001/test/merge/";
        IHeadline.init(getApplicationContext(), Constant.APPID, Constant.APPName);
        IHeadline.setEnableShare(true);
        IHeadline.setEnableGoStore(false);
        IHeadline.setExtraMseUrl(extraUrl);
        IHeadline.setExtraMergeAudioUrl(extraMergeUrl);
        IHeadline.setTitleHolderType(HolderType.SMALL);

        //个人中心mob配置
        List<String> list = new ArrayList<>();
        list.add("QQ");
        list.add("QQ空间");
        list.add("新浪微博");
        list.add("微信好友");
        list.add("微信收藏");
        list.add("微信朋友圈");
        ShareBottomDialog.setSharedPlatform(list);
        MobShareExecutor executor = new MobShareExecutor();
        ShareExecutor.getInstance().setRealExecutor(executor);

        //设置我的收藏 过滤

        List<String> typeFilter = new ArrayList<>();
        typeFilter.add(HeadlineType.MEIYU);
        typeFilter.add(HeadlineType.BBCWORDVIDEO);
        typeFilter.add(HeadlineType.SMALLVIDEO);
        typeFilter.add(HeadlineType.TED);
        typeFilter.add(HeadlineType.TOPVIDEOS);
        typeFilter.add(HeadlineType.VOAVIDEO);
        typeFilter.add(HeadlineType.VOA);
        typeFilter.add(HeadlineType.BBC);
        BasicFavor.setTypeFilter(typeFilter);

        PrivacyInfoHelper.init(this);


        ITraining.init(this, Constant.APPID, Constant.APPName);
        ITraining.setEnableShare(true);
        ITraining.setExtraEvaluateUrl("http://iuserspeech.iyuba.cn:9001/test/ai/");
        //学校选择控件初始化
        IUniversityPicker.init(this);

        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                //指定为经典Header，默认是 贝塞尔雷达Header
                return new ClassicsHeader(context);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context);
            }
        });

        YouDaoAd.getYouDaoOptions().setCanObtainAndroidId(false);
        YouDaoAd.getNativeDownloadOptions().setConfirmDialogEnabled(true);
        YouDaoAd.getYouDaoOptions().setAppListEnabled(false);
        YouDaoAd.getYouDaoOptions().setPositionEnabled(false);
        YouDaoAd.getYouDaoOptions().setSdkDownloadApkEnabled(true);
        YouDaoAd.getYouDaoOptions().setDeviceParamsEnabled(false);
        YouDaoAd.getYouDaoOptions().setWifiEnabled(false);
        YouDaoAd.getYouDaoOptions().setCanObtainAndroidId(false);


        boolean isPrivacyAgree = ConfigManager.Instance().loadBoolean("isPrivacyAgree");
        if (isPrivacyAgree) {
            initSdk();
        }
    }

    public static void initSdk() {

        MobSDK.submitPolicyGrantResult(true);

        UMConfigure.init(mInstance, UM_APPKEY, null, UMConfigure.DEVICE_TYPE_PHONE, "");

        System.loadLibrary("msaoaidsec");
        //广告
        YdConfig.getInstance().init(mInstance, Constant.APPID);

        MyOaidHelper myOaidHelper = new MyOaidHelper(new MyOaidHelper.AppIdsUpdater() {
            @Override
            public void onIdsValid(String ids) {

                OAIDHelper.getInstance().init(mInstance);
                OAIDHelper.getInstance().setOAID(ids);
                AdListLibInit.setOAID(ids);
            }
        });
        myOaidHelper.getDeviceIds(mInstance);

        YoudaoSDK.init(mInstance);
        String userid = AccountManager.Instace(mInstance).getId();
        //初始化广告hide
        AdListLibInit.init(mInstance, Integer.parseInt(Constant.APPID), null, userid, mInstance.getPackageName());
        AdListLibInit.setIsSubmitClick(false);
        AdListLibInit.setAdKeys(new Ad[]{
                        new Ad(AdLog.NAME_CSJ, "0112", 3),
                        new Ad(AdLog.NAME_YLH, "0472", 2)
                },
                new Ad[]{
                        new Ad(AdLog.NAME_CSJ, "0468", 3),
                        new Ad(AdLog.NAME_YLH, "0474", 2),
                        new Ad(AdLog.NAME_KS, "0483", 4),
                        new Ad(AdLog.NAME_BD, "0479", 1),
                        new Ad(AdLog.NAME_YOUDAO, "0", 0)
                }, null, null, null, null);
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what) {
                case 0:
                    for (Activity activity : activityList) {
                        activity.finish();
                    }
                    break;

                default:
                    break;
            }
            return true;
        }
    });


    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
//        IMoviesApp.stopService();
        super.onTerminate();
    }

    public RequestQueue getQueue() {
        return queue;
    }

    // 程序加入运行列表
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 程序退出
    public void exit() {
        handler.sendEmptyMessage(0);
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
