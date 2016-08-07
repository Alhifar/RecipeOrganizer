package com.crimbogrotto.alhifar.recipeorganizer.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.crimbogrotto.alhifar.recipeorganizer.R;
import com.crimbogrotto.alhifar.recipeorganizer.db.RecipeContract;
import com.crimbogrotto.alhifar.recipeorganizer.db.RecipeDbHelper;
import com.crimbogrotto.alhifar.recipeorganizer.view.EditTagView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alhifar on 8/4/2016.
 */
public class EditActivity extends AppCompatActivity {
    private int id;
    private String title;
    private String filename;
    private String tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        id = Integer.parseInt(intent.getStringExtra("id"));
        title = intent.getStringExtra("title");
        filename = intent.getStringExtra("filename");
        tags = intent.getStringExtra("tags");
        List<String> tagList = Arrays.asList(TextUtils.split(tags, ","));

        if (id != -1)
        {
            ((EditText) findViewById(R.id.title_edit)).setText(title);
            ((AutoCompleteTextView) findViewById(R.id.filename_edit)).setText(filename);
        }
        else
        {

        }

        int tagCount = tagList.size();
        if (tagCount > 0)
        {
            ((AutoCompleteTextView) findViewById(R.id.tag1_edit)).setText(tagList.get(0));
        }
        if (tagCount > 1)
        {
            ViewGroup tagListView = ((ViewGroup) findViewById(R.id.edit_tag_list));
            for (int i=1;i<tagCount;i++)
            {
                EditTagView tag = new EditTagView(this);
                tagListView.addView(tag);
                AutoCompleteTextView tagTextView = (AutoCompleteTextView)((ViewGroup)tag.getChildAt(0)).getChildAt(0);
                tagTextView.setText(tagList.get(i));
            }
        }
    }

    public void addTag(View addButton)
    {
        ViewGroup tagListView = ((ViewGroup) findViewById(R.id.edit_tag_list));
        EditTagView tag = new EditTagView(this);
        tagListView.addView(tag);
    }

    public void deleteTag(View deleteButton)
    {
        ViewGroup tagListView = ((ViewGroup) findViewById(R.id.edit_tag_list));
        EditTagView tag = (EditTagView) deleteButton.getParent().getParent();
        tagListView.removeView(tag);
    }

    public void saveClick(View saveButton)
    {
        String newTitle = ((EditText)findViewById(R.id.title_edit)).getText().toString();
        String newFilename = ((EditText)findViewById(R.id.filename_edit)).getText().toString();
        List<String> newTags = new ArrayList<String>();
        ViewGroup tagListView = (ViewGroup) findViewById(R.id.edit_tag_list);
        int count = tagListView.getChildCount();
        for(int i=0;i<count;i++)
        {
            View child = tagListView.getChildAt(i);
            if (child instanceof EditTagView)
            {
                child = ((EditTagView) child).getChildAt(0);
            }
            if (child instanceof LinearLayout) //EditTagView is a LinearLayout
            {
                int childCount = ((ViewGroup)child).getChildCount();
                for (int n=0;n<childCount;n++)
                {
                    View subChild = ((ViewGroup) child).getChildAt(n);
                    Log.d("subChild", subChild.getClass().toString());
                    if (subChild instanceof AutoCompleteTextView)
                    {
                        newTags.add(((AutoCompleteTextView)subChild).getText().toString());
                    }
                }
            }
        }

        String tagText = TextUtils.join(",", newTags);

        RecipeDbHelper dbHelper = new RecipeDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecipeContract.RecipeEntry.COLUMN_NAME_TITLE, newTitle);
        values.put(RecipeContract.RecipeEntry.COLUMN_NAME_TAGS, tagText);
        values.put(RecipeContract.RecipeEntry.COLUMN_NAME_FILENAME, newFilename);

        if (id != -1)
        {
            String selection = RecipeContract.RecipeEntry._ID + " LIKE ?";
            String[] selectionArgs = {String.valueOf(id)};

            db.update(RecipeContract.RecipeEntry.TABLE_NAME, values, selection, selectionArgs);
        }
        else
        {
            db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, values);
        }
        db.close();
        finish();
    }
    public void deleteRecipe(View deleteButton)
    {
        RecipeDbHelper dbHelper = new RecipeDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = RecipeContract.RecipeEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.delete(RecipeContract.RecipeEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        finish();
    }
}
