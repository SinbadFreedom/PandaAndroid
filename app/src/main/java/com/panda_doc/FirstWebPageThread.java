package com.panda_doc;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.panda_doc.conf.Conf;
import com.panda_doc.conf.Constants;
import com.panda_doc.view_model.UserInfoViewModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FirstWebPageThread {

    private Context context;
    private UserInfoViewModel userInfoViewModel;
    private WebView webView;

    public FirstWebPageThread(Context context, UserInfoViewModel userInfoViewModel, WebView webView) {
        this.context = context;
        this.userInfoViewModel = userInfoViewModel;
        this.webView = webView;
    }

    public void userLogin() {
        String urlCatalog = Conf.DOMAIN + "/index.html?nav=doc&tag=" + Conf.DOC_TAG + "&language=" + Constants.LAN_ZH_CN
                + "&contentid=1&anchor=1_";
        try {
            urlCatalog = URLEncoder.encode(urlCatalog, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String postData = Constants.KEY_HEADIMGURL + "=" + userInfoViewModel.getHeadimgurl().get()
                + "&" + Constants.KEY_NICKNAME + "=" + userInfoViewModel.getNickname().get()
                + "&" + Constants.KEY_USERID + "=" + userInfoViewModel.getUserId().get()
                + "&" + Constants.URL_RELOAD + "=" + urlCatalog
                + "&" + Constants.KEY_OPENID + "=" + userInfoViewModel.getOpenId()
                + "&" + Constants.KEY_UNIONID + "=" + userInfoViewModel.getUnionId();

        Log.d(Conf.DOC_TAG, postData);

        webView.postUrl(Conf.URL_USER_LOGIN, postData.getBytes());
    }
}
