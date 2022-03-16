package com.techload.madinatic.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.techload.madinatic.R;
import com.techload.madinatic.adapters.MainPagerScreenSlideAdapter;
import com.techload.madinatic.utils.ZoomOutPagerAnimation;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainPager extends Fragment {
    //
    public static ViewPager contentFragmentsPager;
    public static TabLayout contentTabs;
    public static MainPagerScreenSlideAdapter mainScreenAdapter;
    //


    public MainPager() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_pager, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setting up the View-pager and tabLayout to change while sliding and clicking
        contentFragmentsPager = view.findViewById(R.id.content_fragments_pager);
        contentFragmentsPager.setPageTransformer(true, new ZoomOutPagerAnimation());
        contentTabs = view.findViewById(R.id.tabLayout);
        //
        mainScreenAdapter = new MainPagerScreenSlideAdapter(getChildFragmentManager());
        contentFragmentsPager.setAdapter(mainScreenAdapter);
        contentTabs.setupWithViewPager(contentFragmentsPager);
        /**/
        contentFragmentsPager.setCurrentItem(1);
        /**/
        //custon icon views with selectors with 2 icons for 2 states for each of the tabs


        for (int i = 0; i < contentTabs.getTabCount(); i++) {
            contentTabs.getTabAt(i).setCustomView(R.layout.custom_main_pager_tab);
        }
        /**/
        ImageView icon = contentTabs.getTabAt(0).getCustomView().findViewById(R.id.tab_icon);
        icon.setImageResource(R.drawable.my_reports_selector);

        /**/
        icon = contentTabs.getTabAt(1).getCustomView().findViewById(R.id.tab_icon);
        icon.setImageResource(R.drawable.home_selector);

        /**/
        icon = contentTabs.getTabAt(2).getCustomView().findViewById(R.id.tab_icon);
        icon.setImageResource(R.drawable.my_account_selector);


    }/**/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        contentFragmentsPager = null;
        contentTabs = null;
        mainScreenAdapter = null;
    }
}//
