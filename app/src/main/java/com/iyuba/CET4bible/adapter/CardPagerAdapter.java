package com.iyuba.CET4bible.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.iyuba.CET4bible.R;
import com.iyuba.core.eventbus.LoginEvent;
import com.iyuba.CET4bible.event.MainMicroClassEvent;
import com.iyuba.CET4bible.sqlite.mode.ImageData;
import com.iyuba.configation.Constant;
import com.iyuba.core.activity.Web;
import com.iyuba.core.manager.AccountManager;
import com.iyuba.core.me.activity.VipCenterActivity;
import com.iyuba.core.util.MD5;
import com.iyuba.core.util.TouristUtil;
import com.iyuba.imooclib.ui.content.ContentActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<LinearLayout> mViews;
    private List<ImageData> mData;

    public CardPagerAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(ImageData data) {
        mViews.add(null);
        mData.add(data);
    }

    public void clear() {
        mViews.clear();
        mData.clear();
    }


    @Override
    public int getCount() {
//        return mData.size();
        return mData.size() <= 1 ? mData.size() : Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter, container, false);
        container.addView(view);
        if (mData.size()== 0){
            return view;
        }
        bind(mData.get(position % mData.size()), view);

        LinearLayout cardView = view.findViewById(R.id.cardView);

        mViews.set(position % mData.size(), cardView);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position % mData.size(), null);
    }

    private void bind(final ImageData data, View view) {
        ImageView iv_card = view.findViewById(R.id.iv_card);
        Glide.with(mContext).load("http://dev.iyuba.cn/" + data.pic)
                .placeholder(com.iyuba.biblelib.R.drawable.nearby_no_icon2).dontAnimate().into(iv_card);

        iv_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String name = data.name;
                String desc = data.desc1;
                if (name.equals("1")) {
                    VipCenterActivity.start(mContext, true);
                } else if (name.equals("2")) {
                    if (AccountManager.Instace(mContext).checkUserLogin()) {
                        if (TouristUtil.isTourist()) {

                            EventBus.getDefault().post(new LoginEvent());
                            ToastUtils.showShort( "请注册正式账户后，再进行领取");
                            return;
                        }

                        intent.setClass(mContext, Web.class);
                        intent.putExtra("url", "http://m.iyuba.cn/mall/addressManage.jsp?"
                                + "&uid=" + AccountManager.Instace(mContext).userId
                                + "&sign=" + MD5.getMD5ofStr("iyuba" + AccountManager.Instace(mContext).userId + "camstory")
                                + "&username=" + AccountManager.Instace(mContext).getUserName()
                                + "&appid="
                                + Constant.APPID + "&presentId=" + Constant.APP_CONSTANT.presentId());

                        intent.putExtra("title", "免费送英语"+Constant.APP_CONSTANT.TYPE()+"级真题书");
                        mContext.startActivity(intent);
                    } else {

                        EventBus.getDefault().post(new LoginEvent());
                    }
                } else if (name.equals("3")) {
                    mContext.startActivity(ContentActivity.buildIntent(mContext, Integer.parseInt(data.course_id),Constant.IMOOC_TYPE_DESC));
                } else if (name.equals("4")) {
                    intent.setClass(mContext, Web.class);
                    intent.putExtra("url", desc);
                    mContext.startActivity(intent);

                } else if (name.equals("6")) {
                    if (!Constant.APP_CONSTANT.isEnglish()) {
                        EventBus.getDefault().post(new MainMicroClassEvent("6"));
                    }
                }
            }
        });
    }

}
