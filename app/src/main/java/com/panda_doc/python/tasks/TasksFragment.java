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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.panda_doc.python.R;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.util.NumberUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class TasksFragment extends Fragment {

    private WebView mWebView;
    private PopupMenu popupMenu;

    public static String currentPageNum = Conf.URL_HOME_PAGE_NUM;
    private static String anchor;

    public TasksFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tasks_frag, container, false);

        mWebView = root.findViewById(R.id.task_web_view);
        /** 设置webview不缓存*/
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        /** 修复webview 加载的网页中包含的js不生效*/
        mWebView.setWebChromeClient(new WebChromeClient());
        /** 修复加载外部css和js不生效的问题*/
        mWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        /** 显示Index主页*/
        this.showWebPage(currentPageNum, anchor);

        BottomNavigationView navigation = (BottomNavigationView) root.findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // 这里的view代表popupMenu需要依附的view
        popupMenu = new PopupMenu(this.getContext(), root.findViewById(R.id.navigation_translate));
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.language, popupMenu.getMenu());
        initPopMenuEvent();

        /** 开启手势滑动*/
        ((TasksActivity) getActivity()).openSlide();
        return root;
    }

    public void showWebPage(String pageNum, String anc) {
        currentPageNum = pageNum;
        anchor = anc;

        String url = null;
        switch (TasksActivity.languageState) {
            case TasksActivity.LAN_ZH_CN:
                url = Conf.URL_DOC_CONTENT_PRE + currentPageNum + ".cn.html";
                break;
            case TasksActivity.LAN_EN:
                url = Conf.URL_DOC_CONTENT_PRE + currentPageNum + ".html";
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
                case R.id.navigation_home:
                    /** 上一篇*/
                    if (mWebView != null) {
                        showLastPage();
                    }
                    return true;
                case R.id.navigation_about:
                    /** 关于*/
                    Toast.makeText(TasksFragment.this.getContext(), TasksActivity.versionName, Toast.LENGTH_LONG).show();
                    return true;
                case R.id.navigation_note:
                    /** 笔记*/
                    NavHostFragment.findNavController(TasksFragment.this).navigate(R.id.action_tasksFragment3_to_doc_note2);
                    return true;
                case R.id.navigation_translate:
                    /** 切换语言状态*/
                    popupMenu.show();
                    return true;
                case R.id.navigation_notifications:
                    /** 下一篇*/
                    if (mWebView != null) {
                        showNextPage();
                    }
                    return true;
            }
            return false;
        }
    };

    private void showLastPage() {
        boolean isInteger = NumberUtil.isInteger(currentPageNum);
        if (isInteger) {
            Integer integer = Integer.parseInt(currentPageNum);
            if (integer > 1) {
                integer--;
                showWebPage(integer + "", null);
            } else {
                Toast.makeText(this.getContext(), getResources().getString(R.string.this_is_the_first),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showNextPage() {
        boolean isInteger = NumberUtil.isInteger(currentPageNum);
        if (isInteger) {
            Integer num = Integer.parseInt(currentPageNum);
            if (num < TasksActivity.taskAdapter.getDocCount()) {
                num++;
                showWebPage(num + "", null);
            } else {
                Toast.makeText(this.getContext(), getResources().getString(R.string.this_is_the_last), Toast.LENGTH_LONG).show();
            }
        }
    }

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
                // 控件每一个item的点击事件
                String catalogUrl = null;
                switch (item.getItemId()) {
                    case R.id.popupmenu_ch_cn:
                        catalogUrl = Conf.URL_DOC_CONTENT_PRE + Conf.URL_CATALOG_CN;
                        TasksActivity.languageState = TasksActivity.LAN_ZH_CN;
                        break;
                    case R.id.popupmenu_en:
                        catalogUrl = Conf.URL_DOC_CONTENT_PRE + Conf.URL_CATALOG;
                        TasksActivity.languageState = TasksActivity.LAN_EN;
                        break;
                }

                if (catalogUrl != null) {
                    TasksActivity.mNetworkFragment.startDownload(catalogUrl);
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
