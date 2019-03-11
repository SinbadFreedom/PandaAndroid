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

package com.dashidan.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dashidan.R;
import com.dashidan.conf.Conf;
import com.dashidan.http.JavaScriptWebViewClient;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TasksFragment extends Fragment {

    private WebView mWebView;
    private String currentPageNum;
    private String anchor;

    public TasksFragment() {
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tasks_frag, container, false);

        mWebView = root.findViewById(R.id.task_web_view);

        /** 修复webview 加载的网页中包含的js不生效*/
        mWebView.setWebChromeClient(new WebChromeClient());
        /** 修复加载外部css和js不生效的问题*/
        mWebView.setWebViewClient(new JavaScriptWebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        this.showWebPage(Conf.URL_HOME_PAGE_NUM, null);
        /** 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开*/
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /** 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器*/
                view.loadUrl(url);
                return true;
            }
        });

        return root;
    }

    public void showWebPage(String pageNum, String anchor) {
        this.currentPageNum = pageNum;
        this.anchor = anchor;

        switch (TasksActivity.languageState) {
            case TasksActivity.LAN_ZH_CN:
                pageNum = pageNum + ".cn";
                break;
            case TasksActivity.LAN_EN:
                break;
        }

        String url = Conf.URL_DOC_CONTENT_PRE + pageNum + ".html";

        if (anchor != null) {
            /** 跳转锚点*/
            url = url + "#" + anchor;
        }
        mWebView.loadUrl(url);
    }

    public WebView getmWebView() {
        return mWebView;
    }

    public String getCurrentPageNum() {
        return currentPageNum;
    }

    public String getAnchor() {
        return anchor;
    }
}
