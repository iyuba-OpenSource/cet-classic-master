package com.iyuba.CET4bible.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.CET4bible.BuildConfig;
import com.iyuba.CET4bible.sqlite.db.DatabaseService;
import com.iyuba.configation.Constant;
import com.iyuba.core.sqlite.mode.test.CetExplain;

import java.util.ArrayList;

public class NewTypeExplainOp extends DatabaseService {

    public static final String TABLE_NAME = "newtype_explain" + Constant.APP_CONSTANT.TYPE();
    public static final String TESTTIME = "testtime";
    public static final String NUMBER = "number";
    public static final String KEY = "keyss";
    public static final String EXPLAIN = "explains";
    public static final String KNOWLEDGE = "knowledge";

    public NewTypeExplainOp(Context context) {
        super(context);
    }

    public ArrayList<CetExplain> selectData(String year) {
        ArrayList<CetExplain> texts = new ArrayList<CetExplain>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + TESTTIME + "," + NUMBER + "," + KEY + "," + EXPLAIN
                        + "," + KNOWLEDGE + " from " + getTableName() + " where "
                        + TESTTIME + "= ?", new String[]{year});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            texts.add(fillIn(cursor));
        }
        closeDatabase(null,cursor);

        if (texts.size() != 0) {
            return texts;
        }
        return null;
    }

    private String getTableName() {
        return "newtype_explain" + Constant.APP_CONSTANT.TYPE();
    }

    private CetExplain fillIn(Cursor cursor) {
        CetExplain explain = new CetExplain();
        explain.id = cursor.getString(1);
        explain.keys = cursor.getString(2);
        explain.explain = cursor.getString(3);
        explain.knowledge = cursor.getString(4);
        return explain;
    }


}
