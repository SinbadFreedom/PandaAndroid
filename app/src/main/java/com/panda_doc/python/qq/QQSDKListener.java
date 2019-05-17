package com.panda_doc.python.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.panda_doc.python.DocActivity;
import com.panda_doc.python.MainActivity;
import com.panda_doc.python.conf.Constants;
import com.panda_doc.python.view_model.UserInfoViewModel;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class QQSDKListener implements IUiListener {

    private static final String TAG = MainActivity.class.getName();

    private Activity activity;
    private Tencent mTencent;
    private UserInfo mInfo;
    private boolean isServerSideLogin = false;

    /**
     * token
     */
    private String openId;
    private String accessToken;
    /**
     * user info
     */
    private String nickname;
    private String sex;
    private String province;
    private String city;
    private String headimgurl;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                /** 用户文本信息*/
                /**
                 ret
                 msg
                 is_lost
                 nickname
                 gender
                 province
                 city
                 year
                 constellation
                 figureurl
                 figureurl_1
                 figureurl_2
                 figureurl_qq_1
                 figureurl_qq_2
                 figureurl_qq
                 figureurl_type
                 is_yellow_vip
                 vip
                 yellow_vip_level
                 level
                 is_yellow_year_vip
                 * */
                JSONObject response = (JSONObject) msg.obj;
                try {
                    nickname = response.getString("nickname");
                    sex = response.getString("gender");
                    province = response.getString("province");
                    city = response.getString("city");
                    headimgurl = response.getString("figureurl");
                    updateQQLoginState();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    public QQSDKListener(Activity activity) {
        this.activity = activity;
        mTencent = Tencent.createInstance(Constants.APP_ID_QQ, activity.getApplicationContext());
    }

    public void loginQQ() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(activity, "all", this);
            isServerSideLogin = false;
            Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
        } else {
            if (isServerSideLogin) { // Server-Side 模式的登陆, 先退出，再进行SSO登陆
                mTencent.logout(activity);
                mTencent.login(activity, "all", this);
                isServerSideLogin = false;
                Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
                return;
            }
            mTencent.logout(activity);
            updateUserInfo();
        }
    }

    @Override
    public void onComplete(Object response) {
        if (null == response) {
            Log.e(TAG, "返回为空 登录失败 null == response");
            return;
        }
        JSONObject jsonResponse = (JSONObject) response;
        if (null != jsonResponse && jsonResponse.length() == 0) {
            Log.e(TAG, "返回为空 登录失败 jsonResponse.length() == 0");
            return;
        }

        Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
        initOpenidAndToken(jsonResponse);
        updateUserInfo();
    }

    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            accessToken = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
            openId = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
            String expires = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
            String expires_time = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_TIME);
            String pf = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_PLATFORM_ID);
            if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(accessToken, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUserInfo() {
        if (mTencent == null || !mTencent.isSessionValid()) {
            Log.e(TAG, " updateUserInfo mTencent == null || !mTencent.isSessionValid()");
            return;
        }

        IUiListener listener = new IUiListener() {

            @Override
            public void onError(UiError e) {

            }

            @Override
            public void onComplete(final Object response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 0;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onCancel() {

            }
        };
        mInfo = new UserInfo(activity, mTencent.getQQToken());
        mInfo.getUserInfo(listener);
    }

    @Override
    public void onError(UiError uiError) {

    }

    @Override
    public void onCancel() {

    }

    private void updateQQLoginState() {
        /** 传递用户数据到Doc应用*/
        Intent intent = new Intent(activity, DocActivity.class);
        intent.putExtra(Constants.KEY_OPENID, openId);
        intent.putExtra(Constants.KEY_ACCESS_TOKEN, accessToken);

        intent.putExtra(Constants.KEY_HEADIMGURL, headimgurl);
        intent.putExtra(Constants.KEY_NICKNAME, nickname);
        intent.putExtra(Constants.KEY_SEX, sex);
        intent.putExtra(Constants.KEY_PROVINCE, province);
        intent.putExtra(Constants.KEY_CITY, city);
        intent.putExtra(Constants.KEY_LOGIN_TYPE, UserInfoViewModel.LOGIN_QQ);
        activity.startActivity(intent);
    }
}
