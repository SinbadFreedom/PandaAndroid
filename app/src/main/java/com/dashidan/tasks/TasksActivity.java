/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dashidan.tasks;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dashidan.R;
import com.dashidan.conf.Conf;
import com.dashidan.http.NetworkFragment;
import com.dashidan.util.ActivityUtils;
import com.dashidan.util.NumberUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

public class TasksActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout;
    private NetworkFragment mNetworkFragment;
    private TaskAdapter taskAdapter;
    private TasksFragment tasksFragment;

    private long firstPressedTime;
    /**
     * 语言状态
     */
    public static final int LAN_ZH_CN = 1;
    public static final int LAN_EN = 2;
    //    public static final int LAN_ZH_TW = 3;
    public static int languageState = LAN_ZH_CN;

    PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tasks_act);

//        AssetManager assets = this.getAssets();
//        Typeface.create(droidsansfallback);
//        this.getCol
//        FONT = Typeface.createFromAsset(assets, "font/droidsansfallback.ttf");

        // Set up the navigation drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
//        /** 禁止手势滑动*/
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // drawer
        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager());

        tasksFragment = (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = TasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }

        taskAdapter = new TaskAdapter(mDrawerLayout, tasksFragment, this);

        ListView listView = (ListView) findViewById(R.id.tasks_list);
        listView.setAdapter(taskAdapter);
        /** 点击标题事件*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof LinearLayout) {
                    TextView textView = (TextView) ((LinearLayout) view).getChildAt(0);
                    String str = (String) textView.getText();
                    str = str.trim();
                    String[] numArr = str.split("\\.");
                    if (numArr.length > 0) {
                        /** 获取文章编号*/
                        String num = numArr[0];
                        /** 获取完整标题*/
                        Title title = taskAdapter.getItem(position);
                        /**
                         * 获取标题id
                         * 与showdown转化的html标题id规则统一，目前是去掉了空格和“.”， 变小写
                         * 有可能有特殊字符过滤，后续添加。
                         */
                        String anchor = title.getFullTitle().trim().split(" " + "")[0];
                        /** 切换文章内容*/
                        tasksFragment.showWebPage(num, anchor);
                    } else {
                        Log.e(Conf.LOG_TAG, " numArr.length == 0 " + str);
                    }
                } else {
                    Log.e(Conf.LOG_TAG, "view instanceof LinearLayout false position " + position);
                }
                mDrawerLayout.closeDrawers();
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // 这里的view代表popupMenu需要依附的view
        popupMenu = new PopupMenu(this, findViewById(R.id.navigation_translate));
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.language, popupMenu.getMenu());
        initPopMenuEvent();
    }

    public void updateFromDownload(ArrayList<String> result) {
        if (null == result) {
            Log.e(Conf.LOG_TAG, "updateFromDownload null == result");
        } else {
            taskAdapter.initContents(result);
        }
    }

    public void finishDownloading() {
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    /** 上一篇*/
                    if (tasksFragment.getmWebView() != null) {
                        showLastPage();
                    }
                    return true;
                case R.id.navigation_homepage:
                    /** 主页*/
                    if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                        mDrawerLayout.closeDrawers();
                    }
                    tasksFragment.showWebPage(Conf.URL_HOME_PAGE_NUM, null);
                    return true;
                case R.id.navigation_translate:
                    /** 切换语言状态*/
                    popupMenu.show();
                    return true;
                case R.id.navigation_notifications:
                    /** 下一篇*/
                    if (tasksFragment.getmWebView() != null) {
                        showNextPage();
                    }
                    return true;
            }
            return false;
        }
    };

    /**
     * 后退键处理,按一次出提示文字，再按一次退出
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            /** 按返回键时如果开启了滑动屏，则关闭*/
            return;
        }

        if (System.currentTimeMillis() - firstPressedTime < Conf.TOAST_EXIT_SHOW_TIME) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, getResources().getString(R.string.toast_press_to_exit),
                    Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }

    public void showLastPage() {
        boolean isInteger = NumberUtil.isInteger(tasksFragment.getCurrentPageNum());
        if (isInteger) {
            Integer integer = Integer.parseInt(tasksFragment.getCurrentPageNum());
            if (integer > 1) {
                integer--;
                tasksFragment.showWebPage(integer + "", null);
            } else {
                Toast.makeText(this, getResources().getString(R.string.this_is_the_first),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showNextPage() {
        boolean isInteger = NumberUtil.isInteger(tasksFragment.getCurrentPageNum());
        if (isInteger) {
            Integer num = Integer.parseInt(tasksFragment.getCurrentPageNum());
            if (num < taskAdapter.getDocCount()) {
                num++;
                tasksFragment.showWebPage(num + "", null);
            } else {
                Toast.makeText(this, getResources().getString(R.string.this_is_the_last), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 切换语言状态
     */
    public void updateCharset() {
        tasksFragment.showWebPage(tasksFragment.getCurrentPageNum(), tasksFragment.getAnchor());
    }

    private void initPopMenuEvent() {
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 控件每一个item的点击事件
                String catalogUrl = null;
                switch (item.getItemId()) {
                    case R.id.popupmenu_ch_cn:
                        catalogUrl = Conf.URL_DOC_CONTENT_PRE + Conf.URL_CATALOG_CN;
                        languageState = LAN_ZH_CN;
                        break;
                    case R.id.popupmenu_en:
                        catalogUrl = Conf.URL_DOC_CONTENT_PRE + Conf.URL_CATALOG;
                        languageState = LAN_EN;
                        break;
                    case R.id.popupmenu_ch_tw:

                        break;
                }

                if (catalogUrl != null) {
                    mNetworkFragment.startDownload(catalogUrl);
                }

                updateCharset();
                return true;
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // 控件消失时的事件
            }
        });
    }
}

