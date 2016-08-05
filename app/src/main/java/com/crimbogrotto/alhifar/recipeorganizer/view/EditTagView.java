package com.crimbogrotto.alhifar.recipeorganizer.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.crimbogrotto.alhifar.recipeorganizer.R;

/**
 * Created by Alhifar on 8/5/2016.
 */
public class EditTagView extends LinearLayout{
    public EditTagView(Context context)
    {
        super(context);
        View child = LayoutInflater.from(context).inflate(R.layout.edit_tag_view, null);
        this.addView(child);
    }
}
