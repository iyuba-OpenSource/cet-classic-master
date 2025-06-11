package com.iyuba.CET4bible.listening;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.iyuba.CET4bible.MyApplication;
import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.event.MergeAudioEventbus;
import com.iyuba.CET4bible.event.ShareMerge;
import com.iyuba.CET4bible.manager.ListenDataManager;
import com.iyuba.CET4bible.widget.subtitle.Subtitle;
import com.iyuba.abilitytest.network.AbilityTestRequestFactory;
import com.iyuba.abilitytest.network.EvaMixBean;
import com.iyuba.abilitytest.network.PublishResponse;
import com.iyuba.base.util.L;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.manager.AccountManager;
import com.iyuba.core.sqlite.mode.test.CetText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 真题 - 题型 - 听力 - sectionA页面 - 评测页面
 */
public class ReadEvaluateFragment extends Fragment {

    private static final String TAG = ReadEvaluateFragment.class.getSimpleName();


    ReadEvaluateAdapterNew mAdapter;
    List<Subtitle> mSubTextABList = new ArrayList<>();

    getItemABCallBack mCallBack;

    private String shareWebHeader = "http://voa.iyuba.cn/voa/play.jsp?";


    private boolean mIsFirstTimeEvaluate = false;

    private String curTextSound;

    RecyclerView mRecyclerView;
    ImageView mPlayMergeControl;
    TextView mTvMergeCurrentTime;
    SeekBar tvMergeSeekBar;
    TextView mTvMergeTotalTime;
    TextView mTvPulish;
    LinearLayout mSectionaMerge;
    TextView tvMergeTotalScore;


    private String mergeAudioPrefix = "http://voa.iyuba.cn/voa/";
    private MediaPlayer mergePlayer;
    private String shuoshuoId;
    private String mSection;
    private String publishURL;

