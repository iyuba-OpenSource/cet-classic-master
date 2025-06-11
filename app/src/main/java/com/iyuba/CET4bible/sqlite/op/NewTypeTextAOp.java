package com.iyuba.CET4bible.sqlite.op;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.CET4bible.sqlite.db.DatabaseService;
import com.iyuba.CET4bible.widget.subtitle.Subtitle;
import com.iyuba.abilitytest.network.SendEvaluateResponse;
import com.iyuba.configation.Constant;
import com.iyuba.core.sqlite.mode.test.CetText;

import java.util.ArrayList;
import java.util.List;

public class NewTypeTextAOp extends DatabaseService {

    public static final String TABLE_NAME = "newtype_texta" + Constant.APP_CONSTANT.TYPE();

    public static final String NEWTYPE_TABLE_TEXTA = "newtype_texta" + Constant.APP_CONSTANT.TYPE();
    public static final String NEWTYPE_TABLE_TEXTB = "newtype_textb" + Constant.APP_CONSTANT.TYPE();
    public static final String NEWTYPE_TABLE_TEXTC = "newtype_textc" + Constant.APP_CONSTANT.TYPE();

    public String NEWTYPE_TABLE_TEXTA_WORDS = "newtype_texta" + Constant.APP_CONSTANT.TYPE() + "_words";//评测单词详情
    public String NEWTYPE_TABLE_TEXTB_WORDS = "newtype_textb" + Constant.APP_CONSTANT.TYPE() + "_words";
    public String NEWTYPE_TABLE_TEXTC_WORDS = "newtype_textc" + Constant.APP_CONSTANT.TYPE() + "_words";

    public static final String TESTTIME = "TestTime";
    public static final String NUMBER = "Number";
    public static final String NUMBERINDEX = "NumberIndex";
    public static final String TIMING = "Timing";
    public static final String SENTENCE = "Sentence";
    public static final String SEX = "Sex";
    public static final String SOUND = "Sound";
    public static final String GOOD = "good";
    public static final String BAD = "bad";
    public static final String SCORE = "score";
    public static final String URL = "record_url";

