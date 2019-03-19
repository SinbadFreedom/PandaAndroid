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

package com.panda_doc.python.tasks;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.panda_doc.python.R;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.http.NetworkFragment;
import com.panda_doc.python.note.DocNoteAddFragment;
import com.panda_doc.python.note.DocNoteFragment;
import com.panda_doc.python.note.Title;
import com.panda_doc.python.version.CheckVersionDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

public class TasksActivity extends FragmentActivity {
    /**
     * 语言状态
     */
    public static final int LAN_ZH_CN = 1;
    public static final int LAN_EN = 2;
    public static int languageState = LAN_ZH_CN;

    private DrawerLayout mDrawerLayout;
    public static NetworkFragment mNetworkFragment;
    public static TaskAdapter taskAdapter;

    private static String TAG_DIALOG = "TASK_FRAGE";

    public static String versionName = "";

    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tasks_act);
        // Set up the navigation drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        // drawer
        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager());
        taskAdapter = new TaskAdapter(this);

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
                        Fragment fragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
                        if (fragment instanceof TasksFragment) {
                            ((TasksFragment) fragment).showWebPage(num, anchor);
                        }
                    } else {
                        Log.e(Conf.LOG_TAG, " numArr.length == 0 " + str);
                    }
                } else {
                    Log.e(Conf.LOG_TAG, "view instanceof LinearLayout false position " + position);
                }
                mDrawerLayout.closeDrawers();
            }
        });

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.garden_nav_fragment);
        versionCheck();
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

        Fragment fragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
        if (fragment instanceof DocNoteFragment) {
            navHostFragment.findNavController(fragment).navigateUp();
        } else if (fragment instanceof DocNoteAddFragment) {
            navHostFragment.findNavController(fragment).navigateUp();
        }
    }

    /**
     * 版本检测
     */
    private void versionCheck() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = null;
        switch (TasksActivity.languageState) {
            case TasksActivity.LAN_ZH_CN:
                url = Conf.URL_VERSION;
                break;
            case TasksActivity.LAN_EN:
                url = Conf.URL_VERSION_CN;
                break;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(" response " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String version = jsonObject.getString(Conf.KEY_VERSION);
                            String apk_url = jsonObject.getString(Conf.KEY_APK_URL);
                            String apk_name = jsonObject.getString(Conf.KEY_APK_NAME);

                            versionName = TasksActivity.this.getPackageManager().
                                    getPackageInfo(TasksActivity.this.getPackageName(), 0).versionName;
                            if (!version.equals(versionName)) {
                                showUpdateUI(apk_url, apk_name);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }

    private void showUpdateUI(String apkUrl, String apkName) {
        CheckVersionDialogFragment fragment = new CheckVersionDialogFragment();
        fragment.setApkUrl(this, apkUrl, apkName);
        fragment.showNow(getSupportFragmentManager(), TAG_DIALOG);
    }

    /**
     * 开启手势滑动
     */
    public void openSlide() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * 关闭手势滑动
     */
    public void closeSlide() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

}

