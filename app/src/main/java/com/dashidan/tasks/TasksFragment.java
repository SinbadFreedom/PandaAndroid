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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dashidan.R;
import com.dashidan.conf.Conf;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TasksFragment extends Fragment {

    private WebView mWebView;

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
        this.showWebPage(1);
        /** 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开*/
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /** 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器*/
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    /** 按返回键操作并且能回退网页*/
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                        /** 后退*/
                        mWebView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
        return root;
    }

    public void showWebPage(int pageNum) {
        mWebView.loadUrl(Conf.URL_DOC_CONTENT_PRE + pageNum + ".html");
    }

}
