package com.iyuba.CET4bible.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.fragment.FavoriteFragment;
import com.iyuba.CET4bible.util.FavoriteUtil;
import com.iyuba.base.BaseActivity;

/**
 * FavoriteActivity
 *
 * @author wayne
 * @date 2017/12/12
 */
public class FavoriteActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("收藏");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(com.iyuba.base.R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ViewPager viewPager = findView(R.id.viewpager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            String[] title = {"听力", "翻译", "写作", "选词填空", "段落匹配", "仔细阅读"};
            int[] type = new int[]{
                    FavoriteUtil.Type.listening, FavoriteUtil.Type.translate,
                    FavoriteUtil.Type.write, FavoriteUtil.Type.fillInBlack,
                    FavoriteUtil.Type.paragraph, FavoriteUtil.Type.reading
            };

            @Override
            public Fragment getItem(int position) {
                return FavoriteFragment.getInstance(type[position]);
            }

            @Override
            public int getCount() {
                return title.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return title[position];
            }
        });


        TabLayout tabLayout = findView(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, View.generateViewId(), 0, "长按可删除收藏").setIcon(com.iyuba.base.R.drawable.ic_more_vert_white_24dp);
        return super.onCreateOptionsMenu(menu);
    }
}
