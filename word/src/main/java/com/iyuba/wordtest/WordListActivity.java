package com.iyuba.wordtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.wordtest.adapter.SimpleWordListAdapter;
import com.iyuba.wordtest.db.CetDataBase;
import com.iyuba.wordtest.entity.CetRootWord;
import com.iyuba.wordtest.widget.RecyclerViewSideBar;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

/**
 * 单词闯关 - 关卡 - 生词列表
 */
public class WordListActivity extends AppCompatActivity {
    //    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    //    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    //    @BindView(R2.id.sidebar)
    RecyclerViewSideBar sidebar;
    //    @BindView(R2.id.study)
    TextView study;
    //    @BindView(R2.id.test)
    TextView test;
    private boolean showSideBar;

    public static void startIntnent(Context mContext, int stage, boolean showSideBar) {
        Intent intent = new Intent(mContext, WordListActivity.class);
        intent.putExtra("stage", stage);
        intent.putExtra("showSideBar", showSideBar);
        mContext.startActivity(intent);
    }


    CetDataBase db;
    Context context;
    SimpleWordListAdapter adapter;
    List<CetRootWord> list;
    int stage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        intiView();
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        context = this;


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        stage = getIntent().getExtras().getInt("stage");
        showSideBar = getIntent().getExtras().getBoolean("showSideBar");
        db = CetDataBase.getInstance(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (stage == -1) {
            list = db.getCetRootWordDao().getWordsCollect();
        } else {
            list = db.getCetRootWordDao().getWordsByStage(stage);
        }
        adapter = new SimpleWordListAdapter(list, false);
        adapter.setShowOrder(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void intiView() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        sidebar = findViewById(R.id.sidebar);
        study = findViewById(R.id.study);
        test = findViewById(R.id.test);
    }

    //    @OnClick({R2.id.study, R2.id.test})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.study) {
            Intent intent = new Intent(context, WordDetailActiivty.class);
            intent.putExtra("stage", stage);
            startActivity(intent);
        } else if (id == R.id.test) {
            WordTestActivity.start(context, stage);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (stage == -1) {
            list = db.getCetRootWordDao().getWordsCollect();
            test.setVisibility(View.GONE);
            study.setVisibility(View.GONE);
            adapter = new SimpleWordListAdapter(list, false);
        } else if (stage == 0) {
            list = db.getCetRootWordDao().getAllRootWord();
            test.setVisibility(View.GONE);
            study.setVisibility(View.GONE);
            adapter = new SimpleWordListAdapter(list, true);
            adapter.setShowOrder(true);
        } else {
            list = db.getCetRootWordDao().getWordsByStage(stage);
            adapter = new SimpleWordListAdapter(list, true);
        }
        recyclerView.setAdapter(adapter);
//        sidebar.setFloatLetterTextView(mFloatLetterTv);
        sidebar.setSelectedSideBarColor(R.color.app_color);
        sidebar.setRecyclerView(recyclerView);
        if (!showSideBar) {
            sidebar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
