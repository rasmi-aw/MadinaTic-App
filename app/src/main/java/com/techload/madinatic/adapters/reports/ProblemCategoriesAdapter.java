package com.techload.madinatic.adapters.reports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.techload.madinatic.R;

import java.util.ArrayList;
import java.util.List;

public class ProblemCategoriesAdapter extends BaseAdapter {
    private ArrayList<String> categories;
    private Context context;


    public ProblemCategoriesAdapter(@NonNull Context context, @NonNull List<String> objects) {
        this.context = context;
        categories = (ArrayList<String>) objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //
        View item = LayoutInflater.from(context).inflate(R.layout.drop_down_item, parent, false);
        ImageView ic = item.findViewById(R.id.drop_down_ic);
        ((TextView) item.findViewById(R.id.drop_down_item_text)).setText(categories.get(position));
        //
        String current = categories.get(position).toLowerCase();
        if (current.contains("eau")) {
            ic.setImageResource(R.drawable.water);

        } else if (current.contains("route")) {
            ic.setImageResource(R.drawable.road);

        } else if (current.contains("clairage")) {
            ic.setImageResource(R.drawable.lighting);

        } else {
            ic.setImageResource(R.drawable.warning);
            ic.setImageTintList(ContextCompat.getColorStateList(context, R.color.warning));
        }


        return item;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return categories.size();
    }
}
