package com.iyuba.CET4bible.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.iyuba.CET4bible.fragment.ReadingFragment;

public class ReadingFragmentAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public ReadingFragmentAdapter(Context mContext, FragmentManager fm) {
        super(fm);
        this.mContext = mContext;

    }

    @Override
    public Fragment getItem(int position) {
        return ReadingFragment.newInstance(mContext, position);

    }

    @Override
    public int getCount() {

        return 2;
    }

}
