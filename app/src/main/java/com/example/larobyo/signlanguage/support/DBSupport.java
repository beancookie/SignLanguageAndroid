package com.example.larobyo.signlanguage.support;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBSupport extends SQLiteOpenHelper {
    private static final String CREATE_WORD = "CREATE TABLE Word ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "chineseName TEXT UNIQUE, "
            + "pinYin TEXT, "
            + "englishName TEXT, "
            + "description TEXT, "
            + "pictureLocationPath TEXT, "
            + "pictureUrl TEXT, "
            + "isCollected INTEGER DEFAULT 0)";

    private Context context;

    public DBSupport(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Word");
    }
}
