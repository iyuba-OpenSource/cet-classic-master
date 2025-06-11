package wordtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.widget.RecyclerViewSideBar;
import com.iyuba.base.BaseActivity;
import com.iyuba.configation.ConfigManager;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

import adapter.SimpleWordListAdapter;
import newDB.CetDataBase;
import newDB.CetRootWord;

public class WordListActivity extends BaseActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerViewSideBar sidebar;
    TextView study;
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


    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        sidebar = findViewById(R.id.sidebar);
        study = findViewById(R.id.study);
        test = findViewById(R.id.test);

        study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onViewClicked(view);
            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onViewClicked(view);
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list_main);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        context = this;
        initView();

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

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.study) {
            Intent intent = new Intent(context, WordDetailActiivty.class);
            intent.putExtra("stage", ConfigManager.Instance().loadInt("stage", 1));
            startActivity(intent);
        } else if (id == R.id.test) {
            WordTestActivity.start(context, ConfigManager.Instance().loadInt("stage", 1));
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
        sidebar.setSelectedSideBarColor(com.iyuba.configation.R.color.app_color);
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
