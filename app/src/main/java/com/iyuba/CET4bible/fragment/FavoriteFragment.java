package com.iyuba.CET4bible.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.activity.MainActivity;
import com.iyuba.CET4bible.adapter.FavoriteAdapter;
import com.iyuba.CET4bible.adapter.FavoriteFillInBlankAdapter;
import com.iyuba.CET4bible.adapter.FavoriteParagraphMatchingAdapter;
import com.iyuba.CET4bible.adapter.FavoriteReadingAdapter;
import com.iyuba.CET4bible.adapter.FavoriteTranslateAdapter;
import com.iyuba.CET4bible.sqlite.mode.FillInBlankBean;
import com.iyuba.CET4bible.sqlite.mode.PackInfo;
import com.iyuba.CET4bible.sqlite.mode.ParagraphMatchingBean;
import com.iyuba.CET4bible.sqlite.mode.Write;
import com.iyuba.CET4bible.sqlite.op.FillInBlankOp;
import com.iyuba.CET4bible.sqlite.op.ParagraphMatchingOp;
import com.iyuba.CET4bible.sqlite.op.ReadingInfoOp;
import com.iyuba.CET4bible.sqlite.op.TranslateOp;
import com.iyuba.CET4bible.sqlite.op.WriteOp;
import com.iyuba.CET4bible.strategy.ContentStrategy;
import com.iyuba.CET4bible.util.AdInfoFlowUtil;
import com.iyuba.CET4bible.util.FavoriteUtil;
import com.iyuba.base.BaseFragment;
import com.iyuba.base.BaseRecyclerViewAdapter;
import com.iyuba.base.util.SimpleLineDividerDecoration;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.manager.AccountManager;
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
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * FavoriteFragment
 *
 * @author wayne
 * @date 2017/12/12
 */
