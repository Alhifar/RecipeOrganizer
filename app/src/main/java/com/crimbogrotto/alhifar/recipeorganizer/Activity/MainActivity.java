package com.crimbogrotto.alhifar.recipeorganizer.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.crimbogrotto.alhifar.recipeorganizer.adapter.EditListAdapter;
import com.crimbogrotto.alhifar.recipeorganizer.R;
import com.crimbogrotto.alhifar.recipeorganizer.db.RecipeContract;
import com.crimbogrotto.alhifar.recipeorganizer.db.RecipeDbHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static ArrayList<HashMap<String,String>> recipeList;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        Bitmap deleteImage = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_delete);
        Bitmap dropShadowDeleteImage = createShadowBitmap(deleteImage);
        View deleteButton = findViewById(R.id.delete_button);
        ((ImageView) deleteButton).setImageBitmap(dropShadowDeleteImage);

        ListView tag_list = (ListView) findViewById(R.id.tag_list);
        ArrayList<String> tagList = new ArrayList<String>();
        for (int i=0;i<=10;i++)
        {
            tagList.add("Test " + i);
        }
        ArrayAdapter<String> tagListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tagList.toArray(new String[0]));
        tag_list.setAdapter(tagListAdapter);

        updateRecipeList();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateRecipeList();
    }

    private void updateRecipeList()
    {
        recipeList = getRecipeList();

        HashMap<String, String> item = new HashMap<String, String>();
        item.put("id", "-1");
        item.put("title", "Add new recipe");
        item.put("tags", "");
        item.put("filename", "");
        recipeList.add(item);

        ListView recipe_list = (ListView) findViewById(R.id.recipe_list);
        EditListAdapter recipeListAdapter = new EditListAdapter(this, recipeList);
        recipe_list.setAdapter(recipeListAdapter);
    }

    private ArrayList<HashMap<String,String>> getRecipeList() {
        ArrayList<HashMap<String,String>> recipeList = new ArrayList<HashMap<String, String>>();
        RecipeDbHelper dbHelper = new RecipeDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                RecipeContract.RecipeEntry._ID,
                RecipeContract.RecipeEntry.COLUMN_NAME_TITLE,
                RecipeContract.RecipeEntry.COLUMN_NAME_TAGS,
                RecipeContract.RecipeEntry.COLUMN_NAME_FILENAME
        };

        String sortOrder = RecipeContract.RecipeEntry.COLUMN_NAME_TITLE + " DESC";

        try(Cursor c = db.query(RecipeContract.RecipeEntry.TABLE_NAME, projection, null, null, null, null, sortOrder))
        {
            while (c.moveToNext()) {
                HashMap<String, String> hm = new HashMap<String, String>();
                hm.put("id", c.getString(c.getColumnIndex(RecipeContract.RecipeEntry._ID)));
                hm.put("title", c.getString(c.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME_TITLE)));
                hm.put("tags", c.getString(c.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME_TAGS)));
                hm.put("filename", c.getString(c.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME_FILENAME)));
                recipeList.add(hm);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        db.close();
        return recipeList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_action_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        final ListView recipeList = (ListView) findViewById(R.id.recipe_list);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                ((EditListAdapter)recipeList.getAdapter()).getFilter().filter(text);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                super.onOptionsItemSelected(item);
        }
        return false;
    }

    public static Bitmap createShadowBitmap(Bitmap originalBitmap) {
        BlurMaskFilter blurFilter = new BlurMaskFilter(5, BlurMaskFilter.Blur.OUTER);
        Paint shadowPaint = new Paint();
        shadowPaint.setMaskFilter(blurFilter);

        int[] offsetXY = new int[2];
        Bitmap shadowImage = originalBitmap.extractAlpha(shadowPaint, offsetXY);

        /* Need to convert shadowImage from 8-bit to ARGB here. */
        Bitmap shadowImage32 = shadowImage.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(shadowImage32);
        c.drawBitmap(originalBitmap, -offsetXY[0], -offsetXY[1], null);

        return shadowImage32;
    }

    public void deleteClick(View delButton)
    {
        EditText tag_search = (EditText) findViewById(R.id.tag_search);
        tag_search.setText("");
    }

    @SuppressWarnings("unchecked")
    public void recipeClick(View recipeView)
    {
        Intent intent = new Intent(this, PDFDisplayActivity.class);
        for (Map.Entry<String, String> entry : ((HashMap<String, String>)recipeView.getTag()).entrySet())
        {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        startActivity(intent);
    }

    @SuppressWarnings("unchecked")
    public void editClick(View editButton)
    {
        Intent intent = new Intent(this, EditActivity.class);
        for (Map.Entry<String, String> entry : ((HashMap<String, String>)editButton.getTag()).entrySet())
        {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        startActivity(intent);
    }

    @SuppressWarnings("unchecked")
    static public View.OnClickListener addRecipe = new View.OnClickListener() {
        @Override
        public void onClick(View addButton) {
            Intent intent = new Intent(addButton.getContext(), EditActivity.class);
            for (Map.Entry<String, String> entry : ((HashMap<String, String>)addButton.getTag()).entrySet())
            {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
            addButton.getContext().startActivity(intent);
        }
    };
}
