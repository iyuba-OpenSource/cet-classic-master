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
import com.iyuba.CET4bible.activity.FillInBlankActivity;
import com.iyuba.CET4bible.sqlite.mode.FillInBlankBean;
import com.iyuba.CET4bible.widget.CustomBgView;
import com.iyuba.base.BaseRecyclerViewAdapter;
import com.youdao.sdk.nativeads.NativeResponse;

import java.util.List;

/**
 * FavoriteAdapter
 *
 * @author wayne
 * @date 2017/12/12
 */
public class FavoriteFillInBlankAdapter extends BaseRecyclerViewAdapter<FavoriteFillInBlankAdapter.Holder> {
    private final static int[] colorful = new int[]{0xffe14438, 0xff826cab,
            0xff62aa46, 0xffe38f2b, 0xff4fbdf0};
    private final static int[] drawables = new int[]{
            R.drawable.icon1,
            R.drawable.icon2,
            R.drawable.icon3,
            R.drawable.icon4,
            R.drawable.icon5,
            R.drawable.icon6};
    private List mList;
    private Boolean isHome ;

    public FavoriteFillInBlankAdapter(Context mContext, List list , Boolean isHome) {
        super(mContext);
        this.mList = list;
        this.isHome = isHome;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_reading, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder curViewHolder, int position) {
        super.onBindViewHolder(curViewHolder, position);
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
            return;
        }

        final FillInBlankBean bean = (FillInBlankBean) mList.get(position);
        curViewHolder.bg_item_reading.setVisibility(View.VISIBLE);
//        String month = bean.year.substring(4, 6);
//        if (month.equals("12")) {
//            curViewHolder.bg_item_reading.setText(bean.year.substring(0, 4));
//            curViewHolder.bg_item_reading.setSubText("12月");
//        } else {
//            curViewHolder.bg_item_reading.setText(bean.year.substring(0, 4));
//            curViewHolder.bg_item_reading.setSubText("6月");
//        }
        curViewHolder.bg_item_reading.setBg(drawables[position%6]);

        curViewHolder.title.setText(bean.title);
        curViewHolder.append.setBackgroundColor(colorful[position % colorful.length]);


        curViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FillInBlankActivity.class);
                intent.putExtra("data", bean);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (isHome){
            return 4 ;
        }
        return mList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        CustomBgView bg_item_reading;
        TextView title;
        View append;
        ImageView ivAd;

        public Holder(View itemView) {
            super(itemView);
            bg_item_reading = itemView.findViewById(R.id.bg_item_reading);
            title = itemView.findViewById(R.id.reading_title);
            append = itemView.findViewById(R.id.append);
            ivAd = itemView.findViewById(R.id.iv_ad);
        }
    }
}
