package com.elishai.officemisdeeds.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.elishai.officemisdeeds.DB.DBScheme.MisdeedTable;

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION            = 1;
    private static final String DATABASE_NAME   = "MisdeedBase.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + MisdeedTable.NAME + "("  +
                               " _id integer primary key autoincrement, " +
                MisdeedTable.Cols.UUID +", " +
                MisdeedTable.Cols.TITLE +", " +
                MisdeedTable.Cols.DATE + ", " +
                MisdeedTable.Cols.SOLVED + ", " +
                MisdeedTable.Cols.SUSPECT +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
