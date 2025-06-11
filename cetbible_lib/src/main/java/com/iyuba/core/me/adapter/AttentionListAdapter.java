package com.iyuba.core.me.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.biblelib.R;
import com.iyuba.core.me.sqlite.mode.Attention231219;
import com.iyuba.core.thread.GitHubImageLoader;

import java.util.ArrayList;

/**
 * 关注适配器
 *
 * @author 陈彤
 * @version 1.0
 */
public class AttentionListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Attention231219.DataDTO> mList = new ArrayList<>();
    private ViewHolder viewHolder;

    /**
     * @param mContext
     */
    public AttentionListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * @param mContext
     * @param mList
     */
    public AttentionListAdapter(Context mContext, ArrayList<Attention231219.DataDTO> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {

        return mList.size();
    }

    @Override
    public Object getItem(int position) {

        if (position == 0) {
            return 0;
        } else {
            return mList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    public void setData(ArrayList<Attention231219.DataDTO> List) {
        this.mList = List;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Attention231219.DataDTO curAttention = mList.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_fans, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.username = convertView
                    .findViewById(R.id.username);
            viewHolder.message = convertView
                    .findViewById(R.id.content);
            viewHolder.userImageView = convertView
                    .findViewById(R.id.pic);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (TextUtils.isEmpty(curAttention.getFusername()) || "null".equals(curAttention.getFusername())) {
            curAttention.setFusername(curAttention.getFollowuid());
        }

        viewHolder.username.setText(curAttention.getFusername());
        viewHolder.message.setText(curAttention.getDoing());
        GitHubImageLoader.Instace(mContext).setCirclePic(curAttention.getFollowuid(),
                viewHolder.userImageView);
        return convertView;
    }

    public class ViewHolder {
        TextView username;
        TextView message;// 当前状态
        ImageView userImageView;
    }
}
