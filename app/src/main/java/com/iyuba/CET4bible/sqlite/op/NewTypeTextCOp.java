package com.iyuba.CET4bible.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.CET4bible.sqlite.db.DatabaseService;
import com.iyuba.configation.Constant;
import com.iyuba.core.sqlite.mode.test.CetText;

import java.util.ArrayList;

public class NewTypeTextCOp extends DatabaseService {

    public static final String TABLE_NAME = "newtype_textc" + Constant.APP_CONSTANT.TYPE();
    public static final String TESTTIME = "TestTime";
    public static final String NUMBER = "Number";
    public static final String NUMBERINDEX = "NumberIndex";
    public static final String TIMING = "Timing";
    public static final String SENTENCE = "Sentence";
    public static final String SOUND = "Sound";
    public static final String SEX = "sex";
    public static final String GOOD = "good";
    public static final String BAD = "bad";
    public static final String SCORE = "score";
    public static final String URL = "record_url";

    public NewTypeTextCOp(Context context) {
        super(context);
    }


    /**
     * 获取number
     *
     * @param year
     * @param sound
     * @return
     */
    public int getNumber(String year, String sound) {

        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select * from " + getTableName() + " where TestTime = ? and Sound = ? ",
                new String[]{year, sound});

        CetText text = null;
        if (cursor.moveToNext()) {

            text = fillIn(cursor);
        }
        cursor.close();
        closeDatabase(null);

        if (text != null) {

            return Integer.parseInt(text.id);
        } else {

            return -1;
        }
    }

    public ArrayList<CetText> selectTextData(String year, String number) {
        ArrayList<CetText> texts = new ArrayList<CetText>();

        String sqlStr = "select * from " + getTableName() + " where Number = ? and TestTime = ? order by Number ";
        Cursor cursor = importDatabase.openDatabase().rawQuery(sqlStr, new String[]{number, year});
        while (cursor.moveToNext()) {
            texts.add(fillIn(cursor));
        }
        closeDatabase(null);
        if (texts.size() != 0) {
            cursor.close();
            return texts;
        }
        cursor.close();
        return null;
    }

    public ArrayList<CetText> selectData(String year) {
        ArrayList<CetText> texts = new ArrayList<CetText>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + TESTTIME + "," + NUMBER + "," + NUMBERINDEX + ","
                        + TIMING + "," + SENTENCE + "," + SOUND + "," + SEX
                        + "," + GOOD + "," + BAD + "," + SCORE + "," + URL
                        + " from "
                        + getTableName() + " where " + TESTTIME + "= ?",
                new String[]{year});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            texts.add(fillIn(cursor));
        }
        closeDatabase(null);
        if (texts.size() != 0) {
            return texts;
        }
        return null;
    }

    public ArrayList<CetText> selectData(String year, String id) {
        ArrayList<CetText> texts = new ArrayList<CetText>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select " + TESTTIME + "," + NUMBER + "," + NUMBERINDEX + ","
                        + TIMING + "," + SENTENCE + "," + SOUND + "," + SEX
                        + "," + GOOD + "," + BAD + "," + SCORE + "," + URL

                        + " from "
                        + getTableName() + " where " + TESTTIME + "= ? and "
                        + NUMBER + "= ?", new String[]{year, id});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            texts.add(fillIn(cursor));
        }
        closeDatabase(null);
        if (texts.size() != 0) {
            return texts;
        }
        cursor.close();
        return null;
    }

    private String getTableName() {

        return "newtype_textc" + Constant.APP_CONSTANT.TYPE();
    }

    private CetText fillIn(Cursor cursor) {
        CetText text = new CetText();
        text.testTime = cursor.getString(cursor.getColumnIndexOrThrow("TestTime"));
        text.id = cursor.getString(cursor.getColumnIndexOrThrow("Number"));
        text.index = cursor.getString(cursor.getColumnIndexOrThrow("NumberIndex"));
        text.time = cursor.getString(cursor.getColumnIndexOrThrow("Timing"));
        text.sentence = cursor.getString(cursor.getColumnIndexOrThrow("Sentence"));
        text.sex = cursor.getString(cursor.getColumnIndexOrThrow("Sex"));
        text.good = cursor.getString(cursor.getColumnIndexOrThrow("good"));
        text.bad = cursor.getString(cursor.getColumnIndexOrThrow("bad"));
        text.score = cursor.getString(cursor.getColumnIndexOrThrow("score"));
        text.record_url = cursor.getString(cursor.getColumnIndexOrThrow("record_url"));
        return text;
    }


}
