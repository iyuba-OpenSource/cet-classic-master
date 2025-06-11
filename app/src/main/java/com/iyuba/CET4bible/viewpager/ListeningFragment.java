package com.iyuba.CET4bible.viewpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.activity.MainActivity;
import com.iyuba.CET4bible.adapter.ListeningTestListAdapter;
import com.iyuba.CET4bible.manager.ListenDataManager;
import com.iyuba.CET4bible.util.exam.DbExamListBean;
import com.iyuba.CET4bible.util.exam.ExamDataUtil;
import com.iyuba.CET4bible.util.exam.ExamListOp;
import com.iyuba.base.BaseFragment;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.headlinelibrary.data.model.StreamTypeInfo;
import com.iyuba.module.toolbox.RxUtil;
import com.iyuba.sdk.data.iyu.IyuNative;
import com.iyuba.sdk.data.ydsdk.YDSDKTemplateNative;
import com.iyuba.sdk.data.youdao.YDNative;
import com.iyuba.sdk.mixnative.MixAdRenderer;
import com.iyuba.sdk.mixnative.MixNative;
import com.iyuba.sdk.mixnative.MixViewBinder;
import com.iyuba.sdk.mixnative.PositionLoadWay;
import com.iyuba.sdk.nativeads.NativeAdPositioning;
import com.iyuba.sdk.nativeads.NativeEventListener;
import com.iyuba.sdk.nativeads.NativeRecyclerAdapter;
import com.iyuba.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 真题 - 题型 - 听力
 * 真题 - 题目列表
 */
public class ListeningFragment extends BaseFragment {

    RecyclerView recyclerView;
    ListeningTestListAdapter listeningTestListAdapter;
    ArrayList mList;
    SwipeRefreshLayout swipeRefreshLayout;
    private Disposable mTypeDisposable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listening_cet, container, false);
        if (containerVp != null) {
            containerVp.setObjectForPosition(view, 0);
        }
        getStreamType();

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> refreshExamList());

        mList = new ArrayList<>();
        listeningTestListAdapter = new ListeningTestListAdapter(mContext, getActivity() instanceof MainActivity);
        listeningTestListAdapter.setList(mList);
        recyclerView.setAdapter(listeningTestListAdapter);


        refreshListAdapter();

        if (ExamDataUtil.isFirstRequestData(mContext)) {
            swipeRefreshLayout.setRefreshing(true);
            refreshExamList();
        }
    }

    private void refreshExamList() {
        ExamDataUtil.requestList(Constant.APP_CONSTANT.TYPE(), list -> {
            swipeRefreshLayout.setRefreshing(false);
            if (list != null && list.size() > 0) {
                try {
                    ExamDataUtil.writeListData2DB(mContext, list);
                    ExamDataUtil.setFirstRequestData(mContext, false);
                    refreshListAdapter();
                } catch (Exception e) {
                    e.printStackTrace();
                    showLong("题库加载失败...");
                }
            } else {
                showLong("题目加载失败");
            }
        });
    }

    private void refreshListAdapter() {
        ExamListOp listOp = new ExamListOp(mContext);
        List<DbExamListBean> examListBeans = listOp.findAll();
        mList.clear();
        Map<String, String> images = new HashMap<>();
        for (DbExamListBean examListBean : examListBeans) {
            mList.add(examListBean.year);
            images.put(examListBean.year, examListBean.image);
        }
        if (mList.size() == 0) {
            mList.addAll(Arrays.asList(ListenDataManager.getTestNewType()));
            listeningTestListAdapter.setImageMap();
        } else {
            listeningTestListAdapter.setImageUrls(images);
        }

        listeningTestListAdapter.notifyDataSetChanged();

//        adInfoFlowUtil.setAdRequestSize(5).resetLastPosition().refreshAd();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        adInfoFlowUtil.destroy();
    }


    private void getStreamType() {


        String userid = ConfigManager.Instance().loadString("userId", "0");
        RxUtil.dispose(mTypeDisposable);
        mTypeDisposable = com.iyuba.headlinelibrary.data.DataManager
                .getInstance()
                .getStreamType(Integer.parseInt(userid))
                .compose(RxUtil.applySingleIoScheduler())
                .subscribe(streamTypeInfo -> {

                    setAdAdapter(streamTypeInfo);
                }, throwable -> {

                    StreamTypeInfo streamTypeInfo = new StreamTypeInfo();
                    streamTypeInfo.first = 2;
                    streamTypeInfo.second = 2;
                    streamTypeInfo.third = 2;
                    setAdAdapter(streamTypeInfo);
                });
    }


    private void setAdAdapter(StreamTypeInfo dataBean) {

        OkHttpClient mClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                RequestParameters.NativeAdAsset.TITLE,
                RequestParameters.NativeAdAsset.TEXT,
                RequestParameters.NativeAdAsset.ICON_IMAGE,
                RequestParameters.NativeAdAsset.MAIN_IMAGE,
                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);
        RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder()
                .location(null)
                .keywords("英语四级")
                .keywords("英语六级")
                .keywords("英语")
                .desiredAssets(desiredAssets)
                .build();
        YDNative ydNative = new YDNative(requireActivity(), "edbd2c39ce470cd72472c402cccfb586", requestParameters);

        IyuNative iyuNative = new IyuNative(requireActivity(), Constant.APPID, mClient);

