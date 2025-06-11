package com.iyuba.CET4bible.viewpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.activity.MainActivity;
import com.iyuba.CET4bible.adapter.FavoriteTranslateAdapter;
import com.iyuba.CET4bible.sqlite.mode.Write;
import com.iyuba.CET4bible.sqlite.op.TranslateOp;
import com.iyuba.CET4bible.sqlite.op.WriteOp;
import com.iyuba.CET4bible.strategy.ContentStrategy;
import com.iyuba.CET4bible.util.AdInfoFlowUtil;
import com.iyuba.base.BaseFragment;
import com.iyuba.base.util.SimpleLineDividerDecoration;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.manager.DataManager;
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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 真题  - 题型页面 - 翻译
 */
public class WriteFragment extends BaseFragment {
    RecyclerView recyclerView;
    FavoriteTranslateAdapter favoriteTranslateAdapter;
    ArrayList mList;

    AdInfoFlowUtil adInfoFlowUtil;
    public static final int TRANSLATE = 1;
    public static final int WRITE = 2;

    int type;

    private Disposable mTypeDisposable;

    ContentStrategy mContentStrategy;
    private RecyclerView.Adapter mWorkAdapter;
    int[] mStreamTypes;
    private int mStrategyCode;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fillinblank, container, false);
        if (containerVp != null) {
            containerVp.setObjectForPosition(view, 1);
        }
        getStreamType();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new SimpleLineDividerDecoration(mContext).setColor(R.color.darkgray));

        type = getArguments().getInt("type", 1);

        mList = new ArrayList<>();
        //  翻译
        if (type == TRANSLATE) {
            mList.addAll(new TranslateOp(mContext).selectData());
        } else {
            // 写作
            mList.addAll(new WriteOp(mContext).selectData());
        }


        List<Write> writes = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            Write w = (Write) mList.get(i);
            int year = Integer.parseInt(w.num);

            if (type == TRANSLATE) {
                if (year > 20150000) {
                    writes.add(w);
                }
            } else {
                if (year > 20160000) {
                    writes.add(w);
                }
            }
        }
        mList.removeAll(writes);
        mList.addAll(0, sort(writes));

        favoriteTranslateAdapter = new FavoriteTranslateAdapter(mContext, mList, getActivity() instanceof MainActivity);
        favoriteTranslateAdapter.setWrite(type != 1);

        recyclerView.setAdapter(favoriteTranslateAdapter);

//        adInfoFlowUtil = new AdInfoFlowUtil(mContext, AccountManager.isVip(), new AdInfoFlowUtil.Callback() {
//            @Override
//            public void onADLoad(List ads) {
//                AdInfoFlowUtil.insertAD(mList, ads, adInfoFlowUtil);
//                adapter.notifyDataSetChanged();
//            }
//        });
//        if (type == WRITE) {
//            adInfoFlowUtil.setSupportVideo(true);
//            adInfoFlowUtil.setAdRequestSize(14).setVideoAdRequestSize(1).refreshAd();
//        } else {
//            adInfoFlowUtil.setAdRequestSize(15).refreshAd();
//        }
    }

    private List sort(List<Write> writes) {
        if (writes.size() % 3 == 0) {
            List list = new ArrayList();

            for (int i = 0; i < writes.size() / 3; i++) {
                Write w = writes.get(i * 3);
                Write w2 = writes.get(i * 3 + 1);
                Write w3 = writes.get(i * 3 + 2);

                list.add(w3);
                list.add(w2);
                list.add(w);
            }
            writes.clear();
            writes.addAll(list);
        }
        return writes;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            DataManager.Instance().currentType = type;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        adInfoFlowUtil.destroy();
    }


    private void getStreamType() {

        String userid = ConfigManager.Instance().loadString("userId", "0");
        RxUtil.dispose(mTypeDisposable);
//        mTypeDisposable = com.iyuba.headlinelibrary.data.DataManager.getInstance().getStreamType(Constant.APPID)
        mTypeDisposable = com.iyuba.headlinelibrary.data.DataManager.getInstance().getStreamType(Integer.parseInt(userid))
                .compose(RxUtil.<StreamTypeInfo>applySingleIoScheduler())
                .subscribe(new Consumer<StreamTypeInfo>() {
                    @Override
                    public void accept(StreamTypeInfo streamTypeInfo) throws Exception {

                        setAdAdapter(streamTypeInfo);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        StreamTypeInfo streamTypeInfo = new StreamTypeInfo();
                        streamTypeInfo.first = 2;
                        streamTypeInfo.second = 2;
                        streamTypeInfo.third = 2;
                        setAdAdapter(streamTypeInfo);
                    }
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
                .keywords("日语")
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
        NativeRecyclerAdapter mAdAdapter = new NativeRecyclerAdapter(requireActivity(), favoriteTranslateAdapter, positioning);
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
