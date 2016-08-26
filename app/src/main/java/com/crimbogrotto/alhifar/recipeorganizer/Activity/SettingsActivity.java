package com.crimbogrotto.alhifar.recipeorganizer.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.crimbogrotto.alhifar.recipeorganizer.R;
import com.crimbogrotto.alhifar.recipeorganizer.db.RecipeContract;
import com.crimbogrotto.alhifar.recipeorganizer.db.RecipeDbHelper;
import com.crimbogrotto.alhifar.recipeorganizer.db.TagContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alhifar on 8/2/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void importClick(View view)
    {
        importDatabase();
        setStatus("Imported database");
    }

    public void exportClick(View view)
    {
        exportDatabase();
        setStatus("Exported database");
    }

    public void regenTagList(View view)
    {
        List<String> tagList = new ArrayList<String>();
        RecipeDbHelper dbHelper = new RecipeDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                RecipeContract.RecipeEntry.COLUMN_NAME_TAGS,
        };

        try(Cursor c = db.query(RecipeContract.RecipeEntry.TABLE_NAME, projection, null, null, null, null, null))
        {
            while (c.moveToNext())
            {
                String tags = c.getString(c.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME_TAGS));
                for (String tag : tags.split(","))
                {
                    if (!tagList.contains(tag))
                    {
                        tagList.add(tag);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        db.delete(TagContract.TagEntry.TABLE_NAME, null, null);
        for (String tag : tagList)
        {
            ContentValues tagValues = new ContentValues();
            tagValues.put(TagContract.TagEntry.COLUMN_NAME_TITLE, tag.toLowerCase());
            db.insert(TagContract.TagEntry.TABLE_NAME, null, tagValues);
        }

        db.close();
        setStatus("Regenerated tag list");
    }

    private void setStatus(String text)
    {
        ((TextView)findViewById(R.id.status_text)).setText(text);
    }

    private void exportDatabase() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + getPackageName() + "//databases//" + RecipeDbHelper.DATABASE_NAME;
                String backupDBPath = RecipeDbHelper.DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }
    private void importDatabase() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + getPackageName() + "//databases//" + RecipeDbHelper.DATABASE_NAME;
                String backupDBPath = RecipeDbHelper.DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }
}


