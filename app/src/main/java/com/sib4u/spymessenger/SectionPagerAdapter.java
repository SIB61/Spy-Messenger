package com.sib4u.spymessenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionPagerAdapter extends FragmentPagerAdapter {
    public SectionPagerAdapter(@NonNull FragmentManager fm) {
        super ( fm );
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatFragment ( );
            case 1:
                return new RequestFragment ( );
            case 2:
                return new FriendsFragment ( );
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "chats";
            case 1:
                return "requests";
            case 2:
                return "friends";
            default:
                return null;
        }
    }
}
