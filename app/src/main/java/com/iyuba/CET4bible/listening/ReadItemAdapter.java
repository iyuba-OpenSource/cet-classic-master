package com.iyuba.CET4bible.listening;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.iyuba.CET4bible.MyApplication;
import com.iyuba.CET4bible.R;
import com.iyuba.core.eventbus.LoginEvent;
import com.iyuba.CET4bible.sqlite.op.NewTypeTextAOp;
import com.iyuba.abilitytest.network.AbilityTestRequestFactory;
import com.iyuba.abilitytest.network.SendGoodResponse;
import com.iyuba.abilitytest.network.SpeakRankWork;
import com.iyuba.core.manager.AccountManager;
import com.iyuba.core.util.ToastUtil;
import com.lid.lib.LabelTextView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * @author yq QQ:1032006226
 * @name cetListening-1
 * @class name：com.iyuba.cet4.activity.adapter
 * @class describe
 * @time 2019/1/23 14:17
 * @change
 * @chang time
 * @class describe
 */
public class ReadItemAdapter extends RecyclerView.Adapter<ReadItemAdapter.ViewHolder> {

    private Context context;
    private List<SpeakRankWork> speaks;
    private List<SpeakRankWork> currentWorks = new ArrayList<>();

    private MediaPlayer mPlayer;
    private String userName;
    private String userImage;
    private String para;

    AnimationDrawable animationDrawable;

    private RxDataStore<Preferences> dataStore;


    public void closeDataStore() {

        if (dataStore != null) {

            dataStore.dispose();
        }
    }

