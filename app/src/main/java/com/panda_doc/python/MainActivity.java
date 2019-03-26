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

package com.panda_doc.python;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.conf.Constants;
import com.panda_doc.python.version.CheckVersionDialogFragment;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {


    private static String TAG_DIALOG = "TASK_FRAGE";

    public static String versionName = "";

    ImageButton wechatLogin;
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        wechatLogin = (ImageButton) findViewById(R.id.login_wechat);
        wechatLogin.setImageResource(R.drawable.icon48_wx_button);
        wechatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** 微信登陆*/
                getWxUserInfo();
            }
        });

        regToWx();
        versionCheck();
    }

    /**
     * 版本检测
     */
    private void versionCheck() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Conf.URL_VERSION_CN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String version = jsonObject.getString(Conf.KEY_VERSION);
                            String apk_url = jsonObject.getString(Conf.KEY_APK_URL);
                            String apk_name = jsonObject.getString(Conf.KEY_APK_NAME);

                            versionName = MainActivity.this.getPackageManager().
                                    getPackageInfo(MainActivity.this.getPackageName(), 0).versionName;
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

        /** 替换 fr.showNowe()方法, 原方法会报异常*/
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fragment, TAG_DIALOG);
        ft.commitAllowingStateLoss();
    }

    /**
     * app注册到微信
     */
    private void regToWx() {
        /** 通过WXAPIFactory工厂，获取IWXAPI的实例*/
        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, false);
        /** 将应用的appId注册到微信*/
        api.registerApp(Constants.WX_APP_ID);
    }

    /**
     * 获取用户信息
     */
    private void getWxUserInfo() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "python_doc_android_state";
        api.sendReq(req);
    }

}

