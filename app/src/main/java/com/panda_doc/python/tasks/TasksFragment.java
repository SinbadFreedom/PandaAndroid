/*
 * Copyright 2016, The Android Open Source Project
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

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.panda_doc.python.MainActivity;
import com.panda_doc.python.R;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.note.Title;

import java.io.UnsupportedEncodingException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class TasksFragment extends Fragment {

    private WebView mWebView;
    private PopupMenu popupMenu;

    public static String currentPageNum;
    private static String anchor;

    private DrawerLayout mDrawerLayout;
    public static TaskAdapter taskAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tasks_act, container, false);

        mDrawerLayout = (DrawerLayout) root.findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        taskAdapter = new TaskAdapter(this.getContext());

        ListView listView = (ListView) root.findViewById(R.id.tasks_list);
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
                        showWebPage(num, anchor);
                    } else {
                        Log.e(Conf.LOG_TAG, " numArr.length == 0 " + str);
                    }
                } else {
                    Log.e(Conf.LOG_TAG, "view instanceof LinearLayout false position " + position);
                }
                closeTaskDrawer();
            }
        });


        mWebView = root.findViewById(R.id.task_web_view);
        /** 设置webview不缓存*/
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        /** 修复webview 加载的网页中包含的js不生效*/
        mWebView.setWebChromeClient(new WebChromeClient());
        /** 修复加载外部css和js不生效的问题*/
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (!url.contains(Conf.URL_DOC_CONTENT_PRE)) {
                    /** 非api文章返回，置空 currentPageNum*/
                    currentPageNum = null;
                    anchor = null;
                    return;
                }

                String docName = url.replace(Conf.URL_DOC_CONTENT_PRE, "");
                String[] arr = docName.split("\\.");
                if (arr.length > 0) {
                    String index = arr[0];
                    currentPageNum = index;
                    anchor = null;
                } else {
                    Log.e(Conf.LOG_TAG, "arr.length == 0 url " + url);
                }
            }

        });
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        /** currentPageNum 为空 显示Index主页, 不为空 显示当前页， 用在从笔记页面切换回来的时候*/
        if (null == currentPageNum) {
            this.showWebPage(Conf.URL_INDEX, anchor);
        } else {
            this.showWebPage(currentPageNum, anchor);
        }

        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // 这里的view代表popupMenu需要依附的view
        popupMenu = new PopupMenu(this.getContext(), root.findViewById(R.id.navigation_translate));
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.language, popupMenu.getMenu());
        initPopMenuEvent();

        getCatalog();
        return root;
    }

    private void showWebPage(String pageNum, String anc) {
        if (null == pageNum) {
            Log.e(Conf.LOG_TAG, "showWebPage pageNum == null");
            return;
        }
        anchor = anc;
        String url = null;
        switch (MainActivity.languageState) {
            case MainActivity.LAN_ZH_CN:
                url = Conf.URL_DOC_CONTENT_PRE + pageNum + ".cn.html";
                break;
            case MainActivity.LAN_EN:
                url = Conf.URL_DOC_CONTENT_PRE + pageNum + ".html";
                break;
        }

        if (anchor != null) {
            /** 跳转锚点*/
            url = url + "#" + anchor;
        }
        mWebView.loadUrl(url);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_about:
                    /** 关于*/
                    Toast.makeText(TasksFragment.this.getContext(), MainActivity.versionName, Toast.LENGTH_LONG).show();
                    return true;
                case R.id.navigation_note:
                    /** 笔记*/
                    NavHostFragment.findNavController(TasksFragment.this).navigate(R.id.action_tasksFragment3_to_doc_note2);
                    return true;
                case R.id.navigation_translate:
                    /** 切换语言状态*/
                    popupMenu.show();
                    return true;
            }
            return false;
        }
    };

    /**
     * 切换语言状态
     */
    private void updateCharset() {
        showWebPage(currentPageNum, anchor);
    }

    private void initPopMenuEvent() {
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                /** 控件每一个item的点击事件*/
                int lastLanaguage = MainActivity.languageState;
                switch (item.getItemId()) {
                    case R.id.popupmenu_ch_cn:
                        MainActivity.languageState = MainActivity.LAN_ZH_CN;
                        break;
                    case R.id.popupmenu_en:
                        MainActivity.languageState = MainActivity.LAN_EN;
                        break;
                }

                if (lastLanaguage != MainActivity.languageState) {
                    getCatalog();
                }

                updateCharset();
                return true;
            }
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                /** 控件消失时的事件*/
            }
        });
    }

    /**
     * 按返回键时如果开启了滑动屏，则关闭
     */
    public void closeTaskDrawer() {
        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }
    }

    /**
     * 初始化目录内容
     */
    private void getCatalog() {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());

        String catalogUrl = null;
        switch (MainActivity.languageState) {
            case MainActivity.LAN_ZH_CN:
                catalogUrl = Conf.URL_DOC_CONTENT_PRE + Conf.URL_CATALOG_CN;
                break;
            case MainActivity.LAN_EN:
                catalogUrl = Conf.URL_DOC_CONTENT_PRE + Conf.URL_CATALOG;
                break;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, catalogUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("")) {
                            return;
                        }
                        String[] titles = response.split("\n");
                        taskAdapter.initContents(titles);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Conf.LOG_TAG, error.fillInStackTrace().toString());
            }
        }) {

            /** 解决 Volley utf-8 乱码*/
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // Since minSdkVersion = 8, we can't call
                    // new String(response.data, Charset.defaultCharset())
                    // So suppress the warning instead.
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(stringRequest);
    }

}