    /**
     * 更新数据
     */
    public void updateDataList() {

        mSubTextABList.clear();
        initSubtitleList();
        if (mAdapter != null) {

            mAdapter.setPlayUrl(mAdapter.getPlayUrl());
            mAdapter.setmItems(mSubTextABList);
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsFirstTimeEvaluate = ConfigManager.Instance().loadBoolean("firstEvaluate", true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_read_evaluate, container, false);
        initView(root);
        initSubtitleList();
        initPlayer();
        return root;
    }


    private void initView(View view) {
        mPlayMergeControl = view.findViewById(R.id.play_merge_control);
        mTvMergeCurrentTime = view.findViewById(R.id.tv_merge_current_time);
        tvMergeSeekBar = view.findViewById(R.id.newsectiona_merge_seekbar);

        mRecyclerView = view.findViewById(R.id.recycler);
        tvMergeTotalScore = view.findViewById(R.id.tv_merge_total_score);
        mSectionaMerge = view.findViewById(R.id.sectiona_merge);
        mTvPulish = view.findViewById(R.id.tv_pulish);
        mTvMergeTotalTime = view.findViewById(R.id.tv_merge_total_time);

        TextView tv_mereg = view.findViewById(R.id.tv_mereg);
        tv_mereg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onMTvMeregClicked();
            }
        });
        mPlayMergeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onMPlayMergeControlClicked();
            }
        });
        mTvPulish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onMTvPulishClicked();
            }
        });

        mPlayMergeControl.setVisibility(View.GONE);
    }


    private void initPlayer() {
        mergePlayer = new MediaPlayer();
        mergePlayer.setOnPreparedListener(mergePreparedListener);
        mergePlayer.setOnCompletionListener(mp -> {

            mPlayMergeControl.setImageResource(com.iyuba.trainingcamp.R.drawable.trainingcamp_icon_play);
        });
    }

    private MediaPlayer.OnPreparedListener mergePreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {

            if (mAdapter != null) {

                if (mAdapter.isRecording() || mAdapter.isPlaying()) {

                    mAdapter.stopRunningJob();
                    mAdapter.setPlaying(false);
                    mAdapter.setRecording(false);
                    mAdapter.notifyDataSetChanged();
                }
            }

            mergePlayer.start();
            mPlayMergeControl.setVisibility(View.VISIBLE);
            mPlayMergeControl.setImageResource(com.iyuba.trainingcamp.R.drawable.trainingcamp_icon_pause);
            tvMergeSeekBar.setMax(mergePlayer.getDuration());
            mTvMergeTotalTime.setText(formatTime(mergePlayer.getDuration()));
            UpdateSeekBarHandler.sendEmptyMessage(100);
        }
    };


    @SuppressLint("HandlerLeak")
    Handler UpdateSeekBarHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    try {
                        tvMergeSeekBar.setProgress(mergePlayer.getCurrentPosition());
                        sendEmptyMessageDelayed(100, 1000);
                        mTvMergeCurrentTime.setText(formatTime(mergePlayer.getCurrentPosition()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }


        }
    };

    /**
     * 格式化时间
     */
    public String formatTime(int time) {
        int sec = time / 1000 % 60;
        int min = time / 1000 / 60;
        if (sec < 10) {
            if (min < 10) {
                return "0" + min + ":" + "0" + sec;
            } else {
                return min + ":" + "0" + sec;
            }
        } else {
            if (min < 10) {
                return "0" + min + ":" + sec;
            } else {
                return min + ":" + sec;
            }
        }
    }

    private void initSubtitleList() {
        ArrayList<CetText> textList = ListenDataManager.Instance().textList;
        if (textList != null) {
            int size = textList.size();
            Subtitle st;
            StringBuffer sb;
            for (int i = 0; i < size; i++) {
                st = new Subtitle();
                sb = new StringBuffer();
                if (!TextUtils.isEmpty(textList.get(i).sex)) {
                    sb.append("\t");
                    sb.append(textList.get(i).sex);
                    sb.append(": ");
                    sb.append(textList.get(i).sentence);
                    sb.append("\n");
                } else {
                    sb.append("\t");
                    sb.append(textList.get(i).sentence);
                    sb.append("\n");
                }
                L.e("---- ====  - -- -  -  " + sb.toString());
                st.content = sb.toString().substring(3);
                st.testTime = textList.get(i).testTime;
                st.Number = Integer.parseInt(textList.get(i).id);
                st.NumberIndex = Integer.parseInt(textList.get(i).index);
                if (textList.get(i).time != null) {
                    st.pointInTime = Integer.parseInt(textList.get(i).time);
                }
                mSubTextABList.add(st);
            }
        }

    }


    public void onMTvMeregClicked() {

        if (mAdapter != null && mAdapter.isRecording()) {

            mAdapter.stopRunningJob();
            mAdapter.notifyDataSetChanged();
            return;
        }

        StringBuilder s = new StringBuilder();
        int count = 0;
        for (Subtitle subTextAB : mSubTextABList) {
            if (!TextUtils.isEmpty(subTextAB.record_url)) {
                s.append(subTextAB.record_url).append(",");
                count++;
            }
        }
        if (count < 2) {
            ToastUtils.showShort("请至少评测两句才可以合成");
            return;
        }
        startMerge(s.toString());
    }

    private void startMerge(String mergeString) {
        AbilityTestRequestFactory.getEvaluateApi().audioComposeApi(mergeString, com.iyuba.configation.Constant.mListen)
                .subscribeOn(Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Observer<EvaMixBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(EvaMixBean evaMixBean) {
                        publishURL = evaMixBean.getURL();
                        ToastUtils.showShort("合成完成");
                        tvMergeTotalScore.setVisibility(View.VISIBLE);
                        tvMergeTotalScore.setText(String.valueOf(getAverage()));
                        mTvPulish.setTextColor(getResources().getColor(R.color.colorPrimary));
                        mTvPulish.setBackground(getResources().getDrawable(R.drawable.round_text_selector));
                        mTvPulish.setText("发布");
                        try {
                            mergePlayer.reset();
                            mergePlayer.setDataSource(mergeAudioPrefix + publishURL);
                            mergePlayer.prepareAsync();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.showShort("出现错误请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void onMPlayMergeControlClicked() {

        if (mAdapter.isRecording()) {

            Toast.makeText(MyApplication.getInstance(), "请先评测完成", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAdapter.isPlaying()) {

            Toast.makeText(MyApplication.getInstance(), "请等待播放完成", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mergePlayer.isPlaying()) {
            mergePlayer.pause();
            mPlayMergeControl.setImageResource(com.iyuba.trainingcamp.R.drawable.trainingcamp_icon_play);
        } else {
            mPlayMergeControl.setImageResource(com.iyuba.trainingcamp.R.drawable.trainingcamp_icon_pause);
            mergePlayer.start();
        }
    }

    public void onMTvPulishClicked() {
        if (TextUtils.equals("分享", mTvPulish.getText().toString())) {
            EventBus.getDefault().post(new ShareMerge(publishURL, shuoshuoId));
            return;
        }
        if (!AccountManager.Instace(getActivity()).checkUserLogin()) {
            ToastUtils.showShort("请登录后发布");
            return;
        }
        if (TextUtils.isEmpty(publishURL)) {
            ToastUtils.showShort("未合成语音,不能发布");
            return;
        }

        ToastUtils.showShort("正在发布");
        Call<PublishResponse> call = AbilityTestRequestFactory.getPublishApi().publishMerge(buildMap());
        call.enqueue(new Callback<PublishResponse>() {
            @Override
            public void onResponse(@NonNull Call<PublishResponse> call, @NonNull Response<PublishResponse> response) {
                ToastUtils.showShort("发布成功");
                shuoshuoId = response.body().getShuoShuoId() + "";
                mTvPulish.setText("分享");
            }

            @Override
            public void onFailure(@NonNull Call<PublishResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private Map buildMap() {
        Map map = new HashMap<>();
        String userId = com.iyuba.configation.ConfigManager.Instance().loadString("userId");
        if (TextUtils.isEmpty(com.iyuba.configation.ConfigManager.Instance().loadString("userId"))) {
            userId = "0";
        }
        String userName = com.iyuba.configation.ConfigManager.Instance().loadString("userName");
        if (TextUtils.isEmpty(com.iyuba.configation.ConfigManager.Instance().loadString("userName"))) {
            userName = "0";
        }
        map.put("topic", AbilityTestRequestFactory.fromString(Constant.mListen));
        map.put("protocol", AbilityTestRequestFactory.fromString("60002"));
        map.put("platform", AbilityTestRequestFactory.fromString("android"));
        map.put("shuoshuotype", AbilityTestRequestFactory.fromString("4"));
        map.put("format", AbilityTestRequestFactory.fromString("json"));
        String examTime = ((SectionAActivity) getActivity()).mExamTime;
        String mSection = ((SectionAActivity) getActivity()).section;
        map.put("voaid", AbilityTestRequestFactory.fromString(String.valueOf(examTime)));
//        mSection = ACache.get(getActivity()).getAsString("testType");
        if (mSection.equalsIgnoreCase("a")) {
            map.put("paraid", AbilityTestRequestFactory.fromString("1"));
        } else if (mSection.equalsIgnoreCase("b")) {
            map.put("paraid", AbilityTestRequestFactory.fromString("2"));
        } else if (mSection.equalsIgnoreCase("c")) {
            map.put("paraid", AbilityTestRequestFactory.fromString("3"));
        }
        map.put("score", AbilityTestRequestFactory.fromString(String.valueOf(getAverage())));
        map.put("userid", AbilityTestRequestFactory.fromString(userId));// TODO this is not good
        map.put("username", AbilityTestRequestFactory.fromString(userName));
        map.put("content", AbilityTestRequestFactory.fromString(publishURL));
        return map;
    }


    private int getAverage() {
        int readScore = 0, count = 0;
        for (int i = 0; i < mSubTextABList.size(); i++) {
            if (mSubTextABList.get(i).isRead) {
                readScore += mSubTextABList.get(i).readScore;
                count++;
            }
        }
        readScore = readScore / count;
        return readScore;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new ReadEvaluateAdapterNew(mSubTextABList, ((SectionAActivity) getContext()).mExamTime,
                ((SectionAActivity) getContext()).getSoundPath(), getActivity(),((SectionAActivity) getContext()).section);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        EventBus.getDefault().register(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mIsFirstTimeEvaluate) {
                mIsFirstTimeEvaluate = false;
                ConfigManager.Instance().putBoolean("firstEvaluate", false);
            }
        } else {
            if (mergePlayer != null && mergePlayer.isPlaying()) mergePlayer.pause();
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


    public void stopEvaluateJobsCompletly() {
        if (mAdapter != null)
            mAdapter.stopRunningJobTotally();
    }

    public interface getItemABCallBack {
        List<Subtitle> onSubTextABs(List<Subtitle> subTextABS);
    }

    public void setCallBack(getItemABCallBack callBack) {
        mCallBack = callBack;
    }

    /**
     * 停止录音和播放
     */
    public void stopEvaluateJobs() {

        if (mAdapter != null) {
            mAdapter.stopRunningJob();
            mAdapter.notifyDataSetChanged();

            Log.d("bible", "mAdapter.stopRunningJob();");
        }
    }

    /**
     * 点击录音评测
     * 停止播放合成音频，如果合成音频在播放
     *
     * @param mergeAudioEventbus
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MergeAudioEventbus mergeAudioEventbus) {

        if (mergePlayer != null && mergePlayer.isPlaying()) {

            mPlayMergeControl.setImageResource(com.iyuba.trainingcamp.R.drawable.trainingcamp_icon_play);
            mergePlayer.pause();
        }
    }

    @Subscribe
    public void onEvent(ShareMerge merge) {
        int readScore = 0, count = 0;
        for (int i = 0; i < mAdapter.getmItems().size(); i++) {
            if (mAdapter.getmItems().get(i).isRead) {
                readScore += mAdapter.getmItems().get(i).readScore;
                count++;
            }
        }
        readScore = readScore / count;

        String text = "我在" + Constant.APPName + "语音评测中得了" + readScore + "分";
        ((SectionAActivity) getActivity()).showShare(text, "", shareWebHeader + "id=" + merge.shuoshuoId + "&appid=" + Constant.APPID + "&apptype=" + com.iyuba.configation.Constant.mListen + "&addr=" + merge.publishUrl + "&from=singemessage");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mAdapter != null) {
            mAdapter.stopRunningJobTotally();
        }
    }
}