    public NewTypeTextAOp(Context context) {
        super(context);
        /**
         * 由于以前的老代码没有维护数据库更新的代码，
         * 所以新加的表放在这里
         */
        String sql1 = "CREATE TABLE IF NOT EXISTS " + NEWTYPE_TABLE_TEXTA_WORDS + " (\n" +
                "  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "  \"TestTime\" integer(100),\n" +
                "  \"Number\" integer(100),\n" +
                "  \"NumberIndex\" integer(100),\n" +
                "  \"content\" text(100),\n" +
                "  \"delete\" text(100),\n" +
                "  \"insert\" text(100),\n" +
                "  \"pron\" text(100),\n" +
                "  \"pron2\" text(100),\n" +
                "  \"substitute_orgi\" text(100),\n" +
                "  \"substitute_user\" text(100),\n" +
                "  \"user_pron\" text(100),\n" +
                "  \"user_pron2\" text(100),\n" +
                "  \"index\" integer(50),\n" +
                "  \"score\" real(50)\n" +
                ")";
        String sql2 = "CREATE TABLE IF NOT EXISTS " + NEWTYPE_TABLE_TEXTB_WORDS + " (\n" +
                "  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "  \"TestTime\" integer(100),\n" +
                "  \"Number\" integer(100),\n" +
                "  \"NumberIndex\" integer(100),\n" +
                "  \"content\" text(100),\n" +
                "  \"delete\" text(100),\n" +
                "  \"insert\" text(100),\n" +
                "  \"pron\" text(100),\n" +
                "  \"pron2\" text(100),\n" +
                "  \"substitute_orgi\" text(100),\n" +
                "  \"substitute_user\" text(100),\n" +
                "  \"user_pron\" text(100),\n" +
                "  \"user_pron2\" text(100),\n" +
                "  \"index\" integer(50),\n" +
                "  \"score\" real(50)\n" +
                ")";
        String sql3 = "CREATE TABLE IF NOT EXISTS " + NEWTYPE_TABLE_TEXTC_WORDS + " (\n" +
                "  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "  \"TestTime\" integer(100),\n" +
                "  \"Number\" integer(100),\n" +
                "  \"NumberIndex\" integer(100),\n" +
                "  \"content\" text(100),\n" +
                "  \"delete\" text(100),\n" +
                "  \"insert\" text(100),\n" +
                "  \"pron\" text(100),\n" +
                "  \"pron2\" text(100),\n" +
                "  \"substitute_orgi\" text(100),\n" +
                "  \"substitute_user\" text(100),\n" +
                "  \"user_pron\" text(100),\n" +
                "  \"user_pron2\" text(100),\n" +
                "  \"index\" integer(50),\n" +
                "  \"score\" real(50)\n" +
                ")";
        importDatabase.openDatabase().execSQL(sql1);
        importDatabase.openDatabase().execSQL(sql2);
        importDatabase.openDatabase().execSQL(sql3);
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
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select * from " + getTableName() + " where Number = ? and TestTime = ? order by Number ",
                new String[]{number, year});
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
                        + TIMING + "," + SENTENCE + "," + SEX + "," + SOUND + ","
                        + GOOD + "," + BAD + "," + SCORE + "," + URL
                        + " from " + getTableName() + " where " + TESTTIME + "= ?",
                new String[]{year});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
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

    public ArrayList<CetText> selectData(String year, String id) {
        ArrayList<CetText> texts = new ArrayList<CetText>();
        Cursor cursor = importDatabase.openDatabase()
                .rawQuery(
                        "select " + TESTTIME + "," + NUMBER + "," + NUMBERINDEX
                                + "," + TIMING + "," + SENTENCE + "," + SEX
                                + "," + SOUND + ","
                                + GOOD + "," + BAD + "," + SCORE + "," + URL
                                + " from " + getTableName()
                                + " where " + TESTTIME + "= ? and " + NUMBER
                                + "= ?", new String[]{year, id});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
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

    private String getTableName() {
        return "newtype_texta" + Constant.APP_CONSTANT.TYPE();
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


    public Subtitle getRecordingResult(Subtitle subTextAB, String type) {
        SQLiteDatabase mDB;
        String tablename1 = NEWTYPE_TABLE_TEXTA;
        String wordsTabelName = NEWTYPE_TABLE_TEXTA_WORDS;
        if ("A".equals(type)) {
            tablename1 = NEWTYPE_TABLE_TEXTA;
            wordsTabelName = NEWTYPE_TABLE_TEXTA_WORDS;
        } else if ("B".equals(type)) {
            tablename1 = NEWTYPE_TABLE_TEXTB;
            wordsTabelName = NEWTYPE_TABLE_TEXTB_WORDS;
        } else if ("C".equals(type)) {
            tablename1 = NEWTYPE_TABLE_TEXTC;
            wordsTabelName = NEWTYPE_TABLE_TEXTC_WORDS;
        }
        String[] goodsString = new String[]{};
        String[] badsString = new String[]{};
        mDB = importDatabase.openDatabase();
        String querySQL = "select * from " + tablename1 + " where " + NUMBERINDEX + " = " + subTextAB.NumberIndex +
                " and " + NUMBER + " = " + subTextAB.Number +
                " and " + TESTTIME + " = " + subTextAB.testTime;
        Cursor cur = mDB.rawQuery(querySQL, null);
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            subTextAB.goodList = new ArrayList<>();
            subTextAB.badList = new ArrayList<>();
            subTextAB.record_url = cur.getString(cur.getColumnIndex(URL));
            if (!TextUtils.isEmpty(cur.getString(cur.getColumnIndex(SCORE)))) {
                subTextAB.readScore = Integer.parseInt(cur.getString(cur.getColumnIndex(SCORE)));
                subTextAB.isRead = true;
            }
            String goods = cur.getString(cur.getColumnIndex(GOOD));
            String bads = cur.getString(cur.getColumnIndex(BAD));
            // 转义字符
            if (!TextUtils.isEmpty(goods))
                goodsString = goods.split("\\+");
            if (!TextUtils.isEmpty(bads))
                badsString = bads.split("\\+");
            try {
                for (int i = 0; i < goodsString.length; i++) {
                    subTextAB.goodList.add(Integer.valueOf(goodsString[i]));
                }
                for (int i = 0; i < badsString.length; i++) {
                    subTextAB.badList.add(Integer.valueOf(badsString[i]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return subTextAB;
    }


    public void writeRecordingData(Subtitle subTextAB, String type) {
        String tablename1 = NEWTYPE_TABLE_TEXTA;
        String tablename2 = NEWTYPE_TABLE_TEXTB;
        String tablename3 = NEWTYPE_TABLE_TEXTC;
        SQLiteDatabase mDB;

        mDB = importDatabase.openDatabase();
        String goodString = "";
        for (int i = 0; i < subTextAB.goodList.size(); i++) {
            goodString += subTextAB.goodList.get(i);
            if (i + 1 != subTextAB.goodList.size()) {
                goodString += "+";
            }
        }
        String badString = "";
        for (int i = 0; i < subTextAB.badList.size(); i++) {
            badString += subTextAB.badList.get(i);
            if (i + 1 != subTextAB.badList.size()) {
                badString += "+";
            }
        }

        String tablename = NEWTYPE_TABLE_TEXTA;
        String wordsTabelName = NEWTYPE_TABLE_TEXTA_WORDS;
        if ("A".equals(type)) {
            tablename = NEWTYPE_TABLE_TEXTA;
            wordsTabelName = NEWTYPE_TABLE_TEXTA_WORDS;
        } else if ("B".equals(type)) {
            tablename = NEWTYPE_TABLE_TEXTB;
            wordsTabelName = NEWTYPE_TABLE_TEXTB_WORDS;
        } else if ("C".equals(type)) {
            tablename = NEWTYPE_TABLE_TEXTC;
            wordsTabelName = NEWTYPE_TABLE_TEXTC_WORDS;
        }
        String sql = "UPDATE " + tablename + " SET " + BAD + " = \"" + badString + "\"," +
                GOOD + " = \"" + goodString + "\" ," +
                URL + " = \"" + subTextAB.record_url + "\" ," +
                SCORE + " = \"" + subTextAB.readScore + "\"" +
                " WHERE " + NUMBER + " = " + subTextAB.Number +
                " AND " + TESTTIME + " = " + subTextAB.testTime +
                " AND " + NUMBERINDEX + " = " + subTextAB.NumberIndex;
        for (SendEvaluateResponse.DataBean.WordsBean wordsBean : subTextAB.mDataBean) {
            ContentValues cv = new ContentValues();
            cv.put("TestTime", subTextAB.testTime);
            cv.put("Number", subTextAB.Number);
            cv.put("NumberIndex", subTextAB.NumberIndex);
            cv.put("pron2", wordsBean.getPron2());
            cv.put("user_pron2", wordsBean.getUser_pron2());
            cv.put("content", wordsBean.getContent());
            mDB.insert(wordsTabelName, null, cv);
        }
        mDB.execSQL(sql);
    }


    /**
     * 清除评测的数据
     */
    public void clearRecord() {

        SQLiteDatabase sqLiteDatabase = importDatabase.openDatabase();
        String sql = "delete from newtype_texta6_words";
        String sql2 = "delete from newtype_textb6_words";
        String sql3 = "delete from newtype_textc6_words";
        String update = "update newtype_texta6 set good =null,bad=null,score=null,record_url=null";
        String update2 = "update newtype_textb6 set good =null,bad=null,score=null,record_url=null";
        String update3 = "update newtype_textc6 set good =null,bad=null,score=null,record_url=null";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.execSQL(sql2);
        sqLiteDatabase.execSQL(sql3);
        sqLiteDatabase.execSQL(update);
        sqLiteDatabase.execSQL(update2);
        sqLiteDatabase.execSQL(update3);
    }


    public String getSentence(int year, String paraid,
                              String idIndex, String type) {
        SQLiteDatabase mDB;
        String s = "";
        mDB = importDatabase.openDatabase();
//		ArrayList<Explain> explains = new ArrayList<Explain>();

        String tb = NEWTYPE_TABLE_TEXTA;
        if ("A".equals(type)) {
            tb = NEWTYPE_TABLE_TEXTA;
        } else if ("B".equals(type)) {
            tb = NEWTYPE_TABLE_TEXTB;
        } else {
            tb = NEWTYPE_TABLE_TEXTC;
        }
        Cursor cur = null;

        // String sql =
        // "select *  from "+tablename+" where TestTime = "+year+" order by " +
        // FIELD_NUMBER + " asc";
        String sql = "select *  from " + tb + " where TestTime = "
                + year + " and Number = " + paraid + " and NumberIndex = " + idIndex + " order by "
                + NUMBER + " asc";
        Log.d("bible", sql);

        try {
            cur = mDB.rawQuery(sql, null);

            int iC = cur.getCount();
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                s = cur.getString(cur.getColumnIndex("Sentence"));
            }

            cur.close();
            //mDB.close();

        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (cur != null) {
                cur.close();
            }
            if (mDB != null) {
                //mDB.close();
            }
        }
        return s;
    }


}
