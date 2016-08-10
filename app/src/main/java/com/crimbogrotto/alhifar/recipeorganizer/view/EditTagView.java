package com.crimbogrotto.alhifar.recipeorganizer.view;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.crimbogrotto.alhifar.recipeorganizer.R;
import com.crimbogrotto.alhifar.recipeorganizer.db.RecipeContract;
import com.crimbogrotto.alhifar.recipeorganizer.db.RecipeDbHelper;
import com.crimbogrotto.alhifar.recipeorganizer.db.TagContract;

import java.util.ArrayList;

/**
 * Created by Alhifar on 8/5/2016.
 */
public class EditTagView extends LinearLayout{
    public EditTagView(Context context)
    {
        super(context);
        View child = LayoutInflater.from(context).inflate(R.layout.edit_tag_view, null);
        ArrayAdapter<String> tagListAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, getAllTags().toArray(new String[0]));
        ((AutoCompleteTextView)child.findViewById(R.id.tag_edit_autocomplete)).setAdapter(tagListAdapter);
        this.addView(child);
    }

    public ArrayList<String> getAllTags() {
        ArrayList<String> tagList = new ArrayList<String>();
        RecipeDbHelper dbHelper = new RecipeDbHelper(this.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                TagContract.TagEntry.COLUMN_NAME_TITLE
        };

        String sortOrder = TagContract.TagEntry.COLUMN_NAME_TITLE + " DESC";

        try(Cursor c = db.query(TagContract.TagEntry.TABLE_NAME, projection, null, null, null, null, sortOrder))
        {
            while (c.moveToNext()) {
                tagList.add(c.getString(c.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME_TITLE)));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        db.close();
        return tagList;
    }
}
