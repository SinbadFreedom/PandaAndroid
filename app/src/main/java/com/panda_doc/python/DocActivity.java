package com.panda_doc.python;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.conf.Constants;
import com.panda_doc.python.uikit.NetworkUtil;
import com.panda_doc.python.view_model.UserInfoViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DocActivity extends FragmentActivity {

    private UserInfoViewModel userInfoViewModel;
    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_act);

        Intent intent = getIntent();
        String openId = intent.getStringExtra(Constants.KEY_OPENID);
        String headimgurl = intent.getStringExtra(Constants.KEY_HEADIMGURL);
        String nickname = intent.getStringExtra(Constants.KEY_NICKNAME);
        String accessToken = intent.getStringExtra(Constants.KEY_ACCESS_TOKEN);
        String sex = intent.getStringExtra(Constants.KEY_SEX);
        String province = intent.getStringExtra(Constants.KEY_PROVINCE);
        String city = intent.getStringExtra(Constants.KEY_CITY);
        byte[] imgdata = intent.getByteArrayExtra(Constants.KEY_HEAD_IMG_DATA);
        int loginType = (int) intent.getIntExtra(Constants.KEY_LOGIN_TYPE, 0);

        userInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);

        userInfoViewModel.setOpenId(openId);
        userInfoViewModel.setHeadimgurl(headimgurl);
        userInfoViewModel.setNickname(nickname);
        userInfoViewModel.setLoginType(loginType);

        mWebView = (WebView) findViewById(R.id.task_web_view);
        /** 统一初始化WebView设置*/
        NetworkUtil.initWebView(mWebView);
        /** 修复加载外部css和js不生效的问题 重置当前页面的信息和锚点信息*/
        mWebView.setWebViewClient(new WebViewClient() {
        });

//        /** 首次开启应用 注册 名字和头像变化事件 */
//        userInfoViewModel.getNickname().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable sender, int propertyId) {
//                String nameStr = ((ObservableField<String>) sender).get();
//                Log.i(Conf.DOMAIN, " " + propertyId);
//            }
//        });

//        userInfoViewModel.getUserId().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable sender, int propertyId) {
//                /** userLogin 完成后 设置userInfoViewModel.setUserId(), 这里触发事件, post到catalog.php网页中，记录session */
//                String urlCatalog = Conf.URL_DOC_CONTENT_PRE + Constants.LAN_ZH_CN + "/" + Conf.URL_CATALOG;
//                String postData = Constants.KEY_OPENID + "=" + userInfoViewModel.getOpenId()
//                        + "&" + Constants.KEY_HEADIMGURL + "=" + userInfoViewModel.getHeadimgurl().get()
//                        + "&" + Constants.KEY_NICKNAME + "=" + userInfoViewModel.getNickname().get()
//                        + "&" + Constants.KEY_USERID + "=" + userInfoViewModel.getUserId().get();
//                Log.d(Conf.DOC_TAG, postData);
//                mWebView.postUrl(urlCatalog, postData.getBytes());
//            }
//        });

        /** 登陆*/
        userLogin();
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
     * 本地数据初始化
     */
    private void userLogin() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Conf.URL_USER_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String userId = jsonObject.getString("user_id");
                            boolean isNew = jsonObject.getBoolean("is_new");
                            int exp = jsonObject.getInt("exp");
                            userInfoViewModel.setUserId(userId);

                            /** userLogin 完成后 设置userInfoViewModel.setUserId(), 这里触发事件, post到catalog.php网页中，记录session */
                            String urlCatalog = Conf.URL_DOC_CONTENT_PRE + Constants.LAN_ZH_CN + "/" + Conf.URL_CATALOG;
                            String postData = Constants.KEY_OPENID + "=" + userInfoViewModel.getOpenId()
                                    + "&" + Constants.KEY_HEADIMGURL + "=" + userInfoViewModel.getHeadimgurl().get()
                                    + "&" + Constants.KEY_NICKNAME + "=" + userInfoViewModel.getNickname().get()
                                    + "&" + Constants.KEY_USERID + "=" + userInfoViewModel.getUserId().get();
                            Log.d(Conf.DOC_TAG, postData);
                            mWebView.postUrl(urlCatalog, postData.getBytes());


                            Log.i(Conf.DOMAIN, "userLogin success userId " + userId + " isNew " + isNew);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DocActivity.this, getString(R.string.note_check_net_connect), Toast.LENGTH_LONG).show();
                Log.e(Conf.DOMAIN, error.fillInStackTrace().toString());
            }
        }) {
            /**
             * 加入post参数 除图像外其他的数据
             */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put(Constants.KEY_OPENID, userInfoViewModel.getOpenId());
                map.put(Constants.KEY_ACCESS_TOKEN, userInfoViewModel.getOpenId());
                map.put(Constants.KEY_REFRESH_TOKEN, userInfoViewModel.getOpenId());
                map.put(Constants.KEY_SCOPE, userInfoViewModel.getOpenId());

                map.put(Constants.KEY_HEADIMGURL, userInfoViewModel.getHeadimgurl().get());
                map.put(Constants.KEY_NICKNAME, userInfoViewModel.getNickname().get());
                map.put(Constants.KEY_LOGIN_TYPE, userInfoViewModel.getLoginType() + "");

                map.put(Constants.KEY_CHANNEL, Constants.CHANNEL_APP_PYTHON);
                return map;
            }
        };
        queue.add(stringRequest);
    }
}
