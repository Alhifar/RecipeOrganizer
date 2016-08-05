package com.crimbogrotto.alhifar.recipeorganizer.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.crimbogrotto.alhifar.recipeorganizer.R;
import com.crimbogrotto.alhifar.recipeorganizer.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alhifar on 8/1/2016.
 */
public class EditListAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private ArrayList<HashMap<String, String>> originalData;
    private ArrayList<HashMap<String, String>> filteredData;
    private static LayoutInflater inflater = null;
    private static Bitmap dropShadowEditImage;

    public EditListAdapter(Activity activity, ArrayList<HashMap<String, String>> data)
    {
        this.activity = activity;
        this.originalData = data;
        this.filteredData = data;
        inflater = (LayoutInflater) activity.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
        Bitmap editImage = BitmapFactory.decodeResource(activity.getResources(), android.R.drawable.ic_menu_edit);
        dropShadowEditImage = MainActivity.createShadowBitmap(editImage);
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
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
        TextView textView = (TextView) vi.findViewById(R.id.text);
        HashMap<String, String> item = filteredData.get(position);
        String text = item.get("title");
        textView.setText(text);
        textView.setTag(item);

        ImageView pdfImage = (ImageView) vi.findViewById(R.id.edit_image);
        pdfImage.setTag(item);
        pdfImage.setImageBitmap(dropShadowEditImage);

        return vi;
    }
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<HashMap<String,String>>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                String filterString = constraint.toString().toLowerCase();
                final ArrayList<HashMap<String, String>> list = originalData;

                int count = list.size();
                final ArrayList<HashMap<String, String>> newList = new ArrayList<HashMap<String, String>>(count);

                String filterableString;
                HashMap<String, String> item;

                for (int i = 0; i < list.size(); i++) {
                    item = list.get(i);
                    filterableString = item.get("title");
                    if (filterableString.toLowerCase().contains(filterString)) {
                        newList.add(item);
                    }
                }

                results.values = newList;
                results.count = newList.size();

                return results;
            }
        };

        return filter;
    }
}
