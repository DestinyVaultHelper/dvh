package org.swistowski.vaulthelper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DB {
    private static final String LOG_TAG = "DB";

    private class DBHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "vaulthelper.db";

        public static final String TABLE_LABELS_NAME = "labels";
        public static final String TABLE_LABELS_COL_LABEL = "label";
        public static final String TABLE_LABELS_COL_COLOR = "color";

        public static final String TABLE_ITEMS_NAME = "items";
        public static final String TABLE_ITEMS_COL_LABEL_ID = "label_id";
        public static final String TABLE_ITEMS_COL_ITEM = "item";

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table labels (id integer primary key autoincrement, label text, color integer default 0, unique(label) on conflict replace)");
            db.execSQL("insert into labels (label) values (\"Favorites\")");
            db.execSQL("create table items (label_id integer, item integer, unique(label_id, item) on conflict replace);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion == 2 && oldVersion != 1) {
                onCreate(db);
            }
            if (newVersion == 2 && oldVersion == 1) {
                db.execSQL("create table labels_v2 (id integer primary key autoincrement, label text, color integer default 0, unique(label) on conflict replace)");
                db.execSQL("insert into labels_v2 (label) values (\"Favorites\")");
                db.execSQL("create table items_v2 (label_id integer, item integer, unique(label_id, item) on conflict replace);");
                db.execSQL("insert into items_v2 (label_id, item) select 1, item from labels");
                db.execSQL("drop table labels");

                db.execSQL("alter table items_v2 rename to items;");
                db.execSQL("alter table labels_v2 rename to labels;");
            }
        }
    }

    private DBHelper helper;
    private SQLiteDatabase database;

    public DB(Context context) {
        helper = new DBHelper(context);
        database = helper.getWritableDatabase();
    }

    public HashMap<Long, Set<Long>> getAllItems() {
        String[] cols = new String[]{DBHelper.TABLE_ITEMS_COL_LABEL_ID, DBHelper.TABLE_ITEMS_COL_ITEM};
        Cursor cursor = database.query(true, DBHelper.TABLE_ITEMS_NAME, cols, null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        HashMap<Long, Set<Long>> allItems = new HashMap<>();
        while (cursor.moveToNext()) {
            Long labelId = cursor.getLong(0);
            Long item = cursor.getLong(1);
            if (allItems.get(labelId) == null) {
                allItems.put(labelId, new HashSet<Long>());
            }
            allItems.get(labelId).add(item);
        }
        cursor.close();
        return allItems;
    };


    public void addItem(long item, long labelId) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.TABLE_ITEMS_COL_ITEM, item);
        values.put(DBHelper.TABLE_ITEMS_COL_LABEL_ID, labelId);
        database.insert(DBHelper.TABLE_ITEMS_NAME, null, values);
    }

    public void deleteItem(long item, long labelId) {
        database.delete(DBHelper.TABLE_ITEMS_NAME, DBHelper.TABLE_ITEMS_COL_LABEL_ID + "=? and " + DBHelper.TABLE_ITEMS_COL_ITEM + "=?", new String[]{Long.toString(labelId), Long.toString(item)});
    }

}