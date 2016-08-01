package com.crimbogrotto.alhifar.recipeorganizer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alhifar on 8/1/2016.
 */
public class EditListAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<String> data;
    private static LayoutInflater inflater = null;

    public EditListAdapter(Activity activity, ArrayList<String> data)
    {
        this.activity = activity;
        this.data = data;
        inflater = (LayoutInflater) activity.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parentGroup) {
        View vi = convertView;
        if (convertView == null)
        {
            vi = inflater.inflate(R.layout.edit_list_item, null);
        }
        TextView text = (TextView) vi.findViewById(R.id.text);
        String item = data.get(position);
        text.setText(item);
        return vi;
    }
}
