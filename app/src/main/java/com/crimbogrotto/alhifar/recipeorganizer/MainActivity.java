package com.crimbogrotto.alhifar.recipeorganizer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView tag_list = (ListView) findViewById(R.id.tag_list);
        ArrayList<String> tagList = new ArrayList<String>();
        for (int i=0;i<=10;i++)
        {
            tagList.add("Test " + i);
        }
        ArrayAdapter<String> tagListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tagList.toArray(new String[0]));
        tag_list.setAdapter(tagListAdapter);

        ListView recipe_list = (ListView) findViewById(R.id.recipe_list);
        EditListAdapter recipeListAdapter = new EditListAdapter(this, tagList);
        recipe_list.setAdapter(recipeListAdapter);
    }

    public void deleteClick(View delButton)
    {
        EditText tag_search = (EditText) findViewById(R.id.tag_search);
        tag_search.setText("");
    }
}
