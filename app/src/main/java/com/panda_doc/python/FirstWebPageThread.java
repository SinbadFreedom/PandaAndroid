package com.panda_doc.python;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.conf.Constants;
import com.panda_doc.python.view_model.UserInfoViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirstWebPageThread implements Runnable {

    private Context context;
    private UserInfoViewModel userInfoViewModel;
    private WebView webView;

    public FirstWebPageThread(Context context, UserInfoViewModel userInfoViewModel, WebView webView) {
        this.context = context;
        this.userInfoViewModel = userInfoViewModel;
        this.webView = webView;
    }

    @Override
    public void run() {
        userLogin();
    }

    /**
     * 本地数据初始化
     */
    private void userLogin() {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Conf.URL_USER_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String userId = jsonObject.getString("user_id");
//                            boolean isNew = jsonObject.getBoolean("is_new");
//                            int exp = jsonObject.getInt("exp");
                            userInfoViewModel.setUserId(userId);

                            /** userLogin 完成后 设置userInfoViewModel.setUserId(), 这里触发事件, post到catalog.php网页中，记录session */
                            String urlCatalog = Conf.URL_DOC_CONTENT_PRE + Constants.LAN_ZH_CN + "/" + Conf.URL_CATALOG;
                            String postData = Constants.KEY_HEADIMGURL + "=" + userInfoViewModel.getHeadimgurl().get()
                                    + "&" + Constants.KEY_NICKNAME + "=" + userInfoViewModel.getNickname().get()
                                    + "&" + Constants.KEY_USERID + "=" + userInfoViewModel.getUserId().get();
                            Log.d(Conf.DOC_TAG, postData);
                            webView.postUrl(urlCatalog, postData.getBytes());
                            Log.i(Conf.DOMAIN, "userLogin success userId " + userId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, context.getString(R.string.note_check_net_connect), Toast.LENGTH_LONG).show();
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
                map.put(Constants.KEY_UNIONID, userInfoViewModel.getUnionId());
//                map.put(Constants.KEY_ACCESS_TOKEN, userInfoViewModel.getOpenId());
//                map.put(Constants.KEY_REFRESH_TOKEN, userInfoViewModel.getOpenId());
//                map.put(Constants.KEY_SCOPE, userInfoViewModel.getOpenId());

                map.put(Constants.KEY_HEADIMGURL, userInfoViewModel.getHeadimgurl().get());
                map.put(Constants.KEY_NICKNAME, userInfoViewModel.getNickname().get());
//                map.put(Constants.KEY_LOGIN_TYPE, userInfoViewModel.getLoginType() + "");

//                map.put(Constants.KEY_CHANNEL, Constants.CHANNEL_APP_PYTHON);
                return map;
            }
        };
        queue.add(stringRequest);
    }

}
