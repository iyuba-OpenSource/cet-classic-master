package com.iyuba.CET4bible.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.iyuba.CET4bible.R;
import com.iyuba.CET4bible.viewpager.TestTpyeFragmentAdapter;
import com.iyuba.base.BaseActivity;

/**
 * 真题  - 题型页面
 */

public class TestTypeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("题型");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(mContext, FavoriteActivity.class));
                return true;
            }
        });


        toolbar.setNavigationIcon(com.iyuba.configation.R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ViewPager viewPager = findView(R.id.viewpager);
        viewPager.setAdapter(new TestTpyeFragmentAdapter(mContext, getSupportFragmentManager()));


        TabLayout tabLayout = findView(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite_question, menu);
        return true;
    }
}
