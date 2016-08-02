package com.crimbogrotto.alhifar.recipeorganizer.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.crimbogrotto.alhifar.recipeorganizer.adapter.EditListAdapter;
import com.crimbogrotto.alhifar.recipeorganizer.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        Bitmap deleteImage = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_delete);
        Bitmap dropShadowDeleteImage = createShadowBitmap(deleteImage);
        ((ImageView) findViewById(R.id.delete_button)).setImageBitmap(dropShadowDeleteImage);

        ListView tag_list = (ListView) findViewById(R.id.tag_list);
        ArrayList<String> tagList = new ArrayList<String>();
        for (int i=0;i<=10;i++)
        {
            tagList.add("Test " + i);
        }
        ArrayAdapter<String> tagListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tagList.toArray(new String[0]));
        tag_list.setAdapter(tagListAdapter);

        ArrayList<HashMap<String,String>> recipeList = new ArrayList<HashMap<String,String>>();
        for (int i=0;i<10;i++)
        {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("text", "Test " + i);
            data.put("id", Integer.toString(i));
            recipeList.add(data);
        }
        ListView recipe_list = (ListView) findViewById(R.id.recipe_list);
        EditListAdapter recipeListAdapter = new EditListAdapter(this, recipeList);
        recipe_list.setAdapter(recipeListAdapter);
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

    public void recipeClick(View recipeView)
    {
        Intent intent = new Intent(this, PDFDisplayActivity.class);
        TextView tv = (TextView) recipeView;
        intent.putExtra("id", tv.getTag().toString());
        startActivity(intent);
    }
}
