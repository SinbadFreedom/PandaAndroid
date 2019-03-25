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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.version.CheckVersionDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {
    /**
     * 语言状态
     */
    public static final int LAN_ZH_CN = 1;
    public static final int LAN_EN = 2;
    public static int languageState = LAN_ZH_CN;

    private static String TAG_DIALOG = "TASK_FRAGE";

    public static String versionName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        versionCheck();
    }

    /**
     * 版本检测
     */
    private void versionCheck() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = null;
        switch (MainActivity.languageState) {
            case MainActivity.LAN_ZH_CN:
                url = Conf.URL_VERSION;
                break;
            case MainActivity.LAN_EN:
                url = Conf.URL_VERSION_CN;
                break;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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

}

