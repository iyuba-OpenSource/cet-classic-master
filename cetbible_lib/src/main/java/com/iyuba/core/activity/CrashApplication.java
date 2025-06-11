package com.iyuba.core.activity;

/**
 * 程序崩溃后操作
 *
 * @version 1.0
 * @author 陈彤
 * 修改日期    2014.3.29
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.multidex.MultiDex;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.dlex.bizs.DLManager;
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
import com.iyuba.share.ShareExecutor;
import com.iyuba.share.mob.MobShareExecutor;
import com.iyuba.trainingcamp.ITraining;
import com.iyuba.trainingcamp.data.local.db.TCDBManager;
import com.iyuba.trainingcamp.data.sys.TypefaceProvider;
import com.iyuba.widget.unipicker.IUniversityPicker;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.youdao.sdk.common.YoudaoSDK;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;
import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.data.local.HLDBManager;
import personal.iyuba.personalhomelibrary.ui.widget.dialog.ShareBottomDialog;

public class CrashApplication extends LitePalApplication {
    private static CrashApplication mInstance = null;
    private List<Activity> activityList = new LinkedList<>();

    private int mCount ;
    private long quitTime ,backTime;
    // 全局volley请求队列队列
    private com.android.volley.RequestQueue queue;


    @Override
    public void onCreate() {
        super.onCreate();

        RuntimeManager.setApplicationContext(getApplicationContext());
        RuntimeManager.setApplication(this);
        mInstance = this;

        TCDBManager.init(CrashApplication.this);
        com.iyuba.trainingcamp.data.local.InfoHelper.init(CrashApplication.this);
        TypefaceProvider.init(CrashApplication.this);
//        ConstantManager.init();
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
        BasicFavor.init(this,Constant.APPID);
        BasicFavorDBManager.init(this);
        InfoHelper.init(this);
        DLManager.init(this,5);
        DBManager.init(this);
        HLDBManager.init(this);
        HeadlineInfoHelper.init(this);
        IMoocDBManager.init(this);
        BasicFavorInfoHelper.init(this);
        BasicDLDBManager.init(this);
        PersonalHome.init(getApplicationContext());
        queue = Volley.newRequestQueue(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        initImageLoader();

        String extraUrl = "http://iuserspeech.iyuba.cn:9001/test/ai/";
        String extraMergeUrl = "http://iuserspeech.iyuba.cn:9001/test/merge/";
        IHeadline.init(getApplicationContext(), Constant.APPID, Constant.APPName);
        IHeadline.setEnableShare(true);
        IHeadline.setEnableGoStore(false);
        IHeadline.setExtraMseUrl(extraUrl);
        IHeadline.setExtraMergeAudioUrl(extraMergeUrl);
        IHeadline.setTitleHolderType(HolderType.SMALL);

        //个人中心mob配置
        List<String> list=new ArrayList<>();
        list.add("QQ");
        list.add("QQ空间");
        list.add("新浪微博");
        list.add("微信好友");
        list.add("微信收藏");
        list.add("微信朋友圈");
        ShareBottomDialog.setSharedPlatform(list);
        MobShareExecutor executor = new MobShareExecutor();
        ShareExecutor.getInstance().setRealExecutor(executor);

        ITraining.init(CrashApplication.this, Constant.APPID, Constant.APPName);
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
        boolean isPrivacyAgree = ConfigManager.Instance().loadBoolean("isPrivacyAgree");
        if(isPrivacyAgree){
            initSdk();
        }
    }

    private void initSdk(){
        YoudaoSDK.init(this);
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    for (Activity activity : activityList) {
                        activity.finish();
                    }
                    break;

                default:
                    break;
            }
        }
    };


    public static CrashApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
//        IMoviesApp.stopService();
        super.onTerminate();
    }


    private void initImageLoader() {
        // 初始化ImageLoader
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(com.iyuba.configation.R.drawable.nearby_no_icon) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(com.iyuba.configation.R.drawable.nearby_no_icon) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(com.iyuba.configation.R.drawable.nearby_no_icon) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(options)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(3)//线程池内加载的数量
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                .memoryCacheSize(5 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(config);
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
        MultiDex.install(this);
    }
}