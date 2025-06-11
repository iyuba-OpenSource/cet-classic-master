package wordtest;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.iyuba.CET4bible.R;
import com.iyuba.base.BaseActivity;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.widget.MyGridView;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import adapter.StepAdapter;
import newDB.CetDataBase;
import newDB.CetRootWord;

public class WordStepActivity extends BaseActivity {


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


    private void initView() {

        gridview = findViewById(R.id.gridview);
        words_all = findViewById(R.id.all_words);
        toolbar = findViewById(R.id.toolbar);
        ImageView set = findViewById(R.id.set);

        words_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAllWords();
            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_step_main);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);

        initView();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


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

    @SuppressLint("HandlerLeak")
    Handler mHanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            ConfigManager.Instance().putBoolean("dbChange", false);
            step = ConfigManager.Instance().loadInt("stage", 1);
            words_all.setText(String.format("单词总数:%s   闯关单词数:%s", db.getCetRootWordDao().getAllRootWord().size()
                    , db.getCetRootWordDao().getWordsByStage(step).size()));
            adapter = new StepAdapter(3, step, dbSize / wpd + step);
            gridview.setAdapter(adapter);
            super.handleMessage(msg);
        }
    };

    public void startAllWords() {
        WordListActivity.startIntnent(this, 0, true);
    }


    public void set() {
        showAlert();
    }

    private void showAlert() {
        final String[] wpd = {"30", "50", "70", "100"};
        final String select = String.valueOf(ConfigManager.Instance().loadInt("wpd", 30));
        for (int i = 0; i < wpd.length; i++) {
            if (wpd[i].equals(select)) {
                checkedItem = i;
            }
        }
        new AlertDialog.Builder(mContext)
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
                        ConfigManager.Instance().putInt("wpd", WORD_COUNT);
                        initData();
                        dialog.dismiss();
                    }
                }).create().show();
        ConfigManager.Instance().putBoolean("isWordNumberSelected", true);
        ConfigManager.Instance().putBoolean("dbChange", true);

    }

    private void initData() {
        step = ConfigManager.Instance().loadInt("stage", 1);
        db = CetDataBase.getInstance(this);
        if (db.getCetRootWordDao().getWordsByStage(0).size() > 0) {
            wordList = db.getCetRootWordDao().getAllRootWord();
        } else {
            wordList = db.getCetRootWordDao().getAllRootWord(step);
        }
        dbSize = wordList.size();
        dialog.setMessage("正在加载单词");
        dialog.show();
        wpd = ConfigManager.Instance().loadInt("wpd", 30);
        if (ConfigManager.Instance().loadBoolean("dbChange", true)) {
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
