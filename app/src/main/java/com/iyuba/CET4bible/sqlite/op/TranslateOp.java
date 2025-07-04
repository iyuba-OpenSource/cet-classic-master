/*
 * 文件名
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.CET4bible.sqlite.op;

import android.content.Context;
import android.database.Cursor;

import com.iyuba.CET4bible.sqlite.db.DatabaseService;
import com.iyuba.CET4bible.sqlite.mode.Write;

import java.util.ArrayList;

/**
 * 翻译
 *
 * @author 作者 <br/>
 * 实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class TranslateOp extends DatabaseService {
    public static final String TABLE_NAME = "translate";
    public static final String COMPNUM = "compnum";
    public static final String SENINDEX = "senindex";
    public static final String COMPNAME = "compname";
    public static final String TEXT = "text";
    public static final String COMMENT = "comment";
    public static final String QUESTION = "question";
    public static final String TITLENAME = "titlename";
    public static final String CATEGORYID = "categoryid";
    public static final String CATEGORYNAME = "categoryname";
    public static final String IMAGENAME = "imagename";

    public TranslateOp(Context context) {
        super(context);
    }

    public ArrayList<Write> selectData() {

        ArrayList<Write> writes = new ArrayList<>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select * from " + TABLE_NAME
                        + " GROUP BY CompNum ORDER BY CompNum DESC",
                new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            writes.add(fillIn(cursor));
        }
        cursor.close();
        closeDatabase(null);
        if (writes.size() != 0) {
            return writes;
        }
        return null;
    }

    public ArrayList<Write> selectData(String compnum) {
        ArrayList<Write> writes = new ArrayList<>();
        Cursor cursor = importDatabase.openDatabase().rawQuery(
                "select * from " + TABLE_NAME + " where " + COMPNUM
                        + "=? ORDER BY senindex", new String[]{compnum});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            writes.add(fillIn(cursor));
        }
        closeDatabase(null);
        if (writes.size() != 0) {
            return writes;
        }
        return null;
    }

    private Write fillIn(Cursor cursor) {
        Write write = new Write();
        write.num = cursor.getString(0);
        write.index = cursor.getString(1);
        write.name = cursor.getString(2);
        write.text = cursor.getString(3);
        write.text = write.text.replace("++", "\n\n");
        write.comment = cursor.getString(4);
        write.question = cursor.getString(5);
        write.question = write.question.replace("@@@", "__________");
        write.question = write.question.replace("++", "\n\n");
        write.title = cursor.getString(6);
        write.cateid = cursor.getString(7);
        write.catename = cursor.getString(8);
        write.image = cursor.getString(9);
        return write;
    }
}
