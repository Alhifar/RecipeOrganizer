package com.crimbogrotto.alhifar.recipeorganizer.db;

import android.provider.BaseColumns;

/**
 * Created by Alhifar on 8/4/2016.
 */
public class TagContract {
    public TagContract(){}

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TagEntry.TABLE_NAME + " (" +
                    TagEntry._ID + " INTEGER PRIMARY KEY," +
                    TagEntry.COLUMN_NAME_TITLE + " TEXT" +
                    ")";

    public static abstract class TagEntry implements BaseColumns {
        public static final String TABLE_NAME = "tags";
        public static final String COLUMN_NAME_TITLE = "title";
    }
}
