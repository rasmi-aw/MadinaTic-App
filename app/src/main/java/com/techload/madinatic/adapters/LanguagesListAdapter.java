package com.techload.madinatic.adapters;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techload.madinatic.R;
import com.techload.madinatic.lists_items.Language;
import com.techload.madinatic.utils.AppSettingsUtils;

import java.util.ArrayList;

public class LanguagesListAdapter extends RecyclerView.Adapter<LanguagesListAdapter.LanguageHolder> {
    private ArrayList<Language> languages;
    private Activity mainActivity;
    private LanguageClickedListener languageClickedListener;

    //
    class LanguageHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView icon;
        TextView name;


        public LanguageHolder(View view) {
            super(view);
            this.view = view;
            icon = view.findViewById(R.id.language_icon);
            name = view.findViewById(R.id.language_name);
            view.setOnClickListener(languageClickedListener);
        }

    }


    public LanguagesListAdapter(Activity mainActivity) {
        //
        this.mainActivity = mainActivity;
        languageClickedListener = new LanguageClickedListener();
        //
        languages = new ArrayList<>();
        //
        languages.add(new Language("العربية", R.drawable.algeria_flag));
//        languages.add(new Language("Deutsch", R.drawable.germany_flag));
        languages.add(new Language("English", R.drawable.usa_flag));
//        languages.add(new Language("Espanol", R.drawable.spain_flag));
//        languages.add(new Language("русский", R.drawable.russia_flag));
//        languages.add(new Language("Türk", R.drawable.turkey_flag));
        languages.add(new Language("Français", R.drawable.france_flag));
//        languages.add(new Language("Português", R.drawable.portugal_flag));


    }


    @NonNull
    @Override
    public LanguageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LanguageHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.languages_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageHolder holder, int position) {
        Language current = languages.get(position);
        //
        holder.name.setText(current.name);
        holder.icon.setImageResource(current.icon);
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    class LanguageClickedListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (((TextView) v.findViewById(R.id.language_name)).getText().toString()) {
                case "العربية":
                    AppSettingsUtils.changeAppLanguageAndRestart("ar");
                    return;

                case "English":
                    AppSettingsUtils.changeAppLanguageAndRestart("en");
                    return;

                case "Français":
                    AppSettingsUtils.changeAppLanguageAndRestart("fr");
                    return;

//                case "русский":
//                    AppSettingsUtils.changeAppLanguageAndRestart("ru");
//                    return;
            }
        }
    }


}
