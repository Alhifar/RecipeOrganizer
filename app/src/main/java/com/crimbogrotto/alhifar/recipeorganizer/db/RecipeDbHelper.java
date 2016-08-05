package com.crimbogrotto.alhifar.recipeorganizer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alhifar on 8/4/2016.
 */
public class RecipeDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RecipeOrganizer.db";

    public RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RecipeContract.SQL_CREATE_ENTRIES);
        db.execSQL(TagContract.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //No upgrades at this time
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //No downgrades at this time
    }
}
