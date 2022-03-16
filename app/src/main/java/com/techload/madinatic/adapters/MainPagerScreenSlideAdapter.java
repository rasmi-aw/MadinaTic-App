package com.techload.madinatic.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.techload.madinatic.fragments.Edit_account;
import com.techload.madinatic.fragments.newsFeed.NewsFeed;
import com.techload.madinatic.fragments.reports.ReportsFragment;

public class MainPagerScreenSlideAdapter extends FragmentStatePagerAdapter {


    public MainPagerScreenSlideAdapter(FragmentManager manager) {
        super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ReportsFragment(true);

            case 1:
                return new NewsFeed();

            case 2:
                return new Edit_account();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
