package com.iyuba.CET4bible.viewpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.CET4bible.MyApplication;
import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.activity.MainActivity;
import com.iyuba.CET4bible.adapter.FavoriteParagraphMatchingAdapter;
import com.iyuba.CET4bible.sqlite.op.ParagraphMatchingOp;
import com.iyuba.CET4bible.util.AdInfoFlowUtil;
import com.iyuba.base.BaseFragment;
import com.iyuba.base.util.SimpleLineDividerDecoration;
import com.iyuba.core.manager.AccountManager;
import com.iyuba.core.manager.DataManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择填空列表
 */
public class ParagraphMatchingFragment extends BaseFragment {
    RecyclerView recyclerView;
    FavoriteParagraphMatchingAdapter adapter;
    List mList;
    AdInfoFlowUtil adInfoFlowUtil;


    public ParagraphMatchingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fillinblank, container, false);
        if (containerVp!=null){
            containerVp.setObjectForPosition(view, 1);
        }
        return view ;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new SimpleLineDividerDecoration(mContext).setColor(R.color.darkgray));

        mList = new ArrayList<>();

        adapter = new FavoriteParagraphMatchingAdapter(mContext, mList, getActivity() instanceof MainActivity);
        recyclerView.setAdapter(adapter);

        ParagraphMatchingOp op = new ParagraphMatchingOp(MyApplication.getInstance());
        mList.addAll(op.selectData());
        adapter.notifyDataSetChanged();


        adInfoFlowUtil = new AdInfoFlowUtil(mContext, AccountManager.isVip(), new AdInfoFlowUtil.Callback() {
            @Override
            public void onADLoad(List ads) {
                AdInfoFlowUtil.insertAD(mList, ads, adInfoFlowUtil);
                adapter.notifyDataSetChanged();
            }
        });
        adInfoFlowUtil.setSupportVideo(true).setAdRequestSize(5).refreshAd();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adInfoFlowUtil.destroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser){
            DataManager.Instance().currentType = 4;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
