package com.crimbogrotto.alhifar.recipeorganizer.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.crimbogrotto.alhifar.recipeorganizer.R;
import com.crimbogrotto.alhifar.recipeorganizer.db.RecipeContract;
import com.crimbogrotto.alhifar.recipeorganizer.db.RecipeDbHelper;
import com.crimbogrotto.alhifar.recipeorganizer.db.TagContract;
import com.crimbogrotto.alhifar.recipeorganizer.utils.StringUtils;
import com.crimbogrotto.alhifar.recipeorganizer.view.EditTagView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Alhifar on 8/4/2016.
 */
public class EditActivity extends AppCompatActivity {
    private int id;
    private String title;
    private String filename;
    private String tags;
    private List<String> filenames;

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

        for (int i = 0;i<tagList.size();i++)
        {
            String tag = tagList.get(i);
            String[] words = TextUtils.split(tag, " ");
            for (int n = 0;n < words.length;n++)
            {
                String word = words[n];
                words[n] = StringUtils.properCase(word);
            }
            tag = TextUtils.join(" ", words);
            tagList.set(i, tag);
        }

        if (id != -1)
        {
            ((EditText) findViewById(R.id.title_edit)).setText(title);
            ((AutoCompleteTextView) findViewById(R.id.filename_edit)).setText(filename);
        }

        File dir = Environment.getExternalStoragePublicDirectory("Recipes");
        File[] files = dir.listFiles();
        filenames = new ArrayList<String>();

        for (File file : files)
        {
            filenames.add(file.getName());
        }
        ArrayAdapter<String> filenameListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filenames.toArray(new String[0]));

        ((AutoCompleteTextView) findViewById(R.id.filename_edit)).setAdapter(filenameListAdapter);

        int tagCount = tagList.size();
        if (tagCount > 0)
        {
            ((AutoCompleteTextView) findViewById(R.id.tag1_edit)).setText(tagList.get(0));
            ArrayAdapter<String> tagListAdapter = new ArrayAdapter<String>(this, R.layout.tag_list_item, getAllTags().toArray(new String[0]));
            ((AutoCompleteTextView) findViewById(R.id.tag1_edit)).setAdapter(tagListAdapter);
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

    public ArrayList<String> getAllTags() {
        ArrayList<String> tagList = new ArrayList<String>();
        RecipeDbHelper dbHelper = new RecipeDbHelper(this);
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

        if (!filenames.contains(newFilename))
        {
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(newFilename + " does not exist")
                .setTitle("Invalid filename")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                    }
                });

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

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
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_TITLE, newTitle);
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_TAGS, tagText);
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME_FILENAME, newFilename);

        if (id != -1)
        {
            String recipeSelection = RecipeContract.RecipeEntry._ID + " LIKE ?";
            String[] recipeSelectionArgs = {String.valueOf(id)};

            db.update(RecipeContract.RecipeEntry.TABLE_NAME, recipeValues, recipeSelection, recipeSelectionArgs);
        }
        else
        {
            db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, recipeValues);
        }

        for (String tag : newTags)
        {
            Boolean isNewTag = false;

            String[] projection = {
                    TagContract.TagEntry._ID,
                    TagContract.TagEntry.COLUMN_NAME_TITLE,
            };

            String selection = TagContract.TagEntry.COLUMN_NAME_TITLE + " = ?";
            String[] selectionArgs = { tag.toLowerCase() };

            try (Cursor c = db.query(TagContract.TagEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null))
            {
                if (c.getCount() <= 0)
                {
                    isNewTag = true;
                }
            }

            if (isNewTag)
            {
                ContentValues tagValues = new ContentValues();
                tagValues.put(TagContract.TagEntry.COLUMN_NAME_TITLE, tag.toLowerCase());
                db.insert(TagContract.TagEntry.TABLE_NAME, null, tagValues);
            }
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
