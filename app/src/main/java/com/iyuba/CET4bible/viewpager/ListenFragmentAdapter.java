package com.iyuba.CET4bible.viewpager;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.listening.ListenOriginalFragment;
import com.iyuba.CET4bible.listening.ListenTestFragment;
import com.iyuba.CET4bible.listening.RankFragment;
import com.iyuba.CET4bible.listening.ReadEvaluateFragment;

public class ListenFragmentAdapter extends FragmentStatePagerAdapter {
    protected static String[] CONTENT;
    private Context mContext;
    private String section;
    private String examYear;

    private ListenOriginalFragment listenOriginalFragment;

    private ReadEvaluateFragment readEvaluateFragment;

    private ListenTestFragment listenTestFragment;

    private RankFragment rankFragment;

    public ListenFragmentAdapter(Context context, FragmentManager fm, String section, String examYear) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        CONTENT = mContext.getResources().getStringArray(R.array.listen_title);
        this.section = section;
        this.examYear = examYear;
    }

    @Override
    public int getItemPosition(Object object) {
        super.getItemPosition(object);
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 1:

                if (listenOriginalFragment == null) {

                    listenOriginalFragment = new ListenOriginalFragment();
                }
                fragment = listenOriginalFragment;
                break;
            case 2:

                if (readEvaluateFragment == null) {

                    readEvaluateFragment = new ReadEvaluateFragment();
                }
                fragment = readEvaluateFragment;
                break;
            case 3:

                if (rankFragment == null) {

                    rankFragment = new RankFragment();
                }
                fragment = rankFragment;
                break;
            default:
                if (listenTestFragment == null) {

                    listenTestFragment = new ListenTestFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("section", section);
                    bundle.putString("examtime", examYear);
                    listenTestFragment.setArguments(bundle);
                }
                fragment = listenTestFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return CONTENT.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position];
    }


    public ListenOriginalFragment getListenOriginalFragment() {
        return listenOriginalFragment;
    }

    public void setListenOriginalFragment(ListenOriginalFragment listenOriginalFragment) {
        this.listenOriginalFragment = listenOriginalFragment;
    }

    public ReadEvaluateFragment getReadEvaluateFragment() {
        return readEvaluateFragment;
    }

    public void setReadEvaluateFragment(ReadEvaluateFragment readEvaluateFragment) {
        this.readEvaluateFragment = readEvaluateFragment;
    }

    public ListenTestFragment getListenTestFragment() {
        return listenTestFragment;
    }

    public void setListenTestFragment(ListenTestFragment listenTestFragment) {
        this.listenTestFragment = listenTestFragment;
    }

    public RankFragment getRankFragment() {
        return rankFragment;
    }

    public void setRankFragment(RankFragment rankFragment) {
        this.rankFragment = rankFragment;
    }
}

