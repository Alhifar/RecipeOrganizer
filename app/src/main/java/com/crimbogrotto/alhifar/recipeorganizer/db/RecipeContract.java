package com.crimbogrotto.alhifar.recipeorganizer.db;

import android.provider.BaseColumns;

/**
 * Created by Alhifar on 8/4/2016.
 */
public class RecipeContract {
    public RecipeContract(){}

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RecipeEntry.TABLE_NAME + " (" +
                    RecipeEntry._ID + " INTEGER PRIMARY KEY," +
                    RecipeEntry.COLUMN_NAME_TITLE + " TEXT," +
                    RecipeEntry.COLUMN_NAME_TAGS + " TEXT," +
                    RecipeEntry.COLUMN_NAME_FILENAME + " TEXT" +
                    ")";

    public static abstract class RecipeEntry implements BaseColumns {
        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_TAGS = "tags";
        public static final String COLUMN_NAME_FILENAME = "filename";
    }
}
