package com.iyuba.core.discover.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.biblelib.R;
import com.iyuba.configation.Constant;
import com.iyuba.core.sqlite.mode.Word;
import com.iyuba.core.util.TextAttr;
import com.iyuba.core.widget.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 单词列表适配器
 *
 * @author 陈彤
 * @version 1.0
 */

public class WordSearchAdapter extends BaseAdapter {
    public boolean modeDelete = false;
    private Context mContext;
    private List<Word> mList;

    private Callback callback;

    public WordSearchAdapter(Context context) {
        mContext = context;
    }

    public WordSearchAdapter(Context context, ArrayList<Word> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {

        if (mList == null) {

            return 0;
        } else {

            return mList.size();
        }
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public Object getItem(int position) {

        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    public void setData(ArrayList<Word> list) {
        mList = list;
    }


    public List<Word> getmList() {
        return mList;
    }

    public void setmList(ArrayList<Word> mList) {
        this.mList = mList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.item_word, null);
            viewHolder = new ViewHolder();
            viewHolder.key = convertView.findViewById(R.id.word_key);
            viewHolder.pron = convertView.findViewById(R.id.word_pron);
            viewHolder.key.setTextColor(Color.BLACK);
            viewHolder.def = convertView.findViewById(R.id.word_def);

            viewHolder.deleteBox = convertView.findViewById(R.id.checkBox_isDelete);
            viewHolder.speaker = convertView.findViewById(R.id.word_speaker);
            viewHolder.word_tv_delete = convertView.findViewById(R.id.word_tv_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Word word = mList.get(position);
        viewHolder.setItem(word);
        return convertView;
    }

    public class ViewHolder {

        public TextView def;
        TextView key, pron;
        ImageView deleteBox;
        ImageView speaker;
        TextView word_tv_delete;

        Word word;

        public void setItem(Word word) {

            this.word = word;
            if (modeDelete) {
                deleteBox.setVisibility(View.VISIBLE);
            } else {
                deleteBox.setVisibility(View.GONE);
            }
            if (word.isDelete) {
                deleteBox.setImageResource(R.drawable.check_box_checked);
            } else {
                deleteBox.setImageResource(R.drawable.check_box);
            }
            key.setText(word.key);
            if (word.pron != null) {
                StringBuffer sb = new StringBuffer();
                sb.append('[').append(word.pron).append(']');
                pron.setText(TextAttr.decode(sb.toString()));
            }
            def.setText(word.def.replaceAll("\n", ""));
            if (Constant.APP_CONSTANT.isEnglish()) {
                if (word.audioUrl != null && word.audioUrl.length() != 0) {
                    speaker.setVisibility(View.VISIBLE);
                } else {
                    speaker.setVisibility(View.INVISIBLE);
                }
            } else {
                speaker.setVisibility(View.GONE);
            }
            speaker.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    Player player = new Player(mContext, null);
                    String url = word.audioUrl;
                    player.playUrl(url);
                }
            });
            word_tv_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (callback != null) {

                        callback.delete(word);
                    }
                }
            });
        }
    }

    public interface Callback {

        void delete(Word word);
    }

}
