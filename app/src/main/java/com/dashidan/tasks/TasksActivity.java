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
import android.view.View;
import android.widget.ListView;

import com.dashidan.R;
import com.dashidan.http.NetworkFragment;
import com.dashidan.util.ActivityUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

public class TasksActivity extends FragmentActivity {

    public static final String LOG_TAG = "dashidan.com";

    private DrawerLayout mDrawerLayout;
    private NetworkFragment mNetworkFragment;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_act);

        // Set up the navigation drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        // 禁止手势滑动
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // drawer
        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), "https://dashidan.com/and_doc/python3/catalog.md");

        TasksFragment tasksFragment =
                (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = TasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }

        taskAdapter = new TaskAdapter(mDrawerLayout, tasksFragment);
        ListView listView = (ListView) findViewById(R.id.tasks_list);
        listView.setAdapter(taskAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_task);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                    mDrawerLayout.closeDrawers();
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    public void updateFromDownload(ArrayList<String> result) {
        taskAdapter.setContents(result);
    }

    public void finishDownloading() {
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }
}

