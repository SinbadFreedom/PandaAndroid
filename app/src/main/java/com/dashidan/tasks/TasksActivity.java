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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

public class TasksActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout;
    private NetworkFragment mNetworkFragment;
    private TaskAdapter taskAdapter;
    private TasksFragment tasksFragment;

    private long firstPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tasks_act);

        // Set up the navigation drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
//        /** 禁止手势滑动*/
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // drawer
        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), Conf.URL_CATALOG);

        tasksFragment = (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = TasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }

        taskAdapter = new TaskAdapter(mDrawerLayout, tasksFragment);
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
                        String fullTitle = title.getFullTitle().trim();
                        /**
                         * 获取标题id
                         * 与showdown转化的html标题id规则统一，目前是去掉了空格和“.”， 变小写
                         * 有可能有特殊字符过滤，后续添加。
                         */
                        String anchor = fullTitle.replaceAll("\\.", "")
                                .replaceAll(" ", "")
                                .toLowerCase();
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
    }

    public void updateFromDownload(ArrayList<String> result) {
        if (null == result) {
            Log.e(Conf.LOG_TAG, "updateFromDownload null == result");
        } else {
            taskAdapter.setContents(result);
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
                case R.id.navigation_dashboard:
                    /** 目录*/
                    if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                        mDrawerLayout.closeDrawers();
                    } else {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
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
        if (tasksFragment.getCurrentPageNum() != null) {
            int num = Integer.parseInt(tasksFragment.getCurrentPageNum());
            if (num > 1) {
                num--;
                tasksFragment.showWebPage(num + "", null);
            } else {
                Toast.makeText(this, getResources().getString(R.string.this_is_the_first),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showNextPage() {
        if (tasksFragment.getCurrentPageNum() != null) {
            int num = Integer.parseInt(tasksFragment.getCurrentPageNum());
            if (num < taskAdapter.getDocCount()) {
                num++;
                tasksFragment.showWebPage(num + "", null);
            } else {
                Toast.makeText(this, getResources().getString(R.string.this_is_the_last),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}

