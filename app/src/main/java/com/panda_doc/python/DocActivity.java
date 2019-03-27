package com.panda_doc.python;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.panda_doc.python.conf.Constants;
import com.panda_doc.python.note.DocNoteAddFragment;
import com.panda_doc.python.note.DocNoteFragment;
import com.panda_doc.python.rank_list.RankListFragment;
import com.panda_doc.python.tasks.TasksFragment;
import com.panda_doc.python.view_model.UserInfoViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

public class DocActivity extends FragmentActivity {

    public static FragmentManager fragmentManager;
    private NavHostFragment navHostFragment;

    private UserInfoViewModel userInfoViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_act);

        Intent intent = getIntent();
        String openId = intent.getStringExtra(Constants.KEY_OPENID);
        String accessToken = intent.getStringExtra(Constants.KEY_ACCESS_TOKEN);
        String refreshToken = intent.getStringExtra(Constants.KEY_REFRESH_TOKEN);
        String scope = intent.getStringExtra(Constants.KEY_SCOPE);

        String headimgurl = intent.getStringExtra(Constants.KEY_HEADIMGURL);
        String nickname = intent.getStringExtra(Constants.KEY_NICKNAME);
        String sex = intent.getStringExtra(Constants.KEY_SEX);
        String country = intent.getStringExtra(Constants.KEY_COUNTRY);
        String province = intent.getStringExtra(Constants.KEY_PROVINCE);
        String city = intent.getStringExtra(Constants.KEY_CITY);
        byte[] imgdata = intent.getByteArrayExtra(Constants.KEY_HEAD_IMG_DATA);

        userInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);

        userInfoViewModel.setOpenId(openId);
        userInfoViewModel.setAccessToken(accessToken);
        userInfoViewModel.setRefreshToken(refreshToken);
        userInfoViewModel.setScope(scope);

        userInfoViewModel.setHeadimgurl(headimgurl);
        userInfoViewModel.setNickname(nickname);
        userInfoViewModel.setSex(sex);
        userInfoViewModel.setCountry(country);
        userInfoViewModel.setProvince(province);
        userInfoViewModel.setCity(city);

        if (imgdata != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgdata, 0, imgdata.length);
            userInfoViewModel.setHeadBitmap(bitmap);
        } else {
            Toast.makeText(this, "头像图片获取失败", Toast.LENGTH_LONG).show();
        }

        fragmentManager = getSupportFragmentManager();
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.doc_nav_fragment);

        this.userLogin();
    }

    /**
     * 后退键处理,按一次出提示文字，再按一次退出
     */
    @Override
    public void onBackPressed() {
        Fragment fragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
        if (fragment instanceof DocNoteFragment) {
            NavHostFragment.findNavController(fragment).navigateUp();
        } else if (fragment instanceof DocNoteAddFragment) {
            NavHostFragment.findNavController(fragment).navigateUp();
        } else if (fragment instanceof TasksFragment) {
            ((TasksFragment) (fragment)).closeTaskDrawer();
        } else if (fragment instanceof RankListFragment) {
            NavHostFragment.findNavController(fragment).navigateUp();
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
                            Toast.makeText(DocActivity.this, getString(R.string.note_send_success), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DocActivity.this, getString(R.string.note_check_net_connect), Toast.LENGTH_LONG).show();
                Log.e(Conf.LOG_TAG, error.fillInStackTrace().toString());
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
                map.put(Constants.KEY_SEX, userInfoViewModel.getSex().get());
                map.put(Constants.KEY_PROVINCE, userInfoViewModel.getProvince().get());
                map.put(Constants.KEY_CITY, userInfoViewModel.getCity().get());
                map.put(Constants.KEY_COUNTRY, userInfoViewModel.getCountry().get());

                map.put(Constants.KEY_CHANNEL, Constants.CHANNEL_APP_PYTHON);
                return map;
            }
        };
        queue.add(stringRequest);
    }
}
