package com.iyuba.CET4bible.listening;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.manager.ListenDataManager;
import com.iyuba.abilitytest.network.AbilityTestRequestFactory;
import com.iyuba.abilitytest.network.GetUserWorks;
import com.iyuba.abilitytest.network.SpeakRankWork;
import com.iyuba.base.BaseActivity;
import com.iyuba.configation.Constant;
import com.iyuba.core.util.MD5;
import com.jaeger.library.StatusBarUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 排行榜 - 排行榜详情
 */
public class DubbingPlayListActivity extends BaseActivity {


    RecyclerView mRecycler;

    SwipeRefreshLayout swipeRefreshLayout;

    ReadItemAdapter readItemAdapter;

    int userId;
    String userName;
    int topicId;
    String other;
    String headUrl;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);


    public static Bundle buildArguments(int userid, String username, int voaid, String other, String topicId, String type, String url) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId", userid);
        bundle.putString("userName", username);
        bundle.putString("userid", other);
        bundle.putString("userHeader", url);
        bundle.putString("topicId", topicId);
        bundle.putString("type", type);

        return bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dubbing_list_activity);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);

        initView();
        getIntents();
        getData();
    }

    private void initView() {


        mRecycler = findViewById(R.id.recycler);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        ImageView buttonTitleBack = findViewById(R.id.buttonTitleBack);
        buttonTitleBack.setOnClickListener(view -> {

            onViewClicked();
        });
    }


    private void getIntents() {
        userId = getIntent().getBundleExtra("ARGUMENTS").getInt("userId");
        userName = getIntent().getBundleExtra("ARGUMENTS").getString("userName");
        topicId = Integer.parseInt(getIntent().getBundleExtra("ARGUMENTS").getString("topicId"));
        other = getIntent().getBundleExtra("ARGUMENTS").getString("other");
        headUrl = getIntent().getBundleExtra("ARGUMENTS").getString("userHeader");
    }

    private void getData() {

        String sign = buildWorkSign(userId);
        Map<String, String> extra = new HashMap<>();
        if (topicId != 0) {
            extra.put("topicId", String.valueOf(topicId));
        }
        Call<GetUserWorks> call = AbilityTestRequestFactory.getPublishApi().getUserWorks(userId, Constant.mListen, "2,4", sign, extra);
        call.enqueue(new Callback<GetUserWorks>() {
            @Override
            public void onResponse(Call<GetUserWorks> call, Response<GetUserWorks> response) {
                if (response.body().data != null) {
                    setAdapter(response.body().data);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<GetUserWorks> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getData();
        }
    };

    public void setAdapter(List<SpeakRankWork> list) {
        readItemAdapter = new ReadItemAdapter(headUrl, userName, ListenDataManager.Instance().para);
        readItemAdapter.setData(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(linearLayoutManager);
        mRecycler.setAdapter(readItemAdapter);
    }

    public void onViewClicked() {
        finish();
    }

    private String buildWorkSign(int uid) {
        StringBuilder sb = new StringBuilder();
        sb.append(uid).append("getWorksByUserId").append(SDF.format(new Date()));
        return MD5.getMD5ofStr(sb.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (readItemAdapter != null) {

            readItemAdapter.closeDataStore();
            readItemAdapter.releasePlayer();
        }
    }
}