public class FavoriteFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private List dataList = new ArrayList<>();

    private BaseRecyclerViewAdapter baseRecyclerViewAdapter;

    private int type;

    ReadingInfoOp readingInfoOp;
    FavoriteUtil favoriteUtil;
    String[] prefix = {"listening", "reading", "translate", "write"};
    List<String> keyList = new ArrayList<>();

    private AdInfoFlowUtil adInfoFlowUtil;

    private List<PackInfo> packInfoList;

    private Disposable mTypeDisposable;

    ContentStrategy mContentStrategy;
    private RecyclerView.Adapter mWorkAdapter;
    int[] mStreamTypes;
    private int mStrategyCode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = getArguments().getInt("type");
        getStreamType();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new SimpleLineDividerDecoration(mContext).setColor(R.color.darkgray));

        favoriteUtil = new FavoriteUtil(type);

        if (type == FavoriteUtil.Type.reading) {
            // 阅读
            setReadingAdapter();
        } else if (type == FavoriteUtil.Type.translate) {
            // 翻译
            setTranslateAdapter();
        } else if (type == FavoriteUtil.Type.write) {
            // 写作
            setWriteAdapter();
        } else if (type == FavoriteUtil.Type.listening) {
            // 听力
            setListeningAdapter();
        } else if (type == FavoriteUtil.Type.fillInBlack) {
            // 完形填空
            setFillInBlankAdapter();
        } else if (type == FavoriteUtil.Type.paragraph) {
            // 段落匹配
            setParagraphMatchingAdapter();
        }
        if (baseRecyclerViewAdapter == null) {
            return;
        }
        recyclerView.setAdapter(baseRecyclerViewAdapter);
        baseRecyclerViewAdapter.setOnLongClickListener(new BaseRecyclerViewAdapter.OnClickListener() {
            @Override
            public void onClick(View v, final int pos) {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("确定要删除收藏吗？")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                deleteFavorite(pos);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                baseRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        })
                        .show();
            }
        });
        // 插入广告
        adInfoFlowUtil = new AdInfoFlowUtil(mContext, AccountManager.isVip(), new AdInfoFlowUtil.Callback() {
            @Override
            public void onADLoad(List ads) {
                AdInfoFlowUtil.insertAD(dataList, ads, adInfoFlowUtil);
                baseRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
        adInfoFlowUtil.setAdRequestSize(5).refreshAd();
    }

    private void setParagraphMatchingAdapter() {
        dataList = new ParagraphMatchingOp(mContext).selectData();
        List<String> favoriteList = favoriteUtil.getData();
        List tempList = new ArrayList<>();
        for (Object o : dataList) {
            ParagraphMatchingBean bean = (ParagraphMatchingBean) o;
            if (!favoriteList.contains("paragraph_" + bean.year + bean.index)) {
                tempList.add(bean);
            }
        }
        dataList.removeAll(tempList);

        baseRecyclerViewAdapter = new FavoriteParagraphMatchingAdapter(mContext, dataList, getActivity() instanceof MainActivity);
    }

    private void setFillInBlankAdapter() {
        dataList = new FillInBlankOp(mContext).selectData();
        List<String> favoriteList = favoriteUtil.getData();
        List tempList = new ArrayList<>();
        for (Object o : dataList) {
            FillInBlankBean bean = (FillInBlankBean) o;
            if (!favoriteList.contains("fillInBlank_" + bean.year + bean.index)) {
                tempList.add(bean);
            }
        }
        dataList.removeAll(tempList);

        baseRecyclerViewAdapter = new FavoriteFillInBlankAdapter(mContext, dataList, getActivity() instanceof MainActivity);
    }

    private void setListeningAdapter() {
        dataList = new ArrayList();

        List<String> favoriteList = favoriteUtil.getData();

        for (int i = 0; i < favoriteList.size(); i++) {
            // listening + "_" + year + "_" + section + "_" + sound
            String[] array = favoriteList.get(i).split("_");
            dataList.add(array);
        }

        Collections.sort(dataList, new Comparator<String[]>() {
            @Override
            public int compare(String[] s1, String[] s2) {
                // 按年份和题目顺序排序
                int result = s2[1].compareTo(s1[1]);
                if (result == 0) {
                    String[] aa = s1[3].split("-");
                    String[] bb = s2[3].split("-");
                    for (int i = 0; i < aa.length; i++) {
                        aa[0] = aa[0].length() == 1 ? "0" + aa[0] : aa[0];
                        aa[1] = aa[1].length() == 1 ? "0" + aa[1] : aa[1];
                    }
                    for (int i = 0; i < bb.length; i++) {
                        bb[0] = bb[0].length() == 1 ? "0" + bb[0] : bb[0];
                        bb[1] = bb[1].length() == 1 ? "0" + bb[1] : bb[1];
                    }
                    return (aa[0] + aa[1]).compareTo(bb[0] + bb[1]);
                } else {
                    return result;
                }
            }
        });

        for (int i = 0; i < dataList.size(); i++) {
            String[] key = (String[]) dataList.get(i);
            StringBuilder builder = new StringBuilder();
            for (int xxxx = 0; xxxx < key.length; xxxx++) {
                if (xxxx == 0) {
                    builder.append(key[xxxx]);
                } else {
                    builder.append("_").append(key[xxxx]);
                }
            }
            keyList.add(builder.toString());
        }

        baseRecyclerViewAdapter = new FavoriteAdapter(mContext, dataList);
    }

    private void setWriteAdapter() {
        dataList = new WriteOp(mContext).selectData();
        filterData(dataList, "write");

        baseRecyclerViewAdapter = new FavoriteTranslateAdapter(mContext, dataList, getActivity() instanceof MainActivity);
        ((FavoriteTranslateAdapter) baseRecyclerViewAdapter).setWrite(true);
    }

    private void setTranslateAdapter() {
        dataList = new TranslateOp(mContext).selectData();
        filterData(dataList, "translate");

        baseRecyclerViewAdapter = new FavoriteTranslateAdapter(mContext, dataList, getActivity() instanceof MainActivity);
        ((FavoriteTranslateAdapter) baseRecyclerViewAdapter).setWrite(false);
    }

    private void setReadingAdapter() {
        readingInfoOp = new ReadingInfoOp(mContext);

        List<PackInfo> readingList = readingInfoOp.findAll();
        List<PackInfo> tempList = new ArrayList<>();
        List<String> favoriteList = favoriteUtil.getData();

        for (int i = 0; i < favoriteList.size(); i++) {
            String key = favoriteList.get(i);

            for (int k = 0; k < readingList.size(); k++) {
                PackInfo packInfo = readingList.get(k);
                if (key.startsWith("reading_" + packInfo.TitleNum)) {
                    if (!tempList.contains(packInfo)) {
                        tempList.add(packInfo);
                    }
                    break;
                }
            }
        }
        packInfoList = tempList;

        HashSet<String> set = new HashSet<>();
        for (int i = 0; i < tempList.size(); i++) {
            set.add(tempList.get(i).PackName);
        }

        dataList.addAll(set);
        Collections.sort(dataList, new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return rhs.toString().compareTo(lhs.toString());
            }
        });
        baseRecyclerViewAdapter = new FavoriteReadingAdapter(mContext, (List<String>) dataList, getActivity() instanceof MainActivity);
    }

    private void deleteFavorite(int pos) {
        if (dataList.get(pos) instanceof NativeResponse) {
            return;
        }
        // 翻译和写作
        if (type == FavoriteUtil.Type.translate
                || type == FavoriteUtil.Type.write) {
            favoriteUtil.setFavorite(false, prefix[type] + ((Write) dataList.get(pos)).num + ((Write) dataList.get(pos)).index);
        } else if (type == FavoriteUtil.Type.reading) {
            for (int i = 0; i < packInfoList.size(); i++) {
                PackInfo packInfo = packInfoList.get(i);
                // 根据packName 找到题目，删除所有的收藏
                if (packInfo.PackName.equals(dataList.get(pos))) {
                    for (int k = 0; k < 5; k++) {
                        favoriteUtil.setFavorite(false, "reading_" + packInfo.TitleNum + "_" + (k + 1));
                    }
                }
            }
        } else if (type == FavoriteUtil.Type.listening) {
            favoriteUtil.setFavorite(false, keyList.get(getRealPosition(pos)));
        } else if (type == FavoriteUtil.Type.fillInBlack) {
            favoriteUtil.setFavorite(false, "fillInBlank_" +
                    ((FillInBlankBean) dataList.get(pos)).year + ((FillInBlankBean) dataList.get(pos)).index);
        } else if (type == FavoriteUtil.Type.paragraph) {
            favoriteUtil.setFavorite(false, "paragraph_" +
                    ((ParagraphMatchingBean) dataList.get(pos)).year + ((ParagraphMatchingBean) dataList.get(pos)).index);
        }

        dataList.remove(pos);
        baseRecyclerViewAdapter.notifyDataSetChanged();
    }

    private int getRealPosition(int pos) {
        int result = 0;
        for (int i = 0; i <= pos; i++) {
            if (dataList.get(pos) instanceof NativeResponse) {
                result += 1;
            }
        }
        return pos - result;
    }

    private void filterData(List dataList, String prefix) {
        List<Write> writeList = dataList;
        List tempList = new ArrayList();
        List<String> favoriteList = favoriteUtil.getData();

        for (int i = 0; i < writeList.size(); i++) {
            Write write = writeList.get(i);
            if (!favoriteList.contains(prefix + write.num + write.index)) {
                tempList.add(write);
            }
        }
        writeList.removeAll(tempList);
        tempList.clear();
    }

    public static Fragment getInstance(int position) {
        Fragment fragment = new FavoriteFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", position);
        fragment.setArguments(bundle);
        return fragment;
    }


    private void getStreamType() {

        String userId = ConfigManager.Instance().loadString("userId", "0");
        RxUtil.dispose(mTypeDisposable);
//        mTypeDisposable = com.iyuba.headlinelibrary.data.DataManager.getInstance().getStreamType(Constant.APPID)
        mTypeDisposable = com.iyuba.headlinelibrary.data.DataManager.getInstance().getStreamType(Integer.parseInt(userId))
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
        NativeRecyclerAdapter mAdAdapter = new NativeRecyclerAdapter(requireActivity(), baseRecyclerViewAdapter, positioning);
        mAdAdapter.setNativeEventListener(new NativeEventListener() {
            @Override
            public void onNativeImpression(View view, com.iyuba.sdk.nativeads.NativeResponse nativeResponse) {

            }

            @Override
            public void onNativeClick(View view, com.iyuba.sdk.nativeads.NativeResponse nativeResponse) {

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
