package com.iyuba.CET4bible.sqlite.op;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.iyuba.CET4bible.sqlite.db.DatabaseService;
import com.iyuba.CET4bible.sqlite.mode.ParagraphMatchingBean;

import java.util.ArrayList;

/**
 * FillInBlankOp
 *
 * @author wayne
 * @date 2017/12/20
 */
public class ParagraphMatchingOp extends DatabaseService {
    public static final String TABLE_NAME = "paragraphmatching";
    public static final String YEAR = "year";
    public static final String INDEX = "_index";
    public static final String TITLE = "title";
    public static final String ORIGINAL = "original";
    public static final String TRANSLATION = "translation";
    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";
    public static final String EXPLANATION = "explanation";

    public ParagraphMatchingOp(Context context) {
        super(context);
    }

    @SuppressLint("Range")
    public synchronized ArrayList<ParagraphMatchingBean> selectData() {
        ArrayList<ParagraphMatchingBean> writes = new ArrayList<>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select * from paragraphmatching ORDER BY " + INDEX + " DESC",
                null);
        while (cursor.moveToNext()) {

            ParagraphMatchingBean bean = new ParagraphMatchingBean();
            bean.year = cursor.getString(cursor.getColumnIndex(YEAR));
            bean.index = cursor.getInt(cursor.getColumnIndex(INDEX));
            bean.title = cursor.getString(cursor.getColumnIndex(TITLE));
            bean.original = cursor.getString(cursor.getColumnIndex(ORIGINAL));
            bean.translation = cursor.getString(cursor.getColumnIndex(TRANSLATION));
            bean.question = cursor.getString(cursor.getColumnIndex(QUESTION));
            bean.answer = cursor.getString(cursor.getColumnIndex(ANSWER));
            bean.explanation = cursor.getString(cursor.getColumnIndex(EXPLANATION));
            Log.d("ParagraphMa", bean.toString());
            writes.add(bean);
        }
        cursor.close();
        closeDatabase(null);
        if (writes.size() != 0) {
            return writes;
        }
        return writes;
    }

    public ArrayList<ParagraphMatchingBean> selectData(String number) {
        ArrayList<ParagraphMatchingBean> writes = new ArrayList<>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select * from " + TABLE_NAME + " where " + YEAR
                        + "=? " + "ORDER BY " + INDEX + " DESC",
                new String[]{number});
        while (cursor.moveToNext()) {
            writes.add(fillIn(cursor));
        }
        cursor.close();
        closeDatabase(null);
        if (writes.size() != 0) {
            return writes;
        }
        return writes;
    }

    @SuppressLint("Range")
    private ParagraphMatchingBean fillIn(Cursor cursor) {

        ParagraphMatchingBean bean = new ParagraphMatchingBean();
        bean.year = cursor.getString(cursor.getColumnIndex(YEAR));
        bean.index = cursor.getInt(cursor.getColumnIndex(INDEX));
        bean.title = cursor.getString(cursor.getColumnIndex(TITLE));
        bean.original = cursor.getString(cursor.getColumnIndex(ORIGINAL));
        bean.translation = cursor.getString(cursor.getColumnIndex(TRANSLATION));
        bean.question = cursor.getString(cursor.getColumnIndex(QUESTION));
        bean.answer = cursor.getString(cursor.getColumnIndex(ANSWER));
        bean.explanation = cursor.getString(cursor.getColumnIndex(EXPLANATION));
        return bean;
    }
}
