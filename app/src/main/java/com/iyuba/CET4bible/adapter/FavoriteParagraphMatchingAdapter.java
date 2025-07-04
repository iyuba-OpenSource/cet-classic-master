package com.iyuba.CET4bible.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.activity.ParagraphMatchingActivity;
import com.iyuba.CET4bible.sqlite.mode.ParagraphMatchingBean;
import com.iyuba.CET4bible.widget.CustomBgView;
import com.iyuba.base.BaseRecyclerViewAdapter;
import com.youdao.sdk.common.YouDaoAd;
import com.youdao.sdk.nativeads.ListVideoAdRenderer;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.ViewBinder;
import com.youdao.sdk.video.VideoStrategy;

import java.net.URLDecoder;
import java.util.List;

/**
 * FavoriteParagraphMatchingAdapter
 *
 * @author wayne
 * @date 2017/12/12
 */
public class FavoriteParagraphMatchingAdapter extends BaseRecyclerViewAdapter {
    private final static int[] colorful = new int[]{0xffe14438, 0xff826cab,
            0xff62aa46, 0xffe38f2b, 0xff4fbdf0};
    private final static int[] drawables = new int[]{
            R.drawable.icon1,
            R.drawable.icon2,
            R.drawable.icon3,
            R.drawable.icon4,
            R.drawable.icon5,
            R.drawable.icon6};
    private final ListVideoAdRenderer youdaoAdRenders;

    private List mList;
    private Boolean isHome;

    public FavoriteParagraphMatchingAdapter(Context mContext, List list, Boolean isHome) {
        super(mContext);
        this.mList = list;
        this.isHome = isHome;


        final VideoStrategy videoStrategy = YouDaoAd.getYouDaoOptions()
                .getDefaultVideoStrategy();
        videoStrategy.setPlayVoice(true);
        videoStrategy.setVisiblePlay(true);
        //根据需要设置视频策略
        YouDaoAd.getYouDaoOptions().setVideoStrategy(videoStrategy);
        youdaoAdRenders = new ListVideoAdRenderer(
                new ViewBinder.Builder(R.layout.youdao_video_ad_item_small)
                        .videoId(R.id.mediaView).titleId(R.id.native_title)
                        .textId(R.id.native_content)
                        .build());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new VideoHolder(youdaoAdRenders.createAdView(mContext, parent));
        }
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_faragraph_matching, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if (getItemViewType(position) == 1) {
            final NativeResponse response = (NativeResponse) mList.get(position);
            response.recordImpression(holder.itemView);
            youdaoAdRenders.renderAdView(holder.itemView, response);
            VideoHolder h = (VideoHolder) holder;
            h.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    response.handleClick(v);
                }
            });
            return;
        }

        final Holder curViewHolder = (Holder) holder;

        if (mList.get(position) instanceof NativeResponse) {
            final NativeResponse response = (NativeResponse) mList.get(position);
            response.recordImpression(curViewHolder.itemView);
            curViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    response.handleClick(curViewHolder.itemView);
                }
            });
            Glide.with(mContext).load(response.getMainImageUrl())
                    .error(com.iyuba.biblelib.R.drawable.nearby_no_icon2)
                    .placeholder(com.iyuba.biblelib.R.drawable.nearby_no_icon2)
                    .into(curViewHolder.ivAd);
            curViewHolder.bg_item_reading.setVisibility(View.INVISIBLE);

            curViewHolder.title.setText(response.getTitle() + "（推广）");
            curViewHolder.subTitle.setText("");
            return;
        }
        final ParagraphMatchingBean bean = (ParagraphMatchingBean) mList.get(position);
        curViewHolder.bg_item_reading.setVisibility(View.VISIBLE);
        curViewHolder.bg_item_reading.setBg(drawables[position % 6]);
        curViewHolder.title.setText(bean.title);
        try {

            if (bean.original.contains("+")) {

                curViewHolder.subTitle.setText(bean.original.substring(0, bean.original.indexOf("+")));
            } else {

                String originalStr = URLDecoder.decode(bean.original, "utf-8");
                curViewHolder.subTitle.setText(originalStr.substring(0, originalStr.indexOf("+")));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        curViewHolder.append.setBackgroundColor(colorful[position % colorful.length]);


        curViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ParagraphMatchingActivity.class);
                intent.putExtra("data", bean);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        CustomBgView bg_item_reading;
        TextView title, subTitle;
        View append;
        ImageView ivAd;

        public Holder(View itemView) {
            super(itemView);
            bg_item_reading = itemView.findViewById(R.id.bg_item_reading);
            title = itemView.findViewById(R.id.reading_title);
            subTitle = itemView.findViewById(R.id.tv_subtitle);
            append = itemView.findViewById(R.id.append);
            ivAd = itemView.findViewById(R.id.iv_ad);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position) instanceof NativeResponse) {
            NativeResponse nativeResponse = (NativeResponse) mList.get(position);
            if (nativeResponse.getVideoAd() != null) {
                return 1;
            }
        }
        return 0;
    }

    class VideoHolder extends RecyclerView.ViewHolder {
        View view;

        public VideoHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.ll);
        }
    }
}
