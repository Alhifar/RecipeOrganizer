package com.crimbogrotto.alhifar.recipeorganizer.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.crimbogrotto.alhifar.recipeorganizer.R;
import com.crimbogrotto.alhifar.recipeorganizer.view.EditTagView;

/**
 * Created by Alhifar on 8/4/2016.
 */
public class EditActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
    }

    public void addTag(View addButton)
    {
        ViewGroup tagList = ((ViewGroup) findViewById(R.id.edit_tag_list));
        EditTagView tag = new EditTagView(this);
        tagList.addView(tag);
    }

    public void deleteTag(View deleteButton)
    {
        ViewGroup tagList = ((ViewGroup) findViewById(R.id.edit_tag_list));
        EditTagView tag = (EditTagView) deleteButton.getParent().getParent();
        tagList.removeView(tag);
    }

    public void saveClick(View saveButton)
    {

    }
}
