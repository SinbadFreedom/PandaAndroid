package com.panda_doc;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.panda_doc.main.R;
import com.panda_doc.conf.Constants;
import com.panda_doc.view_model.UserInfoViewModel;

public class DocActivity extends FragmentActivity {

    private UserInfoViewModel userInfoViewModel;
    private WebView mWebView;

    public static final int WEB_VIEW_STATE_LOAD_LOACAL_INDEX = 1;
    public static final int WEB_VIEW_STATE_LOAD_WEB_CONTENT = 2;
    public static final int WEB_VIEW_STATE_NORMAL = 3;
    public static int web_view_state = WEB_VIEW_STATE_LOAD_LOACAL_INDEX;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.doc_act);
        Intent intent = getIntent();
        String openId = intent.getStringExtra(Constants.KEY_OPENID);
        String unionId = intent.getStringExtra(Constants.KEY_UNIONID);
        String headimgurl = intent.getStringExtra(Constants.KEY_HEADIMGURL);
        String nickname = intent.getStringExtra(Constants.KEY_NICKNAME);
        String accessToken = intent.getStringExtra(Constants.KEY_ACCESS_TOKEN);
        String sex = intent.getStringExtra(Constants.KEY_SEX);
        String province = intent.getStringExtra(Constants.KEY_PROVINCE);
        String city = intent.getStringExtra(Constants.KEY_CITY);

        userInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);

        userInfoViewModel.setOpenId(openId);
        userInfoViewModel.setUnionId(unionId);
        userInfoViewModel.setHeadimgurl(headimgurl);
        userInfoViewModel.setNickname(nickname);

        mWebView = (WebView) findViewById(R.id.task_web_view);
        /** 统一初始化WebView设置*/
        initWebView(mWebView);
        /** i18n可以通过修改文件名对应不同的文件*/
        mWebView.loadUrl("file:///android_asset/" + getString(R.string.local_index_name));
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (web_view_state == WEB_VIEW_STATE_LOAD_LOACAL_INDEX) {
                    web_view_state = WEB_VIEW_STATE_LOAD_WEB_CONTENT;
                    /** 加载本地index.html成功后再启动，登陆线程，解决白屏问题*/
                    FirstWebPageThread firstWebPageThread = new FirstWebPageThread(DocActivity.this, userInfoViewModel, mWebView);
                    new Thread(firstWebPageThread).start();
                } else if (web_view_state == WEB_VIEW_STATE_LOAD_WEB_CONTENT) {
                    /**
                     * 清除本地index.html记录，防止goBack()，需要加成web内容成功后执行clearHistory()才生效，
                     * 后续的网页历史不清除, 切换为NORMAL状态
                     */
                    web_view_state = WEB_VIEW_STATE_NORMAL;
                    mWebView.clearHistory();
                }
            }
        });
    }

    /**
     * 后退键处理,按一次出提示文字，再按一次退出
     */
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            /** 论坛，评论页面返回*/
            mWebView.goBack();
        }
    }

    /**
     * 初始化WebView设置，修正在本页打开页面，不调用外部浏览器
     */
    private static void initWebView(WebView webView) {
        /** 设置webview不缓存*/
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        /** 修复webview 加载的网页中包含的js不生效*/
        webView.setWebChromeClient(new WebChromeClient());
        /** 修复加载外部css和js不生效的问题*/
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
    }
}