package com.panda_doc.python;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.panda_doc.python.note.DocNoteAddFragment;
import com.panda_doc.python.note.DocNoteFragment;
import com.panda_doc.python.rank_list.RankListFragment;
import com.panda_doc.python.tasks.TasksFragment;
import com.panda_doc.python.uikit.NetworkUtil;
import com.panda_doc.python.view_model.UserInfoViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

public class DocActivity extends FragmentActivity {

    private static String TAG = "MicroMsg.UserInfoActivity";

    public static FragmentManager fragmentManager;
    private NavHostFragment navHostFragment;

    private static String refreshToken;
    private static String openId;
    private static String accessToken;
    private static String scope;

    private static MyHandler handler;

    private UserInfoViewModel userInfoViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_act);

        handler = new MyHandler(this);

        Intent intent = getIntent();
        openId = intent.getStringExtra("openId");
        accessToken = intent.getStringExtra("accessToken");
        refreshToken = intent.getStringExtra("refreshToken");
        scope = intent.getStringExtra("scope");

        fragmentManager = getSupportFragmentManager();
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.doc_nav_fragment);

        userInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);

        /** 获取用户信息和头像*/
        if (accessToken != null && openId != null) {
            NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/auth?" +
                    "access_token=%s&openid=%s", accessToken, openId), NetworkUtil.CHECK_TOKEN);
        } else {
            Toast.makeText(this, "请先获取code", Toast.LENGTH_LONG).show();
        }
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

    private class MyHandler extends Handler {
        private final WeakReference<DocActivity> userInfoActivityWR;

        public MyHandler(DocActivity userInfoActivity) {
            userInfoActivityWR = new WeakReference<DocActivity>(userInfoActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            int tag = msg.what;
            Bundle data = msg.getData();
            JSONObject json = null;
            switch (tag) {
                case NetworkUtil.CHECK_TOKEN: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        int errcode = json.getInt("errcode");
                        if (errcode == 0) {
                            NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/userinfo?" +
                                    "access_token=%s&openid=%s", accessToken, openId), NetworkUtil.GET_INFO);
                        } else {
                            NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
                                            "appid=%s&grant_type=refresh_token&refresh_token=%s", "wxd930ea5d5a258f4f", refreshToken),
                                    NetworkUtil.REFRESH_TOKEN);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                }
                case NetworkUtil.REFRESH_TOKEN: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        openId = json.getString("openid");
                        accessToken = json.getString("access_token");
                        refreshToken = json.getString("refresh_token");
                        scope = json.getString("scope");
                        NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/userinfo?" +
                                "access_token=%s&openid=%s", accessToken, openId), NetworkUtil.GET_INFO);
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                }
                case NetworkUtil.GET_INFO: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        final String nickname, sex, province, city, country, headimgurl;
                        headimgurl = json.getString("headimgurl");
                        NetworkUtil.getImage(handler, headimgurl, NetworkUtil.GET_IMG);
                        String encode = getcode(json.getString("nickname"));
                        nickname = new String(json.getString("nickname").getBytes(encode), "utf-8");
                        sex = json.getString("sex");
                        province = json.getString("province");
                        city = json.getString("city");
                        country = json.getString("country");

                        userInfoViewModel.setNickname(nickname);
                        userInfoViewModel.setHeadimgurl(headimgurl);
                        userInfoViewModel.setSex(sex);
                        userInfoViewModel.setCountry(country);
                        userInfoViewModel.setProvince(province);
                        userInfoViewModel.setCity(city);
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    break;
                }
                case NetworkUtil.GET_IMG: {
                    byte[] imgdata = data.getByteArray("imgdata");
                    final Bitmap bitmap;
                    if (imgdata != null) {
                        bitmap = BitmapFactory.decodeByteArray(imgdata, 0, imgdata.length);
                    } else {
                        bitmap = null;
                        showToast(userInfoActivityWR.get(), "头像图片获取失败");
                    }

                    userInfoViewModel.setHeadBitmap(bitmap);
                    break;
                }
            }
        }
    }

    private static String getcode(String str) {
        String[] encodelist = {"GB2312", "ISO-8859-1", "UTF-8", "GBK", "Big5", "UTF-16LE", "Shift_JIS", "EUC-JP"};
        for (String anEncodelist : encodelist) {
            try {
                if (str.equals(new String(str.getBytes(anEncodelist), anEncodelist))) {
                    return anEncodelist;
                }
            } catch (Exception ignored) {

            }
        }
        return "";
    }

    private static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
