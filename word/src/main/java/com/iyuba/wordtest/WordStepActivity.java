package com.iyuba.wordtest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.iyuba.wordtest.adapter.StepAdapter;
import com.iyuba.wordtest.db.CetDataBase;
import com.iyuba.wordtest.entity.CetRootWord;
import com.iyuba.wordtest.manager.WordConfigManager;
import com.iyuba.wordtest.widget.MyGridView;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 单词闯关 关卡列表
 */
public class WordStepActivity extends AppCompatActivity {
    MyGridView gridview;
    TextView words_all;
    Toolbar toolbar;
    StepAdapter adapter;
    CetRootWord word;
    CetDataBase db;
    int dbSize;
    int wpd;
    int step;

    List<CetRootWord> wordList;
    private int checkedItem;
    public static int WORD_COUNT = 30;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_step);
        initView();
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        initToolBar();
    }

    private void initView() {


        gridview = findViewById(R.id.gridview);
        words_all = findViewById(R.id.all_words);
        toolbar = findViewById(R.id.toolbar);
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    Handler mHanler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {

            dialog.dismiss();
            WordConfigManager.Instance(getApplicationContext()).putBoolean("dbChange", false);
            step = WordConfigManager.Instance(getApplicationContext()).loadInt("stage", 1);
            words_all.setText(String.format("单词总数:%s   闯关单词数:%s", db.getCetRootWordDao().getAllRootWord().size()
                    , db.getCetRootWordDao().getWordsByStage(step).size()));
            adapter = new StepAdapter(3, step, dbSize / wpd + step);
            gridview.setAdapter(adapter);
            return false;
        }
    });


    //    @OnClick(R2.id.words_all)
    public void startAllWords(View view) {
        WordListActivity.startIntnent(this, 0, true);
    }


    //    @OnClick(R2.id.set)
    public void set(View view) {
        showAlert();
    }

    private void showAlert() {
        final String[] wpd = {"30", "50", "70", "100"};
        final String select = String.valueOf(WordConfigManager.Instance(this).loadInt("wpd", 30));
        for (int i = 0; i < wpd.length; i++) {
            if (wpd[i].equals(select)) {
                checkedItem = i;
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("请选择每关单词数")
                .setSingleChoiceItems(wpd, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WORD_COUNT = Integer.parseInt(wpd[which]);
                    }
                })
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WordConfigManager.Instance(getApplicationContext()).putInt("wpd", WORD_COUNT);
                        initData();
                        dialog.dismiss();
                    }
                }).create().show();
        WordConfigManager.Instance(this).putBoolean("isWordNumberSelected", true);
        WordConfigManager.Instance(this).putBoolean("dbChange", true);

    }

    private void initData() {
        step = WordConfigManager.Instance(this).loadInt("stage", 1);
        db = CetDataBase.getInstance(this);
        if (db.getCetRootWordDao().getWordsByStage(0).size() > 0) {
            wordList = db.getCetRootWordDao().getAllRootWord();
        } else {
            wordList = db.getCetRootWordDao().getAllRootWord(step);
        }
        dbSize = wordList.size();
        dialog.setMessage("正在加载单词");
        dialog.show();
        wpd = WordConfigManager.Instance(this).loadInt("wpd", 30);
        if (WordConfigManager.Instance(this).loadBoolean("dbChange", true)) {
            new Thread(runnable).start();
        } else {
            mHanler.sendEmptyMessage(1);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            List<CetRootWord> list = new ArrayList<>();
            for (int i = 0; i < dbSize; i++) {
                word = wordList.get(i);
                word.stage = i / wpd + step;
                list.add(word);
            }
            db.getCetRootWordDao().updateWordSetStage(list);
            mHanler.sendEmptyMessage(1);
        }
    };
}