    public ReadItemAdapter(String userImageUrl, String userName, String para) {
        userImage = userImageUrl;
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                notifyDataSetChanged();
            }
        });
        this.para = para;
        this.userName = userName;
        dataStore = new RxPreferenceDataStoreBuilder(MyApplication.getInstance(), "ZAN").build();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
        context = group.getContext();
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_read_rank_work, group, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        Timber.tag("bible").d("onBindViewHolder: %s", currentWorks.get(i).imgsrc);
        Glide.with(context).asBitmap().load(userImage).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                holder.mImageUserHead.setImageBitmap(resource);
            }
        });

        holder.mTextUsername.setText(userName);
        holder.mTextScore.setText(currentWorks.get(i).score + "");
        holder.mTextTime.setText(currentWorks.get(i).createdate);
        holder.mTextDownvoteCount.setText(currentWorks.get(i).getDownvoteCount() + "");
        holder.mTextUpvoteCount.setText(currentWorks.get(i).getUpvoteCount() + "");
        holder.item = currentWorks.get(i);
        holder.mTextReadSentence.setText(getSentence(currentWorks.get(i)));
        holder.mTextLabelStub.setLabelTextSize(26);
        holder.mTextLabelStub.setLabelBackgroundColor(R.color.colorPrimary);
        holder.mTextLabelStub.setLabelText(holder.item.idindex + "");


        if (holder.item.shuoshuoType == 4) {
            holder.mTextLabelStub.setLabelBackgroundColor(Color.parseColor("#FBA474"));
            holder.mTextLabelStub.setLabelText("播音");
        } else {
            holder.mTextLabelStub.setLabelBackgroundColor(R.color.colorPrimary);
            holder.mTextLabelStub.setLabelText("跟读");
        }

        if (animationDrawable != null) {

            if (mPlayer.isPlaying()) {

//                holder.animationDrawable.stop();
            } else {

                if (animationDrawable.isRunning()) {

                    animationDrawable.stop();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return currentWorks.size();
    }

    public void setData(List<SpeakRankWork> list) {
        speaks = list;
        for (int i = 0; i < speaks.size(); i++) {
//            if (speaks.get(i).idindex.split("000")[0].equals(para)||speaks.get(i).shuoshuoType==4) {
            currentWorks.add(speaks.get(i));
//            } else {
//
//            }
        }
        Collections.sort(currentWorks);
    }

    private String getSentence(SpeakRankWork speakRankWork) {
        NewTypeTextAOp helper = new NewTypeTextAOp(context);

        if ((speakRankWork.idindex + "").contains("000")) {
            String a = speakRankWork.idindex.split("000")[0];
            String b = speakRankWork.idindex.split("000")[1];
            if (speakRankWork.paraid == 1) {
                return helper.getSentence(speakRankWork.voaId, a, b, "A");
            } else if (speakRankWork.paraid == 2) {
                return helper.getSentence(speakRankWork.voaId, a, b, "B");
            } else if (speakRankWork.paraid == 3) {
                return helper.getSentence(speakRankWork.voaId, a, b, "C");
            }
            return helper.getSentence(speakRankWork.voaId, a, b, "C");
        }
        return "";
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mImageUserHead;
        TextView mTextUsername;
        TextView mTextTime;
        LinearLayout mLinearNameContainer;
        LabelTextView mTextLabelStub;
        ImageView mImageBody;
        TextView mTextReadSentence;
        LinearLayout mLinearAudioCommentContainer;
        TextView mTextScore;
        ImageView mImageShare;
        ImageView mImageUpvote;
        TextView mTextUpvoteCount;
        ImageView mImageDownvote;
        TextView mTextDownvoteCount;

        Disposable disposable;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageUpvote = itemView.findViewById(R.id.image_upvote);
            mTextUpvoteCount = itemView.findViewById(R.id.text_upvote_count);
            mImageDownvote = itemView.findViewById(R.id.image_downvote);
            mTextDownvoteCount = itemView.findViewById(R.id.text_downvote_count);


            mTextReadSentence = itemView.findViewById(R.id.text_read_sentence);
            mLinearAudioCommentContainer = itemView.findViewById(R.id.linear_audio_comment_container);
            mTextScore = itemView.findViewById(R.id.text_score);
            mImageShare = itemView.findViewById(R.id.image_share);


            mImageUserHead = itemView.findViewById(R.id.image_user_head);
            mTextUsername = itemView.findViewById(R.id.text_username);
            mTextTime = itemView.findViewById(R.id.text_time);
            mLinearNameContainer = itemView.findViewById(R.id.linear_name_container);
            mTextLabelStub = itemView.findViewById(R.id.text_label_stub);
            mImageBody = itemView.findViewById(R.id.image_body);

            mImageBody.setOnClickListener(view -> onMImageBodyClicked());

            mTextReadSentence.setOnClickListener(view -> onMTextReadSentenceClicked());
            mLinearAudioCommentContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onMLinearAudioCommentContainerClicked();
                }
            });
            mImageUpvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    onMImageUpvoteClicked();
                    if (AccountManager.Instace(MyApplication.getInstance()).checkUserLogin()) {

                        dealZan();
                    } else {

                        Toast.makeText(MyApplication.getInstance(), "请登录", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new LoginEvent());
                    }
                }
            });
            mImageDownvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onMImageDownvoteClicked();
                }
            });
        }

        SpeakRankWork item;


        public void onMImageBodyClicked() {

            if (animationDrawable != null && animationDrawable.isRunning()) {

                animationDrawable.stop();
            }

            animationDrawable = (AnimationDrawable) mImageBody.getBackground();


            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                animationDrawable.stop();
            } else {
                playUrl(item.getShuoShuoUrl());
                animationDrawable.start();
            }
        }

        public void onMTextReadSentenceClicked() {
        }

        public void onMLinearAudioCommentContainerClicked() {
        }

        public void onMTextScoreClicked() {
        }

        public void onMImageShareClicked() {
        }


        @SuppressLint("CheckResult")
        public void dealZan() {

            disposable = dataStore
                    .data()
                    .map(new Function<Preferences, Boolean>() {
                        @Override
                        public Boolean apply(Preferences preferences) throws Exception {

                            Preferences.Key<Boolean> EXAMPLE_COUNTER = PreferencesKeys.booleanKey("IS_ZAN" + item.id);

                            boolean isZan;
                            if (preferences.contains(EXAMPLE_COUNTER)) {

                                isZan = preferences.get(EXAMPLE_COUNTER);
                            } else {

                                isZan = false;
                            }
                            return isZan;
                        }
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {

                            disposable.dispose();
                            if (aBoolean) {

                                Toast.makeText(MyApplication.getInstance(), "已经点过赞了", Toast.LENGTH_SHORT).show();
                            } else {

                                onMImageUpvoteClicked();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                            throwable.printStackTrace();
                        }
                    });
        }

        public void onMImageUpvoteClicked() {

            Call<SendGoodResponse> call = AbilityTestRequestFactory.getPublishApi().sendGood(item.id, "61001");
            call.enqueue(new Callback<SendGoodResponse>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<SendGoodResponse> call, Response<SendGoodResponse> response) {
                    if (response.body().getResultCode().equals("001")) {
                        ToastUtil.showToast(context, "点赞成功");
                        mTextUpvoteCount.setText(String.valueOf(item.getUpvoteCount() + 1));

                        Single<Preferences> updateResult = dataStore.updateDataAsync(prefsIn -> {


                            Preferences.Key<Boolean> EXAMPLE_COUNTER = PreferencesKeys.booleanKey("IS_ZAN" + item.id);

                            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
                            mutablePreferences.set(EXAMPLE_COUNTER, true);
                            return Single.just(mutablePreferences);
                        });
                    }
                }

                @Override
                public void onFailure(Call<SendGoodResponse> call, Throwable t) {

                }
            });
        }

        public void onMImageDownvoteClicked() {
            Call<SendGoodResponse> call = AbilityTestRequestFactory.getPublishApi().sendGood(item.id, "61002");
            call.enqueue(new Callback<SendGoodResponse>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<SendGoodResponse> call, Response<SendGoodResponse> response) {
                    if (response.body().getResultCode().equals("001")) {
                        mTextDownvoteCount.setText(String.valueOf(item.getDownvoteCount() + 1));

                    }
                }

                @Override
                public void onFailure(Call<SendGoodResponse> call, Throwable t) {

                }
            });
        }


    }

    private void playUrl(String url) {
        Timber.tag("bible").d(url);

        mPlayer.reset();
        try {
            Timber.tag("bible").d(url);
            mPlayer.setDataSource(url.replace("iyuba.com", "iyuba.cn"));
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void releasePlayer() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }
}
