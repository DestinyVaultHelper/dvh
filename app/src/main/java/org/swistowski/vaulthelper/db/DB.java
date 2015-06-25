package org.swistowski.vaulthelper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class DB {
    private static final String LOG_TAG = "DB";

    private class DBHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "vaulthelper.db";

        public static final String TABLE_LABELS_NAME = "labels";
        public static final String TABLE_LABELS_COL_LABEL = "label";
        public static final String TABLE_LABELS_COL_ITEM = "item";

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final String sql = "create table " + TABLE_LABELS_NAME + "(id integer primary key autoincrement, " + TABLE_LABELS_COL_LABEL + " text, " + TABLE_LABELS_COL_ITEM + " integer,  UNIQUE(" + TABLE_LABELS_COL_LABEL + ", " + TABLE_LABELS_COL_ITEM + ") ON CONFLICT REPLACE)";
            Log.v(LOG_TAG, sql);
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion == 1) {
                onCreate(db);
            }
        }
    }

    private DBHelper helper;
    private SQLiteDatabase database;

    public DB(Context context) {
        helper = new DBHelper(context);
        database = helper.getWritableDatabase();
    }

    public Cursor getAllLabels() {
        String[] cols = new String[]{DBHelper.TABLE_LABELS_COL_LABEL};
        Cursor cursor = database.query(true, DBHelper.TABLE_LABELS_NAME, cols, null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void addLabel(long item, String label) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.TABLE_LABELS_COL_ITEM, item);
        values.put(DBHelper.TABLE_LABELS_COL_LABEL, label);
        database.insert(DBHelper.TABLE_LABELS_NAME, null, values);
    }

    public void deleteLabel(long item, String label) {
        database.delete(DBHelper.TABLE_LABELS_NAME, DBHelper.TABLE_LABELS_COL_ITEM + "=? and " + DBHelper.TABLE_LABELS_COL_LABEL + "=?", new String[]{Long.toString(item), label});
    }

    public Cursor itemLabels(long item) {
        Cursor cursor = database.query(true, DBHelper.TABLE_LABELS_NAME, new String[]{DBHelper.TABLE_LABELS_COL_LABEL}, DBHelper.TABLE_LABELS_COL_ITEM + "=?", new String[]{Long.toString(item)}, null, null, null, null);
        return cursor;

    }

    public Set<Long> labelItems(String label) {
        Cursor cursor = database.query(true, DBHelper.TABLE_LABELS_NAME, new String[]{DBHelper.TABLE_LABELS_COL_ITEM}, DBHelper.TABLE_LABELS_COL_LABEL + "=?", new String[]{label}, null, null, null, null);
        Set<Long> items = new HashSet<Long>();
        while(cursor.moveToNext()){
            items.add( cursor.getLong(0));
        }
        cursor.close();
        return items;
    }

    public boolean itemHasLabel(long item, String label) {
        Cursor cursor = database.query(true, DBHelper.TABLE_LABELS_NAME, new String[]{DBHelper.TABLE_LABELS_COL_ITEM}, DBHelper.TABLE_LABELS_COL_ITEM + "=? and " + DBHelper.TABLE_LABELS_COL_LABEL + "=?", new String[]{Long.toString(item), label}, null, null, null, null);
        cursor.moveToFirst();
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
}