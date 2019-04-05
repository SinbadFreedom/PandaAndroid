package com.panda_doc.python.tasks;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.view_model.UserInfoViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateExp extends Thread {
    Context context;
    UserInfoViewModel userInfoViewModel;

    public UpdateExp(Context context, UserInfoViewModel userInfoViewModel) {
        this.context = context;
        this.userInfoViewModel = userInfoViewModel;
    }


    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(Conf.UPDATE_EXP_TIME);
                updateExp();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateExp() {
        if (userInfoViewModel.getOpenId() == null) {
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Conf.URL_UPDATE_EXP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /** 更新经验返回结果*/
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has(Conf.KEY_STATE)) {
                                int state = jsonObject.getInt(Conf.KEY_STATE);
                                if (state == 0) {
                                    int exp = jsonObject.getInt(Conf.KEY_EXP);
                                    userInfoViewModel.setExp(exp);
                                    Toast.makeText(context, "+1 Exp: " + exp, Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Conf.DOMAIN, error.fillInStackTrace().toString());
            }

        }) {
            /**
             * 加入post参数
             */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // 请求参数
                Map<String, String> map = new HashMap<>();
                //new 一个Map  参数放到Map中
                map.put("openid", userInfoViewModel.getOpenId());
                map.put("userid", userInfoViewModel.getUserId());
                return map;
            }
        };

        queue.add(stringRequest);
    }
}
