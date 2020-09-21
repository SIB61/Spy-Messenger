package com.sib4u.spymessenger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;
import java.util.Map;

public class SectionPagerAdapter extends FragmentPagerAdapter {
    Bundle bundle;
    List<Map<String, Object>> maps;
    List<Map<String, Object>> maps2;

    public SectionPagerAdapter(@NonNull FragmentManager fm, List<Map<String, Object>> maps, List<Map<String, Object>> maps2) {
        super ( fm );
        this.maps = maps;
        this.maps2 = maps2;
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
                FriendsFragment friendsFragment = FriendsFragment.newInstance ( maps );
                return friendsFragment;
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
