package com.prakriti.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {
    // deprecated Adapter

    public TabAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int tabPosition) {
        switch (tabPosition) { // add a case for each tab class
            // left to right
            case 0: return new NewPostTab();
            case 1: return new UsersTab();
            case 2: return new ProfileTab();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 3; // number of tabs
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "New Post";
            case 1: return "Users";
            case 2: return "Profile";
            default: return null;
        }
    }

}
