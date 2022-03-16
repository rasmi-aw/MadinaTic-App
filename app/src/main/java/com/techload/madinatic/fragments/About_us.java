package com.techload.madinatic.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.utils.AppSettingsUtils;
import com.techload.madinatic.utils.AppStats;


public class About_us extends Fragment {
    private TextView aboutApp, companyDesc, policy;


    public About_us() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        aboutApp = view.findViewById(R.id.about_the_app_text);
        companyDesc = view.findViewById(R.id.company_text);
        policy = view.findViewById(R.id.policy_text);
        //filling app stats card
        String extras = ((MainActivity.appLanguage).equals("ar") ? "" : "(s)");
        AppStats stats = new AppStats(MainActivity.sharedPreferences.getString(AppSettingsUtils.APPLICATION_LAST_STATISTICS, ""));
        ((TextView) view.findViewById(R.id.reports_stats)).setText(Html.fromHtml(stats.getReportsNumber() + " " + getString(R.string.report) + " <small>" + extras + "</small>"));
        ((TextView) view.findViewById(R.id.posts_stats)).setText(Html.fromHtml(stats.getPostsNumber() + " " + getString(R.string.post) + " <small>" + extras + "</small>"));
        ((TextView) view.findViewById(R.id.users_stats)).setText(Html.fromHtml(stats.getUsersNumber() + " " + getString(R.string.user) + " <small>" + extras + "</small>"));
        ((TextView) view.findViewById(R.id.last_update_time)).setText(Html.fromHtml(" <small>" +
                AppSettingsUtils.formatDate(MainActivity.appLanguage,stats.getDate()) + "</small>"));

        //filling about app section
        aboutApp.setText(Html.fromHtml(getResources().getString(R.string.creation_reason)));
        companyDesc.setText(Html.fromHtml(getResources().getString(R.string.company_description)));
        policy.setText(Html.fromHtml(getResources().getString(R.string.read_policy)));

        //
        view.findViewById(R.id.web_site_visit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://madinatic-bccc1.web.app/"));
                startActivity(intent);
            }
        });


    }
}//