//        YDSDKTemplateNative csjTemplateNative = new YDSDKTemplateNative(requireActivity(), BuildConfig.TEMPLATE_SCREEN_AD_KEY_CSJ);
//        YDSDKTemplateNative ylhTemplateNative = new YDSDKTemplateNative(requireActivity(), BuildConfig.TEMPLATE_SCREEN_AD_KEY_YLH);
//        YDSDKTemplateNative ksTemplateNative = new YDSDKTemplateNative(requireActivity(), BuildConfig.TEMPLATE_SCREEN_AD_KEY_KS);
//        YDSDKTemplateNative bdTemplateNative = new YDSDKTemplateNative(requireActivity(), BuildConfig.TEMPLATE_SCREEN_AD_KEY_BD);

        //添加key
        HashMap<Integer, YDSDKTemplateNative> ydsdkMap = new HashMap<>();
//        ydsdkMap.put(StreamType.TT, csjTemplateNative);
//        ydsdkMap.put(StreamType.GDT, ylhTemplateNative);
//        ydsdkMap.put(StreamType.KS, ksTemplateNative);
//        ydsdkMap.put(StreamType.BAIDU, bdTemplateNative);


        MixNative mixNative = new MixNative(ydNative, iyuNative, ydsdkMap);
        PositionLoadWay loadWay = new PositionLoadWay();
        loadWay.setStreamSource(new int[]{
                2,
                2,
                2});
        mixNative.setLoadWay(loadWay);

        int startPosition = 3;
        int positionInterval = 5;
        NativeAdPositioning.ClientPositioning positioning = new NativeAdPositioning.ClientPositioning();
        positioning.addFixedPosition(startPosition);
        positioning.enableRepeatingPositions(positionInterval);
        NativeRecyclerAdapter mAdAdapter = new NativeRecyclerAdapter(requireActivity(), listeningTestListAdapter, positioning);
        mAdAdapter.setNativeEventListener(new NativeEventListener() {
            @Override
            public void onNativeImpression(View view, NativeResponse nativeResponse) {

            }

            @Override
            public void onNativeClick(View view, NativeResponse nativeResponse) {

            }
        });
        mAdAdapter.setAdSource(mixNative);
        mixNative.setYDSDKTemplateNativeClosedListener(new MixNative.YDSDKTemplateNativeClosedListener() {
            @Override
            public void onClosed(View view) {
                View itemView = (View) ((View) view.getParent()).getParent();
                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(itemView);
                int position = viewHolder.getBindingAdapterPosition();
                mAdAdapter.removeAdsWithAdjustedPosition(position);
            }
        });

        MixViewBinder mixViewBinder = new MixViewBinder.Builder(R.layout.item_ad_mix)
                .templateContainerId(R.id.mix_fl_ad)
                .nativeContainerId(R.id.headline_ll_item)
                .nativeImageId(R.id.native_main_image)
                .nativeTitleId(R.id.native_title)
                .build();
        MixAdRenderer mixAdRenderer = new MixAdRenderer(mixViewBinder);
        mAdAdapter.registerAdRenderer(mixAdRenderer);
        recyclerView.setAdapter(mAdAdapter);
        mAdAdapter.loadAds();
    }
}
