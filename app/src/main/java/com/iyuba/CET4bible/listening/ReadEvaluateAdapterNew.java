package com.iyuba.CET4bible.listening;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.iyuba.CET4bible.MyApplication;
import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.event.MergeAudioEventbus;
import com.iyuba.CET4bible.sqlite.op.NewTypeTextAOp;
import com.iyuba.CET4bible.util.SentenceSpanUtils;
import com.iyuba.CET4bible.widget.subtitle.Subtitle;
import com.iyuba.abilitytest.network.AbilityTestRequestFactory;
import com.iyuba.abilitytest.network.EvaluateApi;
import com.iyuba.abilitytest.network.PublishVoiceResponse;
import com.iyuba.abilitytest.network.SendEvaluateResponse;
import com.iyuba.abilitytest.utils.ToastUtil;
import com.iyuba.configation.Constant;
import com.iyuba.core.eventbus.LoginEvent;
import com.iyuba.core.manager.AccountManager;
import com.iyuba.core.manager.RecordManager;
import com.iyuba.core.widget.RoundProgressBar;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lid.lib.LabelTextView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * @author yq QQ:1032006226
 * @name cetListening-1
 * @class name：com.iyuba.cet4.activity.fragment
 * @class describe
 * @time 2019/1/21 11:32
 * @change
 * @chang time
 * @class describe
 */
public class ReadEvaluateAdapterNew extends RecyclerView.Adapter<ReadEvaluateAdapterNew.ViewHolder> {

    private int mActivePosition = 0;
    private String playUrl;

    NewTypeTextAOp cetHelper;
    private String shareWebHeader = "http://voa.iyuba.cn/voa/play.jsp?";
    private String paraId;
    private RecordManager mRecordManager;
    private String examTime;

    private String section;
    private Disposable recordSubscription;
    private Disposable s;
    private Disposable s2;
    private boolean isRecording = false;
    private KProgressHUD publishingDialog;

    private Typeface typeface = Typeface.SERIF;

    private MediaPlayer mPlayerOrigin;
    private MediaPlayer mPlayerRecord;
    private Context mContext;
    private String userId;
    private Disposable recordingObservable;
    private Disposable recordingTimerSubscription;
    private boolean isPlaying = false;
    private int playingIndex = -1;

    /**
     * 录音的位置
     */
    private int recordPos = -1;

    private SharedPreferences evalSp;


    public ReadEvaluateAdapterNew(List<Subtitle> items, String ExamTime, String url, Context context, String section) {
        mItems = items;
        examTime = ExamTime;   // 当做lessonid ;
        playUrl = url;
        mContext = context;
        this.section = section;

        evalSp = context.getSharedPreferences("EVAL", Context.MODE_PRIVATE);
        initVariables();

        for (Subtitle text : mItems) {
            text = cetHelper.getRecordingResult(text, ((SectionAActivity) mContext).section);
        }
        try {
            mPlayerOrigin.setDataSource(playUrl);
            mPlayerOrigin.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {

        this.playUrl = playUrl;
        if (mPlayerOrigin != null) {
            mPlayerOrigin.reset();
            try {
                mPlayerOrigin.setDataSource(playUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mPlayerOrigin.prepareAsync();
        }
    }

    private void initVariables() {
        mPlayerOrigin = new MediaPlayer();
        mPlayerRecord = new MediaPlayer();
        cetHelper = new NewTypeTextAOp(mContext);
        publishingDialog = KProgressHUD.create(mContext)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍等")
                .setDetailsLabel("正在上传...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        userId = com.iyuba.configation.ConfigManager.Instance().loadString("userId");
        if (TextUtils.isEmpty(com.iyuba.configation.ConfigManager.Instance().loadString("userId"))) {
            userId = "0";
        }

        ((SectionAActivity) mContext).setOriginFragmentPlayer(mPlayerOrigin);
        ((SectionAActivity) mContext).setRecordFragmentPlayer(mPlayerRecord);
    }


    public List<Subtitle> getmItems() {
        return mItems;
    }

    public void setmItems(List<Subtitle> mItems) {
        this.mItems = mItems;
    }

    private List<Subtitle> mItems;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_read_evaluate, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setActive((position == mActivePosition));
        if (position < mItems.size() - 1) {
            holder.setItem(mItems.get(position), mItems.get(position + 1));
        } else {
            holder.setItem(mItems.get(position), mItems.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        LabelTextView enTextView;
        View separateLine;
        LinearLayout buttonsLayout;
        RoundProgressBar playProgressBar;
        RoundProgressBar readProgressBar;
        FrameLayout readPlayLayout;
        ImageView readPlayImageView;
        RoundProgressBar recordPlayProgressBar;
        ImageView sendImageView;
        ImageView shareImageView;
        TextView readResultTextView;

        Subtitle item;
        Subtitle itemNext;

        boolean isActive = false;
        private int inteval;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            initView(itemView);
            enTextView.setTypeface(typeface);
        }


        private void initView(View view) {

            enTextView = view.findViewById(R.id.sen_en);
            separateLine = view.findViewById(R.id.sep_line);
            buttonsLayout = view.findViewById(R.id.bottom_view);
            playProgressBar = view.findViewById(R.id.sen_play);

            readProgressBar = view.findViewById(R.id.sen_i_read);
            readPlayLayout = view.findViewById(R.id.sen_read_button);
            readPlayImageView = view.findViewById(R.id.sen_read_play);

            recordPlayProgressBar = view.findViewById(R.id.sen_read_playing);
            sendImageView = view.findViewById(R.id.sen_read_send);
            shareImageView = view.findViewById(R.id.sen_read_collect);
            readResultTextView = view.findViewById(R.id.sen_read_result);

            LinearLayout linear_text_container = view.findViewById(R.id.linear_text_container);
            linear_text_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    clickTextPart();
                }
            });
            recordPlayProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    stop_play();
                }
            });
            sendImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    sendVoice();
                }
            });
            readPlayImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onClickrecordPlayProgressBar();
                }
            });
            playProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    clickPlayOriginal();
                }
            });
            readProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickEvaluate();
                }
            });
            shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onCLickshare();
                }
            });
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        private void setReadScoreViewContent(TextView view, int score) {
            if (score < 50) {
                view.setText("");
                view.setBackgroundResource(R.drawable.sen_score_lower60);
            } else if (score > 80) {
                view.setText(String.valueOf(score));
                view.setBackgroundResource(R.drawable.sen_score_higher_80);
            } else {
                view.setText(String.valueOf(score));
                view.setBackgroundResource(R.drawable.sen_score_60_80);
            }
        }

        public void setItem(Subtitle item, Subtitle itemNext) {
            this.item = item;
            this.itemNext = itemNext;


            if (!isRecording) {

                readProgressBar.setProgress(0);
            }

            enTextView.setLabelText(item.NumberIndex + "");
            enTextView.setText(item.content);
            shareImageView.setVisibility(View.INVISIBLE);
            if (isActive) {
                separateLine.setVisibility(View.VISIBLE);
                buttonsLayout.setVisibility(View.VISIBLE);
                if (item.isRead) {
                    readResultTextView.setVisibility(View.VISIBLE);
                    setReadScoreViewContent(readResultTextView, (int) item.readScore);
                    setViewVisibilityAndClickability(readPlayLayout, true);
                    setViewVisibilityAndClickability(sendImageView, true);
                } else {
                    setViewVisibilityAndClickability(readPlayLayout, false);
                    setViewVisibilityAndClickability(sendImageView, false);
                }
            } else {
                separateLine.setVisibility(View.GONE);
                buttonsLayout.setVisibility(View.GONE);
            }
            if (item.goodList != null || item.badList != null) {
                setWordsBean(item);
            }

        }

        MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
                recordPlayProgressBar.setVisibility(View.GONE);
                readPlayImageView.setVisibility(View.VISIBLE);
            }
        };


        MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                inteval = 0;
                mp.start();
                isPlaying = true;
                recordPlayProgressBar.setMax(mp.getDuration());
                readPlayImageView.setVisibility(View.INVISIBLE);
                recordPlayProgressBar.setVisibility(View.VISIBLE);
                recordSubscription = Observable.interval(100, TimeUnit.MILLISECONDS)
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {

                                recordPlayProgressBar.setProgress(inteval * 100);
                                inteval++;
                            }
                        });
            }
        };

        private void setViewVisibilityAndClickability(View view, boolean isAble) {
            if (isAble) {
                view.setVisibility(View.VISIBLE);
                view.setClickable(true);
            } else {
                view.setVisibility(View.INVISIBLE);
                view.setClickable(false);
            }
        }

        public void clickTextPart() {
            if (mActivePosition != getAdapterPosition()) {
                isPlaying = false;
                stopRunningJob();
                mActivePosition = getAdapterPosition();
                notifyDataSetChanged();
            }
        }

        public void stop_play() {


            if (isPlaying) {
                recordPlayProgressBar.setProgress(0);
                recordPlayProgressBar.setVisibility(View.GONE);
                readPlayImageView.setVisibility(View.VISIBLE);
                mPlayerRecord.pause();
                isPlaying = false;
                Timber.tag("bible").d("1234");
                return;
            }
        }


        public void sendVoice() {
            if (!AccountManager.Instace(mContext).checkUserLogin()) {
                ToastUtils.showShort("请登录后发布");
                return;
            }
            publishingDialog.setDetailsLabel("正在发布").setLabel("请稍等").show();

            Map<String, String> map = new HashMap<>();
            map.put("topic", (com.iyuba.configation.Constant.mListen));
            map.put("protocol", ("60002"));
            map.put("platform", ("android"));
            map.put("shuoshuotype", ("2"));
            map.put("format", ("json"));
            map.put("voaid", (String.valueOf(examTime)));
//            map.put("paraId",(String.valueOf(item.Number+")));
            if (((SectionAActivity) mContext).section.equals("A")) {
                map.put("paraid", ("1"));
            } else if (((SectionAActivity) mContext).section.equals("B")) {
                map.put("paraid", ("2"));
            } else if (((SectionAActivity) mContext).section.equals("C")) {
                map.put("paraid", ("3"));
            }
            map.put("idIndex", (item.Number + "000" + item.NumberIndex));
            map.put("score", (String.valueOf(item.readScore)));
            map.put("userid", (userId));// TODO this is not good
//            map.put("username", AbilityTestRequestFactory.fromString(userName));
            map.put("content", (item.record_url));

            Call<PublishVoiceResponse> call = AbilityTestRequestFactory.getPublishApi().publishVoice(map);

            call.enqueue(new Callback<PublishVoiceResponse>() {
                @Override
                public void onResponse(Call<PublishVoiceResponse> call, Response<PublishVoiceResponse> response) {
                    publishingDialog.dismiss();
                    setViewVisibilityAndClickability(sendImageView, false);
                    item.shuoshuoid = response.body().getShuoShuoId();
                    shareImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<PublishVoiceResponse> call, Throwable t) {
                    publishingDialog.dismiss();
                }
            });
        }

        public void onClickrecordPlayProgressBar() {

            stopRunningJob();
            if (mRecordManager != null && isRecording) {

                readProgressBar.setProgress(0);
                mRecordManager.stopRecord();
                recordPos = -1;
                isRecording = false;
            }

            Timber.tag("bible").d(isPlaying + "");
            File file = new File(Environment.getExternalStorageDirectory() + "/iyuba/" + com.iyuba.configation.Constant.mListen + "/"
                    + "audio/"
                    + examTime + "-" + paraId + "-"
                    + item.NumberIndex + ".amr");
            Timber.tag("bible").d(file.getAbsolutePath());
            if (file.exists()) {
                try {
                    mPlayerRecord.reset();
                    mPlayerRecord.setDataSource(file.getAbsolutePath());
                    mPlayerRecord.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            isPlaying = true;
            playingIndex = mActivePosition;
            mPlayerRecord.setOnPreparedListener(onPreparedListener);
            mPlayerRecord.setOnCompletionListener(completionListener);

        }

        public void clickPlayOriginal() {
            if (isRecording) {
                return;
            }
            stopRunningJob();

            if (isPlaying && playingIndex == mActivePosition) {
                playProgressBar.setBackgroundResource(R.drawable.sen_play);
                playProgressBar.setProgress(0);
                isPlaying = false;
                return;
            }

            Timber.e("playOri");
            Timber.tag("bible").e(String.valueOf(item.pointInTime));
            playProgressBar.setMax(100);
            if (s2 != null) {
                s2.dispose();
            }
            inteval = 0;
            isPlaying = true;
            playingIndex = mActivePosition;
            mPlayerOrigin.seekTo(item.pointInTime * 1000);
            Log.d("bible", item.pointInTime * 1000 + "");
            mPlayerOrigin.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mPlayerOrigin.start();
                    playProgressBar.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.sen_stop));
                    if (mActivePosition == playingIndex) {
                        s = Observable.interval(100, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {

                                Timber.tag("bible").d(mPlayerOrigin.getCurrentPosition() + ":" + item.pointInTime * 1000 + ":" + item.endTiming * 1000);
                                if (mActivePosition == mItems.size() - 1) {
                                    playProgressBar.setProgress((inteval) * 10 / 20);
                                } else {
                                    playProgressBar.setProgress((inteval) * 10 / (itemNext.pointInTime - item.pointInTime));
                                }
                                inteval++;
                            }
                        });
                        if (mActivePosition == mItems.size() - 1) {
                            s2 = Observable.timer(20, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {

                                    mPlayerOrigin.pause();
                                    playProgressBar.setBackgroundResource(R.drawable.sen_play);
                                    playProgressBar.setProgress(0);
                                    isPlaying = false;
                                    s.dispose();
                                }
                            });
                        } else {
                            s2 = Observable.timer((long) (itemNext.pointInTime - item.pointInTime + 0.75), TimeUnit.SECONDS)
                                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {

                                            mPlayerOrigin.pause();
                                            playProgressBar.setBackgroundResource(R.drawable.sen_play);
                                            playProgressBar.setProgress(0);
                                            isPlaying = false;
                                            s.dispose();
                                        }
                                    });
                        }
                    }

                }
            });
        }

        public void clickEvaluate() {


            if (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || mContext.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {


                new AlertDialog.Builder(mContext)
                        .setTitle("权限说明")
                        .setMessage("读写权限：存储录音文件\n录音权限：录音进行评测")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //停止合成音频的播放
                                EventBus.getDefault().post(new MergeAudioEventbus());
                                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
                                //验证是否许可权限
                                for (String str : permissions) {
                                    if (mContext.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                                        ((SectionAActivity) mContext).requestPermissions(permissions, SectionAActivity.REQUEST_CODE_CONTACT);
                                        return;
                                    }
                                }
                            }
                        })
                        .show();

            } else {

                startEvaluateJobs();
            }
        }

        /**
         * 评测测试弹窗
         * true  可评测
         * false  不可评测
         *
         * @return
         */
        private boolean evalAlert() {

            if (AccountManager.Instace(MyApplication.getInstance()).checkUserLogin()) {

                if (AccountManager.Instace(MyApplication.getInstance()).userInfo.vipStatus.equals("0")) {

                    int count = evalSp.getInt("count" + examTime + section, 0);
                    if (count >= 5) {

                        new AlertDialog.Builder(mContext)
                                .setTitle("提示")
                                .setMessage("非会员用户每篇可免费评测5句")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        return false;
                    }
                }
            } else {

                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("是否去登录？")
                        .setPositiveButton("去登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EventBus.getDefault().post(new LoginEvent());
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .show();
                return false;
            }
            return true;
        }

        private void startEvaluateJobs() {
            stopRunningJob();
            if (!NetworkUtils.isConnected()) {
                ToastUtil.showToast(itemView.getContext(), "请检查网络连接");
                return;
            }

            //检测评测次数

            if (!evalAlert()) {

                return;
            }

            if (recordPos != -1 && recordPos != getBindingAdapterPosition()) {

                if (mRecordManager != null && isRecording) {

                    mRecordManager.stopRecord();
                    isRecording = false;
                    recordPos = -1;
                }
                return;
            }

            final File file = new File(MyApplication.getInstance().getExternalFilesDir(null) + "/iyuba/" + com.iyuba.configation.Constant.mListen + "/"
                    + "audio/"
                    + examTime + "-" + item.Number + "-"
                    + item.NumberIndex + ".amr");
            if (!isRecording) {
                if (!file.exists()) {
                    try {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                recordPos = getBindingAdapterPosition();//操作的位置
                mRecordManager = new RecordManager(file);
                mRecordManager.startRecord();
                isRecording = true;
                readProgressBar.setMax(100);
                recordingTimerSubscription = Observable.interval(200, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {

                                readProgressBar.setProgress((int) mRecordManager.getVolume());
                            }
                        });
                recordingObservable = Observable.timer(item.content.length() * 120 + 200, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {

                                        if (isRecording && isActive) {

                                            mRecordManager.stopRecord();
                                            isRecording = false;
                                            recordPos = -1;
                                            startEvaluate(item, file);
                                            recordingTimerSubscription.dispose();
                                        }
                                    }
                                }
                        );
            } else {

                try {//处理连续点击的异常
                    mRecordManager.stopRecord();
                    isRecording = false;
                    recordPos = -1;//操作位置重置
                    recordingTimerSubscription.dispose();
                    recordingObservable.dispose();
                    startEvaluate(item, file);
                } catch (RuntimeException e) {

                    isRecording = false;
                    recordPos = -1;//操作位置重置
                    recordingTimerSubscription.dispose();
                    recordingObservable.dispose();
                    Log.d("ReadEvaluateAdapterNew", e.getMessage() + "");
                }
            }
        }

        public void onCLickshare() {
            String text = "我在" + Constant.APPName + "语音评测中得了" + item.readScore + "分";
            ((SectionAActivity) mContext).showShare(text, item.content,
                    shareWebHeader + "id=" + item.shuoshuoid + "&appid=" + Constant.APPID + "&apptype=" + com.iyuba.configation.Constant.mListen + "&addr=" + item.record_url
                            + "&from=singemessage");
        }

        private void startEvaluate(Subtitle Subtitle, File file) {
            ToastUtils.showLong("正在测评,请稍等");


            readProgressBar.setProgress(0);
            Map<String, RequestBody> map = new HashMap<>(6);
            map.put(EvaluateApi.GetVoa.Param.Key.SENTENCE, AbilityTestRequestFactory.fromString(Subtitle.content));
            map.put(EvaluateApi.GetVoa.Param.Key.IDINDEX, AbilityTestRequestFactory.fromString(item.Number + "000" + item.NumberIndex));
            map.put(EvaluateApi.GetVoa.Param.Key.NEWSID, AbilityTestRequestFactory.fromString(examTime));
            map.put(EvaluateApi.GetVoa.Param.Key.PARAID, AbilityTestRequestFactory.fromString(Subtitle.Number + ""));
            map.put(EvaluateApi.GetVoa.Param.Key.TYPE, AbilityTestRequestFactory.fromString(com.iyuba.configation.Constant.mListen + ""));
            map.put(EvaluateApi.GetVoa.Param.Key.USERID, AbilityTestRequestFactory.fromString(userId));
            map.put(EvaluateApi.GetVoa.Param.Key.APPID, AbilityTestRequestFactory.fromString(Constant.APPID));
            map.put(EvaluateApi.GetVoa.Param.Key.FLG, AbilityTestRequestFactory.fromString("0"));
            map.put(EvaluateApi.GetVoa.Param.Key.WORDID, AbilityTestRequestFactory.fromString("0"));
            Call<SendEvaluateResponse> call = AbilityTestRequestFactory.getEvaluateApi().sendVoice(map, AbilityTestRequestFactory.fromFile(file));
            call.enqueue(new Callback<SendEvaluateResponse>() {
                @Override
                public void onResponse(Call<SendEvaluateResponse> call, final Response<SendEvaluateResponse> response) {
                    itemView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (response.body() != null) {
                                item.isRead = true;
                                if (response.body().getResult().equals("0")) {
                                    ToastUtils.showShort("评测失败");
                                    return;
                                }
                                if (response.body().getData() != null) {

                                    //记录评测次数
                                    int count = evalSp.getInt("count" + examTime + section, 0);
                                    SharedPreferences.Editor editor = evalSp.edit();
                                    editor.putInt("count" + examTime + section, ++count);
                                    editor.apply();

                                    setReadScoreViewContent(readResultTextView, (int) (Double.parseDouble(response.body().getData().getTotal_score()) * 20));
                                    item.readScore = (int) (Double.parseDouble(response.body().getData().getTotal_score()) * 20);
                                    item.mDataBean = response.body().getData().getWords();
                                    item.record_url = response.body().getData().getURL();
                                    List<Integer> goodlist = new ArrayList<>();
                                    List<Integer> badList = new ArrayList<>();

                                    for (int i = 0; i < response.body().getData().getWords().size(); i++) {
                                        SendEvaluateResponse.DataBean.WordsBean bean = response.body().getData().getWords().get(i);
                                        if (bean.getScore() < 2.5) {
                                            badList.add(i);
                                        } else if (bean.getScore() > 4) {
                                            goodlist.add(i);
                                        }
                                    }
                                    item.badList = badList;
                                    item.goodList = goodlist;
                                    writeNewDataToDB(item);
                                    notifyDataSetChanged();
                                } else {
                                    ToastUtil.showToast(mContext, "失败");
                                }
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Call<SendEvaluateResponse> call, Throwable t) {
                    ToastUtils.showShort("评测失败");
                }
            });

        }

        public void setWordsBean(Subtitle item) {
            if (item.goodList == null || item.badList == null) {
                return;
            }
            SpannableStringBuilder builder = SentenceSpanUtils.getSpanned(mContext, item.content,
                    item.goodList, item.badList);
            enTextView.setText(builder);
        }

        private void stopRunningJob() {

            if (recordSubscription != null) recordSubscription.dispose();
            if (s != null) s.dispose();
            if (s2 != null) s2.dispose();
            if (recordingTimerSubscription != null) recordingTimerSubscription.dispose();
            if (recordingObservable != null) recordingObservable.dispose();
            if (mPlayerRecord.isPlaying()) mPlayerRecord.pause();
            if (mPlayerOrigin != null && mPlayerOrigin.isPlaying()) {
                mPlayerOrigin.pause();
                playProgressBar.setBackgroundResource(R.drawable.sen_play);
                playProgressBar.setProgress(0);
            }
            readPlayImageView.setVisibility(View.VISIBLE);
            recordPlayProgressBar.setVisibility(View.GONE);
        }
    }

    private void writeNewDataToDB(Subtitle item) {
        cetHelper.writeRecordingData(item, ((SectionAActivity) mContext).section);
    }

    public void stopRunningJob() {

        if (recordSubscription != null) recordSubscription.dispose();
        if (s != null) s.dispose();
        if (s2 != null) s2.dispose();
        if (recordingTimerSubscription != null) recordingTimerSubscription.dispose();
        if (recordingObservable != null) recordingObservable.dispose();
        if (mPlayerRecord != null) mPlayerRecord.reset();
        if (mPlayerOrigin != null && mPlayerOrigin.isPlaying()) {

            mPlayerOrigin.pause();
            playingIndex = -1;
        }
        if (mRecordManager != null && isRecording) {

            mRecordManager.stopRecord();
            isRecording = false;
            recordPos = -1;
        }
    }

    public void stopRunningJobTotally() {
        if (recordSubscription != null) recordSubscription.dispose();
        if (s != null) s.dispose();
        if (s2 != null) s2.dispose();
        if (recordingTimerSubscription != null) recordingTimerSubscription.dispose();
        if (recordingObservable != null) recordingObservable.dispose();
        if (mPlayerRecord != null) mPlayerRecord.reset();
        if (mPlayerOrigin != null) {
            mPlayerOrigin.stop();
            mPlayerOrigin.release();
        }
    }


    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }
}
